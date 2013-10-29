package nl.siegmann.ehcachetag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import org.apache.commons.lang.StringUtils;
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

	static final String NO_SUCH_CACHE = new String();
	static final String NO_CACHED_VALUE = new String();

	private static final long serialVersionUID = 333106287254149880L;
	private static final String[] NO_MODIFIERS = new String[0];
	
	private static final Logger LOG = LoggerFactory.getLogger(CacheTag.class);
	
	// attributes set in the jsp page
	private Object key;
	private String cacheName = EHCacheTagConstants.DEFAULT_CACHE_NAME;
	private String[] modifiers = NO_MODIFIERS;

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
	 * This method is called before doing a lookup in the cache.
	 * Invoke the cacheTagInterceptor.beforeLookup.
	 */
	void doBeforeLookup() throws Exception {
		for (CacheTagModifier cacheTagModifier: findCacheTagInterceptor()) {
			cacheTagModifier.beforeLookup(this, pageContext);
		}
	}
	
	/**
	 * Called before content is saved in the cache.
	 * 
	 * @param content
	 * @return
	 */
	private String doBeforeUpdate(String content) throws Exception {
		String result = content;
		for (CacheTagModifier cacheTagModifier: findCacheTagInterceptor()) {
			result = cacheTagModifier.beforeUpdate(this, pageContext, result);
		}
		return result;
	}

	/**
	 * Called after content is retrieved from the cache but before it is written to the response.
	 * @param content
	 * @return
	 */
	private String doAfterRetrieval(String content) throws Exception {
		String result = content;
		for (CacheTagModifier cacheTagModifier: findCacheTagInterceptor()) {
			result = cacheTagModifier.afterRetrieval(this, pageContext, result);
		}
		return result;
	}


	/**
	 * Finds the CacheTagModifiers to use.
	 * 
	 * @return will always return a list, empty if applicable.
	 */
	private List<CacheTagModifier> findCacheTagInterceptor() {
		// locate the cacheKeyMetaFactory
		CacheTagModifierFactory cacheTagInterceptorFactory = (CacheTagModifierFactory) pageContext.findAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE);
		if (cacheTagInterceptorFactory == null) {
			return Collections.emptyList();
		}

		List<CacheTagModifier> result = new ArrayList<CacheTagModifier>(modifiers.length);
		for (String modifierName: modifiers) {
			// let the cacheKeyMetaFactory determine the cacheKey
			CacheTagModifier cacheTagModifier = cacheTagInterceptorFactory.getCacheTagModifier(modifierName);
			if (cacheTagModifier != null) {
				result.add(cacheTagModifier);
			}
		}

		return result;
	}

	/**
	 * Tries to get the body content from the cache using the cacheKey.
	 * 
	 * @param cacheKey
	 * @return The cached content, null if none found.
	 */
	private String getCachedBodyContent(String cacheName, Object cacheKey) {
		Object cachedObject = getContent(cacheName, cacheKey);
		if (cachedObject == NO_CACHED_VALUE || cachedObject == NO_SUCH_CACHE) {
			return (String) cachedObject;
		}
		if(! (cachedObject instanceof String)) {
			LOG.error("Cached object with key '" + cacheKey + "' in cache '" + cacheName + "' is of unexpected type " + (cachedObject == null ? "<null>" : cachedObject.getClass().getName()) + ", called with tag with key \'" + key + "\' (after modification), class " + pageContext.getPage().getClass().getName() + " and url " + getLocationForLog());
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
			return result;
		}

		// store new bodyContent using cacheKey.
		putContent(cacheName, key, bodyContentAsString);
		
		// write bodyContent
		try {
			pageContext.getOut().write(bodyContentAsString);
		} catch (IOException e) {
			throw new JspException(e);
		} finally {
			cleanup();
		}
		
		cleanup();
		
		return result;
	}

	/**
	 * Cleanup for the next use of the tag
	 */
	private void cleanup() {
		key = null;
		cacheName = null;
		modifiers = NO_MODIFIERS;
	}
	
	/**
	 * Try and get cached content.
	 * 
	 * @param cacheName
	 * @param cacheKey
	 * @return Cached content, NO_SUCH_CACHE if the cache does not exist, NO_CACHED_VALUE of there is no matching value in the cache.
	 */
	private Object getContent(String cacheName, Object cacheKey) {
		Ehcache ehcache = getCache(cacheName);
		if (ehcache == null) {
			return NO_SUCH_CACHE;
		}

		Element cacheElement = ehcache.get(cacheKey);
		if (cacheElement == null) {
			return NO_CACHED_VALUE;
		}
		return cacheElement.getObjectValue();
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
			LOG.error("Cache with name \'" + cacheName + "\' not found, called with tag with key \'" + key + "\' (after modification), class " + pageContext.getPage().getClass().getName() + " and url " + getLocationForLog());
		}
		return result;
	}
	
	private String getLocationForLog() {
		ServletRequest request = pageContext.getRequest();
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest) request).getRequestURI();
		} else {
			return "<unknown>";
		}
	}
	
	CacheManager getCacheManager() {
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
