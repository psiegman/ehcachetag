package nl.siegmann.ehcachetag.cachekeyfactories;

import java.io.Serializable;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class LocaleCacheKeyFactory extends AbstractCacheKeyFactory {

	@Override
	public Serializable createCacheKey(Serializable tagCacheKey, PageContext pageContext) {
		Locale locale = ((HttpServletRequest) pageContext.getRequest()).getLocale();
		return new CompositeCacheKey(tagCacheKey, locale);
	}
}
