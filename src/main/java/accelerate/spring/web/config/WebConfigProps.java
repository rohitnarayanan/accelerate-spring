package accelerate.spring.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Bean class to hold security configuration properties
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 23, 2018
 */
@Component
public class WebConfigProps {
	/**
	 * Path to the favicon for the application
	 */
	@Value("${accelerate.spring.web.favicon:/accelerate/images/favicon.png}")
	private String favicon = null;

	/**
	 * URL to be redirected on invalid session
	 */
	@Value("${accelerate.spring.web.home-path:}")
	private String homePath = null;

	/**
	 * Getter method for "favicon" property
	 * 
	 * @return favicon
	 */
	public String getFavicon() {
		return this.favicon;
	}

	/**
	 * Setter method for "favicon" property
	 * 
	 * @param aFavicon
	 */
	public void setFavicon(String aFavicon) {
		this.favicon = aFavicon;
	}

	/**
	 * Getter method for "homePath" property
	 * 
	 * @return homePath
	 */
	public String getHomePath() {
		return this.homePath;
	}

	/**
	 * Setter method for "homePath" property
	 * 
	 * @param aHomePath
	 */
	public void setHomePath(String aHomePath) {
		this.homePath = aHomePath;
	}
}
