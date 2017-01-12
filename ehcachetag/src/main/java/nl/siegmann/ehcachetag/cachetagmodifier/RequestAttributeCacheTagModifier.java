package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.CacheTag;

/**
 * Creates a cachekey by combining the cacheKey from the tag with the value of the given request attribute.
 * 
 * Potentially dangerous because an attacker can generate a lot of requests
 * with different attribute values thus ensuring a lot of cache churn.
 * 
 * @author paul
 *
 */
public class RequestAttributeCacheTagModifier extends AbstractCacheTagModifier {

	private String attribute;
	
	@Override
	public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
		Object requestAttribute = ((HttpServletRequest) pageContext.getRequest()).getAttribute(attribute);
		addCacheKeyComponent(requestAttribute, cacheTag);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
}
