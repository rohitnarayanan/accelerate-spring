package accelerate.spring.logging;

import static com.walgreens.springboot.lang.CommonConstants.EMPTY_STRING;
import static com.walgreens.springboot.lang.CommonConstants.PIPE_CHAR;
import static com.walgreens.springboot.lang.CommonConstants.SPACE_CHAR;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.ProfilerRegistry;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.walgreens.springboot.aop.MethodInterceptor;
import com.walgreens.springboot.config.BaseConfigProps;
import com.walgreens.springboot.config.ConfigConstants;
import com.walgreens.springboot.util.CommonUtils;
import com.walgreens.springboot.util.JSONUtils;
import com.walgreens.springboot.util.StringUtils;

/**
 * {@link MethodInterceptor} implementation to log {@link Profiler} information
 * of intercepted methods. This logger uses debug level and same can be
 * suppressed via logback configuration. For more information visit
 * <a>https://www.slf4j.org/extensions.html#profiler</a>
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since July 05, 2019
 */
@Profile(ConfigConstants.PROFILE_LOGGING)
@ConditionalOnExpression("${com.walgreens.springboot.logging.profiling:${com.walgreens.springboot.defaults:true}}")
@Component
@Aspect
public class ProfilingLogger {
	/**
	 * The base-packages for the application
	 */
	@Autowired
	private BaseConfigProps baseConfigProps = null;

	/**
	 * @param aJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("@within(Profiled)")
	public Object interceptMethod(ProceedingJoinPoint aJoinPoint) throws Throwable {
		String[] signature = aJoinPoint.getSignature().toString().split(SPACE_CHAR);

		Object[] params = aJoinPoint.getArgs();
		String returnType = signature[0];
		String method = signature[1];
		Object returnValue = null;
		Throwable error = null;

		/*
		 * log method entry
		 */
		logEntry(method, params);

		/*
		 * start method profiling
		 */
		boolean nestedFlag = startProfiler(method);
		try {
			returnValue = aJoinPoint.proceed();
		} catch (Throwable throwable) {
			error = throwable;
		}

		/*
		 * log profiler info
		 */
		stopProfiler(nestedFlag);

		/*
		 * log method exit
		 */
		logExit(method, returnType, returnValue, error);

		if (error != null) {
			throw error;
		}

		return returnValue;
	}

	/**
	 * @param aMethod
	 * @return
	 */
	public static boolean startProfiler(String aMethod) {
		if (!LOGGER.isDebugEnabled()) {
			return false;
		}

		ProfilerRegistry profilerRegistry = ProfilerRegistry.getThreadContextInstance();
		Profiler currentProfiler = profilerRegistry.get(CURRENT_PROFILER);

		if (currentProfiler == null) {
			Profiler rootProfiler = new Profiler(aMethod);
			rootProfiler.registerWith(profilerRegistry);
			rootProfiler.setLogger(LOGGER);
			profilerRegistry.put(ROOT_PROFILER, rootProfiler);
			profilerRegistry.put(CURRENT_PROFILER, rootProfiler);

			return false;
		}

		Profiler nestedProfiler = currentProfiler.startNested(aMethod);
		profilerRegistry.put(CURRENT_PROFILER, nestedProfiler);
		return true;
	}

	/**
	 * @param aNestedProfiler
	 */
	public static void stopProfiler(boolean aNestedProfiler) {
		if (aNestedProfiler) {
			return;
		}

		if (!LOGGER.isDebugEnabled()) {
			return;
		}

		ProfilerRegistry profilerRegistry = ProfilerRegistry.getThreadContextInstance();
		Profiler rootProfiler = profilerRegistry.get(ROOT_PROFILER);
		rootProfiler.stop().log();
		profilerRegistry.clear();
	}

	/**
	 * @param aMethod
	 * @param aParams
	 */
	public void logEntry(String aMethod, Object... aParams) {
		if (!LOGGER.isTraceEnabled()) {
			return;
		}

		try {
			LOGGER.trace("{},{},[{}]", aMethod, "INPUT",
					ObjectUtils.isEmpty(aParams) ? EMPTY_STRING
							: Arrays.stream(aParams)
									.map(aParam -> isJSONSerializable(aParam) ? JSONUtils.serialize(aParam)
											: ObjectUtils.nullSafeToString(aParam))
									.collect(Collectors.joining(PIPE_CHAR)));
		} catch (Exception error) {
			LOGGER.warn("Unable to log input parameters '{}' for {}", ObjectUtils.nullSafeToString(aParams), aMethod,
					error);
		}
	}

	/**
	 * @param aMethod
	 * @param aReturnType
	 * @param aReturnValue
	 * @param aError
	 */
	public void logExit(String aMethod, String aReturnType, Object aReturnValue, Throwable aError) {
		if (!LOGGER.isTraceEnabled()) {
			return;
		}

		try {
			if (aError != null) {
				LOGGER.trace("{},EXIT,{}", aMethod, CommonUtils.getErrorLog(aError));
			} else {
				LOGGER.trace("{},RETURN,{}", aMethod,
						StringUtils.safeEquals("void", aReturnType) ? "VOID"
								: isJSONSerializable(aReturnValue) ? JSONUtils.serialize(aReturnValue)
										: ObjectUtils.nullSafeToString(aReturnValue));
			}
		} catch (Exception error) {
			LOGGER.warn("Unable to log return value '{}' for '{}'", aReturnType, aMethod, error);
		}
	}

	/**
	 * @param aValue
	 * @return
	 */
	private boolean isJSONSerializable(Object aValue) {
		if ((aValue == null) || (AopUtils.isAopProxy(aValue))) {
			return false;
		}

		for (String basePackage : this.baseConfigProps.getBasePackages()) {
			if (aValue.getClass().getName().startsWith(basePackage)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Key against which the parent profiler is registered
	 */
	public static final String ROOT_PROFILER = "ROOT_PROFILER";

	/**
	 * Key against which the current profiler is registered
	 */
	public static final String CURRENT_PROFILER = "CURRENT_PROFILER";

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfilingLogger.class);
}