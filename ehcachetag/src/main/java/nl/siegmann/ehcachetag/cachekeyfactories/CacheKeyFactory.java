package nl.siegmann.ehcachetag.cachekeyfactories;

import java.util.Map;

import javax.servlet.jsp.JspContext;

public interface CacheKeyFactory {
	void init(Map<String, String> properties);
	Object createCacheKey(Object tagCacheKey, JspContext jspContext);
	void destroy();
}