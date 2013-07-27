package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class UrlCacheKeyFactory extends AbstractPageCacheKeyFactory {

	@Override
	public Object createCacheKey(Object tagCacheKey, PageContext pageContext) {
		String requestUri = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
		return new CompositeCacheKey(tagCacheKey, requestUri);
	}
}
