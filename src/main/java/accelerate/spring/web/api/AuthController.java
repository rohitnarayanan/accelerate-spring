package accelerate.spring.web.api;

import static accelerate.commons.constant.CommonConstants.COMMA;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import accelerate.spring.ProfileConstants;
import accelerate.spring.logging.Profiled;
import accelerate.spring.web.WebUtils;

/**
 * {@link RestController} providing API for user operations
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 3, 2017
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
@ConditionalOnExpression("${accelerate.spring.web.api.auth:${accelerate.spring.defaults:true}}")
@RestController
@RequestMapping(path = "${accelerate.spring.web.api:/webapi}/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Profiled
public class AuthController {
	/**
	 * @param aRequest
	 * @param aResponse
	 * @throws ServletException
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/logout")
	public static void logout(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException {
		WebUtils.logout(aRequest, aResponse, StringUtils.split(aRequest.getParameter("_clearCookies"), COMMA));
	}

	/**
	 * @param aRequest
	 * @param aResponse
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "/cookies")
	public static void deleteCookies(HttpServletRequest aRequest, HttpServletResponse aResponse) {
		WebUtils.deleteCookies(aRequest, aResponse, StringUtils.split(aRequest.getParameter("_clearCookies"), COMMA));
	}
}
