/* DataMapCacheTest */
DROP TABLE IF EXISTS TestDataBean_Store;
 
CREATE TABLE TestDataBean_Store (
	bean_id INT AUTO_INCREMENT PRIMARY KEY,
	bean_name VARCHAR(250) NOT NULL,
	cache_key VARCHAR(250) NOT NULL
);


/* PropertyCacheTest */
DROP TABLE IF EXISTS PropertyCache_Store;
 
CREATE TABLE PropertyCache_Store (
	property_key VARCHAR(250) PRIMARY KEY,
	property_value VARCHAR(250) NOT NULL,
	environment VARCHAR(250) NOT NULL
);