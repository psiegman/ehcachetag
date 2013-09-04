package nl.siegmann.ehcachetag.cachekeyfactories;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class LocaleCacheKeyFactory extends AbstractPageCacheKeyFactory {

	@Override
	public Object createCacheKey(Object tagCacheKey, PageContext pageContext) {
		Locale locale = ((HttpServletRequest) pageContext.getRequest()).getLocale();
		return new CompositeCacheKey(tagCacheKey, locale);
	}
}
