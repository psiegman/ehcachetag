package nl.siegmann.ehcachetag.cachekeyfactories;

import javax.servlet.jsp.PageContext;

public class NoCacheKeyFactory extends AbstractPageCacheKeyFactory {

	@Override
	public Object createCacheKey(Object tagCacheKey, PageContext pageContext) {
		System.out.println(this.getClass().getName() + " no key");
		return null;
	}
}
