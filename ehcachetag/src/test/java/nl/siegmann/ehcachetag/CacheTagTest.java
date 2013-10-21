package nl.siegmann.ehcachetag;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

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
	private ServletContext servletContext;
	
	@Mock
	private BodyContent bodyContent;

	@Mock
	private JspWriter jspWriter = Mockito.mock(JspWriter.class);
	
	private CacheTag testSubject;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.testSubject = new CacheTag();
		testSubject.setContentCache(contentCache);
		testSubject.setPageContext(pageContext);
		testSubject.setBodyContent(bodyContent);
		Mockito.when(pageContext.getOut()).thenReturn(jspWriter);
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
	}


	@Test
	public void doTestStartTag_cached_Content() throws JspException {
		// given
		Mockito.when(contentCache.getContent("ehcachetag", "greeting")).thenReturn("Hello, world!");
		testSubject.setCache("ehcachetag");
		testSubject.setKey("greeting");
		
		// when
		int startTagReturn = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.SKIP_BODY, startTagReturn);
		Mockito.verify(contentCache).getContent("ehcachetag", "greeting");
		Mockito.verifyNoMoreInteractions(contentCache);
	}

	@Test
	public void doTestStartTag_cached_null_Content() throws JspException {
		// given
		Mockito.when(contentCache.getContent(Mockito.anyString(), Mockito.anyString())).thenReturn(ContentCache.NO_CACHED_VALUE);
		testSubject.setCache("XXX");
		testSubject.setKey("greeting");
		
		// when
		int startTagReturn = testSubject.doStartTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_BODY_BUFFERED, startTagReturn);
		Mockito.verify(contentCache).getContent("XXX", "greeting");
		Mockito.verifyNoMoreInteractions(contentCache);
	}
	
	@Test
	public void testEndTag_no_cache_key() throws JspException, IOException {
		// given

		// when
		int endTagReturn = testSubject.doEndTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_PAGE, endTagReturn);
		Mockito.verify(pageContext).getOut();
		Mockito.verify(bodyContent).writeOut(jspWriter);
		Mockito.verify(pageContext).getAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME, PageContext.APPLICATION_SCOPE);
		Mockito.verifyNoMoreInteractions(contentCache, jspWriter, pageContext, bodyContent);
	}
	
	@Test
	public void testEndTag_no_cache_value() throws JspException, IOException {
		// given
		testSubject.setKey("mykey");
		testSubject.setCache("ehcachetag");
		Mockito.when(bodyContent.getString()).thenReturn("body_value");	
		
		// when
		int endTagReturn = testSubject.doEndTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_PAGE, endTagReturn);
		Mockito.verify(contentCache).putContent("ehcachetag", "mykey", "body_value");
		Mockito.verify(pageContext).getOut();
		Mockito.verify(bodyContent).getString();
		Mockito.verify(jspWriter).write("body_value");
		Mockito.verify(pageContext).getAttribute(EHCacheTagConstants.METAFACTORY_ATTRIBUTE_NAME, PageContext.APPLICATION_SCOPE);
		Mockito.verifyNoMoreInteractions(contentCache, jspWriter, pageContext, bodyContent);
	}
	
	@Test
	public void testEndTag_cached_value() throws JspException, IOException {
		// given
		testSubject.setCachedBodyContent("cached_value");
		
		// when
		int endTagReturn = testSubject.doEndTag();
		
		// then
		Assert.assertEquals(BodyTagSupport.EVAL_PAGE, endTagReturn);
		Mockito.verify(pageContext).getOut();
		Mockito.verify(jspWriter).write("cached_value");
		Mockito.verifyNoMoreInteractions(contentCache, jspWriter, pageContext, bodyContent);
	}
}
