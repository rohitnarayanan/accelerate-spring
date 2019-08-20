package accelerate.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import accelerate.commons.data.DataMap;
import accelerate.commons.exception.ApplicationException;
import accelerate.commons.util.JacksonUtils;
import accelerate.spring.cache.DataMapCache;
import accelerate.spring.cache.PropertyCache;
import accelerate.spring.cache.TestDataBean;

/**
 * PUT DESCRIPTION HERE
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 21, 2018
 */
@SpringBootApplication(scanBasePackages = { "accelerate" }, exclude = { DataSourceAutoConfiguration.class,
		DataSourceHealthIndicatorAutoConfiguration.class })
public class AccelerateSpringTest {
	/**
	 * @param aArgs
	 */
	public static void main(String[] aArgs) {
		try {
			quickTest();
//			runSpringBootApp(aArgs);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * @throws Exception
	 */
	public static final void quickTest() throws Exception {
		DataMapCache<TestDataBean> beanCache = new DataMapCache<>("basicDataMapCache", TestDataBean.class) {
			private static final long serialVersionUID = 1L;

			protected void loadCache(DataMap aCacheMap) throws ApplicationException {
				aCacheMap.put("cacheKey1", new TestDataBean(1));
				aCacheMap.put("cacheKey2", new TestDataBean(2));
				aCacheMap.put("cacheKey3", new TestDataBean(3));
				aCacheMap.put("cacheKey4", new TestDataBean(4));
			}
		};
		beanCache.setCacheSource("INVALID URL", null);
		System.out.println(beanCache.toString());
		System.out.println(JacksonUtils.toJSON(beanCache));
		System.out.println("###################################");

		beanCache.setCacheSource(null, "INVALID QUERY", null, null, null);
		System.out.println(beanCache.toString());
		System.out.println(JacksonUtils.toJSON(beanCache));
		System.out.println("###################################");

		PropertyCache profileCache = new PropertyCache("profilePropertyCache",
				"classpath:/accelerate/spring/cache/accelerate-spring-test.properties", "dev");
		System.out.println(profileCache.toString());
		System.out.println(JacksonUtils.toJSON(profileCache));
	}

	/**
	 * @param aArgs
	 */
	public static final void runSpringBootApp(String[] aArgs) {
		SpringApplication springApplication = new SpringApplication(AccelerateSpringTest.class);
		springApplication.setAdditionalProfiles(//
				ProfileConstants.PROFILE_LOGGING, //
				ProfileConstants.PROFILE_WEB, //
				ProfileConstants.PROFILE_SECURITY //
		);

		springApplication.run(aArgs);
	}
}
