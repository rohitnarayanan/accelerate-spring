package accelerate.spring.cache;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationListener;

import accelerate.spring.cache.CacheLoadEvent.CacheEventType;

/**
 * {@link ApplicationListener} for {@link CacheLoadEventTest}
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since April 23, 2018
 */
@TestComponent
public class TestCacheEventListener implements ApplicationListener<CacheLoadEvent<?>> {
	/**
	 * {@link Set} containing cache names
	 */
	protected Set<CacheLoadEvent<?>> cacheSet = new HashSet<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.
	 * springframework.context.ApplicationEvent)
	 */
	/**
	 * @param aEvent
	 */
	@Override
	public void onApplicationEvent(CacheLoadEvent<?> aEvent) {
		if (aEvent.getCacheEventType() == CacheEventType.INIT) {
			this.cacheSet.add(aEvent);
		}
	}
}