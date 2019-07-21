package accelerate.spring.security;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;

/**
 * Utility class for security module
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
public class SecurityUtils {
	/**
	 * {@link Logger} instance
	 */
	public static Logger _logger = LoggerFactory.getLogger(SecurityUtils.class);

	/**
	 * @param aRequest
	 * @param aResponse
	 * @param aLogger
	 * @throws ServletException
	 */
	public static final void logout(HttpServletRequest aRequest,
			@SuppressWarnings("unused") HttpServletResponse aResponse, Logger aLogger) throws ServletException {
		HttpSession session = aRequest.getSession(false);
		if (session != null) {
			aLogger.warn("Invalidating session[{}] for user [{}]", session.getId(), aRequest.getRemoteUser());
			session.invalidate();
		}

		aRequest.logout();
	}

	/**
	 * @param aLoginPath
	 * @param aAuthenticationException
	 * @return
	 */
	public static final String getAuthErrorURL(String aLoginPath, AuthenticationException aAuthenticationException) {
		StringBuilder errorURL = new StringBuilder(aLoginPath);

		if (aAuthenticationException instanceof InsufficientAuthenticationException) {
			return errorURL.toString();
		}

		if (aAuthenticationException instanceof BadCredentialsException) {
			errorURL.append("?errorType=incorrectLogin");
		} else if (aAuthenticationException instanceof DisabledException) {
			errorURL.append("?errorType=userDisabled");
		} else if (aAuthenticationException instanceof AccountExpiredException) {
			errorURL.append("?errorType=userAccountExpired");
		} else if (aAuthenticationException instanceof CredentialsExpiredException) {
			errorURL.append("?errorType=userCredentialsExpired");
		} else if (aAuthenticationException instanceof LockedException) {
			errorURL.append("?errorType=userAccountLocked");
		} else {
			errorURL.append("?errorType=other");
		}

		return errorURL.toString();
	}
}