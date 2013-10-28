package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.jsp.JspContext;

import nl.siegmann.ehcachetag.CacheTag;

/**
 * CacheTagModifier with debugging info.
 * 
 * @author paul
 *
 */
public class DebugCacheTagModifier extends AbstractCacheTagModifier {

	@Override
	public String afterRetrieval(CacheTag cacheTag,
			JspContext jspContext, String content) {
		return "retrieved from cache " + cacheTag.getCache() + " and key " + cacheTag.getKey() + ": " + content;
	}
}
