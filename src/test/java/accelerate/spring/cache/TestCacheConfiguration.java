package accelerate.spring.cache;

import static accelerate.spring.CommonTestConstants.KEY;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
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
	 * @param aDataSource
	 * @return
	 */
	@Bean
	public static PropertyCache jdbcPropertyCache(@Autowired DataSource aDataSource) {
		PropertyCache jdbcPropertyCache = new PropertyCache("jdbcPropertyCache",
				"classpath:/accelerate/spring/cache/PropertyCacheTest.properties", "dev");
		jdbcPropertyCache.setCacheSource(aDataSource, "SELECT * FROM PropertyCache_Store WHERE environment = ?",
				"PROPERTY_KEY", "PROPERTY_VALUE", "dev");
		return jdbcPropertyCache;
	}

	/**
	 * @return
	 */
	@Bean
	public static DataMapCache<TestDataBean> basicDataMapCache() {
		DataMapCache<TestDataBean> basicDataMapCache = new DataMapCache<>("basicDataMapCache", TestDataBean.class) {
			private static final long serialVersionUID = 1L;

			protected void loadCache(DataMap aCacheMap) throws ApplicationException {
				aCacheMap.put("cacheKey1", new TestDataBean(1));
				aCacheMap.put("cacheKey2", new TestDataBean(2));
				aCacheMap.put("cacheKey3", new TestDataBean(3));
				aCacheMap.put("cacheKey4", new TestDataBean(4));
			}
		};
		basicDataMapCache.setExpiration("5 SECONDS");

		return basicDataMapCache;
	}

	/**
	 * @return
	 */
	@Bean
	public static DataMapCache<TestDataBean> apiDataMapCache() {
		DataMapCache<TestDataBean> apiDataMapCache = new DataMapCache<>("apiDataMapCache", TestDataBean.class);
		apiDataMapCache.setCacheSource(
				"https://raw.githubusercontent.com/rohitnarayanan/accelerate-spring/release_2/src/test/resources/accelerate/spring/cache/DataMapCacheTest.json",
				aTestDataBean -> aTestDataBean.get(KEY));
		return apiDataMapCache;
	}

	/**
	 * @param aDataSource
	 * @return
	 */
	@Bean
	public static DataMapCache<TestDataBean> jdbcDataMapCache(@Autowired DataSource aDataSource) {
		DataMapCache<TestDataBean> jdbcDataMapCache = new DataMapCache<>("jdbcDataMapCache", TestDataBean.class);
		jdbcDataMapCache.setCacheSource(aDataSource, "SELECT * FROM TestDataBean_Store",
				aDataMap -> !"cacheKey3".equals(aDataMap.get("CACHE_KEY")), aDataMap -> aDataMap.getString("CACHE_KEY"),
				aDataMap -> new TestDataBean(aDataMap.getNumber("BEAN_ID", Integer.class),
						aDataMap.getString("BEAN_NAME")));
		return jdbcDataMapCache;
	}
}