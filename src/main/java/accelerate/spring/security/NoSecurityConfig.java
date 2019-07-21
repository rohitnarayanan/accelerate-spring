package accelerate.spring.security;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * {@link WebSecurityConfigurerAdapter} extension for configuring NO security
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 16, 2018
 */
// @Profile("!" + ConfigConstants.PROFILE_SECURITY)
// @Configuration
public class NoSecurityConfig {// extends WebSecurityConfigurerAdapter {
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
//	@Override
	public static void configure(WebSecurity aWebSecurity) throws Exception {
		aWebSecurity.ignoring().antMatchers("/**");
	}
}
