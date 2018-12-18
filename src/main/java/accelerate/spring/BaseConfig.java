package accelerate.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Base configuration class for accelerate
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 20, 2018
 */
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootConfiguration
@EnableConfigurationProperties
@ComponentScan(basePackages = "accelerate")
@Configuration
public class BaseConfig {
	/**
	 * Flag to indicate whether debug logs are enabled
	 */
	@Value("#{'${accelerate.spring.jackson.defaults:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
	protected boolean jacksonDefaultsEnabled;

	/**
	 * @return
	 */
	@Order(1)
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
		return (aJackson2ObjectMapperBuilder) -> {
			if (this.jacksonDefaultsEnabled) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
				mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
				aJackson2ObjectMapperBuilder.serializationInclusion(Include.NON_NULL);
				aJackson2ObjectMapperBuilder.filters(
						new SimpleFilterProvider().setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept()));
				
				aJackson2ObjectMapperBuilder.configure(mapper);
			}
		};
	}
}
