package accelerate.spring.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.walgreens.springboot.lang.DataBean;

/**
 * {@link DataBean} extension for HTTP Requests in Web Application
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 3, 2017
 */
public class WebRequest extends DataBean {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the action to be invoked. Can be used across multiple frameworks
	 */
	private String actionId;

	/**
	 * Generic query string. Can be used in customized ways by applications
	 */
	private String queryString;

	/**
	 * {@link Throwable} instance caught required to be passed to another service
	 */
	@JsonIgnore
	private Exception error;

	/**
	 * default constructor
	 */
	public WebRequest() {
	}

	/**
	 * Getter method for "actionId" property
	 * 
	 * @return actionId
	 */
	public String getActionId() {
		return this.actionId;
	}

	/**
	 * Setter method for "actionId" property
	 * 
	 * @param aActionId
	 */
	public void setActionId(String aActionId) {
		this.actionId = aActionId;
	}

	/**
	 * Getter method for "queryString" property
	 * 
	 * @return queryString
	 */
	public String getQueryString() {
		return this.queryString;
	}

	/**
	 * Setter method for "queryString" property
	 * 
	 * @param aQueryString
	 */
	public void setQueryString(String aQueryString) {
		this.queryString = aQueryString;
	}

	/**
	 * Getter method for "error" property
	 * 
	 * @return error
	 */
	public Exception getError() {
		return this.error;
	}

	/**
	 * Setter method for "error" property
	 * 
	 * @param aError
	 */
	public void setError(Exception aError) {
		this.error = aError;
	}
}