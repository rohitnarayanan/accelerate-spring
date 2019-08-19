package accelerate.spring.cache;

import static accelerate.commons.constant.CommonConstants.COMMA;
import static accelerate.commons.constant.CommonConstants.EMPTY_STRING;
import static accelerate.commons.constant.CommonConstants.PERIOD;

import java.util.List;
import java.util.function.Function;

import javax.sql.DataSource;

import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import accelerate.commons.data.DataMap;
import accelerate.commons.exception.ApplicationException;
import accelerate.commons.util.ConfigurationUtils;
import accelerate.commons.util.StringUtils;

/**
 * <p>
 * This class provides an implementation for {@link DataMapCache} to store
 * configuration properties in the form of key-value pairs
 * </p>
 * <p>
 * The properties can be read from a file viw HTTP URL/local file
 * system/classpath. Currently only Properties format is supported.
 * </p>
 * <p>
 * Properties can also be pulled from a database table and the user will have to
 * provide the data source and query via the
 * {@link #setCacheSource(DataSource, String, String, String, Object...)}
 * method. For more complex implementation refer to
 * {@link DataMapCache#setCacheSource(DataSource, String, Function, Function, Function, Object[])}
 * </p>
 * <p>
 * The source (file/db table) can have properties for multiple environments.
 * Just set the {@link #profileName} property and only those property prefixed
 * by it will be loaded.
 * </p>
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 19, 2019
 */
public class PropertyCache extends DataMapCache<String> {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The attribute contains the URL to the property file. The URL can be in any
	 * format supported by the {@link PropertiesUtil} class.
	 */
	private String configURL;

	/**
	 * Name of the profile for which to cache the properties. Usually properties
	 * would be in the format of <profileName>.x.y.z=value. It will be cached as
	 * x.y.z=value
	 * <p>
	 * Allows to store same properties with different values.
	 */
	private String profileName;

	/**
	 * Default Constructor
	 *
	 * @param aCacheName
	 */
	public PropertyCache(String aCacheName) {
		super(aCacheName, String.class);
	}

	/**
	 * Constructor to provide URL to load configuration from
	 *
	 * @param aCacheName
	 * @param aConfigURL
	 */
	public PropertyCache(String aCacheName, String aConfigURL) {
		this(aCacheName);
		this.configURL = aConfigURL;
	}

	/**
	 * Constructor to provide URL to load configuration from
	 *
	 * @param aCacheName
	 * @param aConfigURL
	 * @param aProfileName
	 */
	public PropertyCache(String aCacheName, String aConfigURL, String aProfileName) {
		this(aCacheName);
		this.configURL = aConfigURL;
		this.profileName = aProfileName;
	}

	/**
	 * This method creates a "." seperated key from the array of tokens passed and
	 * delegates to the {@link #get(String)} method for lookup.
	 *
	 * @param aPropertyKeys array containing string to be concatenated to form the
	 *                      property key
	 * @return {@link String} value stored against the key
	 */
	@ManagedOperation(description = "This method returns the element stored in cache against the given key")
	public String get(String... aPropertyKeys) {
		return super.get(String.join(PERIOD, aPropertyKeys));
	}

	/**
	 * This method get the property value using {@link #get(String...)} and then
	 * return a {@link List} of tokens by spliting the value by ','.
	 *
	 * @param aPropertyKeys array of strings to be concatenated to form the property
	 *                      key
	 * @return array of values
	 */
	public String[] getPropertyList(String... aPropertyKeys) {
		String value = get(aPropertyKeys);
		return StringUtils.isEmpty(value) ? new String[] {} : value.split(COMMA);
	}

	/**
	 * This method uses the {@link #get(String...)} method to get the property value
	 * and checks if the value is "true".
	 *
	 * @param aPropertyKeys array containing string to be concatenated to form the
	 *                      property key
	 * @return boolean result of the comparison
	 */
	public boolean isTrue(String... aPropertyKeys) {
		return hasValue(Boolean.TRUE.toString(), aPropertyKeys);
	}

