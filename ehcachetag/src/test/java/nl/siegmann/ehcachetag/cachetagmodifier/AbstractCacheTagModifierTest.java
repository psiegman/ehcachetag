package nl.siegmann.ehcachetag.cachetagmodifier;

import nl.siegmann.ehcachetag.CacheTag;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AbstractCacheTagModifierTest {
	
	private AbstractCacheTagModifier abstractCacheTagModifier;
	
	@Mock
	private CacheTag cacheTag;
	
	@Before
	public void setUp() {
		this.abstractCacheTagModifier = new AbstractCacheTagModifier() {
		};
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testAddCacheKeyComponent_null_key_null_extraComponent() {
		// given
		Object extraKeyComponent = null;
		Mockito.when(cacheTag.getKey()).thenReturn(null);
		
		// when
		abstractCacheTagModifier.addCacheKeyComponent(extraKeyComponent, cacheTag);
		
		// then
		Mockito.verify(cacheTag).getKey();
		Mockito.verifyNoMoreInteractions(cacheTag);
	}

	@Test
	public void testAddCacheKeyComponent_notnull_key_null_extraComponent() {
		// given
		Object extraKeyComponent = null;
		Mockito.when(cacheTag.getKey()).thenReturn("hi");
		
		// when
		abstractCacheTagModifier.addCacheKeyComponent(extraKeyComponent, cacheTag);
		
		// then
		Mockito.verify(cacheTag).getKey();
		Mockito.verify(cacheTag).setKey(new CompositeCacheKey("hi", null));
		Mockito.verifyNoMoreInteractions(cacheTag);
	}

	@Test
	public void testAddCacheKeyComponent_null_key_notnull_extraComponent() {
		// given
		Object extraKeyComponent = "extra component";
		Mockito.when(cacheTag.getKey()).thenReturn(null);
		
		// when
		abstractCacheTagModifier.addCacheKeyComponent(extraKeyComponent, cacheTag);
		
		// then
		Mockito.verify(cacheTag).getKey();
		Mockito.verify(cacheTag).setKey(new CompositeCacheKey(null, "extra component"));
		Mockito.verifyNoMoreInteractions(cacheTag);
	}

	@Test
	public void testAddCacheKeyComponent_notnull_key_notnull_extraComponent() {
		// given
		Object extraKeyComponent = "extra key";
		Mockito.when(cacheTag.getKey()).thenReturn("hi");
		
		// when
		abstractCacheTagModifier.addCacheKeyComponent(extraKeyComponent, cacheTag);
		
		// then
		Mockito.verify(cacheTag).getKey();
		Mockito.verify(cacheTag).setKey(new CompositeCacheKey("hi", "extra key"));
		Mockito.verifyNoMoreInteractions(cacheTag);
	}
}
