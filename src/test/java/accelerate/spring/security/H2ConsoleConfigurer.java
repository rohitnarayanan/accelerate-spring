package accelerate.spring.security;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * {@link SecurityConfigurer} implementation to provide h2-access
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 20, 2019
 */
@TestComponent
public class H2ConsoleConfigurer implements SecurityConfigurer {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * accelerate.spring.security.SecurityConfigurer#configure(org.springframework.
	 * security.config.annotation.web.builders.HttpSecurity)
	 */
	/**
	 * @param aHttpSecurity
	 * @throws Exception
	 */
//	@Override
//	public void configure(HttpSecurity aHttpSecurity) throws Exception {
//		aHttpSecurity.requestMatchers().antMatchers("/h2-console/**").and().csrf().disable();
//		aHttpSecurity.authorizeRequests().antMatchers().permitAll().and().headers().frameOptions()
//				.sameOrigin();
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * accelerate.spring.security.SecurityConfigurer#configure(org.springframework.
	 * security.config.annotation.web.builders.WebSecurity)
	 */
	/**
	 * @param aWebSecurity
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity aWebSecurity) throws Exception {
		aWebSecurity.ignoring().antMatchers("/h2-console/**");
	}
}
