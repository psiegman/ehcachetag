package nl.siegmann.ehcachetag;

import java.io.Serializable;
import java.util.AbstractMap;

import javax.servlet.jsp.PageContext;

public class EHCacheTagUtil {

	public static Serializable createCacheKey(Serializable part1, Serializable part2) {
		return new AbstractMap.SimpleEntry<Serializable, Serializable>(part1, part2);

	}

	static CacheKeyMetaFactory getCacheKeyMetaFactory(PageContext pageContext) {
		return (CacheKeyMetaFactory) pageContext.getAttribute("ehcachetag.cacheKeyMetaFactory", PageContext.APPLICATION_SCOPE);
	}

	static CacheKeyFactory getCacheKeyFactory(PageContext pageContext, String cacheKeyFactoryName) {
		CacheKeyMetaFactory cacheKeyMetaFactory = getCacheKeyMetaFactory(pageContext);
		if (cacheKeyMetaFactory == null) {
			return null;
		}
		return cacheKeyMetaFactory.getCacheKeyFactory(cacheKeyFactoryName);
	}
}
