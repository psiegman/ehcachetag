package nl.siegmann.ehcachetag.cachetagmodifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.CacheTag;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class UrlCacheTagModifierTest {

	private UrlCacheTagModifier testSubject;
	
	@Mock
	private CacheTag cacheTag;
	
	@Mock
	private PageContext pageContext;
	
	@Mock
	private HttpServletRequest request;
	
	@Before
	public void setUp() {
		testSubject = new UrlCacheTagModifier();
		MockitoAnnotations.initMocks(this);
		Mockito.when(pageContext.getRequest()).thenReturn(request);
		Mockito.when(request.getRequestURI()).thenReturn("/foo/bar");
	}
	
	@Test
	public void testBeforeLookup_simple() {
		// given
		Mockito.when(cacheTag.getKey()).thenReturn("mykey");
		
		// when
		testSubject.beforeLookup(cacheTag, pageContext);
		
		// then
		Mockito.verify(pageContext).getRequest();
		Mockito.verify(request).getRequestURI();
		Mockito.verify(cacheTag).getKey();
		Mockito.verify(cacheTag).setKey(Mockito.eq(new CompositeCacheKey("mykey", "/foo/bar")));
		Mockito.verifyNoMoreInteractions(pageContext, cacheTag, request);
	}
}
