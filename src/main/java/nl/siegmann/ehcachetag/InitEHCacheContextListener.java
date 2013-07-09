package nl.siegmann.ehcachetag;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitEHCacheContextListener implements ServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(InitEHCacheContextListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		String metaFactoryClassName = servletContextEvent.getServletContext().getInitParameter(EHCacheTagConstants.METAFACTORY_CLASS_PARAM_NAME);
		String factoriesPropertiesString = servletContextEvent.getServletContext().getInitParameter(EHCacheTagConstants.METAFACTORY_CONFIG_PARAM_NAME);
		CacheKeyMetaFactory cacheKeyMetaFactory = createCacheKeyMetaFactory(metaFactoryClassName, factoriesPropertiesString);
		servletContextEvent.getServletContext().setAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME, cacheKeyMetaFactory);
	}

	private CacheKeyMetaFactory createCacheKeyMetaFactory(String cacheKeyMetaFactoryClassName, String propertiesAsString) {
		CacheKeyMetaFactory result = null;
		try {
			result = (CacheKeyMetaFactory) Class.forName(cacheKeyMetaFactoryClassName).newInstance();
			result.init(propertiesAsString);
		} catch (InstantiationException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		CacheKeyMetaFactory cacheKeyMetaFactory = (CacheKeyMetaFactory) servletContextEvent.getServletContext().getAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME);
		cacheKeyMetaFactory.destroy();
	}

}
