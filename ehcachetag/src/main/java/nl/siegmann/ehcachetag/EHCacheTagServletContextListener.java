package nl.siegmann.ehcachetag;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.siegmann.ehcachetag.cachekeyfactories.CacheKeyMetaFactory;
import nl.siegmann.ehcachetag.cachekeyfactories.DefaultCacheKeyMetaFactory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EHCacheTagServletContextListener implements ServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(EHCacheTagServletContextListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		ServletContext servletContext = servletContextEvent.getServletContext();
		
		// get the class name of the cacheKeyMetaFactory
		String metaFactoryClassName = servletContext.getInitParameter(EHCacheTagConstants.METAFACTORY_CLASS_PARAM_NAME);

		// create the metaFactory
		CacheKeyMetaFactory cacheKeyMetaFactory;
		
		if (StringUtils.isBlank(metaFactoryClassName)) {
			// create Default CacheKeyMetaFactory
			cacheKeyMetaFactory = new DefaultCacheKeyMetaFactory();
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
			servletContext.setAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME, cacheKeyMetaFactory);
		}
	}

	private CacheKeyMetaFactory createCacheKeyMetaFactory(String cacheKeyMetaFactoryClassName, ServletContext servletContext) {
		CacheKeyMetaFactory result = null;
		try {
			result = (CacheKeyMetaFactory) Class.forName(cacheKeyMetaFactoryClassName).newInstance();
			result.init(servletContext);
		} catch (Exception e) {
			LOG.error(e.toString(), e);
		}
		return result;
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		CacheKeyMetaFactory cacheKeyMetaFactory = (CacheKeyMetaFactory) servletContextEvent.getServletContext().getAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME);
		if (cacheKeyMetaFactory != null) {
			try {
				cacheKeyMetaFactory.destroy();
			} catch(Exception e) {
				LOG.error(e.toString(), e);
			}
		}
	}

}
