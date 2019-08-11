package accelerate.spring.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import accelerate.commons.util.JacksonUtils;
import accelerate.spring.ProfileConstants;
import accelerate.spring.config.ConfigProps;

/**
 * {@link BeanPostProcessor} to log all {@link ConfigProps} initialized by
 * Spring
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since December 11, 2017
 */
@Profile(ProfileConstants.PROFILE_LOGGING)
@Component
@ConditionalOnExpression("${accelerate.spring.logging.config-props:${accelerate.spring.defaults:true}}")
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
			LOGGER.debug("{}.INIT: {}", aBean.getClass().getName(), JacksonUtils.toJSON(aBean));
		}

		return aBean;
	}

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPropsLogger.class);
}
