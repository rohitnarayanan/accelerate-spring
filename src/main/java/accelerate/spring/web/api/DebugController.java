package accelerate.spring.web.api;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import accelerate.commons.data.DataMap;
import accelerate.commons.utils.WebUtils;

/**
 * {@link RestController} providing API for HTTP debugging
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 20, 2018
 */
@ConditionalOnWebApplication
@ConditionalOnExpression("#{'${accelerate.spring.web.api.debug:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
@RestController
@RequestMapping(path = "${accelerate.spring.web.api.base-path:/webapi}/debug", produces = MediaType.APPLICATION_JSON_VALUE)
public class DebugController {
	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request")
	public static DataMap<Object> debugRequest(HttpServletRequest aRequest) {
		return WebUtils.debugRequest(aRequest);
	}

	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/deep")
	public static DataMap<Object> debugRequestDeep(HttpServletRequest aRequest) {
		return WebUtils.debugRequestDeep(aRequest);
	}

	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/session")
	public static DataMap<Object> debugSession(HttpServletRequest aRequest) {
		DataMap<Object> dataMap = DataMap.newMap();

		HttpSession session = aRequest.getSession(false);
		if (session == null) {
			return dataMap;
		}

		dataMap.put("id", session.getId());

		Map<Object, Object> attributeMap = Collections.list(session.getAttributeNames()).stream()
				.map(name -> new Object[] { name, session.getAttribute(name) })
				.collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
		dataMap.put("attributes", attributeMap);

		return dataMap;
	}
}
