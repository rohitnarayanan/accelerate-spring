package accelerate.spring.security;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UrlPathHelper;

import accelerate.commons.utils.JSONUtils;
import accelerate.spring.ProfileConstants;
import accelerate.spring.logging.Log;
import accelerate.spring.web.config.SecurityConfigProps;

/**
 * {@link WebSecurityConfigurerAdapter} extension for base security
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@Profile(ProfileConstants.PROFILE_SECURITY)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
@Log
@Order(101)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	/**
	 * {@link Logger} instance
	 */
	private static final Logger _logger = LoggerFactory.getLogger(WebSecurityConfig.class);

	/**
	 * {@link SessionRegistry} instance
	 */
	public static final SessionRegistry sessionRegistry = new SessionRegistryImpl();

	/**
	 * {@link RedirectStrategy} instance
	 */
	public static final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/**
	 * {@link UrlPathHelper} instance
	 */
	public static final UrlPathHelper urlPathHelper = new UrlPathHelper();

	/**
	 * {@link SecurityConfigProps} instance
	 */
	@Autowired
	private SecurityConfigProps securityConfigProps = null;

	/**
	 * {@link SecurityConfigurer} instance
	 */
	@Autowired(required = false)
	private SecurityConfigurer[] securityConfigurers = null;

	/**
	 * error path of the application
	 */
	@Value("${server.error.path:${error.path:/error}}")
	private String errorPath = null;

	/**
	 * actuatorPath path of the application
	 */
	@Value("${management.endpoints.web.base-path:/actuator}")
	private String actuatorPath = null;

	/**
	 * @return
	 */
	@Bean
	public static SessionRegistry sessionRegistry() {
		return sessionRegistry;
	}

	/**
	 * Method to initialize {@link #securityConfigurers}
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	public void intialize() throws Exception {
		_logger.debug("Config Props: [{}]", JSONUtils.serialize(this.securityConfigProps));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.config.annotation.web.configuration.
	 * WebSecurityConfigurerAdapter#configure(org.springframework.security.config.
	 * annotation.authentication.builders.AuthenticationManagerBuilder)
	 */
	/**
	 * @param aAuthenticationManagerBuilder
	 * @throws Exception
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder aAuthenticationManagerBuilder) throws Exception {
		// allow sub modules to configure
		if (!ObjectUtils.isEmpty(this.securityConfigurers)) {
			for (SecurityConfigurer configurer : this.securityConfigurers) {
				configurer.configure(aAuthenticationManagerBuilder);
			}
		}
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
		if (this.securityConfigProps.isEnabled()) {
			aHttpSecurity
					// authorize all URLs
					// .antMatcher("/**")
					.authorizeRequests()
					// allow login/logout
					.antMatchers(this.securityConfigProps.getLoginPath() + "/**",
							this.securityConfigProps.getLogoutPath() + "/**")
					.permitAll()
					// allow permitted URLs configured in properties file
					.antMatchers(this.securityConfigProps.getAllowURLPatterns()).permitAll()
					.antMatchers(this.actuatorPath + "/**").hasAnyRole(this.securityConfigProps.getActuatorRoles())
					// authenticate all other URLs
					.anyRequest().authenticated();

			// logout settings
			aHttpSecurity.logout().clearAuthentication(true)
					.deleteCookies(this.securityConfigProps.getLogoutClearCookies()).invalidateHttpSession(true)
					.logoutSuccessUrl(this.securityConfigProps.getPostLogoutURL());

			// custom handling for authentication and authorization failures
			aHttpSecurity.exceptionHandling().authenticationEntryPoint((aRequest, aResponse, aAuthException) -> {
				redirectStrategy.sendRedirect(aRequest, aResponse,
						SecurityUtils.getAuthErrorURL(this.securityConfigProps.getLoginPath(), aAuthException));
			});

			// configure session management properties
			aHttpSecurity.sessionManagement().maximumSessions(this.securityConfigProps.getMaxUserSessions())
					.sessionRegistry(sessionRegistry()).expiredUrl(this.securityConfigProps.getSessionExpiredURL());
		}

		// allow sub modules to configure
		if (!ObjectUtils.isEmpty(this.securityConfigurers)) {
			for (SecurityConfigurer configurer : this.securityConfigurers) {
				configurer.configure(aHttpSecurity);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.config.annotation.web.configuration.
	 * WebSecurityConfigurerAdapter#configure(org.springframework.security.config.
	 * annotation.web.builders.WebSecurity)
	 */
	/**
	 * @param aWebSecurity
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity aWebSecurity) throws Exception {
		if (this.securityConfigProps.isEnabled()) {
			aWebSecurity.ignoring().antMatchers(this.securityConfigProps.getIgnoreURLPatterns())
					.antMatchers(this.errorPath);
		}

		// allow sub modules to configure
		if (!ObjectUtils.isEmpty(this.securityConfigurers)) {
			for (SecurityConfigurer configurer : this.securityConfigurers) {
				configurer.configure(aWebSecurity);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.config.annotation.web.configuration.
	 * WebSecurityConfigurerAdapter#authenticationManagerBean()
	 */
	/**
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.config.annotation.web.configuration.
	 * WebSecurityConfigurerAdapter#userDetailsServiceBean()
	 */
	/**
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return super.userDetailsServiceBean();
	}
}