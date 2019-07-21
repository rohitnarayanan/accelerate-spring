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
public class SecurityConfigProps {
	/**
	 * Flag to indicate whether default handling is to be enabled
	 */
	@Value("#{'${accelerate.spring.security.enabled:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
	private boolean enabled = true;

	/**
	 * URL patterns to be allowed for all
	 */
	@Value("${accelerate.spring.security.allow-url-Patterns:}")
	private String[] allowURLPatterns = {};

	/**
	 * URL patterns to be omitted from security
	 */
	@Value("${accelerate.spring.security.ignore-url-Patterns:}")
	private String[] ignoreURLPatterns = {};

	/**
	 * Path to handle login
	 */
	@Value("${accelerate.spring.security.login-path:/login}")
	private String loginPath = null;

	/**
	 * Path to handle logout
	 */
	@Value("${accelerate.spring.security.logout-path:/logout}")
	private String logoutPath = null;

	/**
	 * Cookies to be cleared on logout
	 */
	@Value("${accelerate.spring.security.logout-clear-cookies:['JSESSIONID']}")
	private String[] logoutClearCookies = null;

	/**
	 * URL to be redirected to after logout
	 */
	@Value("${accelerate.spring.security.post-logout-url:#{securityConfigProps.loginPath}?logout}")
	private String postLogoutURL = null;

	/**
	 * List of roles that are allowed to acces Actuator endpoints
	 */
	@Value("${accelerate.spring.security.actuator-roles:ADMIN}")
	private String[] actuatorRoles = { "ADMIN" };

	/**
	 * Maximum number of sessions allowed for a user
	 */
	@Value("${accelerate.spring.security.max-user-sessions:2}")
	private int maxUserSessions = 0;

	/**
	 * URL to be redirected on invalid session
	 */
	@Value("${accelerate.spring.security.session-invalid-url:#{securityConfigProps.loginPath}?sessionInvalid}")
	private String sessionInvalidURL = null;

	/**
	 * URL to be redirected on session expire
	 */
	@Value("${accelerate.spring.security.session-expired-url:#{securityConfigProps.loginPath}?sessionExpired}")
	private String sessionExpiredURL = null;

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
	 * Getter method for "allowURLPatterns" property
	 * 
	 * @return allowURLPatterns
	 */
	public String[] getAllowURLPatterns() {
		return this.allowURLPatterns;
	}

	/**
	 * Setter method for "allowURLPatterns" property
	 * 
	 * @param aAllowURLPatterns
	 */
	public void setAllowURLPatterns(String[] aAllowURLPatterns) {
		this.allowURLPatterns = aAllowURLPatterns;
	}

	/**
	 * Getter method for "ignoreURLPatterns" property
	 * 
	 * @return ignoreURLPatterns
	 */
	public String[] getIgnoreURLPatterns() {
		return this.ignoreURLPatterns;
	}

	/**
	 * Setter method for "ignoreURLPatterns" property
	 * 
	 * @param aIgnoreURLPatterns
	 */
	public void setIgnoreURLPatterns(String[] aIgnoreURLPatterns) {
		this.ignoreURLPatterns = aIgnoreURLPatterns;
	}

	/**
	 * Getter method for "loginPath" property
	 * 
	 * @return loginPath
	 */
	public String getLoginPath() {
		return this.loginPath;
	}

	/**
	 * Setter method for "loginPath" property
	 * 
	 * @param aLoginPath
	 */
	public void setLoginPath(String aLoginPath) {
		this.loginPath = aLoginPath;
	}

	/**
	 * Getter method for "logoutPath" property
	 * 
	 * @return logoutPath
	 */
	public String getLogoutPath() {
		return this.logoutPath;
	}

	/**
	 * Setter method for "logoutPath" property
	 * 
	 * @param aLogoutPath
	 */
	public void setLogoutPath(String aLogoutPath) {
		this.logoutPath = aLogoutPath;
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
	 * Getter method for "postLogoutURL" property
	 * 
	 * @return postLogoutURL
	 */
	public String getPostLogoutURL() {
		return this.postLogoutURL;
	}

	/**
	 * Setter method for "postLogoutURL" property
	 * 
	 * @param aPostLogoutURL
	 */
	public void setPostLogoutURL(String aPostLogoutURL) {
		this.postLogoutURL = aPostLogoutURL;
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

	/**
	 * Getter method for "maxUserSessions" property
	 * 
	 * @return maxUserSessions
	 */
	public int getMaxUserSessions() {
		return this.maxUserSessions;
	}

	/**
	 * Setter method for "maxUserSessions" property
	 * 
	 * @param aMaxUserSessions
	 */
	public void setMaxUserSessions(int aMaxUserSessions) {
		this.maxUserSessions = aMaxUserSessions;
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
