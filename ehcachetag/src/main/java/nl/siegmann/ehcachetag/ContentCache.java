package nl.siegmann.ehcachetag;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Wrapper around the CacheManager that makes it easier to mock the CacheManager.
 * 
 * @author paul
 *
 */
class ContentCache {

	public static final String NO_CACHED_VALUE = new String();
	
	public Object getContent(String cacheName, Object cacheKey) {
		Element cacheElement = CacheManager.getInstance().getCache(cacheName).get(cacheKey);
		if (cacheElement == null) {
			return NO_CACHED_VALUE;
		}
		return cacheElement.getObjectValue();
	}
	
	public void putContent(String cacheName, Object cacheKey, String cacheValue) {
		CacheManager.getInstance().getCache(cacheName).put(new Element(cacheKey, cacheValue));
	}
}