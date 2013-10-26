package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

public abstract class AbstractPageCacheKeyFactory implements CacheKeyFactory {

	@Override
	public void init(ServletContext servletContext) {
	}

	@Override
	public Object createCacheKey(Object tagCacheKey,
			JspContext jspContext) {
		if (! (jspContext instanceof PageContext)) {
			return null;
		}
		return createCacheKey(tagCacheKey, (PageContext) jspContext);
	}

	public abstract Object createCacheKey(Object tagCacheKey,
			PageContext pageContext);
	
	
	@Override
	public void destroy() {
	}

}
