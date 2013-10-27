package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class UrlCacheKeyFactory extends AbstractPageCacheKeyFactory {

	@Override
	public CacheLocation createCacheLocation(Object tagCacheKey, PageContext pageContext) {
		String requestUri = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
		return new CacheLocation(new CompositeCacheKey(tagCacheKey, requestUri));
	}
}
