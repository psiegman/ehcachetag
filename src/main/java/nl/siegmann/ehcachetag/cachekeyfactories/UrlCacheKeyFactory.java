package nl.siegmann.ehcachetag.cachekeyfactories;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class UrlCacheKeyFactory extends AbstractCacheKeyFactory {

	@Override
	public Serializable createCacheKey(Serializable tagCacheKey, PageContext pageContext) {
		String requestUri = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
		return new CompositeCacheKey(tagCacheKey, requestUri);
	}
}
