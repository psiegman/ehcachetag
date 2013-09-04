package nl.siegmann.ehcachetag;

import javax.servlet.jsp.JspContext;
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
	static CacheKeyMetaFactory getCacheKeyMetaFactory(JspContext jspContext) {
		return (CacheKeyMetaFactory) jspContext.getAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME, PageContext.APPLICATION_SCOPE);
	}

	
	/**
	 * Tries to get the cachekey factory from the servlet application context.
	 * 
	 * @param jspContext
	 * @return the cachekey factory from the servlet application context, null if not found.
	 */
	static CacheKeyFactory getCacheKeyFactory(JspContext jspContext, String cacheKeyFactoryName) {
		CacheKeyMetaFactory cacheKeyMetaFactory = getCacheKeyMetaFactory(jspContext);
		if (cacheKeyMetaFactory == null) {
			return null;
		}
		return cacheKeyMetaFactory.getCacheKeyFactory(cacheKeyFactoryName);
	}
}
