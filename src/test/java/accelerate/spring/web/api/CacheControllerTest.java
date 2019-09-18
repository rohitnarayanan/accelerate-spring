package accelerate.spring.web.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import accelerate.spring.cache.PropertyCache;
import accelerate.spring.cache.TestCacheConfiguration;
import accelerate.spring.web.WebConfigProps;

/**
 * {@link Test} class for {@link CacheController}
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 21, 2019
 */
@SpringBootTest
@AutoConfigureMockMvc
class CacheControllerTest {
	/**
	 * {@link WebConfigProps} instance
	 */
	@Autowired
	private WebConfigProps webConfigProps = null;

	/**
	 * {@link MockMvc} instance to test API
	 */
	@Autowired
	private MockMvc mockMvc = null;

	/**
	 * {@link TestCacheConfiguration#basicPropertyCache()} instance
	 */
	@Autowired
	private PropertyCache basicPropertyCache = null;

	/**
	 * Test method for {@link CacheController#getCacheList()}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetCacheList() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get(this.webConfigProps.getWebAPIPathPrefix() + "/cache/list")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$", new Object[] {}).isArray()).andExpect(
						jsonPath("$..[?(@.name=='profilePropertyCache')].profileName", new Object[] {}).value("dev"));
	}

	/**
	 * Test method for {@link CacheController#getCacheKeys(String)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testGetCacheKeys() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders
						.get(this.webConfigProps.getWebAPIPathPrefix() + "/cache/keys/basicPropertyCache")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.length()", new Object[] {}).value(7));
	}

	/**
	 * Test method for {@link CacheController#refreshCache(String)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testRefreshCache() throws Exception {
		String refreshedTime = new SimpleDateFormat("MM/dd/yyyy HH:ss:SSS z")
				.format(this.basicPropertyCache.getRefreshedAt());

		this.mockMvc
				.perform(MockMvcRequestBuilders
						.put(this.webConfigProps.getWebAPIPathPrefix() + "/cache/refresh/basicPropertyCache")
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.refreshedAt", new Object[] {}).value(refreshedTime));
	}

	/**
	 * Test method for {@link CacheController#getCacheValue(String, String)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testGet() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders
						.get(this.webConfigProps.getWebAPIPathPrefix() + "/cache/get/basicPropertyCache/key3.innerKey")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.key3.innerKey", new Object[] {}).value("value3"));

		this.mockMvc
				.perform(MockMvcRequestBuilders
						.get(this.webConfigProps.getWebAPIPathPrefix() + "/cache/get/basicDataMapCache/cacheKey4")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.beanId", new Object[] {}).value(4));
	}
}
