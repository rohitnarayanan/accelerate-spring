package accelerate.spring.cache;

import static accelerate.commons.constant.CommonConstants.COMMA;
import static accelerate.commons.constant.CommonConstants.EMPTY_STRING;
import static accelerate.commons.constant.CommonConstants.PERIOD;

import java.util.List;

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
 * system/classpath. Currently Properties or YAML formats are supported.
 * </p>
 * <p>
 * Properties can also be pulled from a database table and the user will have to
 * provide the data source and query via the
 * {@link #setDataSourceAndQuery(DataSource, String)} method. The query should
 * ensure that the key column is the first column in the SELECT clause followed
 * by the value column. For more complex implementation refer to
 * {@link DataMapCache#setDataProviders(DataSource, String, java.util.function.Function, java.util.function.Function)}
 * </p>
 * <p>
 * The source (file/db table) can have properties for multiple environments.
 * Just set the {@link #profileName} property and only those property prefixed
 * by it will be loaded.
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
	 * The attribute enables YAML parsing for {@link #configURL}.
	 */
	private boolean yamlMode;

	/**
	 * Name of the query column in
	 * {@link #setDataSourceAndQuery(DataSource, String)} that contains the property
	 * key. Defaults to "KEY"
	 */
	private String keyColumnName = "KEY";
	
	/**
	 * Name of the query column in
	 * {@link #setDataSourceAndQuery(DataSource, String)} that contains the property
	 * value. Defaults to "VALUE"
	 */
	private String valueColumnName = "VALUE";

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
	 * Constructor to enable YAML source and provide URL to load configuration from
	 *
	 * @param aCacheName
	 * @param aConfigURL
	 * @param aEnableYAML
	 */
	public PropertyCache(String aCacheName, String aConfigURL, boolean aEnableYAML) {
		this(aCacheName);
		this.configURL = aConfigURL;
		this.yamlMode = aEnableYAML;
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
	 * @see accelerate.spring.cache.DataMapCache#loadCache(java.util.Map)
	 */
	/**
	 * @throws ApplicationException
	 */
	@Override
	protected void loadCache() throws ApplicationException {
		final boolean profileEnabled = !StringUtils.isEmpty(this.profileName);
		final String profilePrefix = profileEnabled ? this.profileName + PERIOD : EMPTY_STRING;
		final int prefixLength = profileEnabled ? profilePrefix.length() : 0;

		if (!StringUtils.isEmpty(this.configURL)) {
			DataMap configMap = this.yamlMode ? ConfigurationUtils.loadYAMLFile(this.configURL)
					: ConfigurationUtils.loadPropertyFile(this.configURL);
			configMap.entrySet().parallelStream()
					.filter(aEntry -> profileEnabled ? aEntry.getKey().startsWith(profilePrefix) : true)
					.forEach(aEntry -> put(aEntry.getKey().substring(prefixLength), (String) aEntry.getValue()));

		}

		super.loadCache();
	}

	/**
	 * @param aDataSource
	 * @param aDataQuery
	 */
	public void setDataSourceAndQuery(DataSource aDataSource, String aDataQuery) {
		super.setDataProviders(aDataSource, aDataQuery, aRowData -> aRowData.get(this.keyColumnName).toString(),
				aRowData -> aRowData.get(this.valueColumnName).toString());
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
	 * Getter method for "yamlMode" property
	 * 
	 * @return yamlMode
	 */
	public boolean isYamlMode() {
		return this.yamlMode;
	}

	/**
	 * Setter method for "yamlMode" property
	 * 
	 * @param aYamlMode
	 */
	public void setYamlMode(boolean aYamlMode) {
		this.yamlMode = aYamlMode;
	}

	/**
	 * Getter method for "keyColumnName" property
	 * 
	 * @return keyColumnName
	 */
	public String getKeyColumnName() {
		return this.keyColumnName;
	}

	/**
	 * Setter method for "keyColumnName" property
	 * 
	 * @param aKeyColumnName
	 */
	public void setKeyColumnName(String aKeyColumnName) {
		this.keyColumnName = aKeyColumnName;
	}

	/**
	 * Getter method for "valueColumnName" property
	 * 
	 * @return valueColumnName
	 */
	public String getValueColumnName() {
		return this.valueColumnName;
	}

	/**
	 * Setter method for "valueColumnName" property
	 * 
	 * @param aValueColumnName
	 */
	public void setValueColumnName(String aValueColumnName) {
		this.valueColumnName = aValueColumnName;
	}
}