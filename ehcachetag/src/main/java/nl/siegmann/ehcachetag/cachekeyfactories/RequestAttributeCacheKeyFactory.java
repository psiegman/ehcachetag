package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * Creates a cachekey by combining the cacheKey from the tag with the Locale from the pageContext request.
 * 
 * @author paul
 *
 */
public class RequestAttributeCacheKeyFactory extends AbstractPageCacheKeyFactory {

	private String attribute;
	
	@Override
	public Object createCacheKey(Object tagCacheKey, PageContext pageContext) {
		Object attributeValue = ((HttpServletRequest) pageContext.getRequest()).getAttribute(attribute);
		if (attributeValue == null) {
			return null;
		}
		return new CompositeCacheKey(tagCacheKey, attributeValue);
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
}
