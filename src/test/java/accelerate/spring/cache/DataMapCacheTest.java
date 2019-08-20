package accelerate.spring.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.function.Function;

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
 * P{@link Test} class for {@link DataMapCache}
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
	@Test
	void testSetCacheSourceAPI() {
		assertTrue(true, "To be implemented for " + this.basicDataMapCache.getName());
	}

	/**
	 * Test method for
	 * {@link DataMapCache#setCacheSource(javax.sql.DataSource, String, Function, Function, Function, Object[])}.
	 */
	@Test
	void testSetCacheSourceJDBC() {
		assertTrue(true, "To be implemented for " + this.basicDataMapCache.getName());
	}

	/**
	 * Test method for {@link DataMapCache#setExpiration(String)}.
	 */
	@Test
	void testSetExpiration() {
		this.basicDataMapCache.setExpiration("8 HOURS");

		assertEquals("8 HOURS", this.basicDataMapCache.getExpiration());
		assertTrue(this.basicDataMapCache.getExpiryTime() > 0);

		this.basicDataMapCache.setExpiration(null);
	}

	/**
	 * Test method for {@link DataMapCache#loadCache(DataMap)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	void testLoadCache() {
		assertThrows(ApplicationException.class,
				() -> new DataMapCache<>("testGetStatus", Object.class).loadCache(null));
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
		assertEquals(new TestDataBean(1).toJSON(), this.basicDataMapCache.getJSON("cacheKey1"));
	}

	/**
	 * Test method for {@link DataMapCache#getXML(String)}.
	 */
	@Test
	void testGetXML() {
		assertEquals(new TestDataBean(1).toXML(), this.basicDataMapCache.getXML("cacheKey1"));
	}

	/**
	 * Test method for {@link DataMapCache#getYAML(String)}.
	 */
	@Test
	void testGetYAML() {
		assertEquals(new TestDataBean(1).toYAML(), this.basicDataMapCache.getYAML("cacheKey1"));
	}

	/**
	 * Test method for {@link DataMapCache#refresh()}.
	 */
	@Test
	void testRefresh() {
		assertTrue(true, "To be implemented for " + this.basicDataMapCache.getName());
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
		assertEquals(CacheStatus.NEW, new DataMapCache<>("testGetStatus", Object.class).getStatus());
	}

	/**
	 * Test method for {@link DataMapCache#getExpiration()}.
	 */
	@Test
	void testGetExpiration() {
		assertTrue(StringUtils.isEmpty(this.basicDataMapCache.getExpiration()));
		// remaining logic tested in testSetExpiration()
	}

	/**
	 * Test method for {@link DataMapCache#getExpiryTime()}.
	 */
	@Test
	void testGetExpiryTime() {
		assertEquals(-1, this.basicDataMapCache.getExpiryTime());
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