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
	 * Default principal assigned to a new/unauthenticated user
	 */
	public static final String AUTH_DEFAULT_USER = "wag-user";

	/**
	 * WebRequest attribute storing the authenticator login status
	 */
	public static final String AUTH_LOGIN_STATUS = "authLoginStatus";

	/**
	 * Role prefix used by {@link GrantedAuthorityDefaults}
	 */
	public static final String ROLE_PREFIX = "ROLE_";
}
