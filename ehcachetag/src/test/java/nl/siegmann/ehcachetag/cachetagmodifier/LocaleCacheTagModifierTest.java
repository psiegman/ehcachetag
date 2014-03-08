package nl.siegmann.ehcachetag.cachetagmodifier;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import nl.siegmann.ehcachetag.CacheTag;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class LocaleCacheTagModifierTest {

	private LocaleCacheTagModifier testSubject;
	
	@Mock
	private CacheTag cacheTag;
	
	@Mock
	private PageContext pageContext;
	
	@Mock
	private HttpServletRequest request;
	
	@Before
	public void setUp() {
		testSubject = new LocaleCacheTagModifier();
		MockitoAnnotations.initMocks(this);
		Mockito.when(pageContext.getRequest()).thenReturn(request);
		Mockito.when(request.getLocale()).thenReturn(Locale.CHINESE);
	}
	
	@Test
	public void testBeforeLookup_simple() {
		// given
		Mockito.when(cacheTag.getKey()).thenReturn("mykey");
		
		// when
		testSubject.beforeLookup(cacheTag, pageContext);
		
		// then
		Mockito.verify(pageContext).getRequest();
		Mockito.verify(request).getLocale();
		Mockito.verify(cacheTag).getKey();
		Mockito.verify(cacheTag).setKey(Mockito.eq(new CompositeCacheKey("mykey", Locale.CHINESE)));
		Mockito.verifyNoMoreInteractions(pageContext, cacheTag, request);
	}
}
