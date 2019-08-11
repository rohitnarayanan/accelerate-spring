package accelerate.spring.web;

import static accelerate.spring.security.SecurityConstants.WILDCARD_PATTERN_URL_SUFFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.ObjectUtils;

import accelerate.spring.BaseConfiguration;
import accelerate.spring.ProfileConstants;
import accelerate.spring.logging.Profiled;
import accelerate.spring.security.SecurityConfigurer;

/**
 * Base web configuration class for the application
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 23, 2018
 */
@Profile(ProfileConstants.PROFILE_WEB)
@Configuration
@Profiled
public class WebConfiguration extends SpringBootServletInitializer implements SecurityConfigurer {
	/**
	 * {@link WebConfigProps} instance
	 */
	@Autowired
	protected WebConfigProps webConfigProps = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.boot.context.web.SpringBootServletInitializer#
	 * configure(org.springframework.boot.builder.SpringApplicationBuilder)
	 */
	/**
	 * @param application
	 * @return
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BaseConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.spring.security.SecurityConfigurer#configure(org.
	 * springframework.security.config.annotation.web.builders.WebSecurity)
	 */
	/**
	 * @param aWebSecurity
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity aWebSecurity) throws Exception {
		if (this.webConfigProps.isDefaultViewIndex()) {
			aWebSecurity.ignoring().antMatchers("/", "/index");
		}
	}

	/**
	 * PUT DESCRIPTION HERE
	 * 
	 * @version 1.0 Initial Version
	 * @author Rohit Narayanan
	 * @since July 22, 2019
	 */
	@Configuration
	@Order(Ordered.LOWEST_PRECEDENCE)
	class WebAPISecurityConfigurer extends WebSecurityConfigurerAdapter {
		/**
		 * {@link WebConfigProps} instance
		 */
		@Autowired
		protected WebConfigProps webConfigPropsLocal = null;

		/**
		 * 
		 */
		public WebAPISecurityConfigurer() {
			super(true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.config.annotation.web.configuration.
		 * WebSecurityConfigurerAdapter#configure(org.springframework.security.config.
		 * annotation.web.builders.HttpSecurity)
		 */
		/**
		 * @param aHttpSecurity
		 * @throws Exception
		 */
		@Override
		public void configure(HttpSecurity aHttpSecurity) throws Exception {
			String webAPIPrefix = this.webConfigPropsLocal.getWebAPIPathPrefix() + WILDCARD_PATTERN_URL_SUFFIX;
			aHttpSecurity.requestMatchers().antMatchers(webAPIPrefix).and().csrf().disable();

			if (ObjectUtils.isEmpty(this.webConfigPropsLocal.getWebAPIRoles())) {
				LOGGER.debug("Allowed roles for {}: ALL", webAPIPrefix);
				aHttpSecurity.authorizeRequests().antMatchers(webAPIPrefix).permitAll();
			} else {
				LOGGER.debug("Allowed roles for {}: {}", webAPIPrefix,
						ObjectUtils.nullSafeToString(this.webConfigPropsLocal.getWebAPIRoles()));
				aHttpSecurity.authorizeRequests().antMatchers(webAPIPrefix)
						.hasAnyRole(this.webConfigPropsLocal.getWebAPIRoles());
			}
		}
	}

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfiguration.class);
}
