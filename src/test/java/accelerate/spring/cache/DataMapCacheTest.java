package accelerate.spring.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.function.Function;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jayway.jsonpath.JsonPath;

import accelerate.commons.data.DataMap;
import accelerate.commons.exception.ApplicationException;
import accelerate.commons.util.StringUtils;
import accelerate.spring.cache.DataMapCache.CacheSource;
import accelerate.spring.cache.DataMapCache.CacheStatus;

/**
 * {@link Test} class for {@link DataMapCache}
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 19, 2019
 */
@SpringBootTest
class DataMapCacheTest {
	/**
	 * {@link TestCacheConfiguration#basicDataMapCache()} instance
	 */
	@Autowired
	private DataMapCache<TestDataBean> basicDataMapCache = null;

	/**
	 * {@link TestCacheConfiguration#apiDataMapCache()} instance
	 */
	@Autowired
	private DataMapCache<TestDataBean> apiDataMapCache = null;

	/**
	 * {@link TestCacheConfiguration#jdbcDataMapCache(DataSource)} instance
	 */
	@Autowired
	private DataMapCache<TestDataBean> jdbcDataMapCache = null;

	/**
	 * Test method for {@link DataMapCache#DataMapCache(String, Class)}.
	 */
	@Test
	void testDataMapCache() {
		assertEquals("basicDataMapCache", this.basicDataMapCache.getName());
		assertEquals(TestDataBean.class, this.basicDataMapCache.getValueType());
	}

	/**
	 * Test method for
	 * {@link DataMapCache#setCacheSource(String, Function, Object[])}.
	 */
	@SuppressWarnings("static-method")
	@Test
	void testSetCacheSourceAPI() {
		DataMapCache<Object> testCache = new DataMapCache<>("testCache", Object.class);
		testCache.setCacheSource("testSetCacheSourceAPI", null);

		assertEquals(CacheSource.REST_API, testCache.getSource());
		assertEquals("testSetCacheSourceAPI", JsonPath.parse(testCache.toString()).read("$.dataURL"));

		// remaining logic tested in testLoadCache()
	}

	/**
	 * Test method for
	 * {@link DataMapCache#setCacheSource(javax.sql.DataSource, String, Function, Function, Function, Object[])}.
	 */
	@SuppressWarnings("static-method")
	@Test
	void testSetCacheSourceJDBC() {
		DataMapCache<Object> testCache = new DataMapCache<>("testCache", Object.class);
		testCache.setCacheSource(null, "testSetCacheSourceJDBC", null, null, null);

		assertEquals(CacheSource.JDBC, testCache.getSource());
		assertEquals("testSetCacheSourceJDBC", JsonPath.parse(testCache.toString()).read("$.dataQuery"));

		// remaining logic tested in testLoadCache()
	}

	/**
	 * Test method for {@link DataMapCache#setExpiration(String)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	void testSetExpiration() {
		DataMapCache<Object> testCache = new DataMapCache<>("testCache", Object.class);
		testCache.setExpiration("8 HOURS");

		assertEquals("8 HOURS", testCache.getExpiration());
		assertEquals(8 * 60 * 60 * 1000, testCache.getExpiryTime());
	}

	/**
	 * Test method for {@link DataMapCache#loadCache(DataMap)}.
	 */
	@Test
	void testLoadCache() {
		// No cache source provided
		assertThrows(ApplicationException.class, () -> new DataMapCache<>("testCache", Object.class).loadCache(null));

		// Invalid API source
		DataMapCache<Object> testCache = new DataMapCache<>("testCache", Object.class);
		testCache.setCacheSource(
				"https://raw.githubusercontent.com/rohitnarayanan/accelerate-spring/release_2/src/test/resources/accelerate/spring/cache/DataMapCacheTest-invalid.json",
				null);
		assertEquals("API response is not a JSON List",
				assertThrows(ApplicationException.class, () -> testCache.loadCache(null)).getMessage());

		// successfully loaded API source
		assertEquals(1, this.apiDataMapCache.get("cacheKey1").getBeanId());

		// successfully loaded JDBC source
		assertEquals(2, this.jdbcDataMapCache.size());
		assertEquals(2, this.jdbcDataMapCache.get("cacheKey2").getBeanId());
	}

	/**
	 * Test method for {@link DataMapCache#get(String)}.
	 */
	@Test
	void testGet() {
		assertEquals(1, this.basicDataMapCache.get("cacheKey1").getBeanId());
	}

