package accelerate.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import accelerate.spring.ProfileConstants;

/**
 * Bean class to hold logging configuration properties
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 31, 2018
 */
@Profile(ProfileConstants.PROFILE_LOGGING)
@Component
public class LoggerConfigProps {
	/**
	 * Flag to indicate whether debug logs are enabled
	 */
	@Value("#{'${accelerate.spring.logging.debug:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
	private boolean debugEnabled = true;

	/**
	 * Flag to indicate whether audit logs are enabled
	 */
	@Value("#{'${accelerate.spring.logging.audit:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
	private boolean auditEnabled = true;

	/**
	 * Flag to indicate whether metric logs are enabled
	 */
	@Value("#{'${accelerate.spring.logging.metrics:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
	private boolean metricsEnabled = true;

	/**
	 * Getter method for "debugEnabled" property
	 * 
	 * @return debugEnabled
	 */
	public boolean isDebugEnabled() {
		return this.debugEnabled;
	}

	/**
	 * Setter method for "debugEnabled" property
	 * 
	 * @param aDebugEnabled
	 */
	public void setDebugEnabled(boolean aDebugEnabled) {
		this.debugEnabled = aDebugEnabled;
	}

	/**
	 * Getter method for "auditEnabled" property
	 * 
	 * @return auditEnabled
	 */
	public boolean isAuditEnabled() {
		return this.auditEnabled;
	}

	/**
	 * Setter method for "auditEnabled" property
	 * 
	 * @param aAuditEnabled
	 */
	public void setAuditEnabled(boolean aAuditEnabled) {
		this.auditEnabled = aAuditEnabled;
	}

	/**
	 * Getter method for "metricsEnabled" property
	 * 
	 * @return metricsEnabled
	 */
	public boolean isMetricsEnabled() {
		return this.metricsEnabled;
	}

	/**
	 * Setter method for "metricsEnabled" property
	 * 
	 * @param aMetricsEnabled
	 */
	public void setMetricsEnabled(boolean aMetricsEnabled) {
		this.metricsEnabled = aMetricsEnabled;
	}
}