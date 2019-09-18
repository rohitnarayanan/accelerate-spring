package accelerate.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * PUT DESCRIPTION HERE
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 21, 2018
 */
@SpringBootApplication(scanBasePackages = { "accelerate" }, exclude = {})
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
		// no impl
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

	/**
	 * @return
	 * @throws Exception
	 */
	@Bean
	public static Object contextTest() throws Exception {
		// no impl

		return new Object();
	}
}
