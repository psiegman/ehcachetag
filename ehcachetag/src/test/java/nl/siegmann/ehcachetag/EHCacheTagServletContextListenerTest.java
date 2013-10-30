package nl.siegmann.ehcachetag;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import junit.framework.Assert;
import nl.siegmann.ehcachetag.cachetagmodifier.CacheTagModifier;
import nl.siegmann.ehcachetag.cachetagmodifier.CacheTagModifierFactory;
import nl.siegmann.ehcachetag.cachetagmodifier.DefaultCacheTagModifierFactory;

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
	
	static class TestMetaCacheKeyFactory implements CacheTagModifierFactory {

		public static ServletContext servletContext;
		
		@Override
		public CacheTagModifier getCacheTagModifier(String cacheTagPreProcessorName) {
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

		@Override
		public Collection<String> getCacheTagModifierNames() {
			return null;
		}
		
	}
	@Test
	public void test_defaults() {

		// when
		testSubject.contextInitialized(servletContextEvent);
		
		// then
		Mockito.verify(servletContextEvent).getServletContext();
		Mockito.verify(servletContext).setAttribute(Mockito.eq(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE), Mockito.any(DefaultCacheTagModifierFactory.class));
		
	}

	@Test
	public void test_custom_CacheKeyMetaFactory() {
		// given
		Mockito.when(servletContext.getInitParameter(EHCacheTagConstants.MODIFIER_FACTORY_CLASS_PARAM)).thenReturn(TestMetaCacheKeyFactory.class.getName());
		
		// when
		testSubject.contextInitialized(servletContextEvent);
		
		// then
		Mockito.verify(servletContextEvent).getServletContext();
		Mockito.verify(servletContext).setAttribute(Mockito.eq(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE), Mockito.any(DefaultCacheTagModifierFactory.class));
		Assert.assertNotNull(TestMetaCacheKeyFactory.servletContext);
	}
}
