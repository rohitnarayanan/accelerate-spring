package accelerate.spring.web.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import accelerate.commons.constant.CommonConstants;
import accelerate.commons.data.DataMap;
import accelerate.spring.ProfileConstants;
import accelerate.spring.cache.DataMapCache;
import accelerate.spring.logging.Profiled;

/**
 * {@link RestController} providing API for HTTP debugging
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 20, 2018
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
@ConditionalOnExpression("${accelerate.spring.web.api.cache:${accelerate.spring.defaults:true}}")
@RestController
@RequestMapping(path = "${accelerate.spring.web.api:/webapi}/cache", produces = MediaType.APPLICATION_JSON_VALUE)
@Profiled
public class CacheController {
	/**
	 * {@link ApplicationContext} instance
	 */
	@Autowired
	private ApplicationContext context = null;

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/list")
	public @ResponseBody String listCaches() {
		String mapList = this.context.getBeansOfType(DataMapCache.class).values().stream()
				.map(aCache -> aCache.toString()).collect(Collectors.joining(CommonConstants.COMMA));

		if (mapList.length() == 0) {
			return "[]";
		}

		return new StringBuilder(CommonConstants.SQ_BRACKET_OPEN).append(mapList)
				.append(CommonConstants.SQ_BRACKET_CLOSE).toString();
	}

	/**
	 * @param aCacheName
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/{cacheName}/keys")
	public List<String> getCacheKeys(@PathVariable(name = "cacheName", required = true) String aCacheName) {
		return this.context.getBean(aCacheName, DataMapCache.class).getCacheKeys();
	}

	/**
	 * @param aCacheName
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/{cacheName}/refresh")
	public @ResponseBody String refreshCache(@PathVariable(name = "cacheName", required = true) String aCacheName) {
		this.context.getBean(aCacheName, DataMapCache.class).refresh();
		return this.context.getBean(aCacheName, DataMapCache.class).toString();
	}

	/**
	 * @param aCacheName
	 * @param aKey
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/{cacheName}")
	public Object get(@PathVariable(name = "cacheName", required = true) String aCacheName,
			@RequestParam(name = "key", required = true) String aKey) {
		Object value = this.context.getBean(aCacheName, DataMapCache.class).get(aKey);
		return jsonValue(aKey, value);
	}

	/**
	 * @param aCacheName
	 * @param aKey
	 * @param aValue
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT }, path = "/{cacheName}")
	public Object put(@PathVariable(name = "cacheName", required = true) String aCacheName,
			@RequestParam(name = "key", required = true) String aKey, @RequestBody(required = true) String aValue) {
		DataMapCache<?> cache = this.context.getBean(aCacheName, DataMapCache.class);
		cache.putJSON(aKey, aValue);
		return jsonValue(aKey, cache.get(aKey));
	}

	/**
	 * @param aCacheName
	 * @param aKey
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.DELETE }, path = "/{cacheName}")
	public Object delete(@PathVariable(name = "cacheName", required = true) String aCacheName,
			@RequestParam(name = "key", required = true) String aKey) {
		DataMapCache<?> cache = this.context.getBean(aCacheName, DataMapCache.class);
		Object value = cache.remove(aKey);
		return jsonValue(aKey, value);
	}

	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 */
	private static Object jsonValue(String aKey, Object aValue) {
		return (aValue instanceof String) ? DataMap.newMap(aKey, aValue) : aValue;
	}
}
