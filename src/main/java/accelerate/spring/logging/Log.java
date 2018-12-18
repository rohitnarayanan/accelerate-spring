package accelerate.spring.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to configure {@link LoggerAspect} to audit only
 * methods marked by this annotation
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 20, 2018
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
public @interface Log {
	/**
	 * Shortcut for not writing the {@link #log()} attribute when providing single
	 * arguments
	 * 
	 * @return
	 */
	LogType value() default LogType.PROFILE;

	/**
	 * Type of audit information to be logged
	 * 
	 * @return
	 */
	LogType[] log() default {};

	/**
	 * PUT DESCRIPTION HERE
	 * 
	 * @version 1.0 Initial Version
	 * @author Rohit Narayanan
	 * @since October 5, 2017
	 */
	enum LogType {
		/**
		 * Logs when a new instance of the annotated class is created
		 */
		TRACE_NEW,

		/**
		 * Logs the entry and exit of annotated method
		 */
		AUDIT,

		/**
		 * Logs the execution time of annotated method
		 */
		METRICS,

		/**
		 * Logs both AUDIT and METRIC information for annotated method
		 */
		PROFILE,

		/**
		 * Logs the input parameters passed to annotated method
		 */
		DEBUG_INPUT,

		/**
		 * Logs the output returned by annotated method
		 */
		DEBUG_OUTPUT,

		/**
		 * Logs all the above information for the annotated method
		 */
		ALL;
	}
}
