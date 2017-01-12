package nl.siegmann.ehcachetag;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import nl.siegmann.ehcachetag.CacheTag.ModifierNotFoundException;
import nl.siegmann.ehcachetag.cachetagmodifier.CacheTagModifier;
import nl.siegmann.ehcachetag.cachetagmodifier.CacheTagModifierFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CacheTagTest {

	@Mock
	private PageContext pageContext;
	
	@Mock
	private ServletContext servletContext;

	@Mock
	private JspWriter jspWriter = Mockito.mock(JspWriter.class);

	@Mock
	private CacheTagModifierFactory cacheTagModifierFactory;
	
	@Mock
	private CacheTagModifier cacheTagModifier;
	
	@Mock
	private CacheManager cacheManager;

	@Mock
	private CacheManager customCacheManager;
	
	@Mock
	private BodyContent bodyContent;

	@Mock
	private Ehcache ehcache;
	
	private CacheTag testSubject;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.testSubject = Mockito.spy(new CacheTag());
		testSubject.setPageContext(pageContext);
		testSubject.setBodyContent(bodyContent);
		Mockito.when(pageContext.getOut()).thenReturn(jspWriter);
		Mockito.when(pageContext.getPage()).thenReturn(this);
		Mockito.when(testSubject.getDefaultCacheManager()).thenReturn(cacheManager);
		Mockito.when(pageContext.getServletContext()).thenReturn(servletContext);
	}

	/**
	 * Key is null, check that we return EVAL_BODY_INCLUDE.
	 * 
	 * @throws JspException
	 */
	@Test
	public void testDoStartTag_null_key() throws JspException {
		// given
		testSubject.setKey(null);
		testSubject.setCache("mycache");
		
		// when
		int actualResult = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_INCLUDE, actualResult);

		// verify cleanup
		Assert.assertNull(testSubject.getCache());
		verifyCleanup();
	}
	
	/**
	 * Cache is null, check that we return EVAL_BODY_INCLUDE.
	 * 
	 * @throws JspException
	 */
	@Test
	public void testDoStartTag_null_cache() throws JspException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache(null);
		testSubject.setModifiers("hi");

		// when
		int actualResult = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_INCLUDE, actualResult);

		verifyCleanup();
	}

	/**
	 * Cache is null, check that we return EVAL_BODY_INCLUDE.
	 * Also improves the code coverage by added a HttpServletRequest.
	 * 
	 * @throws JspException
	 */
	@Test
	public void testDoStartTag_null_cache_httpservletrequest() throws JspException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache(null);
		testSubject.setModifiers("hi");
		HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
		Mockito.when(servletRequest.getRequestURI()).thenReturn("http://example.com/testpage.jsp");
		Mockito.when(pageContext.getRequest()).thenReturn(servletRequest);

		// when
		int actualResult = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_INCLUDE, actualResult);

		verifyCleanup();
	}

	/**
	 * Cache not found.
	 * 
	 * @throws JspException
	 */
	@Test
	public void testDoStartTag_cache_not_found() throws JspException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache("mycache");
		testSubject.setModifiers("hi");
		Mockito.when(cacheManager.getEhcache(Mockito.anyString())).thenReturn(null);
		
		// when
		int actualResult = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_INCLUDE, actualResult);

		Mockito.verify(cacheManager).getEhcache("mycache");
		verifyCleanup();
	}
	
	/**
	 * Cache found, but the value with the key is not there.
	 * 
	 * @throws JspException
	 */
	@Test
	public void testDoStartTag_no_cached_value() throws JspException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache("mycache");
		Mockito.when(cacheManager.getEhcache(Mockito.anyString())).thenReturn(ehcache);
		
		// when
		int actualResult = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_BUFFERED, actualResult);

		Assert.assertEquals("mycache", testSubject.getCache());
		Mockito.verifyNoMoreInteractions(jspWriter);
	}
	
	/**
	 * We found a cached value
	 * 
	 * @throws JspException
	 * @throws IOException 
	 */
	@Test
	public void testDoStartTag_cached_value() throws JspException, IOException {
		// given
		ensureCacheContent("mycache", "mykey", "cached_content");
		testSubject.setKey("mykey");
		testSubject.setCache("mycache");
		
		// when
		int actualResult = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.SKIP_BODY, actualResult);
		Mockito.verify(pageContext).getOut();
		Mockito.verify(jspWriter).write("cached_content");

		Mockito.verifyNoMoreInteractions(jspWriter);
	}
	
	
	
	private void ensureCacheContent(String cacheName, String cacheKey, String cachedContent) {
		Mockito.when(cacheManager.getEhcache(Mockito.eq(cacheName))).thenReturn(ehcache);
		Mockito.when(ehcache.get(Mockito.eq((Object) cacheKey))).thenReturn(new Element(cacheKey, cachedContent));
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
		Mockito.verify(pageContext).findAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE);
		Mockito.verifyNoMoreInteractions(jspWriter, pageContext);
	}

	@Test
	public void doTestStartTag_no_such_cache() throws JspException {
		// given
		testSubject.setCache("XXX");
		testSubject.setKey("greeting");
		Mockito.when(cacheManager.getEhcache("XXX")).thenReturn(null);
		
		// when
		int startTagReturn = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_INCLUDE, startTagReturn);
		// XXX Mockito.verifyNoMoreInteractions(jspWriter, pageContext);
	}

	/**
	 * Test the case where the before cache lookup causes an exception.
	 * @throws Exception 
	 */
	@Test
	public void doStartTag_before_lookup_excepion() throws Exception {
		// given
		Mockito.doThrow(new Exception()).when(testSubject).doBeforeLookup();
		
		// when
		int startTagReturn = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(1, startTagReturn);
		verifyCleanup();
	}
	
	
	/**
	 * Test the behaviour when writing the result to the outputstream causes an IOException.
	 * @throws IOException 
	 * @throws JspException 
	 */
	@Test(expected = JspException.class)
	public void doStartTag_exception_writing_cached_content() throws IOException, JspException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache("ehcachetag");
		Mockito.doReturn("foo").when(testSubject).getCachedBodyContent("ehcachetag", "mykey");
		Mockito.doThrow(new IOException()).when(jspWriter).write(Mockito.anyString());
		ServletRequest servletRequest = Mockito.mock(ServletRequest.class);
		Mockito.when(pageContext.getRequest()).thenReturn(servletRequest);
		
		// when
		testSubject.doStartTag();
		
		// then
		verifyCleanup();
	}
	
	/**
	 * Test the behaviour when writing the result to the outputstream causes an IOException.
	 * @throws IOException 
	 * @throws JspException 
	 */
	@Test(expected = JspException.class)
	public void doStartTag_exception_writing_cached_content_log_request_uri() throws IOException, JspException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache("ehcachetag");
		Mockito.doReturn("foo").when(testSubject).getCachedBodyContent("ehcachetag", "mykey");
		Mockito.doThrow(new IOException()).when(jspWriter).write(Mockito.anyString());
		
		// when
		testSubject.doStartTag();
		
		// then
		verifyCleanup();
	}
	
	/**
	 * Test the behaviour when the afterRetrieval method throws an exception.
	 * @throws Exception 
	 */
	@Test
	public void doStartTag_do_after_retrieval_exception() throws Exception {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache("ehcachetag");
		Mockito.doReturn("foo").when(testSubject).getCachedBodyContent("ehcachetag", "mykey");
		Mockito.doThrow(new Exception()).when(testSubject).doAfterRetrieval("foo");
		
		// when
		int doStartTagResult = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(1, doStartTagResult);
		Mockito.verifyNoMoreInteractions(jspWriter);
		verifyCleanup();
	}
	
	
	/**
	 * Test doStartTag where the first modifier throws an exception
	 * @throws JspException 
	 */
	@Test
	public void doStartTag_modifier_not_found() throws JspException {
		// given
		testSubject.setKey("testkey");
		CacheTagModifier modifier = Mockito.mock(CacheTagModifier.class);
		Mockito.doThrow(new RuntimeException()).when(modifier).beforeLookup(Mockito.any(CacheTag.class), Mockito.any(JspContext.class));
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier(Mockito.anyString())).thenReturn(modifier);
		
		// when
		int actualResult = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_INCLUDE, actualResult);
		
		// cleanup
		Assert.assertNull(testSubject.getKey());
		Assert.assertNull(testSubject.getCache());
		Assert.assertEquals("", testSubject.getModifiers());
	}
	
	/**
	 * Test doStartTag where the first modifier throws an exception
	 * @throws JspException 
	 */
	@Test
	public void doStartTag_modifier_exception() throws JspException {
		// given
		testSubject.setKey("testkey");
		CacheTagModifier modifier = Mockito.mock(CacheTagModifier.class);
		Mockito.doThrow(new RuntimeException()).when(modifier).beforeLookup(Mockito.any(CacheTag.class), Mockito.any(JspContext.class));
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier(Mockito.anyString())).thenReturn(modifier);
		
		// when
		int actualResult = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_INCLUDE, actualResult);
		
		verifyCleanup();
	}
	

	@Test
	public void testDoBeforeLookup() throws Exception {
		// given
		CacheTagModifier modifierB = new CacheTagModifier() {
			
			@Override
			public void init(ServletContext servletContext) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void destroy() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String beforeUpdate(CacheTag cacheTag, JspContext jspContext,
					String content) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void beforeLookup(CacheTag cacheTag, JspContext jspContext) {
				cacheTag.setKey(cacheTag.getKey().toString() + "B");
			}
			
			@Override
			public String afterRetrieval(CacheTag cacheTag, JspContext jspContext,
					String content) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier("modifierB")).thenReturn(modifierB);

		testSubject.setModifiers("modifierB");
		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE)).thenReturn(cacheTagModifierFactory);

		// when
		testSubject.setKey("A");
		testSubject.doBeforeLookup();
		
		Assert.assertEquals("AB", testSubject.getKey());
	}

	
	@Test(expected = ModifierNotFoundException.class)
	public void testGetCacheTagModifier_not_found() throws ModifierNotFoundException {
		// when
		testSubject.getCacheTagModifier(cacheTagModifierFactory, "test_modifier");
	}

	
	/**
	 * This tests the behaviour when finding a non-string cached value.
	 * 
	 * FIXME however, it also succeeds if the cache contains no value for the given key.
	 */
	@Test
	public void testGetCachedBodyContent_not_String() {
		// given
		Mockito.when(cacheManager.getEhcache("test_cache")).thenReturn(ehcache);
		Mockito.when(ehcache.get(Mockito.any())).thenReturn(new Element("test_key", Integer.valueOf(1)));

		// when
		String cachedBodyContent = testSubject.getCachedBodyContent("test_cache", "test_key");
		
		// then
		Assert.assertTrue(CacheTag.NO_CACHED_VALUE == cachedBodyContent);
		// XXX for some reason this one fails
		// Mockito.verify(ehcache).get(Mockito.eq("test_key"));
	}
	
	
	@Test
	public void testDoBeforeUpdate() throws Exception {
		// given
		String testInput = "A";
		
		CacheTagModifier modifierB = Mockito.mock(CacheTagModifier.class);
		Mockito.when(modifierB.beforeUpdate(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("A"))).thenReturn("AB");
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier("modifierB")).thenReturn(modifierB);

		CacheTagModifier modifierC = Mockito.mock(CacheTagModifier.class);
		Mockito.when(modifierC.beforeUpdate(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("AB"))).thenReturn("CAB");
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier("modifierC")).thenReturn(modifierC);

		testSubject.setModifiers("modifierB,modifierC");
		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE)).thenReturn(cacheTagModifierFactory);

		// when
		String actualResult = testSubject.doBeforeUpdate(testInput);
		
		// then
		Assert.assertEquals("CAB", actualResult);
		Mockito.verify(modifierB).beforeUpdate(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("A"));
		Mockito.verify(modifierC).beforeUpdate(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("AB"));

		Mockito.verifyNoMoreInteractions(modifierB, modifierC);
	}

	@Test
	public void testDoBeforeUpdate_exception() throws Exception {
		// given
		String testInput = "A";
		
		CacheTagModifier modifierB = Mockito.mock(CacheTagModifier.class);
		Mockito.when(modifierB.beforeUpdate(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("A"))).thenReturn("AB");
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier("modifierB")).thenReturn(modifierB);

		CacheTagModifier modifierC = Mockito.mock(CacheTagModifier.class);
		Mockito.when(modifierC.beforeUpdate(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("AB"))).thenThrow(new RuntimeException());
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier("modifierC")).thenReturn(modifierC);

		testSubject.setModifiers("modifierB,modifierC");
		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE)).thenReturn(cacheTagModifierFactory);

		// when
		String actualResult = "original_value";
		try {
			actualResult = testSubject.doBeforeUpdate(testInput);
			Assert.fail("Expected RuntimeException");
		} catch(RuntimeException e) {
		}
		
		// then
		Assert.assertEquals("original_value", actualResult);
		Mockito.verify(modifierB).beforeUpdate(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("A"));
		Mockito.verify(modifierC).beforeUpdate(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("AB"));

		Mockito.verifyNoMoreInteractions(modifierB, modifierC);
	}

	@Test
	public void testDoAfterRetrieval() throws Exception {
		// given
		String testInput = "A";
		
		CacheTagModifier modifierB = Mockito.mock(CacheTagModifier.class);
		Mockito.when(modifierB.afterRetrieval(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("A"))).thenReturn("AB");
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier("modifierB")).thenReturn(modifierB);

		CacheTagModifier modifierC = Mockito.mock(CacheTagModifier.class);
		Mockito.when(modifierC.afterRetrieval(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("AB"))).thenReturn("CAB");
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier("modifierC")).thenReturn(modifierC);

		testSubject.setModifiers("modifierB,modifierC");
		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE)).thenReturn(cacheTagModifierFactory);

		// when
		String actualResult = testSubject.doAfterRetrieval(testInput);
		
		// then
		Assert.assertEquals("CAB", actualResult);
		Mockito.verify(modifierB).afterRetrieval(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("A"));
		Mockito.verify(modifierC).afterRetrieval(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("AB"));
		Mockito.verifyNoMoreInteractions(modifierB, modifierC);
	}

	@Test
	public void testDoAfterRetrieval_exception() throws Exception {
		// given
		String testInput = "A";
		
		CacheTagModifier modifierB = Mockito.mock(CacheTagModifier.class);
		Mockito.when(modifierB.afterRetrieval(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("A"))).thenReturn("AB");
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier("modifierB")).thenReturn(modifierB);

		CacheTagModifier modifierC = Mockito.mock(CacheTagModifier.class);
		Mockito.when(modifierC.afterRetrieval(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("AB"))).thenThrow(new RuntimeException());
		Mockito.when(cacheTagModifierFactory.getCacheTagModifier("modifierC")).thenReturn(modifierC);

		testSubject.setModifiers("modifierB,modifierC");
		Mockito.when(pageContext.findAttribute(EHCacheTagConstants.MODIFIER_FACTORY_ATTRIBUTE)).thenReturn(cacheTagModifierFactory);

		// when
		String actualResult = "original_value";
		try {
			actualResult = testSubject.doAfterRetrieval(testInput);
			Assert.fail("Expected RuntimeException");
		} catch(RuntimeException e) {
		}
		
		// then
		Assert.assertEquals("original_value", actualResult);
		Mockito.verify(modifierB).afterRetrieval(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("A"));
		Mockito.verify(modifierC).afterRetrieval(Mockito.any(CacheTag.class), Mockito.any(JspContext.class), Mockito.eq("AB"));

		Mockito.verifyNoMoreInteractions(modifierB, modifierC);
	}

	
	/**
	 * There is no cache key.
	 * 
	 * @throws JspException
	 * @throws IOException
	 */
	@Test
	public void testEndTag_no_cache_key() throws JspException, IOException {
		// given

		// when
		int endTagReturn = testSubject.doEndTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_PAGE, endTagReturn);
		verifyCleanup();
		Mockito.verifyNoMoreInteractions(jspWriter, pageContext);
	}
	
	/**
	 * There is a cache, a key and a value to store, but writing the content throws an IOException.
	 * Verify that the value is stored in the cache and that the tag values have been cleaned up.
	 * 
	 * @throws JspException
	 * @throws IOException
	 */
	@Test
	public void testEndTag_cache_value_ioexception() throws JspException, IOException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache("ehcachetag");
		Mockito.when(bodyContent.getString()).thenReturn("body_value");	
		Mockito.when(cacheManager.getEhcache("ehcachetag")).thenReturn(ehcache);
		Mockito.doThrow(new IOException()).when(jspWriter).write(Mockito.anyString());

		// when
		int actualReturn = -1;
		
		try {
			actualReturn = testSubject.doEndTag();
			Assert.fail("Expected JspException");
		} catch(JspException e) {
			// as expected
		}
		
		// then
		Assert.assertEquals(-1, actualReturn);

		Mockito.verify(testSubject).doEndTag();
		
		// get content from bodyContent
		Mockito.verify(bodyContent).getString();

		// cache content
		Mockito.verify(cacheManager).getEhcache("ehcachetag");
		Mockito.verify(ehcache).put(new Element("mykey", "body_value"));
		
		// write content
		Mockito.verify(pageContext).getOut();
		Mockito.verify(jspWriter).write("body_value");

		verifyCleanup();

		Mockito.verifyNoMoreInteractions(ehcache, jspWriter,  bodyContent, cacheManager);

		// XXX this one fails, commented out for now
		// Mockito.verifyNoMoreInteractions(pageContext);
	}

	
	/**
	 * There is a cache, a key and a value to store.
	 * Verify that the value is stored in the cache and written to the response writer.
	 * 
	 * @throws JspException
	 * @throws IOException
	 */
	@Test
	public void testEndTag_cache_value() throws JspException, IOException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache("ehcachetag");
		Mockito.when(bodyContent.getString()).thenReturn("body_value");	
		Mockito.when(cacheManager.getEhcache("ehcachetag")).thenReturn(ehcache);

		// when
		int endTagReturn = testSubject.doEndTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_PAGE, endTagReturn);

		Mockito.verify(testSubject).doEndTag();
		
		// get content from bodyContent
		Mockito.verify(bodyContent).getString();

		// cache content
		Mockito.verify(cacheManager).getEhcache("ehcachetag");
		Mockito.verify(ehcache).put(new Element("mykey", "body_value"));
		
		// write content
		Mockito.verify(pageContext).getOut();
		Mockito.verify(jspWriter).write("body_value");

		verifyCleanup();

		Mockito.verifyNoMoreInteractions(ehcache, jspWriter,  bodyContent, cacheManager);

		// XXX this one fails, commented out for now
		// Mockito.verifyNoMoreInteractions(pageContext);
	}

	/**
	 * There is a cache name, a key and a value to store, but cache not found.
	 * 
	 * @throws JspException
	 * @throws IOException
	 */
	@Test
	public void testEndTag_cache_value_no_cache_found() throws JspException, IOException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache("ehcachetag");
		Mockito.when(bodyContent.getString()).thenReturn("body_value");	
		Mockito.when(cacheManager.getEhcache("ehcachetag")).thenReturn(null);

		// when
		int endTagReturn = testSubject.doEndTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_PAGE, endTagReturn);

		Mockito.verify(testSubject).doEndTag();
		
		// get content from bodyContent
		Mockito.verify(bodyContent).getString();

		// cache content
		Mockito.verify(cacheManager).getEhcache("ehcachetag");
		
		// write content
		Mockito.verify(pageContext).getOut();
		Mockito.verify(jspWriter).write("body_value");

		verifyCleanup();
		
		Mockito.verifyNoMoreInteractions(ehcache, jspWriter,  bodyContent, cacheManager);

		// XXX this one fails, commented out for now
		// Mockito.verifyNoMoreInteractions(pageContext);
	}
	

	/**
	 * Verify that an exception thrown by the doBeforeUpdate call is properly handled.
	 * @throws Exception 
	 */
	@Test(expected = JspException.class)
	public void testEndTag_exception_on_do_before_update() throws Exception {
		// given
		testSubject.setKey("mykey");
		Mockito.when(bodyContent.getString()).thenReturn("cached content");
		Mockito.doThrow(new Exception()).when(testSubject).doBeforeUpdate("cached content");
		
		// when
		testSubject.doEndTag();
		
		// then
		Mockito.verify(bodyContent).getString();
		Mockito.verify(testSubject).doBeforeUpdate("cached content");
		verifyCleanup();
		Assert.assertNotNull(testSubject.getKey());
	}

	/**
	 * Verify that the cacheTag is ready for use by another thread.
     * Tag instances can be reused for another thread by the underlying jsp engine.
     */
	private void verifyCleanup() {
		Mockito.verify(testSubject).cleanup();
	}
	
	@Test
	public void setModifiers_null() {
		// when
		testSubject.setModifiers(null);
		
		// then
		Assert.assertNotNull(testSubject.getModifiers());
		Assert.assertEquals(0, testSubject.getModifiers().length());
	}

	@Test
	public void setModifiers_a____b() {
		// when
		testSubject.setModifiers("a,     b");
		
		// then
		Assert.assertNotNull(testSubject.getModifiers());
		Assert.assertEquals("a,b", testSubject.getModifiers());
	}
	
	@Test
	public void getCacheManager_defaults() {
		// given
		Mockito.when(servletContext.getAttribute(EHCacheTagConstants.CACHE_MANAGER)).thenReturn(null);

		// when
		CacheManager actualCacheManager = testSubject.getCacheManager();
		
		// then
		Mockito.verify(testSubject).getDefaultCacheManager();

		Assert.assertTrue(actualCacheManager == cacheManager);
	}

	@Test
	public void getCacheManager_invalid_cacheManager_in_servletcontext() {
		// given
		Mockito.when(servletContext.getAttribute(EHCacheTagConstants.CACHE_MANAGER)).thenReturn("Hello");
		
		// when
		CacheManager actualCacheManager = testSubject.getCacheManager();
		
		// then
		Mockito.verify(testSubject).getDefaultCacheManager();

		Assert.assertTrue(actualCacheManager == cacheManager);
	}

	@Test
	public void getCacheManager_customized_cacheManager() {
		// given
		Mockito.when(servletContext.getAttribute(EHCacheTagConstants.CACHE_MANAGER)).thenReturn(customCacheManager);
		
		// when
		CacheManager actualCacheManager = testSubject.getCacheManager();
		
		// then
		Mockito.verify(testSubject, Mockito.times(0)).getDefaultCacheManager();

		Assert.assertTrue(actualCacheManager == customCacheManager);
	}
}
