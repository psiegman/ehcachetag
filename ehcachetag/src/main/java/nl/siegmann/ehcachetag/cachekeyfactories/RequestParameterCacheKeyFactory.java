package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * Uses the value of the request parameter identified by the parameter property to create the cacheKey.
 * 
 * @author paul
 *
 */
public class RequestParameterCacheKeyFactory extends AbstractPageCacheKeyFactory {

	private String parameter;
	
	@Override
	public Object createCacheKey(Object tagCacheKey, PageContext pageContext) {
		Object parameterValue = ((HttpServletRequest) pageContext.getRequest()).getParameter(parameter);
		if (parameterValue == null) {
			return null;
		}
		return new CompositeCacheKey(tagCacheKey, parameterValue);
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
