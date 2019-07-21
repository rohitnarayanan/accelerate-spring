package mystikos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import accelerate.spring.AccelerateSpringTest;
import accelerate.spring.ProfileConstants;

/**
 * PUT DESCRIPTION HERE
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since July 21, 2019
 */
@SpringBootApplication(scanBasePackages = { "accelerate", "mystikos" }, exclude = { DataSourceAutoConfiguration.class,
		DataSourceHealthIndicatorAutoConfiguration.class })
public class MystikosSpringBootApplication {
	/**
	 * @param aArgs
	 */
	public static void main(String[] aArgs) {
		try {
			runSpringBootApp(aArgs);
		} catch (Exception error) {
			error.printStackTrace();
		}
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
}
