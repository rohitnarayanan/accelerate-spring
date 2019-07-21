package accelerate.spring.cache;

import org.springframework.context.ApplicationEvent;

/**
 * Custom {@link ApplicationEvent} to publish cache load/refresh events
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @param <T>
 * @since June 23, 2019
 */
public class CacheLoadEvent<T> extends ApplicationEvent {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link CacheEventType} for the event
	 */
	private CacheEventType cacheEventType = null;

	/**
	 * @param aDataMapCache
	 * @param aCacheEventType
	 */
	public CacheLoadEvent(DataMapCache<T> aDataMapCache, CacheEventType aCacheEventType) {
		super(aDataMapCache);
		this.cacheEventType = aCacheEventType;
	}

	/**
	 * Getter method for cache instance
	 * 
	 * @return cacheEventType
	 */
	@SuppressWarnings("unchecked")
	public DataMapCache<T> getCache() {
		return (DataMapCache<T>) getSource();
	}

	/**
	 * Getter method for "cacheEventType" property
	 * 
	 * @return cacheEventType
	 */
	public CacheEventType getCacheEventType() {
		return this.cacheEventType;
	}

	/**
	 * Enum to indicate the type of cache event
	 * 
	 * @version 1.0 Initial Version
	 * @author Rohit Narayanan
	 * @since June 23, 2019
	 */
	public enum CacheEventType {
		/**
		 * When cache is initialized
		 */
		INIT,

		/**
		 * When cache is refreshed
		 */
		REFRESH
	}
}