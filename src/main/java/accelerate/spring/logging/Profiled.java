package accelerate.spring.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to configure annotate classes that need to be
 * profiled. {@link ProfilingLogger} then captures the call to login tracing and
 * profiling information.
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since July 07, 2019
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Profiled {
	// Marker annotation for classes that need to be profiled
}
