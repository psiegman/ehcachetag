package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.CacheTag;

/**
 * Uses the value of the request parameter identified by the parameter property to create the cacheKey.
 * 
 * @author paul
 *
 */
public class RequestParameterCacheTagModifier extends AbstractCacheTagModifier {

	private String parameter;

	/**
	 * Modifies the cacheKey to a combination of the tag's key and the parameter from the parameter request property.
	 * 
	 * Returns null and does not cache if the parameter has no value.
	 */
	@Override
	public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
		Object parameterValue = ((HttpServletRequest) pageContext.getRequest()).getParameter(parameter);
		Object cacheKey;
		if (parameterValue == null) {
			cacheKey = null;
		} else {
			cacheKey = new CompositeCacheKey(cacheTag.getKey(), parameterValue);
		}
		cacheTag.setKey(cacheKey);
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
