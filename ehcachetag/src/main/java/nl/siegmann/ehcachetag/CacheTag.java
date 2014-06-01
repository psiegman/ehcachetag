package nl.siegmann.ehcachetag;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import nl.siegmann.ehcachetag.cachetagmodifier.CacheTagModifier;
import nl.siegmann.ehcachetag.cachetagmodifier.CacheTagModifierFactory;
import nl.siegmann.ehcachetag.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A tag for caching jsp page fragments.
 * 
 * When in doubt or anything goes wrong it will not use information from the cache but generate it anew.
 * 
 * @author paul
 *
 */
public class CacheTag extends BodyTagSupport {

	static final String NO_SUCH_CACHE = "NO_SUCH_CACHE";
	static final String NO_CACHED_VALUE = "NO_CACHED_VALUE";

	private static final long serialVersionUID = 333106287254149880L;
	private static final String[] NO_MODIFIERS = new String[0];
	
	private static final Logger LOG = LoggerFactory.getLogger(CacheTag.class);
	
	// attributes set in the jsp page
	private Object key;
	private String cacheName = EHCacheTagConstants.DEFAULT_CACHE_NAME;
	private String[] modifiers = NO_MODIFIERS;
	private CacheManager cacheManager;

	/**
	 * Writes the content of the body to the pageContext writer.
	 * The content may come from the cache.
	 * <br/>
	 * Three scenarios:<br/>
	 * 1. Key is null : proceed as normal, return EVAL_BODY_INCLUDE<br/>
	 * 2. Key is not null, no cached value found: create cached body value, return EVAL_BODY_BUFFERED<br/>
	 * 3. Key is not null, cached value found: return SKIP_BODY.<br/>
	 * 
	 * @throws IOException 
	 * 
	 */
	@Override
	public int doStartTag() throws JspException {

		// do the beforeLookup modifiers
		try {
			doBeforeLookup();
		} catch (Exception e) {
			cleanup();
			return BodyTagSupport.EVAL_BODY_INCLUDE;
		}
		
		// revert to default cache if no cache set
		if (StringUtils.isBlank(cacheName)) {
			cacheName = EHCacheTagConstants.DEFAULT_CACHE_NAME;
		}
		
		if (key == null || cacheName == null) {
			// no cacheKey: generate and write body content in the normal way
			cleanup();
			return BodyTagSupport.EVAL_BODY_INCLUDE;
		}
		
		String cachedBodyContent = getCachedBodyContent(cacheName, key);

		// no such cache,: generate and write body content in the normal way
		if (cachedBodyContent == NO_SUCH_CACHE) {
			cleanup();
			return BodyTagSupport.EVAL_BODY_INCLUDE;
		}
		
		int result;
		
		if (cachedBodyContent == NO_CACHED_VALUE) {
			// we have a key but no cached content, start buffering the bodyContent
			result = BodyTagSupport.EVAL_BODY_BUFFERED;
		} else {
			try {
				cachedBodyContent = doAfterRetrieval(cachedBodyContent);
			} catch (Exception e) {
				cleanup();
				return BodyTagSupport.EVAL_BODY_INCLUDE;
			}

			// we have cached content: write content and skip body
			try {
				pageContext.getOut().write(cachedBodyContent);
			} catch (IOException e) {
				throw new JspException(e);
			} finally {
				cleanup();
			}
			
			// set cacheKey to null so that the endTag knows
			// it does not have to store anything in the cache
			key = null;
			
			result = BodyTagSupport.SKIP_BODY;
		}
		return result;
	}

	/**
	 * Internal exception that is used in case a modifier is search for but not found.
	 * 
	 * @author paul
	 *
	 */
	static final class ModifierNotFoundException extends Exception {

		private static final long serialVersionUID = 4535024464306691589L;

		public ModifierNotFoundException(String message) {
			super(message);
		}
	}
	
