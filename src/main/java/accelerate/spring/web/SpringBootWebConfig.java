package accelerate.spring.web;

import static com.walgreens.springboot.lang.CommonConstants.MATCH_ALL_URL_SUFFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.ObjectUtils;

import com.walgreens.springboot.config.BaseConfiguration;
import com.walgreens.springboot.config.ConfigConstants;
import com.walgreens.springboot.logging.Profiled;
import com.walgreens.springboot.security.SecurityConfigurer;

/**
 * Base web configuration class for the application
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 23, 2018
 */
@Profile(ConfigConstants.PROFILE_WEB)
@Configuration
@Profiled
public class SpringBootWebConfig extends SpringBootServletInitializer implements SecurityConfigurer {
	/**
	 * {@link WebConfigProps} instance
	 */
	@Autowired
	private WebConfigProps webConfigProps = null;

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
	 * @see com.walgreens.springboot.security.SecurityConfigurer#configure(org.
	 * springframework.security.config.annotation.web.builders.HttpSecurity)
	 */
	/**
	 * @param aHttpSecurity
	 * @throws Exception
	 */
	@Override
	public void configure(HttpSecurity aHttpSecurity) throws Exception {
		String webAPIPrefix = this.webConfigProps.getWebAPIPathPrefix() + MATCH_ALL_URL_SUFFIX;
		aHttpSecurity.requestMatcher(new AntPathRequestMatcher(webAPIPrefix)).csrf().disable();

		if (ObjectUtils.isEmpty(this.webConfigProps.getWebAPIRoles())) {
			LOGGER.debug("Allowed roles for {}: ALL", webAPIPrefix);
			aHttpSecurity.authorizeRequests().antMatchers(webAPIPrefix).permitAll();
		} else {
			LOGGER.debug("Allowed roles for {}: {}", webAPIPrefix,
					ObjectUtils.nullSafeToString(this.webConfigProps.getWebAPIRoles()));
			aHttpSecurity.authorizeRequests().antMatchers(webAPIPrefix)
					.hasAnyRole(this.webConfigProps.getWebAPIRoles());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.walgreens.springboot.security.SecurityConfigurer#configure(org.
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
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootWebConfig.class);
}
