package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.CacheTag;

/**
 * Sets the cache key to null, ensuring no caching
 * 
 * @author paul
 *
 */
public class DoNotCacheModifier extends AbstractCacheTagModifier {

	/**
	 * Sets the cache key to null, ensuring no caching
	 */
	@Override
	public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
		cacheTag.setKey(null);
	}
}
