package nl.siegmann.ehcachetag.cachekeyfactories;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Holds the location of an object in the cache.
 * 
 * It consists of the name of the cache and the key of the object within that cache.
 * 
 * @author paul
 *
 */
public class CacheLocation {
	private String cacheName;
	private Object cacheKey;

	public CacheLocation() {
		this(null, null);
	}
	
	public CacheLocation(Object cacheKey) {
		this(cacheKey, null);
	}
	
	public CacheLocation(Object cacheKey, String cacheName) {
		this.cacheKey = cacheKey;
		this.cacheName = cacheName;
	}
	
	/**
	 * The name of the cache.
	 * 
	 * @return
	 */
	public String getCacheName() {
		return cacheName;
	}
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	/**
	 * The key of the object within the cache.
	 * 
	 * @return
	 */
	public Object getCacheKey() {
		return cacheKey;
	}
	public void setCacheKey(Object cacheKey) {
		this.cacheKey = cacheKey;
	}
	
	public int hashCode() {
		return new HashCodeBuilder().append(cacheName).append(cacheKey).toHashCode();
	}
	
	public boolean equals(Object otherObject) {
		if (otherObject == null || (! (otherObject instanceof CacheLocation))) {
			return false;
		}
		return new EqualsBuilder().append(cacheKey, ((CacheLocation) otherObject).cacheKey).append(cacheName, ((CacheLocation) otherObject).cacheName).isEquals();
	}
	
	public String toString() {
		return StringUtils.defaultIfEmpty(cacheName, "<null>")
				+ ":"
				+ StringUtils.defaultIfEmpty(cacheKey.toString(), "<null>");
	}
}
