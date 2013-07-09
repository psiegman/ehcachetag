package nl.siegmann.ehcachetag;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.jsp.PageContext;

public interface CacheKeyFactory {
	void init(Map<String, String> properties);
	Serializable createCacheKey(Serializable tagCacheKey, PageContext pageContext);
	void destroy();
}