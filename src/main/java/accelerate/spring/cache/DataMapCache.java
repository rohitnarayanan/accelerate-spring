package accelerate.spring.cache;

import static accelerate.commons.constants.CommonConstants.EMPTY_STRING;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import accelerate.commons.constants.CommonConstants;
import accelerate.commons.data.DataMap;
import accelerate.commons.exceptions.ApplicationException;
import accelerate.commons.utils.JSONUtils;
import accelerate.commons.utils.StringUtils;
import accelerate.spring.logging.Log;
import accelerate.spring.logging.LoggerAspect;
import accelerate.spring.staticlistener.StaticListenerUtil;

/**
 * This is a generic {@link Map} based cache stored on the JVM heap. It has no
 * persistence mechanism. It is designed to be loaded at startup and provide
 * quick lookup for small data sets. Accelerate also provides JMX operations to
 * manage the cache and a web UI to view the cache.
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
public class DataMapCache<V> implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link Logger} instance
	 */
	protected static final Logger _LOGGER = LoggerFactory.getLogger(DataMapCache.class);

	/**
	 * {@link StaticListenerUtil} instance
	 */
	@Autowired(required = false)
	protected transient StaticListenerUtil staticListenerUtil = null;

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
	private transient final DataMap<V> cacheMap;

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
	protected transient DataSource dataSource;

	/**
	 * SQL query to fetch data to be cached
	 */
	protected String dataQuery;

	/**
	 * {@link Function} implementation to load value from the resultset
	 */
	private transient Function<Map<String, Object>, String> keyProvider;

	/**
	 * 
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

		this.cacheMap = new DataMap<>();
		this.cacheStatus = CacheStatus.NEW;
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
	public void initialize() {
		Assert.state(this.cacheStatus == CacheStatus.NEW, "Cache already initialized");
		Exception methodError = null;

		try {
			loadCache();
			this.cacheInitializedTime = new Date();
			this.cacheRefreshedTime = new Date();

			calculateCacheDuration();
			this.cacheStatus = CacheStatus.INITIALIZED;
			_LOGGER.info("Cache [{}] Initialized", this.cacheName);

//			this.staticListenerUtil.notifyCacheLoad(DataMapCache.this);
		} catch (Exception error) {
			methodError = error;
			ApplicationException.checkAndThrow(error, "Error in initializing cache [%s]", this.cacheName);
		} finally {
			LoggerAspect.logMethodExit(String.format("%s [%s]", this.getClass().getName(), this.cacheName),
					methodError);
		}
	}

	/**
	 * This method fetches the data to be loaded into the cache, from the data
	 * source. sub classes can override the method to load data from other sources
	 * 
	 * @throws ApplicationException Allowing implementations to wrap exceptions in
	 *                              one class
	 */
	protected void loadCache() throws ApplicationException {
		if (StringUtils.isEmpty(this.dataQuery)) {
			return;
		}

		/*
		 * Query the database
		 */
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		List<Map<String, Object>> queryData = jdbcTemplate.queryForList(this.dataQuery);
		queryData.forEach(aRowMap -> put(DataMapCache.this.keyProvider.apply(aRowMap),
				DataMapCache.this.valueProvider.apply(aRowMap)));
	}

	/**
	 * This method refreshes the cache
	 *
	 * @throws ApplicationException thrown by {@link #loadCache()} and
	 *                              {@link StaticListenerUtil#notifyCacheLoad(DataMapCache)}
	 */
	@Log
	@ManagedOperation(description = "This method refreshes the cache")
	public void refresh() throws ApplicationException {
		_LOGGER.debug("Refreshing Cache [{}]", this.cacheName);

		this.cacheStatus = CacheStatus.REFRESHING;

		// reload the cache
		loadCache();

		// update refresh time and age
		this.cacheRefreshedTime = new Date();

		// notify all registered listeners
		if (this.staticListenerUtil != null) {
			this.staticListenerUtil.notifyCacheLoad(DataMapCache.this);
		}

		// reset refresh monitor and wake all threads waiting for access
		this.cacheStatus = CacheStatus.INITIALIZED;

		_LOGGER.info("Cache [{}] Refreshed", this.cacheName);
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
	 * @param aDataMap
	 */
	public void putAll(DataMap<V> aDataMap) {
		this.cacheMap.putAll(aDataMap);
	}

	/**
	 * This method returns the JSON form of value stored in cache against the given
	 * key
	 *
	 * @param aKey key string to fetch value stored in the cache
	 * @return value instance stored against the key
	 * @throws ApplicationException thrown by {@link JSONUtils#serialize(Object)}
	 *                              and {@link JSONUtils#deserialize(String, Class)}
	 */
	@ManagedOperation(description = "This method returns the JSON form of value stored in cache against the given key")
	public String getJSON(String aKey) throws ApplicationException {
		V value = get(aKey);
		if (value == null) {
			return EMPTY_STRING;
		}

		return JSONUtils.serialize(value);
	}

	/**
	 * This method stores the given key-value pair in cache after converting them
	 *
	 * @param aKey       Key to be added to the cache
	 * @param aJSONValue JSON representation of Value to be stored in the cache.
	 * @throws ApplicationException thrown by {@link JSONUtils#serialize(Object)}
	 *                              and {@link JSONUtils#deserialize(String, Class)}
	 */
	@ManagedOperation(description = "This method stores the given key-value pair in cache after converting them")
	public void putJSON(String aKey, String aJSONValue) throws ApplicationException {
		Assert.isTrue(!StringUtils.isEmpty(aJSONValue), "JSON value cannot be empty");

		put(aKey, JSONUtils.deserialize(aJSONValue, this.valueType));
	}

	/*
	 * Cache info methods
	 */
	/**
	 * @return
	 */
	@ManagedOperation(description = "This method returns the basic cache information")
	public String toJSON() {
		ObjectMapper objectMapper = JSONUtils.objectMapper();
		objectMapper.addMixIn(DataMapCache.class, MixIn.class);
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException error) {
			throw new ApplicationException(error);
		}
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
	public Map<String, V> getCacheStore() {
		return Collections.unmodifiableMap(this.cacheMap);
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
	 * Setter method for "dataSource" property
	 * 
	 * @param aDataSource
	 * @param aDataQuery
	 */
	public void setDataProviders(DataSource aDataSource, String aDataQuery) {
		this.dataSource = aDataSource;
		this.dataQuery = aDataQuery;
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
		 * if cache has not been initialized or is not refreshable or is currently
		 * refreshing, return
		 */
		if ((this.cacheStatus != CacheStatus.INITIALIZED) && this.cacheDuration < 0) {
			return;
		}

		if ((System.currentTimeMillis() - this.cacheRefreshedTime.getTime()) > this.cacheDuration) {
			refresh();
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
			String[] tokens = StringUtils.safeSplit(this.cacheAge, CommonConstants.SPACE_CHAR);
			this.cacheDuration = TimeUnit.valueOf(tokens[1]).toMillis(Long.parseLong(tokens[0]));
		}
	}

	/**
	 * PUT DESCRIPTION HERE
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
	 * PUT DESCRIPTION HERE
	 * 
	 * @version 1.0 Initial Version
	 * @author Rohit Narayanan
	 * @since November 10, 2018
	 */
	@SuppressWarnings("hiding")
	abstract class MixIn {
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
}