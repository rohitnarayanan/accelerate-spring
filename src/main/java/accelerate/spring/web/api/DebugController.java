package accelerate.spring.web.api;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
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
	@RequestMapping(method = RequestMethod.GET, path = "/request/deep")
	public static DataMap debugRequestDeep(HttpServletRequest aRequest) {
		return WebUtils.debugRequestDeep(aRequest);
	}

	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/session")
	public static DataMap debugSession(HttpServletRequest aRequest) {
		HttpSession session = aRequest.getSession(false);
		if (session == null) {
			return null;
		}

		DataMap dataMap = DataMap.newMap().putAnd("id", session.getId());

		Map<Object, Object> attributeMap = Collections.list(session.getAttributeNames()).stream()
				.map(name -> new Object[] { name, session.getAttribute(name) })
				.collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
		dataMap.put("attributes", attributeMap);

		return dataMap;
	}
}
