package accelerate.spring.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.ProfilerRegistry;

import accelerate.commons.data.DataBean;
import accelerate.commons.exception.ApplicationException;
import accelerate.commons.util.JacksonUtils;

/**
 * Class providing utility logging methods
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 2, 2017
 */
public final class LogUtils {
	/**
	 * @param aName
	 * @param aLogger
	 * @return
	 */
	public static Profiler startProfiler(String aName, Logger aLogger) {
		ProfilerRegistry profilerRegistry = ProfilerRegistry.getThreadContextInstance();
		Profiler profiler = new Profiler(aName);
		profiler.registerWith(profilerRegistry);
		profiler.setLogger(aLogger);

		return profiler;
	}

	/**
	 * @param aLogger
	 * @param aLogLevel
	 * @param aBean
	 * @throws ApplicationException thrown due to
	 *                              {@link JacksonUtils#toJSON(Object)}
	 */
	public static void logBean(Logger aLogger, Level aLogLevel, Object aBean) throws ApplicationException {
		if (!checkLogger(aLogger, aLogLevel)) {
			return;
		}

		String message = null;
		if (aBean instanceof DataBean) {
			message = ((DataBean) aBean).toJSON();
		} else {
			message = JacksonUtils.toJSON(aBean);
		}

		log(aLogger, aLogLevel, null, message);
	}

	/**
	 * @param aLogger
	 * @param aLogLevel
	 * @param aError
	 * @param aMessage
	 * @param aArgs
	 */
	public static void log(Logger aLogger, Level aLogLevel, Throwable aError, String aMessage, Object... aArgs) {
		if (!checkLogger(aLogger, aLogLevel)) {
			return;
		}

		Object args = aArgs;

		// if provided add exception to arg list
		if (aError != null) {
			List<Object> argList = (args == null) ? new ArrayList<>() : Arrays.asList(aArgs);
			argList.add(aError);
			args = argList.toArray();
		}

		switch (aLogLevel) {
		case TRACE:
			aLogger.trace(aMessage, args);
			break;
		case DEBUG:
			aLogger.debug(aMessage, args);
			break;
		case INFO:
			aLogger.info(aMessage, args);
			break;
		case WARN:
			aLogger.warn(aMessage, args);
			break;
		case ERROR:
			aLogger.error(aMessage, args);
			break;
		}
	}

	/**
	 * @param aLogger
	 * @param aLogLevel
	 * @return
	 */
	public static boolean checkLogger(Logger aLogger, Level aLogLevel) {
		switch (aLogLevel) {
		case TRACE:
			return aLogger.isTraceEnabled();
		case DEBUG:
			return aLogger.isDebugEnabled();
		case INFO:
			return aLogger.isInfoEnabled();
		case WARN:
			return aLogger.isWarnEnabled();
		case ERROR:
			return aLogger.isErrorEnabled();
		}

		return false;
	}

	/**
	 * hidden constructor
	 */
	private LogUtils() {
	}
}