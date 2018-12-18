package accelerate.spring.staticlistener;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation provides non spring managed classes, static access to spring
 * context start and stop events.
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@Retention(RUNTIME)
@Target(value = { TYPE })
@Documented
public @interface StaticContextListener {
	/**
	 * This method will be called when the spring context is initialized
	 */
	public abstract String onContextStarted() default "onContextStarted";

	/**
	 * This method will be called when the spring context is destroyed
	 */
	public abstract String onContextClosed() default "onContextClosed";
}