package nl.siegmann.ehcachetag.cachekeyfactories;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * Creates a cachekey by combining the cacheKey from the tag with the Locale from the pageContext request.
 * 
 * @author paul
 *
 */
public class LocaleCacheKeyFactory extends AbstractPageCacheKeyFactory {

	@Override
	public CacheLocation createCacheLocation(Object tagCacheKey, PageContext pageContext) {
		Locale locale = ((HttpServletRequest) pageContext.getRequest()).getLocale();
		return new CacheLocation(new CompositeCacheKey(tagCacheKey, locale));
	}
}
