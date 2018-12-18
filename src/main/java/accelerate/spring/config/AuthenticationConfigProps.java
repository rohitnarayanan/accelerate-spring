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
@ConfigurationProperties(prefix = "accelerate.spring.security.authentication")
public class AuthenticationConfigProps {

	/**
	 * Flag to indicate whether default handling is to be enabled
	 */
	private boolean enabled = false;

	/**
	 * URL patterns to be allowed for all
	 */
	private String[] allowURLPatterns = {};

	/**
	 * URL patterns to be omitted from security
	 */
	private String[] ignoreURLPatterns = {};

	/**
	 * Path to handle login
	 */
	private String loginPath = "/login";

	/**
	 * Path to handle logout
	 */
	private String logoutPath = "/logout";

	/**
	 * Cookies to be cleared on logout
	 */
	private String[] logoutClearCookies = { "JSESSIONID" };

	/**
	 * URL to be redirected to after logout
	 */
	private String postLogoutURL = "/login?logout";

	/**
	 * List of roles that are allowed to acces Actuator endpoints
	 */
	private String[] actuatorAllowRoles = { "ADMIN" };

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
	 * Getter method for "actuatorAllowRoles" property
	 * 
	 * @return actuatorAllowRoles
	 */
	public String[] getActuatorAllowRoles() {
		return this.actuatorAllowRoles;
	}

	/**
	 * Setter method for "actuatorAllowRoles" property
	 * 
	 * @param aActuatorAllowRoles
	 */
	public void setActuatorAllowRoles(String[] aActuatorAllowRoles) {
		this.actuatorAllowRoles = aActuatorAllowRoles;
	}
}
