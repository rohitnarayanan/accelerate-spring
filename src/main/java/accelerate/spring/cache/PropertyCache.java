package accelerate.spring.cache;

import static accelerate.commons.constant.CommonConstants.COMMA;
import static accelerate.commons.constant.CommonConstants.EMPTY_STRING;
import static accelerate.commons.constant.CommonConstants.PERIOD;

import java.util.List;

import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import accelerate.commons.exception.ApplicationException;
import accelerate.commons.util.CommonUtils;
import accelerate.commons.util.ConfigurationUtils;
import accelerate.commons.util.StringUtils;

/**
 * <p>
 * This class provides an implementation for {@link DataMapCache} to store
 * configuration properties in the form of key-value pairs
 * </p>
 * <p>
 * The properties can also be defined in a database table and the user will have
 * to provide the data source and query via {@link DataMapCache} methods. The
 * query should ensure that the key column is the first column in the SELECT
 * clause followed by the value column. For more complex implementation refer to
 * {@link DataMapCache#recordFilter} / {@link DataMapCache#keyProvider} /
 * {@link DataMapCache#valueProvider}
 * </p>
 * <p>
 * Users also have the option of saving the properties for multiple environments
 * and profiles in the same table. All they need to do is to set the
 * {@link #profileName} property and it will be taken care of.
 * </p>
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 2, 2017
 */
public class PropertyCache extends DataMapCache<String> {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the profile for which to cache the properties. Usually properties
	 * would be in the format of <profileName>.x.y.z=value. It will be cached as
	 * x.y.z=value
	 * <p>
	 * Allows to store same properties with different values.
	 */
	private String profileName;

	/**
	 * The attribute contains the URL to the property file. The URL can be in any
	 * format supported by the {@link PropertiesUtil} class.
	 */
	private String configURL;

	/**
	 * Default Constructor
	 *
	 * @param aCacheName
	 */
	public PropertyCache(String aCacheName) {
		super(aCacheName, String.class);
	}

	/**
	 * Overloaded Constructor
	 *
	 * @param aCacheName
	 * @param aConfigURL
	 */
	public PropertyCache(String aCacheName, String aConfigURL) {
		this(aCacheName);
		this.configURL = aConfigURL;
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
		return (value == null) ? new String[] {} : value.split(COMMA);
	}

	/**
	 * This method uses the {@link #get(String...)} method to get the property value
	 * and checks if the value is "true".
	 *
	 * @param aPropertyKeys array containing string to be concatenated to form the
	 *                      property key
	 * @return boolean result of the comparison
	 */
	public boolean isEnabled(String... aPropertyKeys) {
		return hasValue(Boolean.TRUE.toString(), aPropertyKeys);
	}

	/**
	 * This method creates a "." separated key from the array of tokens passed and
	 * compares the property value against a user specified constant.
	 *
	 * @param aCompareValue value to be compared against
	 * @param aPropertyKeys array of strings to be concatenated to form the property
	 *                      key
	 * @return boolean result of the comparison
	 */
	public boolean hasValue(String aCompareValue, String... aPropertyKeys) {
		return CommonUtils.compare(get(aPropertyKeys), aCompareValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerate.spring.cache.DataMapCache#loadCache(java.util.Map)
	 */
	/**
	 * @throws ApplicationException
	 */
	@Override
	protected void loadCache() throws ApplicationException {
		final String prefix = StringUtils.isEmpty(this.profileName) ? EMPTY_STRING : this.profileName + PERIOD;
		final int prefixLength = prefix.length();

		if (!StringUtils.isEmpty(this.configURL)) {
			ConfigurationUtils.loadPropertyFile(this.configURL).forEach((aKey, aValue) -> {
				if (aKey.startsWith(prefix)) {
					put(aKey.substring(prefixLength), (String) aValue);
				}
			});
		}

		/*
		 * If configQuery is not set or db loading is disabled in the property file then
		 * skip loading properties from database.
		 */
		if (!StringUtils.isEmpty(this.dataQuery)) {
			/*
			 * Query the database
			 */
			JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
			jdbcTemplate.query(this.dataQuery, (aResultSet, aRowNum) -> {
				String key = aResultSet.getString(1);
				if (key.startsWith(prefix)) {
					put(key.substring(prefixLength), aResultSet.getString(2));
				}

				return null;
			});
		}

		setCacheAge(get("cacheAge"));
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

	/**
	 * Setter method for "configURL" property
	 *
	 * @param aConfigURL
	 */
	@ManagedAttribute
	public void setConfigURL(String aConfigURL) {
		this.configURL = aConfigURL;
	}

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
	 * Setter method for "profileName" property
	 *
	 * @param aProfileName
	 */
	@ManagedAttribute
	public void setProfileName(String aProfileName) {
		this.profileName = aProfileName;
	}
}