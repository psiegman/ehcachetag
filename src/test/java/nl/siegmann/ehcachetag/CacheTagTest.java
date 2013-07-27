package nl.siegmann.ehcachetag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

public class CacheTagTest {

	@Mock
	private TagCacheManager tagCacheManager;

	private CacheTag testSubject;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.testSubject = new CacheTag();
		// mock ServletContext
		MockServletContext mockServletContext = new MockServletContext();
		// mock PageContext
		MockPageContext mockPageContext = new MockPageContext(
				mockServletContext);
		testSubject.setPageContext(mockPageContext);
	}

	@Test
	public void test1() {
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
