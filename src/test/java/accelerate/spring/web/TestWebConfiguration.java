package accelerate.spring.web;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import accelerate.spring.security.SecurityConfiguration;
import accelerate.spring.security.SecurityConfigurer;

/**
 * {@link TestConfiguration} class for web layer
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 23, 2018
 */
@TestConfiguration
public class TestWebConfiguration implements WebMvcConfigurer, SecurityConfigurer {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#
	 * addViewControllers(org.springframework.web.servlet.config.annotation.
	 * ViewControllerRegistry)
	 */
	/**
	 * @param aRegistry
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry aRegistry) {
		aRegistry.addViewController("/home").setViewName("thymeleaf/home");
		aRegistry.addViewController("/noauth").setViewName("thymeleaf/noauth");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.spring.security.SecurityConfigurer#configure(HttpSecurity)
	 */
	/**
	 * @param aHttpSecurity
	 * @throws Exception
	 */
	@Override
	public void configure(HttpSecurity aHttpSecurity) throws Exception {
		SecurityConfiguration.defaultSecurity(aHttpSecurity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.spring.security.SecurityConfigurer#configure(
	 * AuthenticationManagerBuilder)
	 */
	/**
	 * @param aAuthenticationManagerBuilder
	 * @throws Exception
	 */
	@Override
	public void configure(AuthenticationManagerBuilder aAuthenticationManagerBuilder) throws Exception {
		SecurityConfiguration.defaultAuthentication(aAuthenticationManagerBuilder);
	}
}
