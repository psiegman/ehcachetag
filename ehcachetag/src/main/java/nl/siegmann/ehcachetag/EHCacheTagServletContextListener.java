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

		// get the class name of the cacheKeyMetaFactory
		String metaFactoryClassName = servletContextEvent.getServletContext().getInitParameter(EHCacheTagConstants.METAFACTORY_CLASS_PARAM_NAME);

		// create the metaFactory
		CacheKeyMetaFactory cacheKeyMetaFactory;
		
		if (StringUtils.isBlank(metaFactoryClassName)) {
			// create Default CacheKeyMetaFactory
			cacheKeyMetaFactory = new DefaultCacheKeyMetaFactory();
			try {
				cacheKeyMetaFactory.init(servletContextEvent.getServletContext());
			} catch (Exception e) {
				LOG.error(e.toString());
			}
		} else {
			cacheKeyMetaFactory = createCacheKeyMetaFactory(metaFactoryClassName, servletContextEvent.getServletContext());
		}
		
		// store metaFactory in servletContext
		if (cacheKeyMetaFactory != null) {
			servletContextEvent.getServletContext().setAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME, cacheKeyMetaFactory);
		}
	}

	private CacheKeyMetaFactory createCacheKeyMetaFactory(String cacheKeyMetaFactoryClassName, ServletContext servletContext) {
		CacheKeyMetaFactory result = null;
		try {
			result = (CacheKeyMetaFactory) Class.forName(cacheKeyMetaFactoryClassName).newInstance();
			result.init(servletContext);
		} catch (Exception e) {
			LOG.error(e.toString());
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
				LOG.error(e.toString());
			}
		}
	}

}