	/**
	 * This method uses the {@link #get(String...)} method to get the property value
	 * and checks if the value equals to aCompareValue
	 *
	 * @param aCompareValue value to be compared against
	 * @param aPropertyKeys array of strings to be concatenated to form the property
	 *                      key
	 * @return boolean result of the comparison
	 */
	public boolean hasValue(String aCompareValue, String... aPropertyKeys) {
		return StringUtils.equals(get(aPropertyKeys), aCompareValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.spring.cache.DataMapCache#loadCache(DataMap)
	 */
	/**
	 * @param aCacheMap
	 * @throws ApplicationException
	 */
	@Override
	protected void loadCache(DataMap aCacheMap) throws ApplicationException {
		if (StringUtils.isEmpty(this.configURL) && getSource() != CacheSource.JDBC) {
			throw new ApplicationException("No cache source provided");
		}

		final boolean profileEnabled = !StringUtils.isEmpty(this.profileName);
		final String profilePrefix = profileEnabled ? this.profileName + PERIOD : EMPTY_STRING;
		final int prefixLength = profileEnabled ? profilePrefix.length() : 0;

		if (!StringUtils.isEmpty(this.configURL)) {
			ConfigurationUtils.loadPropertyFile(this.configURL).entrySet().parallelStream()
					.filter(aEntry -> profileEnabled ? aEntry.getKey().startsWith(profilePrefix) : true)
					.forEach(aEntry -> aCacheMap.put(aEntry.getKey().substring(prefixLength), aEntry.getValue()));
		}

		super.loadCache(aCacheMap);
	}

	/**
	 * @param aDataSource
	 * @param aDataQuery
	 * @param aKeyColumn   Name of the query column containing property key.
	 *                     Defaults to "KEY"
	 * @param aValueColumn Name of the query column containing property value.
	 *                     Defaults to "VALUE"
	 * @param aQueryParams
	 */
	public void setCacheSource(DataSource aDataSource, String aDataQuery, String aKeyColumn, String aValueColumn,
			Object... aQueryParams) {
		final boolean profileEnabled = !StringUtils.isEmpty(this.profileName);
		final String profilePrefix = profileEnabled ? this.profileName + PERIOD : EMPTY_STRING;
		final String keyColumn = !StringUtils.isEmpty(aKeyColumn) ? aKeyColumn : "KEY";
		final String valueColumn = !StringUtils.isEmpty(aValueColumn) ? aValueColumn : "VALUE";

		super.setCacheSource(aDataSource, aDataQuery,
				aRowData -> profileEnabled ? aRowData.get(keyColumn).toString().startsWith(profilePrefix) : true,
				aRowData -> aRowData.get(keyColumn).toString(), aRowData -> aRowData.get(valueColumn).toString(),
				aQueryParams);
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see accelerate.spring.cache.DataMapCache#toString()
//	 */
//	/**
//	 * @return
//	 */
//	@Override
//	public String toString() {
//		ObjectMapper objectMapper = JacksonUtils.objectMapper();
//		objectMapper.addMixIn(PropertyCache.class, PropertyCacheMixIn.class);
//
//		return JacksonUtils.toJSON(objectMapper, this);
//	}

//	/**
//	 * Jackson MixIn to serialize relevant fields
//	 * 
//	 * @version 1.0 Initial Version
//	 * @author Rohit Narayanan
//	 * @since November 10, 2018
//	 */
//	@SuppressWarnings("hiding")
//	abstract class PropertyCacheMixIn extends DataMapCacheMixIn {
//		/**
//		 */
//		@JsonProperty("configURL")
//		public String configURL;
//
//		/**
//		 */
//		@JsonProperty("profileName")
//		public CacheStatus profileName;
//	}

	/**
	 * Getter method for "profileName" property
	 *
	 * @return profileName
	 */
	@ManagedAttribute
	public String getProfileName() {
		return this.profileName;
	}

	/**
	 * Getter method for "configURL" property
	 *
	 * @return configURL
	 */
	@ManagedAttribute
	public String getConfigURL() {
		return this.configURL;
	}
}