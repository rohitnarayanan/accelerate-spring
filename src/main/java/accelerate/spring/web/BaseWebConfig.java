package accelerate.spring.web;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import accelerate.spring.BaseConfiguration;

/**
 * Base web configuration class for the application
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 30, 2018
 */
@Configuration
public class BaseWebConfig extends SpringBootServletInitializer implements WebMvcConfigurer {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.boot.context.web.SpringBootServletInitializer#
	 * configure(org.springframework.boot.builder.SpringApplicationBuilder)
	 */
	/**
	 * @param application
	 * @return
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BaseConfiguration.class);
	}
}
