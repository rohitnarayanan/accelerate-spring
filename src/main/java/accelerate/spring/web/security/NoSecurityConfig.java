package accelerate.spring.web.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import accelerate.spring.constants.ProfileConstants;
import accelerate.spring.logging.Log;

/**
 * Configuration for NO security
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@Profile(ProfileConstants.PROFILE_NO_SECURITY)
@Configuration
@Log
public class NoSecurityConfig extends WebSecurityConfigurerAdapter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.config.annotation.web.configuration.
	 * WebSecurityConfigurerAdapter#configure(org.springframework.security.config.
	 * annotation.web.builders.HttpSecurity)
	 */
	/**
	 * @param aHttp
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity aHttp) throws Exception {
		aHttp.anonymous();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.config.annotation.web.configuration.
	 * WebSecurityConfigurerAdapter#configure(org.springframework.security.config.
	 * annotation.web.builders.WebSecurity)
	 */
	/**
	 * @param aWeb
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity aWeb) throws Exception {
		aWeb.ignoring().antMatchers("/**");
	}
}
