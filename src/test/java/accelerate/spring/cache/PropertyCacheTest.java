package accelerate.spring.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jayway.jsonpath.JsonPath;

import accelerate.commons.exception.ApplicationException;

/**
 * {@link Test} class for {@link PropertyCache}
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 19, 2019
 */
@SpringBootTest
class PropertyCacheTest {
	/**
	 * {@link TestCacheConfiguration#basicPropertyCache()} instance
	 */
	@Autowired
	private PropertyCache basicPropertyCache = null;

	/**
	 * {@link TestCacheConfiguration#profilePropertyCache()} instance
	 */
	@Autowired
	private PropertyCache profilePropertyCache = null;

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.PropertyCache#loadCache(accelerate.commons.data.DataMap)}.
	 */
	@Test
	void testLoadCache() {
		assertEquals(7, this.basicPropertyCache.size());
		assertEquals(2, this.profilePropertyCache.size());

		assertThrows(ApplicationException.class, () -> new PropertyCache("NoSource").loadCache(null));

		assertEquals("dev", JsonPath.parse(this.profilePropertyCache.toString()).read("$.profileName"));
	}

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.PropertyCache#PropertyCache(String)}.
	 */
	@Test
	void testPropertyCacheString() {
		assertEquals("testPropertyCacheString", new PropertyCache("testPropertyCacheString").getName());
		assertEquals("basicPropertyCache", this.basicPropertyCache.getName());
	}

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.PropertyCache#PropertyCache(String, String)}.
	 */
	@Test
	void testPropertyCacheStringString() {
		assertEquals("basicPropertyCache", this.basicPropertyCache.getName());
		assertTrue(this.basicPropertyCache.getConfigURL().endsWith(".properties"));
	}

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.PropertyCache#PropertyCache(String, String, String)}.
	 */
	@Test
	void testPropertyCacheStringStringString() {
		assertEquals("profilePropertyCache", this.profilePropertyCache.getName());
		assertTrue(this.profilePropertyCache.getConfigURL().endsWith(".properties"));
		assertEquals("dev", this.profilePropertyCache.getProfileName());
	}

	/**
	 * Test method for {@link accelerate.spring.cache.PropertyCache#get(String[])}.
	 */
	@Test
	void testGetStringArray() {
		assertEquals("value3", this.basicPropertyCache.get("key3", "innerKey"));
	}

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.PropertyCache#getPropertyList(String[])}.
	 */
	@Test
	void testGetPropertyList() {
		assertEquals(3, this.basicPropertyCache.getPropertyList("key4").length);
	}

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.PropertyCache#isTrue(String[])}.
	 */
	@Test
	void testIsTrue() {
		assertTrue(this.basicPropertyCache.isTrue("key5"));
	}

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.PropertyCache#hasValue(String, String[])}.
	 */
	@Test
	void testHasValue() {
		assertTrue(this.basicPropertyCache.hasValue("value3", "key3", "innerKey"));
	}

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.PropertyCache#setCacheSource(DataSource, String, String, String, Object[])}.
	 */
	@Test
	void testSetCacheSource() {
		assertTrue(true, "To be implemented for " + this.basicPropertyCache.getName());
	}

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.PropertyCache#getProfileName()}.
	 */
	@Test
	void testGetProfileName() {
		assertNull(this.basicPropertyCache.getProfileName());
		assertEquals("dev", this.profilePropertyCache.getProfileName());
	}

	/**
	 * Test method for {@link accelerate.spring.cache.PropertyCache#getConfigURL()}.
	 */
	@Test
	void testGetConfigURL() {
		assertTrue(this.basicPropertyCache.getConfigURL().endsWith(".properties"));
	}
}