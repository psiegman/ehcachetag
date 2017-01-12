package nl.siegmann.ehcachetag;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.ehcache.CacheManager;
import nl.siegmann.ehcachetag.cachetagmodifier.CacheTagModifierFactory;
import nl.siegmann.ehcachetag.cachetagmodifier.DefaultCacheTagModifierFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EHCacheTagServletContextListener implements ServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(EHCacheTagServletContextListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		ServletContext servletContext = servletContextEvent.getServletContext();
		
		// get the class name of the cacheKeyMetaFactory
		String metaFactoryClassName = servletContext.getInitParameter(EHCacheTagConstants.MODIFIER_FACTORY_CLASS_PARAM);

		// create the metaFactory
		CacheTagModifierFactory cacheKeyMetaFactory;
		
		if (StringUtils.isBlank(metaFactoryClassName)) {
			// create Default CacheKeyMetaFactory
			cacheKeyMetaFactory = new DefaultCacheTagModifierFactory();
			try {
				cacheKeyMetaFactory.init(servletContext);
			} catch (Exception e) {
				LOG.error(e.toString(), e);
			}
		} else {
			cacheKeyMetaFactory = createCacheKeyMetaFactory(metaFactoryClassName, servletContext);
		}
		
		// store metaFactory in servletContext
		if (cacheKeyMetaFactory != null) {
			servletContext.setAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE, cacheKeyMetaFactory);
		}
		
		if (cacheKeyMetaFactory != null) {
			LOG.info("Initialized EHCacheTag with cacheKeyMetaFactory " + cacheKeyMetaFactory.getClass().getName());
		}
		
		// initialize the cacheManager
		initCacheManager(servletContext);
	}

	private CacheTagModifierFactory createCacheKeyMetaFactory(String cacheKeyMetaFactoryClassName, ServletContext servletContext) {
		CacheTagModifierFactory result = null;
		try {
			result = (CacheTagModifierFactory) Class.forName(cacheKeyMetaFactoryClassName).newInstance();
			result.init(servletContext);
		} catch (Exception e) {
			LOG.error(e.toString(), e);
		}
		return result;
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		CacheTagModifierFactory cacheKeyMetaFactory = (CacheTagModifierFactory) servletContextEvent.getServletContext().getAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE);
		if (cacheKeyMetaFactory != null) {
			try {
				cacheKeyMetaFactory.destroy();
			} catch(Exception e) {
				LOG.error(e.toString(), e);
			}
		}
	}

	void initCacheManager(ServletContext servletContext) {
		String cacheManagerName = servletContext.getInitParameter(EHCacheTagConstants.CACHE_MANAGER_NAME_PARAM);
		if (StringUtils.isNotBlank(cacheManagerName)) {
			CacheManager cacheManager = getCacheManagerByName(cacheManagerName);
			if (cacheManager == null) {
				LOG.error("failed to load cache manager " + cacheManagerName + ", default cacheManager will be used");
			} else {
				servletContext.setAttribute(EHCacheTagConstants.CACHE_MANAGER, cacheManager);
			}
		}
	}
	
	CacheManager getCacheManagerByName(String cacheManagerName) {
		return CacheManager.getCacheManager(cacheManagerName);
	}
}
