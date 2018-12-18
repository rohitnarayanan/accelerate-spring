package accelerate.spring.staticlistener;

import static accelerate.commons.constants.CommonConstants.COMMA_CHAR;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import accelerate.commons.data.DataMap;
import accelerate.commons.exceptions.ApplicationException;
import accelerate.commons.utils.CommonUtils;
import accelerate.commons.utils.ReflectionUtils;
import accelerate.commons.utils.StringUtils;
import accelerate.spring.cache.DataMapCache;
import accelerate.spring.logging.Log;
import accelerate.spring.logging.LoggerAspect;

/**
 * Utility class to enable static access to spring components via
 * {@link StaticCacheListener} and {@link StaticContextListener} annotations
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@Component
public class StaticListenerUtil implements ApplicationListener<ApplicationReadyEvent>, Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link Logger} instance
	 */
	private static final Logger _LOGGER = LoggerFactory.getLogger(StaticListenerUtil.class);

	/**
	 * static {@link ApplicationContext} instance, to provide spring beans access to
	 * all classes.
	 */
	@Autowired
	private transient ApplicationContext applicationContext = null;

	/**
	 * list of base packages to scan for static listeners
	 */
	@Value("${accelerate.spring.static-listener.scan-base-packages:}")
	private String scanBasePackages;

	/**
	 * {@link Set} of packages derived from {@link #scanBasePackages}
	 */
	private Set<String> scanBasePackagesSet = null;

	/**
	 * List of classes annotated with @AccelerateContextListener. Static reference
	 * is stores to avoid scanning classpath multiple times as it is an expensive
	 * operation
	 */
	private Map<String, Map<String, String>> staticContextListeners = null;

	/**
	 * List of classes annotated with @StaticCacheListener. Static reference is
	 * stores to avoid scanning classpath multiple times as it is an expensive
	 * operation
	 */
	private Map<String, Map<String, String>> staticCacheListeners = null;

	/**
	 * @throws ApplicationException thrown due to
	 *                              {@link #initializeContextListenerMap()} and
	 *                              {@link #initializeCacheListenerMap()}
	 * 
	 */
	@PostConstruct
	public void initialize() {
		Exception methodError = null;

		try {
			this.scanBasePackagesSet = new HashSet<>(
					Arrays.asList(StringUtils.safeSplit(this.scanBasePackages, COMMA_CHAR)));
			this.scanBasePackagesSet.add("accelerate");
			_LOGGER.debug("scanBasePackagesSet: {}", this.scanBasePackagesSet);

			initializeContextListenerMap();
			initializeCacheListenerMap();
		} catch (Exception error) {
			methodError = error;
			ApplicationException.checkAndThrow(error, "Error in initializing StaticListenerUtil");
		} finally {
			LoggerAspect.logMethodExit(String.format("%s.%s", this.getClass().getName(), "initialize()"), methodError);
		}
	}

	/**
	 * This method is called to notify that the application is shutting down or the
	 * context has been destroyed.
	 */
	@PreDestroy
	public void destroy() {
		notifyContextListener("onContextClosed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.
	 * springframework.context.ApplicationEvent)
	 */
	/**
	 * This method initializes Accelerate components to register that a new
	 * {@link BeanFactory} has been initialized. It notifies any custom
	 * implementation of {@link StaticContextListener} provided by the application.
	 * It then notifies any static classes that maybe registered as a listener for
	 * spring context initialization or {@link DataMapCache} reload event.
	 * 
	 * @param aEvent
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent aEvent) {
		notifyContextListener("onContextStarted");
	}

	/**
	 * @throws ApplicationException thrown due to
	 *                              {@link #getAnnotationAttributes(BeanDefinition, Class)}
	 */
	private void initializeContextListenerMap() throws ApplicationException {
		this.staticContextListeners = findCandidateComponents(StaticContextListener.class).stream()
				.flatMap(beanDefinition -> {
					_LOGGER.debug("Registering StaticContextListener [{}]", beanDefinition.getBeanClassName());

					AnnotationAttributes annotationAttributes = getAnnotationAttributes(beanDefinition,
							StaticContextListener.class);
					return Stream.of(
							DataMap.newMap().putAnd("event", "onContextStarted")
									.putAnd("listenerClass", beanDefinition.getBeanClassName())
									.putAnd("handleMethod", annotationAttributes.getString("onContextStarted")),
							DataMap.newMap().putAnd("event", "onContextClosed")
									.putAnd("listenerClass", beanDefinition.getBeanClassName())
									.putAnd("handleMethod", annotationAttributes.getString("onContextClosed")));
				}).collect(Collectors.groupingBy(map -> map.get("event").toString(), () -> new HashMap<>(), Collectors
						.toMap(map -> map.get("listenerClass").toString(), map -> map.get("handleMethod").toString())));
	}

	/**
	 * @throws ApplicationException thrown due to
	 *                              {@link #getAnnotationAttributes(BeanDefinition, Class)}
	 */
	private void initializeCacheListenerMap() throws ApplicationException {
		this.staticCacheListeners = findCandidateComponents(StaticCacheListener.class).stream().map(beanDefinition -> {
			_LOGGER.debug("Registering StaticCacheListener [{}]", beanDefinition.getBeanClassName());

			AnnotationAttributes annotationAttributes = getAnnotationAttributes(beanDefinition,
					StaticCacheListener.class);
			return DataMap.newMap().putAnd("cacheName", annotationAttributes.getString("name"))
					.putAnd("listenerClass", beanDefinition.getBeanClassName())
					.putAnd("handleMethod", annotationAttributes.getString("handler"));
		}).collect(Collectors.groupingBy(map -> map.get("cacheName").toString(), () -> new HashMap<>(), Collectors
				.toMap(map -> map.get("listenerClass").toString(), map -> map.get("handleMethod").toString())));
	}

	/**
	 * @param aAnnotationType
	 * @return
	 */
	private Set<BeanDefinition> findCandidateComponents(Class<? extends Annotation> aAnnotationType) {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(aAnnotationType));

		Set<BeanDefinition> componentSet = new HashSet<>();
		this.scanBasePackagesSet.stream().parallel()
				.forEach(packageName -> componentSet.addAll(provider.findCandidateComponents(packageName)));
		return componentSet;
	}

	/**
	 * @param aBeanDefinition
	 * @param aAnnotationType
	 * @return
	 * @throws ApplicationException thrown due to
	 *                              {@link ReflectionUtils#getFieldValue(Class, Object, String)}
	 */
	private static AnnotationAttributes getAnnotationAttributes(BeanDefinition aBeanDefinition,
			Class<? extends Annotation> aAnnotationType) throws ApplicationException {
		Object metadata = ReflectionUtils.getFieldValue(aBeanDefinition.getClass(), aBeanDefinition, "metadata");
		@SuppressWarnings("unchecked")
		Map<String, LinkedList<AnnotationAttributes>> attributesMap = (Map<String, LinkedList<AnnotationAttributes>>) ReflectionUtils
				.getFieldValue(metadata.getClass(), metadata, "attributesMap");
		return attributesMap.get(aAnnotationType.getName()).get(0);
	}

	/**
	 * @param aContextEvent
	 */
	private void notifyContextListener(String aContextEvent) {
		Map<String, String> listenerMap = this.staticContextListeners.get(aContextEvent);
		if (CommonUtils.isEmpty(listenerMap)) {
			return;
		}

		listenerMap.forEach((aClassName, aHandleMethod) -> {
			try {
				Class<?> targetClass = Class.forName(aClassName);
				ReflectionUtils.invokeMethod(targetClass, null, aHandleMethod,
						new Class<?>[] { ApplicationContext.class }, new Object[] { this.applicationContext });
			} catch (Exception error) {
				ApplicationException.checkAndThrow(error);
			}
		});
	}

	/**
	 * @param aCache
	 */
	@Log
	public void notifyCacheLoad(DataMapCache<?> aCache) {
		Map<String, String> listenerMap = this.staticCacheListeners.get(aCache.getCacheName());
		if (CommonUtils.isEmpty(listenerMap)) {
			return;
		}

		listenerMap.forEach((aClassName, aHandleMethod) -> {
			try {
				Class<?> targetClass = Class.forName(aClassName);
				ReflectionUtils.invokeMethod(targetClass, null, aHandleMethod, new Class<?>[] { aCache.getClass() },
						new Object[] { aCache });
			} catch (Exception error) {
				ApplicationException.checkAndThrow(error);
			}
		});
	}
}