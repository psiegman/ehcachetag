package nl.siegmann.ehcachetag;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CacheTagTest {

	@Mock
	private TagCacheManager tagCacheManager;

	private CacheTag testSubject;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.testSubject = new CacheTag();
		PageContext pageContext = Mockito.mock(PageContext.class);
		ServletContext servletContext = Mockito.mock(ServletContext.class);
		testSubject.setPageContext(pageContext);
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
}
