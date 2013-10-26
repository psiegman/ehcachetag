package nl.siegmann.ehcachetag;

import java.util.Collection;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import junit.framework.Assert;

import nl.siegmann.ehcachetag.cachekeyfactories.CacheKeyFactory;
import nl.siegmann.ehcachetag.cachekeyfactories.CacheKeyMetaFactory;
import nl.siegmann.ehcachetag.cachekeyfactories.DefaultCacheKeyMetaFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class EHCacheTagServletContextListenerTest {

	private EHCacheTagServletContextListener testSubject;
	
	@Mock
	private ServletContextEvent servletContextEvent;
	
	@Mock
	private ServletContext servletContext;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.testSubject = new EHCacheTagServletContextListener();
		Mockito.when(servletContextEvent.getServletContext()).thenReturn(servletContext);
		TestMetaCacheKeyFactory.servletContext = null;
	}
	
	static class TestMetaCacheKeyFactory implements CacheKeyMetaFactory {

		public static ServletContext servletContext;
		
		@Override
		public CacheKeyFactory getCacheKeyFactory(String cacheKeyFactoryName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<Entry<String, CacheKeyFactory>> getCacheKeyFactories() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void init(ServletContext servletContext) {
			TestMetaCacheKeyFactory.servletContext = servletContext;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}
		
	}
	@Test
	public void test_defaults() {

		// when
		testSubject.contextInitialized(servletContextEvent);
		
		// then
		Mockito.verify(servletContextEvent).getServletContext();
		Mockito.verify(servletContext).setAttribute(Mockito.eq(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME), Mockito.any(DefaultCacheKeyMetaFactory.class));
		
	}

	@Test
	public void test_custom_CacheKeyMetaFactory() {
		// given
		Mockito.when(servletContext.getInitParameter("ehcachetag.cacheKeyMetaFactoryClass")).thenReturn(TestMetaCacheKeyFactory.class.getName());
		
		// when
		testSubject.contextInitialized(servletContextEvent);
		
		// then
		Mockito.verify(servletContextEvent).getServletContext();
		Mockito.verify(servletContext).setAttribute(Mockito.eq(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME), Mockito.any(DefaultCacheKeyMetaFactory.class));
		Assert.assertNotNull(TestMetaCacheKeyFactory.servletContext);
	}
}
