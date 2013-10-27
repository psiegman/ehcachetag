package nl.siegmann.ehcachetag;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import nl.siegmann.ehcachetag.cachekeyfactories.CacheKeyFactory;
import nl.siegmann.ehcachetag.cachekeyfactories.CacheKeyMetaFactory;
import nl.siegmann.ehcachetag.cachekeyfactories.CacheLocation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CacheTagTest {

	@Mock
	private ContentCache contentCache;

	@Mock
	private PageContext pageContext;
	
	@Mock
	private JspWriter jspWriter = Mockito.mock(JspWriter.class);

	@Mock
	private CacheKeyMetaFactory cacheKeyMetaFactory;
	
	@Mock
	private CacheKeyFactory cacheKeyFactory;
	
	private CacheTag testSubject;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.testSubject = new CacheTag();
		testSubject.setContentCache(contentCache);
		testSubject.setPageContext(pageContext);
		Mockito.when(pageContext.getOut()).thenReturn(jspWriter);
	}

	@Test
	public void testGetCacheKey_nullKey_nullFactory() {
		// when
		CacheLocation cacheKey = testSubject.createCacheLocation();
		
		// then
		Assert.assertNull(cacheKey);
	}
	
	@Test
	public void testGetCacheKey_validKey_nullMetaFactory() {
		// given
		testSubject.setKey("hello");
		
		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME)).thenReturn(null);
				
		// when
		CacheLocation cacheLocation = testSubject.createCacheLocation();
		
		// then
		Assert.assertEquals("hello", cacheLocation.getCacheKey());
	}

	@Test
	public void testGetCacheKey_validKey_nullFactory() {
		// given
		testSubject.setKey("hello");
		
		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME)).thenReturn(cacheKeyMetaFactory);
		Mockito.when(cacheKeyMetaFactory.getCacheKeyFactory(null)).thenReturn(null);
				
		// when
		CacheLocation cacheKey = testSubject.createCacheLocation();
		
		// then
		Assert.assertEquals(null, cacheKey);
		Mockito.verify(cacheKeyMetaFactory).getCacheKeyFactory(null);
	}

	@Test
	public void testGetCacheKey_validKey_defaultFactory() {
		// given
		testSubject.setKey("hello");
		
		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME)).thenReturn(cacheKeyMetaFactory);
		Mockito.when(cacheKeyMetaFactory.getCacheKeyFactory(null)).thenReturn(cacheKeyFactory);
		Mockito.when(cacheKeyFactory.createCacheLocation(Mockito.eq("hello"), Mockito.any(JspContext.class))).thenReturn(new CacheLocation("hello"));
				
		// when
		CacheLocation cacheLocation = testSubject.createCacheLocation();
		
		// then
		Assert.assertEquals("hello", cacheLocation.getCacheKey());
		Assert.assertEquals(EHCacheTagConstants.DEFAULT_CACHE_NAME, cacheLocation.getCacheName());
		Mockito.verify(cacheKeyMetaFactory).getCacheKeyFactory(null);
		Mockito.verify(cacheKeyFactory).createCacheLocation("hello", pageContext);
	}

	@Test
	public void testGetCacheKey_validKey_validFactory_nullFromFactory() {
		// given
		testSubject.setKey("hello");
		testSubject.setKeyFactory("testKeyFactory");

		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME)).thenReturn(cacheKeyMetaFactory);
		Mockito.when(cacheKeyMetaFactory.getCacheKeyFactory("testKeyFactory")).thenReturn(cacheKeyFactory);
		Mockito.when(cacheKeyFactory.createCacheLocation(Mockito.eq("hello"), Mockito.any(JspContext.class))).thenReturn(null);
		
		// when
		CacheLocation cacheLocation = testSubject.createCacheLocation();
		
		// then
		Assert.assertNull(cacheLocation);
		Mockito.verify(cacheKeyMetaFactory).getCacheKeyFactory("testKeyFactory");
		Mockito.verify(cacheKeyFactory).createCacheLocation("hello", pageContext);
	}
	
	@Test
	public void testDoStartTag_null_cache_key() {
		// given

		// when
		int actualResult = -1;
		try {
			actualResult = testSubject.doStartTag();
		} catch (JspException e) {
			Assert.fail(e.getMessage());
		}

		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_INCLUDE, actualResult);
		Mockito.verify(pageContext).findAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME);
		Mockito.verifyNoMoreInteractions(contentCache, jspWriter, pageContext);
	}


	@Test
	public void doTestStartTag_no_metaFactory_cached_Content() throws JspException, IOException {
		// given
		testSubject.setCache("ehcachetag");
		testSubject.setKey("greeting");

		// setup cache content
		Mockito.when(contentCache.getContent(new CacheLocation("greeting", "ehcachetag"))).thenReturn("Hello, world!");

		// when
		int startTagReturn = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.SKIP_BODY, startTagReturn);
		Mockito.verify(contentCache).getContent(new CacheLocation("greeting", "ehcachetag"));
		Mockito.verify(pageContext).getOut();
		Mockito.verify(jspWriter).write("Hello, world!");
		Mockito.verify(pageContext).findAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME);
		Mockito.verifyNoMoreInteractions(contentCache, jspWriter, pageContext);
	}

	@Test
	public void doTestStartTag_key_metafactory_defaultkeyfactory() throws JspException, IOException {
		// given
		testSubject.setCache("ehcachetag");
		testSubject.setKey("greeting");

		// setup cachekey creation
		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME)).thenReturn(cacheKeyMetaFactory);
		Mockito.when(cacheKeyMetaFactory.getCacheKeyFactory(null)).thenReturn(cacheKeyFactory);
		// emulate default cacheKeyFactory
		Mockito.when(cacheKeyFactory.createCacheLocation(Mockito.eq("greeting"), Mockito.any(JspContext.class))).thenReturn(new CacheLocation("greeting"));

		// setup cache content
		Mockito.when(contentCache.getContent(new CacheLocation("greeting", "ehcachetag"))).thenReturn("Hello, world!");

		// when
		int startTagReturn = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.SKIP_BODY, startTagReturn);
		Mockito.verify(contentCache).getContent(new CacheLocation("greeting", "ehcachetag"));
		Mockito.verify(pageContext).getOut();
		Mockito.verify(jspWriter).write("Hello, world!");
		Mockito.verify(pageContext).findAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME);
		Mockito.verifyNoMoreInteractions(contentCache, jspWriter, pageContext);
	}

	@Test
	public void doTestStartTag_cached_null_Content() throws JspException {
		// given
		Mockito.when(contentCache.getContent(Mockito.any(CacheLocation.class))).thenReturn(ContentCache.NO_CACHED_VALUE);
		testSubject.setCache("XXX");
		testSubject.setKey("greeting");
		
		// when
		int startTagReturn = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_BUFFERED, startTagReturn);
		Mockito.verify(contentCache).getContent(new CacheLocation("greeting", "XXX"));
		Mockito.verify(pageContext).findAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME);
		Mockito.verifyNoMoreInteractions(contentCache, jspWriter, pageContext);
	}
	
	@Test
	public void testEndTag_no_cache_key() throws JspException, IOException {
		// given

		// when
		int endTagReturn = testSubject.doEndTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_PAGE, endTagReturn);
		Mockito.verifyNoMoreInteractions(contentCache, jspWriter, pageContext);
	}
	
	@Test
	public void testEndTag_no_cache_value() throws JspException, IOException {
		// given
		testSubject.setCacheLocation(new CacheLocation("mykey", "ehcachetag"));

		BodyContent bodyContent = Mockito.mock(BodyContent.class);
		testSubject.setBodyContent(bodyContent);
		Mockito.when(bodyContent.getString()).thenReturn("body_value");	
		
		// when
		int endTagReturn = testSubject.doEndTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_PAGE, endTagReturn);
		Mockito.verify(contentCache).putContent(new CacheLocation("mykey", "ehcachetag"), "body_value");
		Mockito.verify(pageContext).getOut();
		Mockito.verify(bodyContent).getString();
		Mockito.verify(jspWriter).write("body_value");
		Mockito.verifyNoMoreInteractions(contentCache, jspWriter, pageContext, bodyContent);
	}
}
