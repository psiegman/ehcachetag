package nl.siegmann.ehcachetag.cachetagmodifier;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.CacheTag;

/**
 * Updates the cacheKey by combining the cacheKey from the tag with the Locale from the pageContext request.
 * 
 * @author paul
 *
 */
public class LocaleCacheTagModifier extends AbstractCacheTagModifier {

	@Override
	public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
		Locale locale = ((HttpServletRequest) pageContext.getRequest()).getLocale();
		addCacheKeyComponent(locale, cacheTag);
	}
}
