package nl.siegmann.ehcachetag;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.siegmann.ehcachetag.cachekeyfactories.CacheKeyMetaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EHCacheTagServletContextListener implements ServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(EHCacheTagServletContextListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		// get the class name of the cacheKeyMetaFactory
		String metaFactoryClassName = servletContextEvent.getServletContext().getInitParameter(EHCacheTagConstants.METAFACTORY_CLASS_PARAM_NAME);
		
		// get the configuration of the cacheKeyMetaFactory
		String factoriesPropertiesString = servletContextEvent.getServletContext().getInitParameter(EHCacheTagConstants.METAFACTORY_CONFIG_PARAM_NAME);
		
		// create the metaFactory
		CacheKeyMetaFactory cacheKeyMetaFactory = createCacheKeyMetaFactory(metaFactoryClassName, factoriesPropertiesString);
		
		// store metaFactory in servletContext
		servletContextEvent.getServletContext().setAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME, cacheKeyMetaFactory);
	}

	private CacheKeyMetaFactory createCacheKeyMetaFactory(String cacheKeyMetaFactoryClassName, String propertiesAsString) {
		CacheKeyMetaFactory result = null;
		try {
			result = (CacheKeyMetaFactory) Class.forName(cacheKeyMetaFactoryClassName).newInstance();
			result.init(propertiesAsString);
		} catch (InstantiationException e) {
			LOG.error(e.toString());
		} catch (IllegalAccessException e) {
			LOG.error(e.toString());
		} catch (ClassNotFoundException e) {
			LOG.error(e.toString());
		}
		return result;
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		CacheKeyMetaFactory cacheKeyMetaFactory = (CacheKeyMetaFactory) servletContextEvent.getServletContext().getAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME);
		cacheKeyMetaFactory.destroy();
	}

}
