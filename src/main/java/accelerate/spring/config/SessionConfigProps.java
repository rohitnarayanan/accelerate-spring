package accelerate.spring.config;

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
@ConfigurationProperties(prefix = "accelerate.spring.security.session")
public class SessionConfigProps {

	/**
	 * Maximum number of sessions allowed for a user
	 */
	private boolean enabled = false;

	/**
	 * Maximum number of sessions allowed for a user
	 */
	private int maxSessionCount = 2;

	/**
	 * URL to be redirected on invalid session
	 */
	private String sessionInvalidURL = "/login?sessionInvalid";

	/**
	 * URL to be redirected on session expire
	 */
	private String sessionExpiredURL = "/login?sessionExpired";

	/**
	 * Getter method for "enabled" property
	 * 
	 * @return enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Setter method for "enabled" property
	 * 
	 * @param aEnabled
	 */
	public void setEnabled(boolean aEnabled) {
		this.enabled = aEnabled;
	}

	/**
	 * Getter method for "maxSessionCount" property
	 * 
	 * @return maxSessionCount
	 */
	public int getMaxSessionCount() {
		return this.maxSessionCount;
	}

	/**
	 * Setter method for "maxSessionCount" property
	 * 
	 * @param aMaxSessionCount
	 */
	public void setMaxSessionCount(int aMaxSessionCount) {
		this.maxSessionCount = aMaxSessionCount;
	}

	/**
	 * Getter method for "sessionInvalidURL" property
	 * 
	 * @return sessionInvalidURL
	 */
	public String getSessionInvalidURL() {
		return this.sessionInvalidURL;
	}

	/**
	 * Setter method for "sessionInvalidURL" property
	 * 
	 * @param aSessionInvalidURL
	 */
	public void setSessionInvalidURL(String aSessionInvalidURL) {
		this.sessionInvalidURL = aSessionInvalidURL;
	}

	/**
	 * Getter method for "sessionExpiredURL" property
	 * 
	 * @return sessionExpiredURL
	 */
	public String getSessionExpiredURL() {
		return this.sessionExpiredURL;
	}

	/**
	 * Setter method for "sessionExpiredURL" property
	 * 
	 * @param aSessionExpiredURL
	 */
	public void setSessionExpiredURL(String aSessionExpiredURL) {
		this.sessionExpiredURL = aSessionExpiredURL;
	}
}
