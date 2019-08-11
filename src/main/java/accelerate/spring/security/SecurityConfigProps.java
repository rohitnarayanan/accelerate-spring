package accelerate.spring.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import accelerate.spring.config.ConfigProps;

/**
 * Bean class to hold security configuration properties
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 30, 2018
 */
@Component
@ConfigProps
public class SecurityConfigProps {
	/**
	 * URL patterns to be omitted from security
	 */
	@Value("${accelerate.spring.security.url-patterns.ignored:}")
	private String[] urlPatternsIgnored;

	/**
	 * URL patterns to be allowed for all
	 */
	@Value("${accelerate.spring.security.url-patterns.allowed:}")
	private String[] urlPatternsAllowed;

	/**
	 * Cookies to be cleared on logout
	 */
	@Value("${accelerate.spring.security.logout.clear-cookies:}")
	private String[] logoutClearCookies;

	/**
	 * URL to be redirected to after logout
	 */
	@Value("${accelerate.spring.security.logout.success-url:/login?logout}")
	private String logoutSuccessURL;

	/**
	 * Maximum number of sessions allowed for a user
	 */
	@Value("${accelerate.spring.security.session.max-count:-1}")
	private int sessionMaxCount;

	/**
	 * URL to be redirected on session expiry
	 */
	@Value("${accelerate.spring.security.session.expired-url:/login?sessionExpired}")
	private String sessionExpiredURL;

	/**
	 * Roles to be allowed access to actuator endpoints
	 */
	@Value("${accelerate.spring.security.actuator.roles:}")
	private String[] actuatorRoles;

	/**
	 * Getter method for "urlPatternsIgnored" property
	 * 
	 * @return urlPatternsIgnored
	 */
	public String[] getUrlPatternsIgnored() {
		return this.urlPatternsIgnored;
	}

	/**
	 * Setter method for "urlPatternsIgnored" property
	 * 
	 * @param aUrlPatternsIgnored
	 */
	public void setUrlPatternsIgnored(String[] aUrlPatternsIgnored) {
		this.urlPatternsIgnored = aUrlPatternsIgnored;
	}

	/**
	 * Getter method for "urlPatternsAllowed" property
	 * 
	 * @return urlPatternsAllowed
	 */
	public String[] getUrlPatternsAllowed() {
		return this.urlPatternsAllowed;
	}

	/**
	 * Setter method for "urlPatternsAllowed" property
	 * 
	 * @param aUrlPatternsAllowed
	 */
	public void setUrlPatternsAllowed(String[] aUrlPatternsAllowed) {
		this.urlPatternsAllowed = aUrlPatternsAllowed;
	}

	/**
	 * Getter method for "logoutClearCookies" property
	 * 
	 * @return logoutClearCookies
	 */
	public String[] getLogoutClearCookies() {
		return this.logoutClearCookies;
	}

	/**
	 * Setter method for "logoutClearCookies" property
	 * 
	 * @param aLogoutClearCookies
	 */
	public void setLogoutClearCookies(String[] aLogoutClearCookies) {
		this.logoutClearCookies = aLogoutClearCookies;
	}

	/**
	 * Getter method for "logoutSuccessURL" property
	 * 
	 * @return logoutSuccessURL
	 */
	public String getLogoutSuccessURL() {
		return this.logoutSuccessURL;
	}

	/**
	 * Setter method for "logoutSuccessURL" property
	 * 
	 * @param aLogoutSuccessURL
	 */
	public void setLogoutSuccessURL(String aLogoutSuccessURL) {
		this.logoutSuccessURL = aLogoutSuccessURL;
	}

	/**
	 * Getter method for "sessionMaxCount" property
	 * 
	 * @return sessionMaxCount
	 */
	public int getSessionMaxCount() {
		return this.sessionMaxCount;
	}

	/**
	 * Setter method for "sessionMaxCount" property
	 * 
	 * @param aSessionMaxCount
	 */
	public void setSessionMaxCount(int aSessionMaxCount) {
		this.sessionMaxCount = aSessionMaxCount;
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

	/**
	 * Getter method for "actuatorRoles" property
	 * 
	 * @return actuatorRoles
	 */
	public String[] getActuatorRoles() {
		return this.actuatorRoles;
	}

	/**
	 * Setter method for "actuatorRoles" property
	 * 
	 * @param aActuatorRoles
	 */
	public void setActuatorRoles(String[] aActuatorRoles) {
		this.actuatorRoles = aActuatorRoles;
	}
}
