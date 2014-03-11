package nl.siegmann.ehcachetag.cachetagmodifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Loads CacheTagModifiers from the Spring context.
 * 
 * @author paul
 *
 */
public class SpringCacheTagModifierFactory implements CacheTagModifierFactory {

	private Map<String, CacheTagModifier> cacheTagModifiers = Collections.emptyMap();
	
	@Override
	public void init(ServletContext servletContext) {
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		cacheTagModifiers = webApplicationContext.getBeansOfType(CacheTagModifier.class);
	}

	@Override
	public Collection<String> getCacheTagModifierNames() {
		return cacheTagModifiers.keySet();
	}

	@Override
	public CacheTagModifier getCacheTagModifier(String cacheTagModifierName) {
		return cacheTagModifiers.get(cacheTagModifierName);
	}

	@Override
	public void destroy() {
		for (CacheTagModifier cacheTagModifier: cacheTagModifiers.values()) {
			cacheTagModifier.destroy();
		}
	}
}
