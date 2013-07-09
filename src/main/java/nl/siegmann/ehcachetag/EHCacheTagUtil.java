package nl.siegmann.ehcachetag;

import java.io.Serializable;
import java.util.AbstractMap;

import javax.servlet.jsp.PageContext;

public class EHCacheTagUtil {

	/**
	 * Creates an object that contains both parts and will use both parts for hashCode and equals.
	 */
	public static Serializable createCacheKey(Serializable part1, Serializable part2) {
		return new AbstractMap.SimpleEntry<Serializable, Serializable>(part1, part2);
	}

	/**
	 * Tries to get the cachekey metafactory from the servlet application context.
	 * 
	 * @param pageContext
	 * @return the cachekey metafactory from the servlet application context, null if not found.
	 */
	static CacheKeyMetaFactory getCacheKeyMetaFactory(PageContext pageContext) {
		return (CacheKeyMetaFactory) pageContext.getAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME, PageContext.APPLICATION_SCOPE);
	}

	
	/**
	 * Tries to get the cachekey factory from the servlet application context.
	 * 
	 * @param pageContext
	 * @return the cachekey factory from the servlet application context, null if not found.
	 */
	static CacheKeyFactory getCacheKeyFactory(PageContext pageContext, String cacheKeyFactoryName) {
		CacheKeyMetaFactory cacheKeyMetaFactory = getCacheKeyMetaFactory(pageContext);
		if (cacheKeyMetaFactory == null) {
			return null;
		}
		return cacheKeyMetaFactory.getCacheKeyFactory(cacheKeyFactoryName);
	}
}
