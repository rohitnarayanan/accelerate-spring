package accelerate.spring.web.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import accelerate.commons.data.DataMap;
import accelerate.spring.ProfileConstants;
import accelerate.spring.logging.Profiled;
import accelerate.spring.web.WebUtils;

/**
 * {@link RestController} providing API for HTTP debugging
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 3, 2017
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
@ConditionalOnExpression("${accelerate.spring.web.api.debug:${accelerate.spring.defaults:true}}")
@RestController
@RequestMapping(path = "${accelerate.spring.web.api:/webapi}/debug", produces = MediaType.APPLICATION_JSON_VALUE)
@Profiled
public class DebugController {
	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request")
	public static DataMap debugRequest(HttpServletRequest aRequest) {
		return WebUtils.debugRequest(aRequest);
	}

	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/session")
	public static DataMap debugSession(HttpServletRequest aRequest) {
		return WebUtils.debugSession(aRequest);
	}

	/**
	 * @param aRequest
	 * @param aAttributeName
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/attribute/{attributeName}")
	public static DataMap debugAttribute(HttpServletRequest aRequest,
			@PathVariable(name = "attributeName", required = true) String aAttributeName) {
		HttpSession session = aRequest.getSession(false);

		return DataMap.newMap("request", aRequest.getAttribute(aAttributeName), "session",
				(session != null) ? session.getAttribute(aAttributeName) : "NOSESSION");
	}
}
