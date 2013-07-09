package nl.siegmann.ehcachetag;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class UrlCacheKeyFactory implements CacheKeyFactory {

	@Override
	public void init(Map<String, String> properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Serializable createCacheKey(Serializable tagCacheKey, PageContext pageContext) {
		String requestUri = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
		return EHCacheTagUtil.createCacheKey(tagCacheKey, requestUri);
	}
}
