package accelerate.spring.cache;

import static accelerate.commons.constant.CommonConstants.EMPTY_STRING;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import accelerate.commons.constant.CommonConstants;
import accelerate.commons.data.DataMap;
import accelerate.commons.exception.ApplicationException;
import accelerate.commons.util.JacksonUtils;
import accelerate.commons.util.StringUtils;
import accelerate.spring.cache.CacheLoadEvent.CacheEventType;
import accelerate.spring.logging.LogUtils;
import accelerate.spring.logging.Profiled;

/**
 * This is a generic {@link Map} based cache stored on the JVM heap. It has no
 * persistence mechanism. It is designed to be loaded at startup and provide
 * quick lookup for small data sets. It also provides JMX operations to manage
 * the cache and a web UI to view the cache.
 * 
 * It is not a replacement for more comprehensive caching frameworks like
 * ehcache etc.
 * 
 * @param <V> Type Variable for the values
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 2, 2017
 */
@ManagedResource(description = "Generic Map providing easy-to-use cache for small static data sets")
@Profiled
public class DataMapCache<V> implements Serializable {
	/**
	 * {@link ApplicationEventPublisher} instance
	 */
	@Autowired
	private transient ApplicationEventPublisher applicationEventPublisher = null;

	/**
	 * Cache Name
	 */
	private final String cacheName;

	/**
	 * Value Type
	 */
	private transient final Class<V> valueType;

	/**
	 * {@link DataMap} instance serving as cache store
	 */
	private transient final DataMap cacheMap;

	/**
	 * Semaphore to block while cache is being refresh
	 */
	private CacheStatus cacheStatus;

	/**
	 * Cache Age
	 */
	private String cacheAge = CommonConstants.EMPTY_STRING;

	/**
	 * {@link DataSource} instance to query db for properties
	 */
	private transient DataSource dataSource;

	/**
	 * SQL query to fetch data to be cached
	 */
	protected String dataQuery;

	/**
	 * {@link Function} implementation to filter which values to load from database.
	 * default implementation does not filter anything
	 */
	private transient Function<Map<String, Object>, Boolean> recordFilter;

	/**
	 * {@link Function} implementation to load key from the result set
	 */
	private transient Function<Map<String, Object>, String> keyProvider;

	/**
	 * {@link Function} implementation to load value from the result set
	 */
	private transient Function<Map<String, Object>, V> valueProvider;

	/**
	 * Init time of Cache
	 */
	@JsonFormat(pattern = "MM/dd/yyyy HH:ss:SSS z")
	private Date cacheInitializedTime = null;

	/**
	 * Refresh Time of Cache
	 */
	@JsonFormat(pattern = "MM/dd/yyyy HH:ss:SSS z")
	private Date cacheRefreshedTime = null;

	/**
	 * Cache Duration
	 */
	private transient long cacheDuration = -1;

	/**
	 * Default Constructor
	 *
	 * @param aCacheName
	 * @param aValueType
	 */
	public DataMapCache(String aCacheName, Class<V> aValueType) {
		this.cacheName = aCacheName;
		this.valueType = aValueType;

		this.cacheMap = DataMap.newMap();
		this.cacheStatus = CacheStatus.NEW;

		this.recordFilter = (aRowMap) -> true;
	}

	/**
	 * This method initializes the cache. This is the first method that should be
	 * called before the cache can be used. It also should be registered as the
	 * "init-method" method in case the Cache class is going to be managed by Spring
	 * Framework.
	 * 
	 * @throws ApplicationException wrapping all possible exceptions
	 */
	@PostConstruct
	private void initialize() {
		Assert.state(this.cacheStatus == CacheStatus.NEW, "Cache already initialized");

		Profiler profiler = LogUtils.startProfiler(this.cacheName + ".initialize", LOGGER);
		try {
			profiler.start("loadCache");

			loadCache(this.cacheMap);

			profiler.start("setup");

			this.cacheInitializedTime = new Date();
			this.cacheRefreshedTime = new Date();

			calculateCacheDuration();
			this.cacheStatus = CacheStatus.INITIALIZED;
			LOGGER.info("Cache [{}] Initialized", this.cacheName);

			// publish the initialized event
			this.applicationEventPublisher.publishEvent(new CacheLoadEvent<>(this, CacheEventType.INIT));
		} catch (Exception error) {
			ApplicationException.checkAndThrow(error, "Error in initializing cache [%s]", this.cacheName);
		} finally {
			profiler.stop().log();
		}
	}

