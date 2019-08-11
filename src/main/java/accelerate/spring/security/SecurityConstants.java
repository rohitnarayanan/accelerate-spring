package accelerate.spring.security;

import org.springframework.security.config.core.GrantedAuthorityDefaults;

/**
 * Class containing security configuration constants
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 24, 2018
 */
public class SecurityConstants {
	/**
	 * Role prefix used by {@link GrantedAuthorityDefaults}
	 */
	public static final String ROLE_PREFIX = "ROLE_";

	/**
	 * Wilcard pattern to match all relative URLs
	 */
	public static final String WILDCARD_PATTERN_URL_SUFFIX = "/**";
}
