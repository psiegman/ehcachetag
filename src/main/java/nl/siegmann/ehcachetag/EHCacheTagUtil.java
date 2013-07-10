package nl.siegmann.ehcachetag;

import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.cachekeyfactories.CacheKeyFactory;
import nl.siegmann.ehcachetag.cachekeyfactories.CacheKeyMetaFactory;

public class EHCacheTagUtil {

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
