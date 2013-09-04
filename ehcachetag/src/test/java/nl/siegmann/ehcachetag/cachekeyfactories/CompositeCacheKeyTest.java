package nl.siegmann.ehcachetag.cachekeyfactories;

import junit.framework.Assert;

import org.junit.Test;

public class CompositeCacheKeyTest {

	@Test
	public void test1() {
		CompositeCacheKey cacheKey1 = new CompositeCacheKey("foo");
		CompositeCacheKey cacheKey2 = new CompositeCacheKey("foo", "bar");
		
		Assert.assertFalse(cacheKey1.hashCode() == cacheKey2.hashCode());
		Assert.assertFalse(cacheKey1.equals(cacheKey2));
	}
	
	
	@Test
	public void test_swap() {
		CompositeCacheKey cacheKey1 = new CompositeCacheKey("foo", "bar");
		CompositeCacheKey cacheKey2 = new CompositeCacheKey("bar", "foo");
		
		Assert.assertFalse(cacheKey1.hashCode() == cacheKey2.hashCode());
		Assert.assertFalse(cacheKey1.equals(cacheKey2));
	}
	
	
	@Test
	public void test2() {
		CompositeCacheKey cacheKey1 = new CompositeCacheKey("bar", "foo");
		CompositeCacheKey cacheKey2 = new CompositeCacheKey("bar", "foo");
		
		Assert.assertTrue(cacheKey1.hashCode() == cacheKey2.hashCode());
		Assert.assertTrue(cacheKey1.equals(cacheKey2));
	}
}