	/**
	 * This method fetches the data to be loaded into the cache, from the data
	 * source. sub classes can override the method to load data from other sources
	 * 
	 * @param aCacheMap
	 * 
	 * @throws ApplicationException Allowing implementations to wrap exceptions in
	 *                              one class
	 */
	protected void loadCache(DataMap aCacheMap) throws ApplicationException {
		if (StringUtils.isEmpty(this.dataQuery)) {
			return;
		}

		/*
		 * Query the database
		 */
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		List<Map<String, Object>> queryData = jdbcTemplate.queryForList(this.dataQuery);
		queryData.parallelStream().filter(aRowMap -> this.recordFilter.apply(aRowMap))
				.forEach(aRowMap -> aCacheMap.put(this.keyProvider.apply(aRowMap), this.valueProvider.apply(aRowMap)));
	}

	/**
	 * This method refreshes the cache
	 *
	 * @throws ApplicationException thrown by {@link #loadCache(DataMap)}
	 */
	@ManagedOperation(description = "This method refreshes the cache")
	public void refresh() throws ApplicationException {
		LOGGER.debug("Refreshing Cache [{}]", this.cacheName);

		this.cacheStatus = CacheStatus.REFRESHING;

		// reload the cache
		DataMap tmpCache = DataMap.newMap();
		loadCache(tmpCache);
		this.cacheMap.clear();
		this.cacheMap.putAll(tmpCache);

		// update refresh time and age
		this.cacheRefreshedTime = new Date();

		// reset refresh monitor and wake all threads waiting for access
		this.cacheStatus = CacheStatus.INITIALIZED;

		// publish the refresh event
		this.applicationEventPublisher.publishEvent(new CacheLoadEvent<>(this, CacheEventType.REFRESH));

		LOGGER.info("Cache [{}] Refreshed", this.cacheName);
	}

	/*
	 * Cache API
	 */
	/**
	 * @param aKey
	 * @return
	 */
	public V get(String aKey) {
		return this.cacheMap.get(aKey);
	}

	/**
	 * @param aKey
	 * @param aValue
	 */
	public void put(String aKey, V aValue) {
		this.cacheMap.put(aKey, aValue);
	}

	/**
	 * @param aKey
	 * @return
	 */
	public V remove(String aKey) {
		return this.cacheMap.remove(aKey);
	}

	/**
	 * This method returns the JSON form of value stored in cache. It uses
	 * {@link JacksonUtils#toJSON(Object)} to serialize the value
	 *
	 * @param aKey Key to be looked up in the cache
	 * @return JSON string for the value cached
	 * @throws ApplicationException thrown by {@link JacksonUtils#toJSON(Object)}
	 */
	@ManagedOperation(description = "This method returns the JSON form of value stored in cache against the given key")
	public String getJSON(String aKey) throws ApplicationException {
		V value = get(aKey);
		if (value == null) {
			return EMPTY_STRING;
		}

		return JacksonUtils.toJSON(value);
	}

	/**
	 * This method returns the XML form of value stored in cache. It uses
	 * {@link JacksonUtils#toXML(Object)} to serialize the value
	 *
	 * @param aKey Key to be looked up in the cache
	 * @return XML string for the value cached
	 * @throws ApplicationException thrown by {@link JacksonUtils#toXML(Object)}
	 */
	@ManagedOperation(description = "This method returns the JSON form of value stored in cache against the given key")
	public String getXML(String aKey) throws ApplicationException {
		V value = get(aKey);
		if (value == null) {
			return EMPTY_STRING;
		}

		return JacksonUtils.toJSON(value);
	}

