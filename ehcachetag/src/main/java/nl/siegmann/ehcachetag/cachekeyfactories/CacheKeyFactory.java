package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspContext;

/**
 * Creates a cacheKey based on the cachetag's cacheKey and the information from the jspContext.
 * An example of this would be a cacheKeyFactory that creates a cacheKey consisting of the original
 * cacheKey and the end-user's Locale.
 * 
 * @author paul
 *
 */
public interface CacheKeyFactory {
	
	/**
	 * Called once on Application startup.
	 * 
	 * @param properties
	 */
	void init(ServletContext servletContext);
	
	/**
	 * Called the cacheTag to create a new cacheKey.
	 * 
	 * @param tagCacheKey the cacheKey as set on the cacheTag. May be null.
	 * @param jspContext The jspContext of the jsp page this method is called from.
	 * 
	 * @return A newly generated cacheKey. Null will be interpreted as "do not cache".
	 */
	CacheLocation createCacheLocation(Object tagCacheKey, JspContext jspContext);
	
	/**
	 * Called on Application shutdown.
	 */
	void destroy();
}