package accelerate.spring.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.walgreens.springboot.logging.ConfigPropsLogger;

/**
 * Annotation to mark components with spring/application properties. This allows
 * {@link ConfigPropsLogger} to log properties set by Spring Boot
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since June 22, 2019
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
public @interface ConfigProps {
	// Marker annotation
}
