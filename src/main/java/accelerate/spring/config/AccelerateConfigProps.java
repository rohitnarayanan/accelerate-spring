package accelerate.spring.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean class to hold security configuration properties
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 23, 2018
 */
@Component
@ConfigurationProperties(prefix = "accelerate.spring")
@SuppressWarnings("unused")
public class AccelerateConfigProps {
	/**
	 * config {@link Map} for "accelerate.spring.beans.*" props
	 */
	private Map<String, Object> webConfig = new HashMap<>();

	/**
	 * config {@link Map} for "accelerate.spring.security.authentication.*" props
	 */
	private Map<String, Object> authenticationConfig = new HashMap<>();

	/**
	 * config {@link Map} for "accelerate.spring.security.session.*" props
	 */
	private Map<String, Object> sessionManagement = new HashMap<>();

	/**
	 * constructor to setup defaults
	 */
	public AccelerateConfigProps() {

	}
}
