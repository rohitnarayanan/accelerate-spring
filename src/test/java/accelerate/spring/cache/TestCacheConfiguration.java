package accelerate.spring.cache;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import accelerate.commons.data.DataMap;
import accelerate.commons.exception.ApplicationException;

/**
 * {@link TestConfiguration} class for accelerate.spring.cache package
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 11, 2019
 */
@TestConfiguration
public class TestCacheConfiguration {
	/**
	 * @return
	 */
	@Bean
	public static PropertyCache basicPropertyCache() {
		return new PropertyCache("basicPropertyCache",
				"classpath:/accelerate/spring/cache/PropertyCacheTest.properties");
	}

	/**
	 * @return
	 */
	@Bean
	public static PropertyCache profilePropertyCache() {
		return new PropertyCache("profilePropertyCache",
				"classpath:/accelerate/spring/cache/PropertyCacheTest.properties", "dev");
	}

	/**
	 * @return
	 */
	@Bean
	public static DataMapCache<TestDataBean> basicDataMapCache() {
		DataMapCache<TestDataBean> beanCache = new DataMapCache<>("basicDataMapCache", TestDataBean.class) {
			private static final long serialVersionUID = 1L;

			protected void loadCache(DataMap aCacheMap) throws ApplicationException {
				aCacheMap.put("cacheKey1", new TestDataBean(1));
				aCacheMap.put("cacheKey2", new TestDataBean(2));
				aCacheMap.put("cacheKey3", new TestDataBean(3));
				aCacheMap.put("cacheKey4", new TestDataBean(4));
			}
		};

		return beanCache;
	}
}