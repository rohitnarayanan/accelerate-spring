package accelerate.spring.cache;

import static accelerate.commons.constant.CommonConstants.EMPTY_STRING;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import accelerate.commons.constant.CommonConstants;
import accelerate.commons.data.DataMap;
import accelerate.commons.exception.ApplicationException;
import accelerate.commons.util.DateTimeUtils;
import accelerate.commons.util.JacksonUtils;
import accelerate.commons.util.StringUtils;
import accelerate.spring.cache.CacheLoadEvent.CacheEventType;
import accelerate.spring.logging.LogUtils;
import accelerate.spring.logging.Profiled;

/**
 * This is a generic {@link DataMap} based cache stored on the heap with no
 * persistence mechanism. It is designed to be loaded at startup and recommended
 * for quick lookup for small data sets. It also provides JMX operations to
 * manage the cache and a rest REST_API to view the cache.
 * 
 * It is not a replacement for more comprehensive caching frameworks like
 * ehcache etc.
 * 
 * @param <V> Type Variable for the values
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 19, 2019
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
	private final String name;

	/**
	 * Value Type
	 */
	private transient final Class<V> valueType;

	/**
	 * {@link CacheStatus} for this instance
	 */
	private CacheStatus status = CacheStatus.NEW;

	/**
	 * {@link DataMap} instance serving as cache store
	 */
	private transient final DataMap cacheMap = DataMap.newMap();

	/**
	 * Expiration Time
	 */
	private String expiration = CommonConstants.EMPTY_STRING;

	/**
	 * Cache Duration
	 */
	private transient long expiryTime = -1;

	/**
	 * Init time of Cache
	 */
	@JsonFormat(pattern = "MM/dd/yyyy HH:ss:SSS z")
	private Date initializedAt = null;

	/**
	 * Refresh Time of Cache
	 */
	@JsonFormat(pattern = "MM/dd/yyyy HH:ss:SSS z")
	private Date refreshedAt = null;

	/**
	 * {@link CacheSource} for this instance
	 */
	private CacheSource source = CacheSource.CUSTOM;

	/**
	 * {@link DataSource} instance to query db for properties
	 */
	private transient DataSource dataSource;

	/**
	 * URL for the Data API
	 */
	private String dataURL;

	/**
	 * Parameters for {@link #dataURL}
	 */
	private transient Object[] urlParams;

	/**
	 * {@link Function} implementation to load value from the object
	 */
	private transient Function<V, String> keyMapper;

	/**
	 * SQL query to fetch data to be cached
	 */
	private String dataQuery;

	/**
	 * Parameters for {@link #dataQuery}
	 */
	private transient Object[] queryParams;

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
	 * Default Constructor
	 *
	 * @param aCacheName
	 * @param aValueType
	 */
	public DataMapCache(String aCacheName, Class<V> aValueType) {
		this.name = aCacheName;
		this.valueType = aValueType;
	}

	/**
	 * This method sets the required attributes to load cache from a database
	 * 
	 * @param aDataURL
	 * @param aKeyMapper
	 * @param aURLVariables
	 */
	public void setCacheSource(String aDataURL, Function<V, String> aKeyMapper, Object... aURLVariables) {
		this.dataURL = aDataURL;
		this.keyMapper = aKeyMapper;
		this.urlParams = aURLVariables;

		this.source = CacheSource.REST_API;
	}

	/**
	 * This method sets the required attributes to load cache from a database
	 * 
	 * @param aDataSource
	 * @param aDataQuery
	 * @param aRecordFilter
	 * @param aKeyProvider
	 * @param aValueProvider
	 * @param aQueryParams
	 */
	public void setCacheSource(DataSource aDataSource, String aDataQuery,
			Function<Map<String, Object>, Boolean> aRecordFilter, Function<Map<String, Object>, String> aKeyProvider,
			Function<Map<String, Object>, V> aValueProvider, Object... aQueryParams) {
		this.dataSource = aDataSource;
		this.dataQuery = aDataQuery;
		this.queryParams = aQueryParams;

		this.recordFilter = (aRecordFilter != null) ? aRecordFilter : ((aRowMap) -> true);
		this.keyProvider = aKeyProvider;
		this.valueProvider = aValueProvider;

		this.source = CacheSource.JDBC;
	}

	/**
	 * This method sets the expiration time for the cache. The format should be
	 * [<Duration> {@link TimeUnit}].
	 * 
	 * <p>
	 * Examples: 300 SECONDS, 8 HOURS, 2.5 DAYS
	 * </p>
	 *
	 * @param aExpiration
	 */
	@ManagedOperation(description = "This method sets the the cache expiry time")
	public void setExpiration(String aExpiration) {
		this.expiration = aExpiration;
		calculateExpiryTime();
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
		Assert.state(this.status == CacheStatus.NEW, "Cache already initialized");

		Profiler profiler = LogUtils.startProfiler(this.name + ".initialize", LOGGER);
		try {
			profiler.start("loadCache");
			loadCache(this.cacheMap);

			profiler.start("setup");
			this.initializedAt = this.refreshedAt = new Date();
			this.status = CacheStatus.OK;

			LOGGER.info("Cache [{}] Initialized", this.name);

			// publish the initialized event
			this.applicationEventPublisher.publishEvent(new CacheLoadEvent<>(this, CacheEventType.INIT));
		} catch (Exception error) {
			ApplicationException.checkAndThrow(error, "Error in initializing cache [%s]", this.name);
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
		switch (this.source) {
		case REST_API:
			loadAPISource(aCacheMap);
			break;
		case JDBC:
			loadJDBCSource(aCacheMap);
			break;
		default:
			throw new ApplicationException(
					"Cache source is set to custom. Either loadCache should be overridden or one of the setDataSource methods should be invoked");
		}
	}

	/**
	 * @param aCacheMap
	 */
	private void loadAPISource(DataMap aCacheMap) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(this.dataURL, String.class, this.urlParams);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode;

		try {
			rootNode = mapper.readTree(response.getBody());
		} catch (IOException error) {
			throw new ApplicationException(error);
		}

		if (!rootNode.isArray()) {
			throw new ApplicationException("API response is not a List of objects. Please verify");
		}

		final ObjectReader reader = mapper.readerFor(this.valueType);
		final Function<V, String> tmpKeyMapper = this.keyMapper;
		rootNode.forEach(aNode -> {
			V parsedObj;
			try {
				parsedObj = reader.readValue(aNode);
			} catch (IOException error) {
				throw new ApplicationException(error);
			}
			aCacheMap.put(tmpKeyMapper.apply(parsedObj), parsedObj);
		});
	}

	/**
	 * @param aCacheMap
	 */
	private void loadJDBCSource(DataMap aCacheMap) {
		if (StringUtils.isEmpty(this.dataQuery)) {
			return;
		}

		/*
		 * Query the database
		 */
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		List<Map<String, Object>> queryData = jdbcTemplate.queryForList(this.dataQuery, this.queryParams);
		queryData.parallelStream().filter(aRowMap -> this.recordFilter.apply(aRowMap))
				.forEach(aRowMap -> aCacheMap.put(this.keyProvider.apply(aRowMap), this.valueProvider.apply(aRowMap)));
	}

	/*
	 * Cache API
	 */
	/**
	 * @param aKey
	 * @return
	 */
	@ManagedOperation(description = "This method returns the cached value in raw form")
	public V get(String aKey) {
		return this.cacheMap.get(aKey);
	}

	/**
	 * This method returns the JSON form of value stored in cache. It uses
	 * {@link JacksonUtils#toJSON(Object)} to serialize the value
	 *
	 * @param aKey Key to be looked up in the cache
	 * @return JSON string for the value cached
	 * @throws ApplicationException thrown by {@link JacksonUtils#toJSON(Object)}
	 */
	@ManagedOperation(description = "This method returns the cached value in JSON format")
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
	@ManagedOperation(description = "This method returns the cached value in XML format")
	public String getXML(String aKey) throws ApplicationException {
		V value = get(aKey);
		if (value == null) {
			return EMPTY_STRING;
		}

		return JacksonUtils.toXML(value);
	}

	/**
	 * This method returns the YAML form of value stored in cache. It uses
	 * {@link JacksonUtils#toYAML(Object)} to serialize the value
	 *
	 * @param aKey Key to be looked up in the cache
	 * @return YAML string for the value cached
	 * @throws ApplicationException thrown by {@link JacksonUtils#toYAML(Object)}
	 */
	@ManagedOperation(description = "This method returns the cached value in YAML format")
	public String getYAML(String aKey) throws ApplicationException {
		V value = get(aKey);
		if (value == null) {
			return EMPTY_STRING;
		}

		return JacksonUtils.toYAML(value);
	}

