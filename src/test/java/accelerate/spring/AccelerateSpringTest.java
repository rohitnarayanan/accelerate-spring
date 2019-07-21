package accelerate.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import accelerate.commons.data.DataBean;
import accelerate.spring.cache.DataMapCache;
import accelerate.spring.cache.PropertyCache;

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
//			quickTest();
			runSpringBootApp(aArgs);
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * @throws Exception
	 */
	public static final void quickTest() throws Exception {
		PropertyCache cache = new PropertyCache("testCache", "/temp.properties");
		cache.initialize();
//		cache.put("key1", "value1");
		System.out.println(">>>" + cache.get("spring", "application", "name"));
	}

	/**
	 * @param aArgs
	 */
	public static final void runSpringBootApp(String[] aArgs) {
		SpringApplication springApplication = new SpringApplication(AccelerateSpringTest.class);
		springApplication.setAdditionalProfiles(//
				ProfileConstants.PROFILE_LOGGING, //
				ProfileConstants.PROFILE_SECURITY);

		springApplication.run(aArgs);
	}

	/**
	 * @param aObjectMapper
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("static-method")
	@Autowired
	public void checkConfig(ObjectMapper aObjectMapper) throws JsonProcessingException {
		DataBean value1 = new DataBean();
		value1.put("key1", "value1");
		System.out.println(aObjectMapper.toString() + ">>>" + aObjectMapper.writeValueAsString(value1));
	}

	/**
	 * @param aObjectMapper
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("static-method")
	@Autowired
	public void checkConfig2(ObjectMapper aObjectMapper) throws JsonProcessingException {
		DataBean value1 = new DataBean();
		value1.put("key1", "value1");
		System.out.println(aObjectMapper.toString() + ">>>" + aObjectMapper.writeValueAsString(value1));
	}

	/**
	 * @return
	 */
	@Bean("testCache")
	public static PropertyCache testCache() {
		return new PropertyCache("testCache", "classpath:/temp.properties");
	}

	/**
	 * @return
	 */
	@Bean("beanCache")
	public static DataMapCache<DataBean> beanCache() {
		DataMapCache<DataBean> beanCache = new DataMapCache<>("beanCache", DataBean.class);

		DataBean value1 = new DataBean();
		value1.put("key1", "value1");
		beanCache.put("key1", value1);

		DataBean value2 = new DataBean();
		value2.put("key2", "value2");
		beanCache.put("key2", value2);

		return beanCache;
	}
}
