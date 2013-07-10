package nl.siegmann.ehcachetag.cachekeyfactories;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.jsp.PageContext;

public abstract class AbstractCacheKeyFactory implements CacheKeyFactory {

	@Override
	public void init(Map<String, String> properties) {
	}

	@Override
	public abstract Serializable createCacheKey(Serializable tagCacheKey,
			PageContext pageContext);

	@Override
	public void destroy() {
	}

}
