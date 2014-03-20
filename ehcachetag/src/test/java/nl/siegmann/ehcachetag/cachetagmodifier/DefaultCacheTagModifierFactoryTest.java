package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.ServletContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DefaultCacheTagModifierFactoryTest {

	private DefaultCacheTagModifierFactory testSubject;
	
	@Mock
	private ServletContext servletContext;
	
	@Before
	public void setUp() {
		testSubject = new DefaultCacheTagModifierFactory();
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testInit_empty() {
		// given
		
		// when
		testSubject.init(servletContext);
		
		// then
		Assert.assertTrue(testSubject.getCacheTagModifierNames().isEmpty());
		Mockito.verify(servletContext).getInitParameter("ehcachetag.cacheTageModifierFactoryConfig");
	}

	@Test
	public void testInit_simple() {
		// given
		String initString = "testmodifier=" + MockCacheTagModifier.class.getName();
		Mockito.when(servletContext.getInitParameter("ehcachetag.cacheTageModifierFactoryConfig")).thenReturn(initString);
		
		// when
		testSubject.init(servletContext);
		
		// then
		Assert.assertEquals(1, testSubject.getCacheTagModifierNames().size());
		Assert.assertEquals("testmodifier", testSubject.getCacheTagModifierNames().iterator().next());
		Assert.assertNotNull(testSubject.getCacheTagModifier("testmodifier"));
		Assert.assertEquals(MockCacheTagModifier.class, testSubject.getCacheTagModifier("testmodifier").getClass());
		Mockito.verify(servletContext).getInitParameter("ehcachetag.cacheTageModifierFactoryConfig");
	}

	@Test
	public void testInit_one_correct_one_wrong_type() {
		// given
		String initString = "testmodifier=" + MockCacheTagModifier.class.getName() + "\n"
				+ "stringmodifier=" + String.class.getName() + "\n"
				+ "testmodifier2=" + MockCacheTagModifier.class.getName();
		Mockito.when(servletContext.getInitParameter("ehcachetag.cacheTageModifierFactoryConfig")).thenReturn(initString);
		
		// when
		testSubject.init(servletContext);
		
		// then
		Assert.assertEquals(2, testSubject.getCacheTagModifierNames().size());
		Assert.assertTrue(testSubject.getCacheTagModifierNames().contains("testmodifier"));
		Assert.assertTrue(testSubject.getCacheTagModifierNames().contains("testmodifier2"));

		Assert.assertNotNull(testSubject.getCacheTagModifier("testmodifier"));
		Assert.assertEquals(MockCacheTagModifier.class, testSubject.getCacheTagModifier("testmodifier").getClass());

		Assert.assertNotNull(testSubject.getCacheTagModifier("testmodifier2"));
		Assert.assertEquals(MockCacheTagModifier.class, testSubject.getCacheTagModifier("testmodifier2").getClass());

		Mockito.verify(servletContext).getInitParameter("ehcachetag.cacheTageModifierFactoryConfig");
	}

	@Test
	public void testInit_wrong_type() {
		// given
		String initString = "testmodifier=" + String.class.getName();
		Mockito.when(servletContext.getInitParameter("ehcachetag.cacheTageModifierFactoryConfig")).thenReturn(initString);
		
		// when
		testSubject.init(servletContext);
		
		// then
		Assert.assertTrue(testSubject.getCacheTagModifierNames().isEmpty());
		Mockito.verify(servletContext).getInitParameter("ehcachetag.cacheTageModifierFactoryConfig");
	}
}
