package accelerate.spring.security;

import static com.walgreens.springboot.lang.CommonConstants.MATCH_ALL_URL_SUFFIX;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.ObjectUtils;

import com.walgreens.springboot.config.ConfigConstants;
import com.walgreens.springboot.logging.Profiled;
import com.walgreens.springboot.web.api.AuthController;

/**
 * {@link WebSecurityConfigurerAdapter} extension for base security
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 23, 2018
 */
@Profile(ConfigConstants.PROFILE_SECURITY)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Profiled
public class SpringBootSecurityConfig extends WebSecurityConfigurerAdapter {
	/**
	 * {@link SecurityConfigProps} instance
	 */
	@Autowired
	private SecurityConfigProps securityConfigProps = null;

	/**
	 * {@link SecurityConfigurer} instance
	 */
	@Autowired
	private SecurityConfigurer[] securityConfigurers = null;

	/**
	 * path for actuator endpoints
	 */
	@Value("${management.endpoints.web.base-path:/actuator}")
	private String actuatorPath = null;

	/**
	 * error path of the application
	 */
	@Value("${server.error.path:${error.path:/error}}")
	private String errorPath = null;

	/**
	 * {@link SessionRegistry} instance TODO: Expose for {@link AuthController}
	 */
	private static final SessionRegistry sessionRegistry = new SessionRegistryImpl();

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
		if (!ObjectUtils.isEmpty(this.securityConfigurers)) {
			for (SecurityConfigurer configurer : this.securityConfigurers) {
				configurer.initialize(this);
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
		/*
		 * logout settings
		 */
		aHttpSecurity.logout().logoutSuccessUrl(this.securityConfigProps.getLogoutSuccessURL()).permitAll();
		if (!ObjectUtils.isEmpty(this.securityConfigProps.getLogoutClearCookies())) {
			aHttpSecurity.logout().deleteCookies(this.securityConfigProps.getLogoutClearCookies());
		}

		/*
		 * Session management settings
		 */
		aHttpSecurity.sessionManagement().maximumSessions(this.securityConfigProps.getSessionMaxCount())
				.expiredUrl(this.securityConfigProps.getSessionExpiredURL());

		/*
		 * Setup allowed URL patterns, if configured
		 */
		if (!ObjectUtils.isEmpty(this.securityConfigProps.getUrlPatternsAllowed())) {
			aHttpSecurity.authorizeRequests().antMatchers(this.securityConfigProps.getUrlPatternsAllowed()).permitAll();
		}

		/*
		 * Setup actuator security, if configured
		 */
		if (!ObjectUtils.isEmpty(this.securityConfigProps.getActuatorRoles())) {
			aHttpSecurity.authorizeRequests().antMatchers(this.actuatorPath + MATCH_ALL_URL_SUFFIX)
					.hasAnyRole(this.securityConfigProps.getActuatorRoles());
		}

		/*
		 * allow sub modules to configure
		 */
		for (SecurityConfigurer configurer : this.securityConfigurers) {
			configurer.configure(aHttpSecurity);
		}

		/*
		 * allow access to error pages
		 */
		aHttpSecurity.authorizeRequests().antMatchers(this.errorPath + MATCH_ALL_URL_SUFFIX).permitAll();

		/*
		 * protect all other URLs
		 */
		aHttpSecurity.authorizeRequests().anyRequest().authenticated();
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
	public void configure(AuthenticationManagerBuilder aAuthenticationManagerBuilder) throws Exception {
		/*
		 * allow sub modules to configure
		 */
//		for (SecurityConfigurer configurer : this.securityConfigurers) {
//			configurer.configure(aAuthenticationManagerBuilder);
//		}

		defaultAuthentication(aAuthenticationManagerBuilder);
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
		/*
		 * Omit all ignore patterns configured from Spring security, and open up error
		 * page for all
		 */
		aWebSecurity.ignoring().antMatchers(this.securityConfigProps.getUrlPatternsIgnored())
				.antMatchers(HttpMethod.OPTIONS, "/**");
//.antMatchers(this.errorPath)

		/*
		 * allow sub modules to configure
		 */
		for (SecurityConfigurer configurer : this.securityConfigurers) {
			configurer.configure(aWebSecurity);
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
	@Bean(name = "authenticationManagerBean")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * Use this method for testing purpose to protect all URLs not configured yet,
	 * and enable form login
	 * 
	 * @param aHttpSecurity
	 * @throws Exception
	 */
	public static void defaultSecurity(HttpSecurity aHttpSecurity) throws Exception {
		aHttpSecurity.formLogin();
	}

	/**
	 * Use this method to enable in-memory authentication for 2 users
	 * 'user'/'admin'. They are assigned roles 'USER' and 'ADMIN' respectively. The
	 * password for 'user' is "user" and the password 'admin' is randomly generated
	 * and printed in the logs at INFO level.
	 * 
	 * @param aAuthenticationManagerBuilder
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static void defaultAuthentication(AuthenticationManagerBuilder aAuthenticationManagerBuilder)
			throws Exception {
		String adminPassword = RandomStringUtils.randomAlphanumeric(16);
		System.setProperty("PASSWORD_ADMIN", adminPassword);
		LOGGER.info("PASSWORD_ADMIN: {}", adminPassword);

		aAuthenticationManagerBuilder.inMemoryAuthentication()
				.passwordEncoder(org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance())
				.withUser(User.withUsername("user").password("user").roles("USER"))
				.withUser(User.withUsername("admin").password(adminPassword).roles("ADMIN"));
	}

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootSecurityConfig.class);
}