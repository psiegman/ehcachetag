package nl.siegmann.ehcachetag;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class TagCacheManager {

	public static Object getCachedValue(String cacheName, Object cacheKey) {
		Element cacheElement = CacheManager.getInstance().getCache(cacheName).get(cacheKey);
		if (cacheElement == null) {
			return null;
		}
		return cacheElement.getObjectValue();
	}

	public static void updateCache(String cacheName, Object cacheKey, Object cacheValue) {
		CacheManager.getInstance().getCache(cacheName).put(new Element(cacheKey, cacheValue));
	}
}
