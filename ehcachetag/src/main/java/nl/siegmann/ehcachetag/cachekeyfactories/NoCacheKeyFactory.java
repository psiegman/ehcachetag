package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.jsp.PageContext;

/**
 * Always returns null for cacheKey.
 * 
 * @author paul
 *
 */
public class NoCacheKeyFactory extends AbstractPageCacheKeyFactory {

	@Override
	public CacheLocation createCacheLocation(Object tagCacheKey, PageContext pageContext) {
		return null;
	}
}
