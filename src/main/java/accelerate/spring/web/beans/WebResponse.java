package accelerate.spring.web.beans;

import accelerate.commons.data.DataBean;

/**
 * {@link DataBean} extension for HTTP WebResponse in Web Application
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 3, 2017
 */
public class WebResponse extends DataBean {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Context Path of the application
	 */
	private String contextPath;

	/**
	 * Return Code. <code>
	 * 0: Success
	 * !=0: Failure/Alternate Result
	 * </code>
	 */
	private int returnCode;

	/**
	 * Flag to indicate that there was an error on the server side while processing
	 * the request.
	 */
	private boolean serverError;

	/**
	 * Result as mapped in the RequestHandlers.xml file
	 */
	private String viewName;

	/**
	 * {@link WebMessage} instance
	 */
	private WebMessage message;

	/**
	 * {@link Exception} caught during process
	 */
	private Exception error;

	/**
	 * {@link WebRequest} instance
	 */
	private transient WebRequest request;

	/**
	 * default constructor
	 */
	public WebResponse() {
	}

	/**
	 * shortcut constructor to set the {@link #viewName}
	 *
	 * @param aViewName
	 */
	public WebResponse(String aViewName) {
		this.viewName = aViewName;
	}

	/**
	 * shortcut constructor to set the {@link #viewName} and {@link #request}
	 *
	 * @param aViewName
	 * @param aWebRequest
	 */
	public WebResponse(String aViewName, WebRequest aWebRequest) {
		this.viewName = aViewName;
		this.request = aWebRequest;
	}

	/**
	 * Getter method for "contextPath" property
	 * 
	 * @return contextPath
	 */
	public String getContextPath() {
		return this.contextPath;
	}

	/**
	 * Setter method for "contextPath" property
	 * 
	 * @param aContextPath
	 */
	public void setContextPath(String aContextPath) {
		this.contextPath = aContextPath;
	}

	/**
	 * Getter method for "returnCode" property
	 * 
	 * @return returnCode
	 */
	public int getReturnCode() {
		return this.returnCode;
	}

	/**
	 * Setter method for "returnCode" property
	 * 
	 * @param aReturnCode
	 */
	public void setReturnCode(int aReturnCode) {
		this.returnCode = aReturnCode;
	}

	/**
	 * Getter method for "serverError" property
	 * 
	 * @return serverError
	 */
	public boolean isServerError() {
		return this.serverError;
	}

	/**
	 * Setter method for "serverError" property
	 * 
	 * @param aServerError
	 */
	public void setServerError(boolean aServerError) {
		this.serverError = aServerError;
	}

	/**
	 * Getter method for "viewName" property
	 * 
	 * @return viewName
	 */
	public String getViewName() {
		return this.viewName;
	}

	/**
	 * Setter method for "viewName" property
	 * 
	 * @param aViewName
	 */
	public void setViewName(String aViewName) {
		this.viewName = aViewName;
	}

	/**
	 * Getter method for "message" property
	 * 
	 * @return message
	 */
	public WebMessage getMessage() {
		return this.message;
	}

	/**
	 * Setter method for "message" property
	 * 
	 * @param aMessage
	 */
	public void setMessage(WebMessage aMessage) {
		this.message = aMessage;
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

	/**
	 * Getter method for "request" property
	 * 
	 * @return request
	 */
	public WebRequest getRequest() {
		return this.request;
	}

	/**
	 * Setter method for "request" property
	 * 
	 * @param aRequest
	 */
	public void setRequest(WebRequest aRequest) {
		this.request = aRequest;
	}
}