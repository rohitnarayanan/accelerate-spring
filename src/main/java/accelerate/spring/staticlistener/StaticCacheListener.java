package accelerate.spring.staticlistener;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import accelerate.spring.cache.DataMapCache;

/**
 * This annotation provides non spring managed classes, static access to
 * {@link DataMapCache} instances
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@Retention(RUNTIME)
@Target(value = { TYPE })
@Documented
public @interface StaticCacheListener {
	/**
	 * Name of the cache the annotated class is listening to
	 *
	 * @return
	 */
	public abstract String name();

	/**
	 * Name of the method that will handle the callback
	 *
	 * @return
	 */
	public abstract String handler() default "handleCacheLoad";
}
