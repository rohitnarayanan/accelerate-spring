package accelerate.spring.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.walgreens.springboot.config.ConfigConstants;
import com.walgreens.springboot.config.ConfigProps;
import com.walgreens.springboot.util.JSONUtils;

/**
 * {@link BeanPostProcessor} to log all {@link ConfigProps} initialized by
 * Spring
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since December 11, 2017
 */
@Profile(ConfigConstants.PROFILE_LOGGING)
@Component
@ConditionalOnExpression("${com.walgreens.springboot.logging.config-props:${com.walgreens.springboot.defaults:true}}")
public class ConfigPropsLogger implements BeanPostProcessor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#
	 * postProcessBeforeInitialization(java.lang.Object, java.lang.String)
	 */
	/**
	 * @param aBean
	 * @param aBeanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInitialization(Object aBean, String aBeanName) throws BeansException {
		return aBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#
	 * postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 */
	/**
	 * @param aBean
	 * @param aBeanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessAfterInitialization(Object aBean, String aBeanName) throws BeansException {
		/*
		 * If debug is enabled and bean is of type DataBean then log information
		 */
		if (LOGGER.isDebugEnabled() && aBean.getClass().isAnnotationPresent(ConfigProps.class)) {
			LOGGER.debug("{}.INIT: {}", aBean.getClass().getName(), JSONUtils.serialize(aBean));
		}

		return aBean;
	}

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPropsLogger.class);
}