	/**
	 * Test method for {@link DataMapCache#getJSON(String)}.
	 */
	@Test
	void testGetJSON() {
		assertTrue(StringUtils.isEmpty(this.basicDataMapCache.getJSON("INVALID")));
		assertEquals(new TestDataBean(1).toJSON(), this.basicDataMapCache.getJSON("cacheKey1"));
	}

	/**
	 * Test method for {@link DataMapCache#getXML(String)}.
	 */
	@Test
	void testGetXML() {
		assertTrue(StringUtils.isEmpty(this.basicDataMapCache.getXML("INVALID")));
		assertEquals(new TestDataBean(1).toXML(), this.basicDataMapCache.getXML("cacheKey1"));
	}

	/**
	 * Test method for {@link DataMapCache#getYAML(String)}.
	 */
	@Test
	void testGetYAML() {
		assertTrue(StringUtils.isEmpty(this.basicDataMapCache.getYAML("INVALID")));
		assertEquals(new TestDataBean(1).toYAML(), this.basicDataMapCache.getYAML("cacheKey1"));
	}

	/**
	 * Test method for {@link DataMapCache#refresh()}.
	 */
	@Test
	void testRefresh() {
		DataMapCache<Object> testCache = new DataMapCache<>("testCache", Object.class) {
			private static final long serialVersionUID = 1L;

			protected void loadCache(DataMap aCacheMap) throws ApplicationException {
				aCacheMap.add("key9", new TestDataBean(9));
			}
		};

		// refresh new cache
		assertFalse(testCache.refresh());

		// refresh non-refreshable cache
		assertFalse(this.apiDataMapCache.refresh());

		// refresh refreshable cache
		assertTrue(this.basicDataMapCache.refresh());
	}

	/**
	 * Test method for {@link DataMapCache#getName()}.
	 */
	@Test
	void testGetName() {
		assertEquals("basicDataMapCache", this.basicDataMapCache.getName());
	}

	/**
	 * Test method for {@link DataMapCache#getValueType()}.
	 */
	@Test
	void testGetValueType() {
		assertEquals(TestDataBean.class, this.basicDataMapCache.getValueType());
	}

	/**
	 * Test method for {@link DataMapCache#getStatus()}.
	 */
	@Test
	void testGetStatus() {
		assertEquals(CacheStatus.OK, this.basicDataMapCache.getStatus());
		assertEquals(CacheStatus.NEW, new DataMapCache<>("testCache", Object.class).getStatus());
	}

	/**
	 * Test method for {@link DataMapCache#getExpiration()}.
	 */
	@Test
	void testGetExpiration() {
		assertTrue(StringUtils.isEmpty(this.apiDataMapCache.getExpiration()));
		// remaining logic tested in testSetExpiration()
	}

	/**
	 * Test method for {@link DataMapCache#getExpiryTime()}.
	 */
	@Test
	void testGetExpiryTime() {
		assertEquals(-1, this.apiDataMapCache.getExpiryTime());
		// remaining logic tested in testSetExpiration()
	}

	/**
	 * Test method for {@link DataMapCache#getInitializedAt()}.
	 */
	@Test
	void testGetInitializedAt() {
		assertTrue(this.basicDataMapCache.getInitializedAt().before(new Date()));
	}

	/**
	 * Test method for {@link DataMapCache#getRefreshedAt()}.
	 */
	@Test
	void testGetRefreshedAt() {
		assertTrue(this.basicDataMapCache.getRefreshedAt().before(new Date()));
	}

	/**
	 * Test method for {@link DataMapCache#getSource()}.
	 */
	@Test
	void testGetSource() {
		assertEquals(CacheSource.CUSTOM, this.basicDataMapCache.getSource());
		// remaining logic tested in testSetCacheSourceAPI() / testSetCacheSourceJDBC()
	}

	/**
	 * Test method for {@link DataMapCache#keys()}.
	 */
	@Test
	void testKeys() {
		assertTrue(this.basicDataMapCache.keys().contains("cacheKey1"));
	}

	/**
	 * Test method for {@link DataMapCache#size()}.
	 */
	@Test
	void testSize() {
		assertTrue(this.basicDataMapCache.size() >= 4);
	}

	/**
	 * Test method for {@link DataMapCache#age()}.
	 */
	@Test
	void testAge() {
		assertThat(this.basicDataMapCache.age()).contains("0 hours", " minutes", " seconds");
	}

	/**
	 * Test method for {@link DataMapCache#toString()}.
	 */
	@Test
	void testToString() {
		assertEquals("basicDataMapCache", JsonPath.parse(this.basicDataMapCache.toString()).read("$.name"));
	}
}