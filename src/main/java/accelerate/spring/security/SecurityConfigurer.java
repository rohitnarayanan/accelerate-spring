package accelerate.spring.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * Definition for modules to provide implementations to configure security
 *
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 01, 2018
 */
public interface SecurityConfigurer {
	/**
	 * Override this method to get a handle to the {@link SpringBootSecurityConfig}
	 * instance
	 * 
	 * @param aBaseSecurityConfig
	 * @throws Exception
	 */
	default void initialize(@SuppressWarnings("unused") SpringBootSecurityConfig aBaseSecurityConfig) throws Exception {
		// default implementation
	}

	/**
	 * Override this method to configure the {@link HttpSecurity}.
	 * 
	 * @param aHttpSecurity
	 * @throws Exception
	 */
	default void configure(@SuppressWarnings("unused") HttpSecurity aHttpSecurity) throws Exception {
		// default implementation
	}

	/**
	 * Override this method to configure the {@link AuthenticationManagerBuilder}.
	 * 
	 * @param aAuthenticationManagerBuilder
	 * @throws Exception
	 */
	default void configure(@SuppressWarnings("unused") AuthenticationManagerBuilder aAuthenticationManagerBuilder)
			throws Exception {
		// default implementation
	}

	/**
	 * Override this method to configure the {@link WebSecurity}.
	 * 
	 * @param aWebSecurity
	 * @throws Exception
	 */
	default void configure(@SuppressWarnings("unused") WebSecurity aWebSecurity) throws Exception {
		// default implementation
	}
}
