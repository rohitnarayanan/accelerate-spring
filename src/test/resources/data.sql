INSERT INTO TestDataBean_Store (bean_name, cache_key) VALUES
	('TestBeanName1', 'cacheKey1'),
	('TestBeanName2', 'cacheKey2'),
	('TestBeanName3', 'cacheKey3');


INSERT INTO PropertyCache_Store (property_key, property_value, environment) VALUES
	('key1', 'value1', 'all'),
	('key2', 'value2', 'all'),
	('dev.key1', 'dev.value1', 'dev'),
	('dev.key2', 'dev.value2.override', 'dev'),
	('dev.key3', 'dev.value3', 'dev');