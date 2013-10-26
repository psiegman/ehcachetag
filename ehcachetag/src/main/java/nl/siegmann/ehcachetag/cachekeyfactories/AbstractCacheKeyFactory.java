package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspContext;

public abstract class AbstractCacheKeyFactory implements CacheKeyFactory {

	@Override
	public void init(ServletContext servletContext) {
	}

	@Override
	public abstract Object createCacheKey(Object tagCacheKey,
			JspContext jspContext);

	@Override
	public void destroy() {
	}

}
