package accelerate.spring.config;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

/**
 * Main class for the application
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 23, 2018
 */
@ComponentScan(basePackages = { "com.walgreens.springboot" })
@EnableAspectJAutoProxy
@EnableConfigurationProperties
public class BaseConfiguration extends SpringBootServletInitializer
		implements ApplicationListener<ApplicationReadyEvent> {
	/**
	 * static {@link ApplicationContext} instance, to provide spring beans access to
	 * all classes.
	 */
	private static ApplicationContext applicationContext = null;

	/**
	 * {@link Environment} instance
	 */
	@Autowired
	private Environment environment = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.
	 * springframework.context.ApplicationEvent)
	 */
	/**
	 * @param aEvent
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent aEvent) {
		applicationContext = aEvent.getApplicationContext();
	}

	/**
	 * @return
	 */
	public static boolean isApplicationReady() {
		return (applicationContext != null);
	}

	/**
	 * @param <T>
	 * @param aBeanName
	 * @param aBeanClass
	 * @return
	 */
	public static <T> T getBean(String aBeanName, Class<T> aBeanClass) {
		if (!isApplicationReady()) {
			throw new IllegalStateException("Spring application is not ready yet");
		}

		return applicationContext.getBean(aBeanName, aBeanClass);
	}

	/**
	 * Validates the application configuration
	 */
	@PostConstruct
	public void validateConfiguration() {
		Collection<String> activeProfiles = Arrays.asList(this.environment.getActiveProfiles());
		LOGGER.info("activeProfiles: {}", activeProfiles);

		/*
		 * check if the base profile has been activated
		 */
		if (activeProfiles.contains(ConfigConstants.PROFILE_SECURITY)) {
			if (!activeProfiles.contains(ConfigConstants.PROFILE_WEB)) {
				String errorMessage = "You have misconfigured your application! Web profile '%s' is required to use security profile '%s'. "
						+ "Update the spring.profiles.active property in your configuration file and retry";
				errorMessage = String.format(errorMessage, ConfigConstants.PROFILE_WEB,
						ConfigConstants.PROFILE_SECURITY);
				LOGGER.error(errorMessage);
				throw new ApplicationContextException(errorMessage);
			}
		}

//		if (activeProfiles.contains(ConfigConstants.PROFILE_EXTN_SECURITY_AUTHENTICATOR)) {
//			if (!activeProfiles.contains(ConfigConstants.PROFILE_SECURITY)) {
//				String errorMessage = "You have misconfigured your application! Base security profile '%s' is required to use authenticator profile '%s'. "
//						+ "Update the spring.profiles.active property in your configuration file and retry";
//				errorMessage = String.format(errorMessage, ConfigConstants.PROFILE_SECURITY,
//						ConfigConstants.PROFILE_EXTN_SECURITY_AUTHENTICATOR);
//
//				LOGGER.error(errorMessage);
//				throw new ApplicationContextException(errorMessage);
//			}
//		}
	}

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfiguration.class);
}