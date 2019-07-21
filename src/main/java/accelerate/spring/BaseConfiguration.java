package accelerate.spring;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Base configuration class for accelerate
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 20, 2018
 */
@ComponentScan(basePackages = "accelerate")
@EnableAspectJAutoProxy
@EnableConfigurationProperties
@Configuration
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

	/**
	 * Flag to indicate whether debug logs are enabled
	 */
	@Value("#{'${accelerate.spring.jackson.defaults:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
	private boolean jacksonDefaultsEnabled;

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
		if (activeProfiles.contains(ProfileConstants.PROFILE_SECURITY)) {
			if (!activeProfiles.contains(ProfileConstants.PROFILE_WEB)) {
				String errorMessage = "You have misconfigured your application! Web profile '%s' is required to use security profile '%s'. "
						+ "Update the spring.profiles.active property in your configuration file and retry";
				errorMessage = String.format(errorMessage, ProfileConstants.PROFILE_WEB,
						ProfileConstants.PROFILE_SECURITY);
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
	 * @return
	 */
	@Order(1)
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
		return (aJackson2ObjectMapperBuilder) -> {
			if (this.jacksonDefaultsEnabled) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
				mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
				aJackson2ObjectMapperBuilder.serializationInclusion(Include.NON_NULL);
				aJackson2ObjectMapperBuilder.filters(
						new SimpleFilterProvider().setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept()));

				aJackson2ObjectMapperBuilder.configure(mapper);
			}
		};
	}

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfiguration.class);
}