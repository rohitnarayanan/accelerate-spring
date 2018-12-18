package accelerate.spring.logging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import accelerate.commons.constants.CommonConstants;
import accelerate.commons.exceptions.ApplicationException;
import accelerate.commons.utils.CommonUtils;
import accelerate.commons.utils.JSONUtils;
import accelerate.commons.utils.StringUtils;
import accelerate.spring.config.LoggerConfigProps;
import accelerate.spring.constants.ProfileConstants;
import accelerate.spring.logging.Log.LogType;

/**
 * Spring {@link Aspect} to allow applications to log various information for
 * auditing, profiling, and debugging purposes.
 * 
 * NOTE: As spring aspects provide load time weaving only, this class is able to
 * capture method calls on spring managed beans. It does not capture
 * constructors, or internal method calls.
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 20, 2018
 */
@Profile(ProfileConstants.PROFILE_LOGGING)
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class LoggerAspect {
	/**
	 * {@link Logger} instance
	 */
	private static final Logger _LOGGER = LoggerFactory.getLogger(LoggerAspect.class);

	/**
	 * {@link LoggerConfigProps} instance
	 */
	@Autowired
	private LoggerConfigProps loggerConfigProps = null;

	/**
	 * This method catches execution of all methods that belong to a class annotated
	 * with {@link Log}. It filters out methods that are themselves annotated with
	 * {@link Log} as those will be handled by
	 * {@link #catchAnnotatedMethod(ProceedingJoinPoint, Log)}
	 * 
	 * @param aJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(*.new(..)) && within(@accelerate.spring.logging.Log *)")
	public Object catchNewObjectOfAnnotatedClass(ProceedingJoinPoint aJoinPoint) throws Throwable {
		if (_LOGGER.isDebugEnabled() && this.loggerConfigProps.isDebugEnabled()) {
			Log log = aJoinPoint.getTarget().getClass().getDeclaredAnnotation(Log.class);
			if (log != null && Arrays.stream(log.log()).filter(aLog -> aLog == Log.LogType.TRACE_NEW).findFirst()
					.orElse(null) != null) {
				_LOGGER.debug("{}.NEW()", aJoinPoint.getTarget().getClass());
			}
		}

		return aJoinPoint.proceed();

	}

	/**
	 * This method catches execution of all methods that belong to a class annotated
	 * with {@link Log}. It filters out methods that are themselves annotated with
	 * {@link Log} as those will be handled by
	 * {@link #catchAnnotatedMethod(ProceedingJoinPoint, Log)}
	 * 
	 * @param aJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* *.*(..)) && within(@accelerate.spring.logging.Log *) && !@annotation(accelerate.spring.logging.Log)")
	public Object catchMethodInAnnotatedClass(ProceedingJoinPoint aJoinPoint) throws Throwable {
		return log(aJoinPoint, aJoinPoint.getTarget().getClass().getDeclaredAnnotation(Log.class));
	}

	/**
	 * This method catches execution of all methods that belong to a class annotated
	 * with {@link Log}
	 * 
	 * @param aJoinPoint
	 * @param aLog
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* *.*(..)) && @annotation(aLog)")
	public Object catchAnnotatedMethod(ProceedingJoinPoint aJoinPoint, Log aLog) throws Throwable {
		return log(aJoinPoint, aLog);
	}

	/**
	 * This method logs various information of the annotated method based on the
	 * parameters passed to {@link Log}.
	 * 
	 * @param aJoinPoint the {@link JoinPoint} caught by the {@link Around} advice
	 * @param aLog
	 * @return the object returned by the target method
	 * @throws Throwable
	 */
	public Object log(ProceedingJoinPoint aJoinPoint, Log aLog) throws Throwable {
		if (!_LOGGER.isDebugEnabled()) {
			return aJoinPoint.proceed();
		}

		Throwable error = null;
		Object returnObject = null;
		boolean[] flags = { /* input */false, /* audit */false, /* metric */false, /* output */false };
		String[] signature = aJoinPoint.getSignature().toString().split(CommonConstants.SPACE_CHAR);
		String returnType = signature[0];
		String methodName = signature[1];
		Set<LogType> logTypes = new HashSet<>();

		if (aLog == null) {
			logTypes.add(Log.LogType.PROFILE);
		} else if (aLog.log() != null) {
			logTypes.addAll(Arrays.asList(aLog.log()));
		} else if (logTypes.isEmpty()) {
			logTypes.add(aLog.value());
		}

		for (Log.LogType logType : logTypes) {
			if (this.loggerConfigProps.isDebugEnabled()
					&& CommonUtils.compareAny(logType, LogType.DEBUG_INPUT, LogType.ALL)) {
				flags[0] = true;
			}

			if (this.loggerConfigProps.isAuditEnabled()
					&& CommonUtils.compareAny(logType, LogType.AUDIT, LogType.PROFILE, LogType.ALL)) {
				flags[1] = true;
			}

			if (this.loggerConfigProps.isMetricsEnabled()
					&& CommonUtils.compareAny(logType, LogType.METRICS, LogType.PROFILE, LogType.ALL)) {
				flags[2] = true;
			}

			if (this.loggerConfigProps.isDebugEnabled()
					&& CommonUtils.compareAny(logType, LogType.DEBUG_OUTPUT, LogType.ALL)) {
				flags[3] = true;
			}
		}

		if (flags[0]) {
			logInputParams(methodName, aJoinPoint.getArgs());
		}

		if (flags[1]) {
			logMethodEntry(methodName);
		}

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			returnObject = aJoinPoint.proceed();
		} catch (Throwable throwable) {
			error = throwable;
		}
		stopWatch.stop();

		if (flags[2]) {
			logMethodTime(methodName, stopWatch);
		}

		if (flags[1]) {
			logMethodExit(methodName, error);
		}

		if (error != null) {
			throw error;
		}

		if (flags[3]) {
			logReturnValue(methodName, returnType, returnObject);
		}

		return returnObject;
	}

	/**
	 * @param aMethodName
	 * @param aInputParams
	 */
	public static final void logInputParams(String aMethodName, Object[] aInputParams) {
		if (!_LOGGER.isDebugEnabled()) {
			return;
		}

		try {
			_LOGGER.debug("{},{},{}", aMethodName, "INPUT",
					(aInputParams.length == 0) ? "[]" : JSONUtils.serialize(aInputParams));
		} catch (@SuppressWarnings("unused") ApplicationException error) {
			// ignore error
		}
	}

	/**
	 * @param aMethodName
	 */
	public static final void logMethodEntry(String aMethodName) {
		if (!_LOGGER.isDebugEnabled()) {
			return;
		}

		_LOGGER.debug("{},{},{}", aMethodName, "START", System.currentTimeMillis());
	}

	/**
	 * @param aMethodName
	 * @param aStopWatch
	 */
	public static final void logMethodTime(String aMethodName, StopWatch aStopWatch) {
		if (!_LOGGER.isDebugEnabled()) {
			return;
		}

		_LOGGER.debug("{},{},{}", aMethodName, "TIME", aStopWatch.getTotalTimeMillis());
	}

	/**
	 * @param aMethodName
	 * @param aError
	 */
	public static final void logMethodExit(String aMethodName, Throwable aError) {
		if (!_LOGGER.isDebugEnabled()) {
			return;
		}

		_LOGGER.debug("{},{},{}", aMethodName, (aError != null) ? "EXIT" : "END", System.currentTimeMillis());
	}

	/**
	 * @param aMethodName
	 * @param aReturnType
	 * @param aReturnValue
	 */
	public static final void logReturnValue(String aMethodName, String aReturnType, Object aReturnValue) {
		if (!_LOGGER.isDebugEnabled()) {
			return;
		}

		try {
			_LOGGER.debug("{},{},{}", aMethodName, "RETURN",
					StringUtils.safeEquals("void", aReturnType) ? "VOID" : JSONUtils.serialize(aReturnValue));
		} catch (@SuppressWarnings("unused") ApplicationException error) {
			// ignore error
		}
	}
}