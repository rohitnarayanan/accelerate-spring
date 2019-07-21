package accelerate.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Bean class to hold security configuration properties
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 30, 2018
 */
@Component
@ConfigProps
public class BaseConfigProps {
	/**
	 * URL patterns to be omitted from security
	 */
	@Value("${com.walgreens.springboot.defaults:true}")
	private boolean defaults;

	/**
	 * The base-packages for the application
	 */
	@Value("${com.walgreens.springboot.base-packages:com.walgreens}")
	private String[] basePackages;

	/**
	 * Getter method for "defaults" property
	 * 
	 * @return defaults
	 */
	public boolean isDefaults() {
		return this.defaults;
	}

	/**
	 * Setter method for "defaults" property
	 * 
	 * @param aDefaults
	 */
	public void setDefaults(boolean aDefaults) {
		this.defaults = aDefaults;
	}

	/**
	 * Getter method for "basePackages" property
	 * 
	 * @return basePackages
	 */
	public String[] getBasePackages() {
		return this.basePackages;
	}

	/**
	 * Setter method for "basePackages" property
	 * 
	 * @param aBasePackages
	 */
	public void setBasePackages(String[] aBasePackages) {
		this.basePackages = aBasePackages;
	}
}
