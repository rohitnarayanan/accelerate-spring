package accelerate.spring.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import accelerate.commons.constants.CommonConstants;
import accelerate.spring.cache.DataMapCache;

/**
 * {@link RestController} providing API for HTTP debugging
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 20, 2018
 */
@ConditionalOnWebApplication
@ConditionalOnExpression("#{'${accelerate.spring.web.api.cache:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
@RestController
@RequestMapping(path = "${accelerate.spring.web.api.base-path:/webapi}/cache", produces = MediaType.APPLICATION_JSON_VALUE)
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
		StringBuilder buffer = new StringBuilder(CommonConstants.SQ_BRACKET_CHAR_OPEN);

		this.context.getBeansOfType(DataMapCache.class).values()
				.forEach(aCache -> buffer.append(aCache.toJSON()).append(CommonConstants.COMMA_CHAR));

		return buffer.replace(buffer.length() - 1, buffer.length(), CommonConstants.SQ_BRACKET_CHAR_CLOSE).toString();
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
	 * @param aKey
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/{cacheName}/get")
	public Object getCachedValue(@PathVariable(name = "cacheName", required = true) String aCacheName,
			@RequestParam(name = "key", required = true) String aKey) {
		return this.context.getBean(aCacheName, DataMapCache.class).get(aKey);
	}

	/**
	 * @param aCacheName
	 * @param aKey
	 * @param aJSONValue
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT }, path = "/{cacheName}/put")
	public Object addToCache(@PathVariable(name = "cacheName", required = true) String aCacheName,
			@RequestParam(name = "key", required = true) String aKey, @RequestBody(required = true) String aJSONValue) {
		DataMapCache<?> cache = this.context.getBean(aCacheName, DataMapCache.class);
		cache.putJSON(aKey, aJSONValue);
		return cache.get(aKey);
	}

	/**
	 * @param aCacheName
	 */
	@RequestMapping(method = RequestMethod.PUT, path = "/{cacheName}/refresh")
	public void refreshCache(@PathVariable(name = "cacheName", required = true) String aCacheName) {
		this.context.getBean(aCacheName, DataMapCache.class).refresh();
	}
}
