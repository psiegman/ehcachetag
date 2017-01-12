package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.CacheTag;

public abstract class AbstractCacheTagModifier implements CacheTagModifier {

	@Override
	public void init(ServletContext servletContext) {
	}

	@Override
	public void beforeLookup(CacheTag cacheTag,
			JspContext jspContext) {
		if (jspContext instanceof PageContext) {
			beforeLookup(cacheTag, (PageContext) jspContext);
		}
	}

	/**
	 * Adds the extraComponent as an extra element to the cacheKey.
	 * 
	 * If the current key is null and the extraComponent is null then the cacheKey is left as null.
	 * 
	 * @param extraComponent
	 * @param cacheTag
	 */
	protected void addCacheKeyComponent(Object extraComponent, CacheTag cacheTag) {
		Object currentCacheKey = cacheTag.getKey();
		
		if (currentCacheKey == null && extraComponent == null) {
			return;
		}

		CompositeCacheKey compositeCacheKey = new CompositeCacheKey(currentCacheKey, extraComponent);
		cacheTag.setKey(compositeCacheKey);
	}

	@Override
	public String beforeUpdate(CacheTag cacheTag,
			JspContext jspContext, String content) {
		if (jspContext instanceof PageContext) {
			return beforeUpdate(cacheTag, (PageContext) jspContext, content);
		} else {
			return content;
		}
	}

	@Override
	public String afterRetrieval(CacheTag cacheTag,
			JspContext jspContext, String content) {
		if (jspContext instanceof PageContext) {
			return afterRetrieval(cacheTag, (PageContext) jspContext, content);
		} else {
			return content;
		}
	}

	public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
		
	}
	
	public String beforeUpdate(CacheTag cacheTage, PageContext pageContext, String content) {
		return content;
	}
	
	public String afterRetrieval(CacheTag cacheTage, PageContext pageContext, String content) {
		return content;
	}
	
	@Override
	public void destroy() {
	}

}
