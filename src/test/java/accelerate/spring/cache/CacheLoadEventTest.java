package accelerate.spring.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import accelerate.spring.cache.CacheLoadEvent.CacheEventType;

/**
 * {@link Test} class for {@link CacheLoadEvent}
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 12, 2019
 */
@SpringBootTest
class CacheLoadEventTest {
	/**
	 * {@link TestCacheEventListener} instance
	 */
	@Autowired
	private TestCacheEventListener listener = null;

	/**
	 * {@link TestCacheConfiguration#basicPropertyCache()} instance
	 */
	@Autowired
	private PropertyCache basicPropertyCache = null;

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.CacheLoadEvent#CacheLoadEvent(accelerate.spring.cache.DataMapCache, accelerate.spring.cache.CacheLoadEvent.CacheEventType)}.
	 */
	@Test
	void testCacheLoadEvent() {
		CacheLoadEvent<String> event = new CacheLoadEvent<>(this.basicPropertyCache, CacheEventType.INIT);
		assertTrue(event.getSource() instanceof PropertyCache);
		assertTrue(event.getTimestamp() <= System.currentTimeMillis());
	}

	/**
	 * Test method for {@link accelerate.spring.cache.CacheLoadEvent#getCache()}.
	 */
	@Test
	void testGetCache() {
		assertTrue(this.listener.cacheSet.size() > 0);
		assertTrue(this.listener.cacheSet.iterator().next().getCache() instanceof PropertyCache);
	}

	/**
	 * Test method for
	 * {@link accelerate.spring.cache.CacheLoadEvent#getCacheEventType()}.
	 */
	@Test
	void testGetCacheEventType() {
		assertEquals(this.listener.cacheSet.iterator().next().getCacheEventType(), CacheEventType.INIT);
	}
}
