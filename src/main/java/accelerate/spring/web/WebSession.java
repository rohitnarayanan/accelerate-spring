package accelerate.spring.web;

import java.util.Date;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;

import com.walgreens.springboot.lang.DataBean;

/**
 * {@link DataBean} extension for HTTP WebSession in Web Application
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since December 11, 2017
 */
public class WebSession extends DataBean implements HttpSessionBindingListener {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSession.class);

	/**
	 * WebSession Id
	 */
	private Date initTime = null;

	/**
	 * WebSession Id
	 */
	private String sessionId = null;

	/**
	 * Login User Name
	 */
	private String username = null;

	/**
	 * Login Password
	 */
	private transient String password = null;

	/**
	 * Default Constructor
	 */
	public WebSession() {
		super("sessionId");

		this.initTime = new Date();
		if (RequestContextHolder.getRequestAttributes() != null) {
			this.sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
		}

		LOGGER.info("WebSession initialized for '{}' with id '{}' at '{}'", this.username, this.sessionId,
				this.initTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.
	 * http.HttpSessionBindingEvent)
	 */
	/**
	 * @param aEvent
	 */
	@Override
	public void valueBound(HttpSessionBindingEvent aEvent) {
		// blank impl
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.
	 * http.HttpSessionBindingEvent)
	 */
	/**
	 * @param aEvent
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent aEvent) {
		// blank impl
	}

	/**
	 * Getter method for "initTime" property
	 * 
	 * @return initTime
	 */
	public Date getInitTime() {
		return this.initTime;
	}

	/**
	 * Setter method for "initTime" property
	 * 
	 * @param aInitTime
	 */
	public void setInitTime(Date aInitTime) {
		this.initTime = aInitTime;
	}

	/**
	 * Setter method for "sessionId" property
	 * 
	 * @param aSessionId
	 */
	public void setSessionId(String aSessionId) {
		this.sessionId = aSessionId;
	}

	/**
	 * Getter method for "username" property
	 * 
	 * @return username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Setter method for "username" property
	 * 
	 * @param aUsername
	 */
	public void setUsername(String aUsername) {
		this.username = aUsername;
	}

	/**
	 * Getter method for "password" property
	 * 
	 * @return password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Setter method for "password" property
	 * 
	 * @param aPassword
	 */
	public void setPassword(String aPassword) {
		this.password = aPassword;
	}
}