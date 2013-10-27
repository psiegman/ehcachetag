package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.jsp.PageContext;

/**
 * A CacheKey Factory that always returns the given key as cacheKey
 * 
 * @author paul
 *
 */
public class DefaultCacheKeyFactory extends AbstractPageCacheKeyFactory {

	@Override
	public Object createCacheKey(Object tagCacheKey, PageContext pageContext) {
		return tagCacheKey;
	}
}
