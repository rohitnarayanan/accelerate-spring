package accelerate.spring.web.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import accelerate.commons.util.StringUtils;
import accelerate.spring.ProfileConstants;
import accelerate.spring.logging.Profiled;

/**
 * {@link RestController} providing API for admin operations
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 16, 2018
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
@ConditionalOnExpression("${accelerate.spring.web.api.admin:${accelerate.spring.defaults:true}}")
@RestController
@RequestMapping(path = "${accelerate.spring.web.api:/webapi}/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@Profiled
public class AdminController {
	/**
	 *
	 */
	@Autowired
	private ApplicationContext applicationContext = null;

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/listSessions")
	public List<Object> listSessions() {
		return this.applicationContext.getBean(SessionRegistry.class).getAllPrincipals();
	}

	/**
	 * @param aAuthId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/logout/{authId}")
	public Map<String, Boolean> logoutUser(@PathVariable("authId") String aAuthId) {
		final SessionRegistry sessionRegistry = this.applicationContext.getBean(SessionRegistry.class);
		return sessionRegistry.getAllPrincipals().stream()
				.filter(principal -> StringUtils.equals((String) ReflectionUtils
						.getField(ReflectionUtils.findField(principal.getClass(), "username"), principal), aAuthId))
				.flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
				.collect(Collectors.toMap(session -> session.getSessionId(), session -> {
					session.expireNow();
					return session.isExpired();
				}));
	}
}