	/**
	 * This method returns the YAML form of value stored in cache. It uses
	 * {@link JacksonUtils#toYAML(Object)} to serialize the value
	 *
	 * @param aKey Key to be looked up in the cache
	 * @return YAML string for the value cached
	 * @throws ApplicationException thrown by {@link JacksonUtils#toYAML(Object)}
	 */
	@ManagedOperation(description = "This method returns the JSON form of value stored in cache against the given key")
	public String getYAML(String aKey) throws ApplicationException {
		V value = get(aKey);
		if (value == null) {
			return EMPTY_STRING;
		}

		return JacksonUtils.toJSON(value);
	}

	/**
	 * This method stores the given key-value pair in cache after converting them
	 *
	 * @param aKey         Key to be added/updated in the cache
	 * @param aStringValue JSON representation of Value to be stored in the cache.
	 * @throws ApplicationException thrown by
	 *                              {@link JacksonUtils#fromJSON(String, Class)}
	 */
	@ManagedOperation(description = "This method stores the given key-value pair in cache after converting them")
	public void putJSON(String aKey, String aStringValue) throws ApplicationException {
		Assert.isTrue(!StringUtils.isEmpty(aStringValue), "JSON value cannot be empty");
		if (this.valueType == String.class) {
			put(aKey, this.valueType.cast(aStringValue));
		} else {
			put(aKey, JacksonUtils.fromJSON(aStringValue, this.valueType));
		}
	}

	/**
	 * This method stores the given key-value pair in cache after converting them
	 *
	 * @param aKey         Key to be added/updated in the cache
	 * @param aStringValue XML representation of Value to be stored in the cache.
	 * @throws ApplicationException thrown by
	 *                              {@link JacksonUtils#fromXML(String, Class)}
	 */
	@ManagedOperation(description = "This method stores the given key-value pair in cache after converting them")
	public void putXML(String aKey, String aStringValue) throws ApplicationException {
		Assert.isTrue(!StringUtils.isEmpty(aStringValue), "XML value cannot be empty");
		if (this.valueType == String.class) {
			put(aKey, this.valueType.cast(aStringValue));
		} else {
			put(aKey, JacksonUtils.fromXML(aStringValue, this.valueType));
		}
	}

	/**
	 * This method stores the given key-value pair in cache after converting them
	 *
	 * @param aKey         Key to be added/updated in the cache
	 * @param aStringValue YAML representation of Value to be stored in the cache.
	 * @throws ApplicationException thrown by
	 *                              {@link JacksonUtils#fromYAML(String, Class)}
	 */
	@ManagedOperation(description = "This method stores the given key-value pair in cache after converting them")
	public void putYAML(String aKey, String aStringValue) throws ApplicationException {
		Assert.isTrue(!StringUtils.isEmpty(aStringValue), "YAML value cannot be empty");
		if (this.valueType == String.class) {
			put(aKey, this.valueType.cast(aStringValue));
		} else {
			put(aKey, JacksonUtils.fromYAML(aStringValue, this.valueType));
		}
	}

	/*
	 * Cache info methods
	 */
	/**
	 * @return
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/**
	 * @return
	 */
	@Override
	@ManagedOperation(description = "This method returns the basic cache information")
	public String toString() {
		ObjectMapper objectMapper = JacksonUtils.objectMapper();
		objectMapper.addMixIn(DataMapCache.class, DataMapCacheMixIn.class);

		return JacksonUtils.toJSON(objectMapper, this);
	}

	/**
	 * Getter method for "cacheName" property
	 * 
	 * @return cacheName
	 */
	public String getCacheName() {
		return this.cacheName;
	}

	/**
	 * This method returns the age of the cache
	 *
	 * @return cache name
	 */
	public String getCacheAge() {
		return this.cacheAge;
	}

	/**
	 * Getter method for "cacheStatus" property
	 * 
	 * @return cacheStatus
	 */
	public CacheStatus getCacheStatus() {
		return this.cacheStatus;
	}

	/**
	 * Method to get size of cache
	 * 
	 * @return size of cache
	 */
	public int getCacheSize() {
		return this.cacheMap.size();
	}

	/**
	 * Method to get list of cached keys
	 * 
	 * @return list of cached keys
	 */
	public List<String> getCacheKeys() {
		return new ArrayList<>(this.cacheMap.keySet());
	}

