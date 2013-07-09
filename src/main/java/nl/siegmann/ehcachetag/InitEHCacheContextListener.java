package nl.siegmann.ehcachetag;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitEHCacheContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		DefaultCacheKeyMetaFactory defaultCacheKeyMetaFactory = new DefaultCacheKeyMetaFactory();
		defaultCacheKeyMetaFactory.init(servletContextEvent.getServletContext().getInitParameter("ehcachetag.cachekeyfactories"));
		servletContextEvent.getServletContext().setAttribute("ehcachetag.cacheKeyMetaFactory", defaultCacheKeyMetaFactory);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		// TODO Auto-generated method stub
		
	}

}
