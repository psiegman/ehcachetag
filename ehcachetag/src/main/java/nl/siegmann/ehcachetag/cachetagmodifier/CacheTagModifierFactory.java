package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.ServletContext;

/**
 * Central location for managing CacheTagPreProcessors.
 * 
 * @author paul
 *
 */
public interface CacheTagModifierFactory {

	/**
	 * Gets the cacheTagModifier with the given name, null if not found.
	 * 
	 * @param cacheTagModifierName name of the CacheTagModifier we're looking for.
	 * 
	 * @return the CacheTagModifier with the given name, null if not found.
	 */
	CacheTagModifier getCacheTagModifier(String cacheTagInterceptorName);

	/**
	 * Called once after application is initialized.
	 * 
	 * @param servletContext
	 */
	void init(ServletContext servletContext);
	
	/**
	 * Called once on application destroy.
	 * 
	 */
	void destroy();
}
