package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.CacheTag;

/**
 * Creates a cachekey by combining the cacheKey from the tag with the Locale from the pageContext request.
 * 
 * @author paul
 *
 */
public class RequestAttributeCacheTagModifier extends AbstractCacheTagModifier {

	private String attribute;
	
	@Override
	public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
		Object attributeValue = ((HttpServletRequest) pageContext.getRequest()).getAttribute(attribute);
		Object cacheKey;
		if (attributeValue == null) {
			cacheKey = null;
		} else {
			cacheKey = new CompositeCacheKey(cacheTag.getKey(), attributeValue);
		}
		cacheTag.setKey(cacheKey);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
}
