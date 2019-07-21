package accelerate.spring.web.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import accelerate.spring.security.SecurityConfigurer;

/**
 * PUT DESCRIPTION HERE
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 30, 2018
 */
@Component
public class AccelerateSecurityConfigurer implements SecurityConfigurer {
	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.spring.security.SecurityConfigurer#configure(org.
	 * springframework. security.config.annotation.web.builders.HttpSecurity)
	 */
	/**
	 * @param aHttpSecurity
	 * @throws Exception
	 */
	@Override
	public void configure(HttpSecurity aHttpSecurity) throws Exception {
		// login configuration
		aHttpSecurity.csrf().disable().formLogin();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.spring.security.SecurityConfigurer#configure(org.
	 * springframework. security.config.annotation.authentication.builders.
	 * AuthenticationManagerBuilder)
	 */
	/**
	 * @param aAuthenticationManagerBuilder
	 * @throws Exception
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void configure(AuthenticationManagerBuilder aAuthenticationManagerBuilder) throws Exception {
		aAuthenticationManagerBuilder.eraseCredentials(true).inMemoryAuthentication().withUser("admin")
				.password("admin").roles("ADMIN").and().withUser("user").password("user").roles("USER").and()
				.passwordEncoder(org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
	}
}