	/**
	 * Method to get list of cached keys
	 * 
	 * @return list of cached keys
	 */
	public DataMap getCacheMap() {
		return (DataMap) Collections.unmodifiableMap(this.cacheMap);
	}

	/*
	 * Cache config methods
	 */
	/**
	 * This method sets the age of the cache. The format of the age should be
	 * [Duration {@link TimeUnit}].
	 * 
	 * <p>
	 * Examples: 4.5 SECONDS, 8 HOURS, 2 DAYS
	 * </p>
	 *
	 * @param aCacheAge
	 */
	@ManagedOperation(description = "This method sets the age of the cache")
	public void setCacheAge(String aCacheAge) {
		this.cacheAge = aCacheAge;
		calculateCacheDuration();
	}

	/**
	 * This method sets the required attributes to load cache from a database
	 * 
	 * @param aDataSource
	 * @param aDataQuery
	 * @param aKeyProvider
	 * @param aValueProvider
	 */
	public void setDataProviders(DataSource aDataSource, String aDataQuery,
			Function<Map<String, Object>, String> aKeyProvider, Function<Map<String, Object>, V> aValueProvider) {
		Assert.noNullElements(new Object[] { aDataSource, aDataQuery, aKeyProvider, aValueProvider },
				"all parameters are required");

		this.dataSource = aDataSource;
		this.dataQuery = aDataQuery;
		this.keyProvider = aKeyProvider;
		this.valueProvider = aValueProvider;
	}

	/*
	 * internal methods
	 */
	/**
	 * A scheduled cron to run every 5 mins to check if the cache needs to be
	 * refreshed
	 * 
	 * @throws ApplicationException thrown by {@link #refresh()}
	 */
	@Scheduled(fixedDelay = 5 * 60 * 1000)
	@Async
	private void checkRefresh() throws ApplicationException {
		/*
		 * if cache has not been initialized or is currently refreshing or is not
		 * refreshable, return
		 */
		if (this.cacheStatus != CacheStatus.INITIALIZED || this.cacheDuration < 0) {
			return;
		}

		if ((System.currentTimeMillis() - this.cacheRefreshedTime.getTime()) > this.cacheDuration) {
			/*
			 * synchronized block to prevent multiple refreshes
			 */
			synchronized (this) {
				if ((System.currentTimeMillis() - this.cacheRefreshedTime.getTime()) > this.cacheDuration) {
					refresh();
				}
			}
		}
	}

	/**
	 * This method calculates the cache duration to determine when the cache is due
	 * to be refreshed from the data store
	 */
	private void calculateCacheDuration() {
		if (StringUtils.isEmpty(this.cacheAge)) {
			this.cacheDuration = -1;
		} else {
			String[] tokens = StringUtils.split(this.cacheAge, CommonConstants.SPACE);
			this.cacheDuration = TimeUnit.valueOf(tokens[1]).toMillis(Long.parseLong(tokens[0]));
		}
	}

	/**
	 * ENUM for cache status values
	 * 
	 * @version 1.0 Initial Version
	 * @author Rohit Narayanan
	 * @since November 09, 2018
	 */
	enum CacheStatus {
		/**
		 * Default status for the cache
		 */
		NEW,
		/**
		 * Status after the cache is built
		 */
		INITIALIZED,
		/**
		 * Status while the cache is being refreshed
		 */
		REFRESHING
	}

	/**
	 * Jackson MixIn to serialize relevant fields
	 * 
	 * @version 1.0 Initial Version
	 * @author Rohit Narayanan
	 * @since November 10, 2018
	 */
	@SuppressWarnings("hiding")
	abstract class DataMapCacheMixIn {
		/**
		 */
		@JsonProperty("name")
		public String cacheName;

		/**
		 */
		@JsonProperty("status")
		public CacheStatus cacheStatus;

		/**
		 */
		@JsonProperty("age")
		public String cacheAge;

		/**
		 * @return
		 */
		@JsonProperty("size")
		abstract int getCacheSize();

		/**
		 */
		@JsonProperty("initializedAt")
		public Date cacheInitializedTime;

		/**
		 */
		@JsonProperty("refreshedAt")
		public Date cacheRefreshedTime;
	}

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DataMapCache.class);
}