	/**
	 * This method is called before doing a lookup in the cache.
	 * Invoke the cacheTagInterceptor.beforeLookup.
	 */
	void doBeforeLookup() throws Exception {
		CacheTagModifierFactory cacheTagModifierFactory = getCacheTagModifierFactory();
		if (cacheTagModifierFactory == null) {
			return;
		}
		
		for (String modifierName: modifiers) {
			CacheTagModifier cacheTagModifier = getCacheTagModifier(cacheTagModifierFactory, modifierName);

			try {
				cacheTagModifier.beforeLookup(this, pageContext);
			} catch (Exception e) {
				LOG.error("Modifier with name '" + modifierName + "' and class " + cacheTagModifier.getClass().getName() + " threw " + e.getClass().getName() + " at " + getLocationForLog());
				throw e;
			}
		}
	}
	
	/**
	 * Called before content is saved in the cache.
	 * 
	 * @param content
	 * @return
	 */
	String doBeforeUpdate(String content) throws Exception {

		CacheTagModifierFactory cacheTagModifierFactory = getCacheTagModifierFactory();
		if (cacheTagModifierFactory == null) {
			return content;
		}

		String result = content;
		
		for (String modifierName: modifiers) {
			CacheTagModifier cacheTagModifier = getCacheTagModifier(cacheTagModifierFactory, modifierName);

			try {
				result = cacheTagModifier.beforeUpdate(this, pageContext, result);
			} catch (Exception e) {
				LOG.error("Modifier with name '" + modifierName + "' and class " + cacheTagModifier.getClass().getName() + " threw " + e.getClass().getName() + " at " + getLocationForLog());
				throw e;
			}
		}
		return result;
	}

	/**
	 * Called after content is retrieved from the cache but before it is written to the response.
	 * 
	 * @param content
	 * @return
	 */
	String doAfterRetrieval(String content) throws Exception {
		CacheTagModifierFactory cacheTagModifierFactory = getCacheTagModifierFactory();
		if (cacheTagModifierFactory == null) {
			return content;
		}
		
		String result = content;
		
		for (String modifierName: modifiers) {
			CacheTagModifier cacheTagModifier = getCacheTagModifier(cacheTagModifierFactory, modifierName);

			try {
				result = cacheTagModifier.afterRetrieval(this, pageContext, result);
			} catch (Exception e) {
				LOG.error("Modifier with name '" + modifierName + "' and class " + cacheTagModifier.getClass().getName() + " threw " + e.getClass().getName() + " at " + getLocationForLog());
				throw e;
			}
		}
		return result;
	}

	/**
	 * Gets the CacheTagModifier with the given name, throws ModifierNotFoundException if not found.
	 *  
	 * @param cacheTagModifierFactory
	 * @param modifierName
	 * @return
	 * @throws ModifierNotFoundException
	 */
	CacheTagModifier getCacheTagModifier(CacheTagModifierFactory cacheTagModifierFactory, String modifierName) throws ModifierNotFoundException {
		CacheTagModifier result = cacheTagModifierFactory.getCacheTagModifier(modifierName);
		if (result == null) {
			String closestMatch = StringUtil.getClosestMatchingString(modifierName, cacheTagModifierFactory.getCacheTagModifierNames());
			String message = "CacheTagModifier with name \'" + modifierName + "\' not found at " + getLocationForLog() + "." + (closestMatch == null ? "" : " Did you mean \'" + closestMatch + "\' ?");
			LOG.error(message);
			throw new ModifierNotFoundException(message);
		}
		return result;

	}