//	/**
//	 * @param aKey
//	 * @param aValue
//	 */
//	protected void put(String aKey, V aValue) {
//		this.cacheMap.put(aKey, aValue);
//	}
//
//	/**
//	 * @param aKey
//	 * @return
//	 */
//	protected V remove(String aKey) {
//		return this.cacheMap.remove(aKey);
//	}
//
//	/**
//	 * This method stores the given key-value pair in cache after converting them
//	 *
//	 * @param aKey         Key to be added/updated in the cache
//	 * @param aStringValue JSON representation of Value to be stored in the cache.
//	 * @throws ApplicationException thrown by
//	 *                              {@link JacksonUtils#fromJSON(String, Class)}
//	 */
//	@ManagedOperation(description = "This method stores the given key-value pair in cache after converting them")
//	public void putJSON(String aKey, String aStringValue) throws ApplicationException {
//		Assert.isTrue(!StringUtils.isEmpty(aStringValue), "JSON value cannot be empty");
//		if (this.valueType == String.class) {
//			put(aKey, this.valueType.cast(aStringValue));
//		} else {
//			put(aKey, JacksonUtils.fromJSON(aStringValue, this.valueType));
//		}
//	}
//
//	/**
//	 * This method stores the given key-value pair in cache after converting them
//	 *
//	 * @param aKey         Key to be added/updated in the cache
//	 * @param aStringValue XML representation of Value to be stored in the cache.
//	 * @throws ApplicationException thrown by
//	 *                              {@link JacksonUtils#fromXML(String, Class)}
//	 */
//	@ManagedOperation(description = "This method stores the given key-value pair in cache after converting them")
//	public void putXML(String aKey, String aStringValue) throws ApplicationException {
//		Assert.isTrue(!StringUtils.isEmpty(aStringValue), "XML value cannot be empty");
//		if (this.valueType == String.class) {
//			put(aKey, this.valueType.cast(aStringValue));
//		} else {
//			put(aKey, JacksonUtils.fromXML(aStringValue, this.valueType));
//		}
//	}
//
//	/**
//	 * This method stores the given key-value pair in cache after converting them
//	 *
//	 * @param aKey         Key to be added/updated in the cache
//	 * @param aStringValue YAML representation of Value to be stored in the cache.
//	 * @throws ApplicationException thrown by
//	 *                              {@link JacksonUtils#fromYAML(String, Class)}
//	 */
//	@ManagedOperation(description = "This method stores the given key-value pair in cache after converting them")
//	public void putYAML(String aKey, String aStringValue) throws ApplicationException {
//		Assert.isTrue(!StringUtils.isEmpty(aStringValue), "YAML value cannot be empty");
//		if (this.valueType == String.class) {
//			put(aKey, this.valueType.cast(aStringValue));
//		} else {
//			put(aKey, JacksonUtils.fromYAML(aStringValue, this.valueType));
//		}
//	}

	/**
	 * This method refreshes the cache
	 *
	 * @throws ApplicationException thrown by {@link #loadCache(DataMap)}
	 */
	@ManagedOperation(description = "This method refreshes the cache")
	public void refresh() throws ApplicationException {
		LOGGER.debug("Refreshing Cache [{}]", this.name);

		this.status = CacheStatus.REFRESHING;

		// reload the cache
		DataMap tmpCache = DataMap.newMap();
		loadCache(tmpCache);
		this.cacheMap.clear();
		this.cacheMap.putAll(tmpCache);

		// update refresh time and age
		this.refreshedAt = new Date();

		// reset refresh monitor and wake all threads waiting for access
		this.status = CacheStatus.OK;

		// publish the refresh event
		this.applicationEventPublisher.publishEvent(new CacheLoadEvent<>(this, CacheEventType.REFRESH));

		LOGGER.info("Cache [{}] Refreshed", this.name);
	}

	/*
	 * Cache info methods
	 */
	/**
	 * Getter method for "name" property
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter method for "valueType" property
	 * 
	 * @return valueType
	 */
	public Class<V> getValueType() {
		return this.valueType;
	}

	/**
	 * Getter method for "status" property
	 * 
	 * @return status
	 */
	public CacheStatus getStatus() {
		return this.status;
	}

	/**
	 * Getter method for "expiration" property
	 * 
	 * @return expiration
	 */
	public String getExpiration() {
		return this.expiration;
	}

	/**
	 * Getter method for "expiryTime" property
	 * 
	 * @return expiryTime
	 */
	public long getExpiryTime() {
		return this.expiryTime;
	}

	/**
	 * Getter method for "initializedAt" property
	 * 
	 * @return initializedAt
	 */
	public Date getInitializedAt() {
		return this.initializedAt;
	}

	/**
	 * Getter method for "refreshedAt" property
	 * 
	 * @return refreshedAt
	 */
	public Date getRefreshedAt() {
		return this.refreshedAt;
	}

	/**
	 * Getter method for "source" property
	 * 
	 * @return source
	 */
	public CacheSource getSource() {
		return this.source;
	}

	/**
	 * Method to get list of cached keys
	 * 
	 * @return list of cached keys
	 */
	public List<String> keys() {
		return new ArrayList<>(this.cacheMap.keySet());
	}

	/**
	 * Method to get size of cache
	 * 
	 * @return size of cache
	 */
	@JsonProperty
	public int size() {
		return this.cacheMap.size();
	}

	/**
	 * @return
	 */
	@JsonProperty
	public String age() {
		return DateTimeUtils.convertToTime(
				(this.refreshedAt == null) ? 0 : System.currentTimeMillis() - this.refreshedAt.getTime());
	}

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
		return JacksonUtils.toJSON(this);
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
		if (this.status != CacheStatus.OK || this.expiryTime < 0) {
			return;
		}

		if ((System.currentTimeMillis() - this.refreshedAt.getTime()) >= this.expiryTime) {
			/*
			 * synchronized block to prevent multiple refreshes
			 */
			synchronized (this) {
				if ((System.currentTimeMillis() - this.refreshedAt.getTime()) > this.expiryTime) {
					refresh();
				}
			}
		}
	}

	/**
	 * This method calculates the cache duration to determine when the cache is due
	 * to be refreshed from the data store
	 */
	private void calculateExpiryTime() {
		if (StringUtils.isEmpty(this.expiration)) {
			this.expiryTime = -1;
		} else {
			String[] tokens = StringUtils.split(this.expiration, CommonConstants.SPACE);
			this.expiryTime = TimeUnit.valueOf(tokens[1]).toMillis(Long.parseLong(tokens[0]));

			LOGGER.debug("Cache Age [{}] translated to [{}] milliseconds", this.expiration, this.expiryTime);
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
		OK,
		/**
		 * Status while the cache is being refreshed
		 */
		REFRESHING
	}

	/**
	 * ENUM for cache source values
	 * 
	 * @version 1.0 Initial Version
	 * @author Rohit Narayanan
	 * @since November 09, 2018
	 */
	enum CacheSource {
		/**
		 * The source of the cache is an HTTP REST_API
		 */
		REST_API,
		/**
		 * The source of the cache is a JDBC data store
		 */
		JDBC,
		/**
		 * The cache will be loaded by custom implementation provided by sub-classes
		 */
		CUSTOM
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