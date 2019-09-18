package accelerate.spring.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import accelerate.spring.config.ConfigProps;

/**
 * Bean class to hold Web configuration properties
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 30, 2018
 */
@Component
@ConfigProps
public class WebConfigProps {
	/**
	 * Path to the favicon for the application
	 */
	@Value("${accelerate.spring.web.path.favicon:favicon.ico}")
	private String faviconPath;

	/**
	 * Home page of the application
	 */
	@Value("${accelerate.spring.web.path.home:}")
	private String homePath;

	/**
	 * Home page of the application
	 */
	@Value("${accelerate.spring.web.api.path-prefix:/webapi}")
	private String webAPIPathPrefix;

	/**
	 * Home page of the application
	 */
	@Value("${accelerate.spring.web.api.roles:}")
	private String[] webAPIRoles;

	/**
	 * Flag to enable/disable {@link DefaultIndexPage}
	 */
	@Value("${accelerate.spring.web.default-view.index:${accelerate.spring.defaults:true}}")
	private boolean defaultViewIndex = false;

	/**
	 * Flag to enable/disable {@link DefaultErrorView}
	 */
	@Value("${accelerate.spring.web.default-view.error:${accelerate.spring.defaults:true}}")
	private boolean defaultViewError = false;

	/**
	 * Getter method for "faviconPath" property
	 * 
	 * @return faviconPath
	 */
	public String getFaviconPath() {
		return this.faviconPath;
	}

	/**
	 * Setter method for "faviconPath" property
	 * 
	 * @param aFaviconPath
	 */
	public void setFaviconPath(String aFaviconPath) {
		this.faviconPath = aFaviconPath;
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

	/**
	 * Getter method for "webAPIPathPrefix" property
	 * 
	 * @return webAPIPathPrefix
	 */
	public String getWebAPIPathPrefix() {
		return this.webAPIPathPrefix;
	}

	/**
	 * Setter method for "webAPIPathPrefix" property
	 * 
	 * @param aWebAPIPathPrefix
	 */
	public void setWebAPIPathPrefix(String aWebAPIPathPrefix) {
		this.webAPIPathPrefix = aWebAPIPathPrefix;
	}

	/**
	 * Getter method for "webAPIRoles" property
	 * 
	 * @return webAPIRoles
	 */
	public String[] getWebAPIRoles() {
		return this.webAPIRoles;
	}

	/**
	 * Setter method for "webAPIRoles" property
	 * 
	 * @param aWebAPIRoles
	 */
	public void setWebAPIRoles(String[] aWebAPIRoles) {
		this.webAPIRoles = aWebAPIRoles;
	}

	/**
	 * Getter method for "defaultViewIndex" property
	 * 
	 * @return defaultViewIndex
	 */
	public boolean isDefaultViewIndex() {
		return this.defaultViewIndex;
	}

	/**
	 * Setter method for "defaultViewIndex" property
	 * 
	 * @param aDefaultViewIndex
	 */
	public void setDefaultViewIndex(boolean aDefaultViewIndex) {
		this.defaultViewIndex = aDefaultViewIndex;
	}

	/**
	 * Getter method for "defaultViewError" property
	 * 
	 * @return defaultViewError
	 */
	public boolean isDefaultViewError() {
		return this.defaultViewError;
	}

	/**
	 * Setter method for "defaultViewError" property
	 * 
	 * @param aDefaultViewError
	 */
	public void setDefaultViewError(boolean aDefaultViewError) {
		this.defaultViewError = aDefaultViewError;
	}
}