	/**
	 * locate the cacheKeyMetaFactory
	 * 
	 * @return
	 */
	private CacheTagModifierFactory getCacheTagModifierFactory() {
		return (CacheTagModifierFactory) pageContext.findAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE);
	}

	
	/**
	 * Tries to get the body content from the cache using the cacheKey.
	 * 
	 * @param cacheKey
	 * @return The cached content, null if none found.
	 */
	String getCachedBodyContent(String cacheName, Object cacheKey) {

		Ehcache ehcache = getCache(cacheName);
		if (ehcache == null) {
			return NO_SUCH_CACHE;
		}

		Element cacheElement = ehcache.get(cacheKey);
		if (cacheElement == null) {
			return NO_CACHED_VALUE;
		}

		Object cachedObject = cacheElement.getObjectValue();
		
		// check this because other parts of the system could also put items in the same cache
		if(! (cachedObject instanceof String)) {
			LOG.error("Cached object with key '" + cacheKey + "' in cache '" + cacheName + "' is of unexpected type " + (cachedObject == null ? "<null>" : cachedObject.getClass().getName()) + ", at " + getLocationForLog());
			return NO_CACHED_VALUE;
		}
		return (String) cachedObject;
	}
	
	/**
	 * Two scenarios:<br/>
	 * 1. key is null or bodyContent is null: do nothing<br/>
	 * 2. otherwise: store bodyContent in cache using cacheKey
	 */
	@Override
	public int doEndTag() throws JspException {
		int result = Tag.EVAL_PAGE;
		
		if (key == null) {
			cleanup();
			return result;
		}
		
		// modify content before storing
		String bodyContentAsString = bodyContent.getString();
		try {
			bodyContentAsString = doBeforeUpdate(bodyContentAsString);
		} catch (Exception e) {
			cleanup();
			LOG.error("modifier threw exception: " + e);
			throw new JspException(e);
		}

		// store new bodyContent using cacheKey.
		putContent(cacheName, key, bodyContentAsString);
		
		// write bodyContent
		try {
			pageContext.getOut().write(bodyContentAsString);
		} catch (IOException e) {
			cleanup();
			throw new JspException(e);
		}
				
		cleanup();
		return result;
	}

	/**
	 * Cleanup for the next use of the tag
	 */
	void cleanup() {
		key = null;
		cacheName = null;
		modifiers = NO_MODIFIERS;
		// we don't delete the CacheManager as it is assumed to be the same for all tags
	}
	
	/**
	 * Update the cached value with the cacheValue.
	 * 
	 * @param cacheName
	 * @param cacheKey
	 * @param cacheValue
	 */
	private void putContent(String cacheName, Object cacheKey, String cacheValue) {
		Ehcache ehcache = getCache(cacheName);
		if (ehcache != null) {
			ehcache.put(new Element(cacheKey, cacheValue));
		}
	}

	private Ehcache getCache(String cacheName) {
		Ehcache result = getCacheManager().getEhcache(cacheName);
		if (result == null) {
			LOG.error("Cache with name \'" + cacheName + "\' not found at " + getLocationForLog());
		}
		return result;
	}
	
	/**
	 * Generate a location of this tag within the system for logging purposes.
	 * 
	 * @return
	 */
	private String getLocationForLog() {
		StringBuilder result = new StringBuilder();
		result.append("cache tag with key \'" + key + "\' (after modification)");
		result.append(", class " + pageContext.getPage().getClass().getName());
		ServletRequest request = pageContext.getRequest();
		if (request instanceof HttpServletRequest) {
			result.append(" and url " + ((HttpServletRequest) request).getRequestURI());
		}
		return result.toString();
	}
	
	/**
	 * Get the default CacheManager or the one defined by {@link EHCacheTagConstants#CACHE_MANAGER_NAME_PARAM}
	 * @return the default CacheManager or the one defined by {@link EHCacheTagConstants#CACHE_MANAGER_NAME_PARAM}
	 */
	CacheManager getCacheManager() {
		if (cacheManager == null) {
			Object customCacheManager = pageContext.getServletContext().getAttribute(EHCacheTagConstants.CACHE_MANAGER);
			if (customCacheManager instanceof CacheManager) {
				this.cacheManager = (CacheManager) customCacheManager;
			} else {
				if (customCacheManager != null) {
					LOG.warn("Found an object of unexpected type " + customCacheManager.getClass().getName() +  " as attribute " + EHCacheTagConstants.CACHE_MANAGER + " in the ServletContext instead of expected type " + CacheManager.class.getName() + ". Will now use default CacheManager");
				}
				this.cacheManager = getDefaultCacheManager();
			}
		}
		return cacheManager;
	}

	CacheManager getDefaultCacheManager() {
		return CacheManager.getInstance();
	}
	
	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public String getCache() {
		return cacheName;
	}

	public void setCache(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * A comma-separated list of the modifiers to use.
	 */
	public String getModifiers() {
		return StringUtils.join(modifiers, ",");
	}

	public void setModifiers(String modifier) {
		if (StringUtils.isBlank(modifier)) {
			this.modifiers = NO_MODIFIERS;
		} else {
			this.modifiers = StringUtils.split(modifier, ", ");
		}
	}
}
