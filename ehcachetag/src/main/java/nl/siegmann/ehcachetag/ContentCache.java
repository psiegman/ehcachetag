package nl.siegmann.ehcachetag;

import java.io.Serializable;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import nl.siegmann.ehcachetag.cachekeyfactories.CacheLocation;

/**
 * Wrapper around the CacheManager that makes it easier to mock the CacheManager.
 * 
 * @author paul
 *
 */
class ContentCache implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3991729685862770264L;

	public static final String NO_CACHED_VALUE = new String();
	
	public Object getContent(CacheLocation cacheLocation) {
		Element cacheElement = CacheManager.getInstance().getCache(cacheLocation.getCacheName()).get(cacheLocation.getCacheKey());
		if (cacheElement == null) {
			return NO_CACHED_VALUE;
		}
		return cacheElement.getObjectValue();
	}
	
	public void putContent(CacheLocation cacheLocation, String cacheValue) {
		CacheManager.getInstance().getCache(cacheLocation.getCacheName()).put(new Element(cacheLocation.getCacheKey(), cacheValue));
	}
}