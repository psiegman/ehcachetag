package nl.siegmann.ehcachetag.cachekeyfactories;

import java.util.Map;

import javax.servlet.jsp.JspContext;

public abstract class AbstractCacheKeyFactory implements CacheKeyFactory {

	@Override
	public void init(Map<String, String> properties) {
	}

	@Override
	public abstract Object createCacheKey(Object tagCacheKey,
			JspContext jspContext);

	@Override
	public void destroy() {
	}

}
