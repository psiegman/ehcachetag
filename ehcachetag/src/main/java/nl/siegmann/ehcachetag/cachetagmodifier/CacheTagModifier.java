package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspContext;

import nl.siegmann.ehcachetag.CacheTag;

/**
 * This class is called before a lookup in the cache is done, after content is retreived from the cache and before
 * content is written to the outputstream.
 * 
 * It gives the ability to modify the cache key, the cache to use, the content to store, the content to display and other
 * aspects of the caching lifecycle.
 * <p/>
 * Thread-safety: This class can and will be used by several concurrent threads.<br/>
 * Init and destroy will be called only once by the application server, but make sure any implementation
 * of this interface is thread-safe.
 * 
 * @author paul
 *
 */
public interface CacheTagModifier {

	/**
	 * CacheTagInterceptor that does not do anything.
	 * 
	 */
	CacheTagModifier NULL_CACHETAG_MODIFIER = new CacheTagModifier() {
		
		@Override
		public void init(ServletContext servletContext) {
		}
		
		@Override
		public void beforeLookup(CacheTag cacheTag, JspContext jspContext) {
		}
		
		@Override
		public String afterRetrieval(CacheTag cacheTag, JspContext jspContext,
				String content) {
			return content;
		}
		
		@Override
		public void destroy() {
		}

		@Override
		public String beforeUpdate(CacheTag cacheTag, JspContext jspContext,
				String content) {
			return content;
		}

	};
	
	/**
	 * Called once on Application startup.
	 * 
	 * @param properties
	 */
	void init(ServletContext servletContext);
	
	/**
	 * Called before Cache lookup.
	 * 
	 * Will be called by different threads concurrently.
	 */
	void beforeLookup(CacheTag cacheTag, JspContext jspContext);
	
	/**
	 * Called before updating the cache with the given content.
	 * 
	 * @param cacheTag
	 * @param jspContext
	 * @param content the content that will be stored in the cache
	 * @return the content to store in the cache.
	 */
	String beforeUpdate(CacheTag cacheTag, JspContext jspContext, String content);
	
	
	/**
	 * Called after retrieving content from the cache but before it's written to the output.
	 * 
	 * @param cacheTag
	 * @param jspContext
	 * @param content the content that will be sent back to the client
	 * @return The content to send back to the client
	 */
	String afterRetrieval(CacheTag cacheTag, JspContext jspContext, String content);

	/**
	 * Called on Application shutdown.
	 */
	void destroy();
}
