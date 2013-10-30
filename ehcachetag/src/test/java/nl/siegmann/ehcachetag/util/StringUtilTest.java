package nl.siegmann.ehcachetag.util;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

	@Test
	public void testGetClosestMatchingString_null() {
		// given
		String input = "a";
		Collection<String> alternatives = null;
		
		// when
		String actualResult = StringUtil.getClosestMatchingString(input, alternatives);
		
		// then
		Assert.assertNull(actualResult);
	}

	@Test
	public void testGetClosestMatchingString1() {
		// given
		String input = "a";
		Collection<String> alternatives = Arrays.asList("cc","b","ddd");
		
		// when
		String actualResult = StringUtil.getClosestMatchingString(input, alternatives);
		
		// then
		Assert.assertEquals("b",actualResult);
	}
	
	@Test
	public void testGetClosestMatchingString_first_match() {
		// given
		String input = "a";
		Collection<String> alternatives = Arrays.asList("b", "cc","ddd");
		
		// when
		String actualResult = StringUtil.getClosestMatchingString(input, alternatives);
		
		// then
		Assert.assertEquals("b",actualResult);
	}
	
	
	
	@Test
	public void testGetClosestMatchingString_last_match() {
		// given
		String input = "a";
		Collection<String> alternatives = Arrays.asList("cc", "ddd", "b");
		
		// when
		String actualResult = StringUtil.getClosestMatchingString(input, alternatives);
		
		// then
		Assert.assertEquals("b",actualResult);
	}
	
	
	@Test
	public void testGetClosestMatchingString_case_sensitive() {
		// given
		String input = "a";
		Collection<String> alternatives = Arrays.asList("A","AA","bb");
		
		// when
		String actualResult = StringUtil.getClosestMatchingString(input, alternatives);
		
		// then
		Assert.assertEquals("A",actualResult);
	}
}
