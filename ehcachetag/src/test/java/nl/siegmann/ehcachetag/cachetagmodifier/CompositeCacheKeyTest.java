package nl.siegmann.ehcachetag.cachetagmodifier;

import junit.framework.Assert;

import nl.siegmann.ehcachetag.cachetagmodifier.CompositeCacheKey;

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

	@Test
	public void test_hash_null_element() {
		// given
		CompositeCacheKey key = new CompositeCacheKey("hi", null, "bye");
		
		// when
		key.hashCode();
		
		// then
		// just checking that a null value doesn't throw any exceptions
	}

	@Test
	public void test_toString_null_value() {
		// given
		CompositeCacheKey key = new CompositeCacheKey("hi", null, "bye");
		
		// when
		String toStringValue = key.toString();
		
		// then
		Assert.assertTrue(toStringValue.contains("null"));
		Assert.assertTrue(toStringValue.contains("hi"));
		Assert.assertTrue(toStringValue.contains("bye"));
	}
}
