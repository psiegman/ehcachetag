package nl.siegmann.ehcachetag.cachetagmodifier;

import java.util.Collection;

import javax.servlet.ServletContext;

/**
 * Central location for managing CacheTagModifiers.
 * 
 * @author paul
 *
 */
public interface CacheTagModifierFactory {

	/**
	 * Called once after application is initialized.
	 * 
	 * @param servletContext
	 */
	void init(ServletContext servletContext);

	/**
	 * The names of the available cacheTagModifiers
	 * 
	 * @return The names of the available cacheTagModifiers
	 */
	Collection<String> getCacheTagModifierNames();

	/**
	 * Gets the cacheTagModifier with the given name, null if not found.
	 * 
	 * @param cacheTagModifierName name of the CacheTagModifier we're looking for.
	 * 
	 * @return the CacheTagModifier with the given name, null if not found.
	 */
	CacheTagModifier getCacheTagModifier(String cacheTagModifierName);

	/**
	 * Called once on application destroy.
	 * 
	 */
	void destroy();
}
