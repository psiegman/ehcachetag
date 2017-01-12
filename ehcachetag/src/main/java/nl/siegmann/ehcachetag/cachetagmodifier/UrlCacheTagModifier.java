package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.CacheTag;

/**
 * Sets the cache key to a combination of the tag's key and the request URI.
 * 
 * @author paul
 *
 */
public class UrlCacheTagModifier extends AbstractCacheTagModifier {

	@Override
	public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
		String requestURI = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
		addCacheKeyComponent(requestURI, cacheTag);
	}
}
