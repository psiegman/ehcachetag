package nl.siegmann.ehcachetag.util;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class StingUtilTest {

	@Test
	public void testGetClosestMatchingString1() {
		// given
		String input = "a";
		Collection<String> alternatives = Arrays.asList("c","b","d");
		
		// when
		String actualResult = StringUtil.getClosestMatchingString(input, alternatives);
		
		// then
		Assert.assertEquals("b",actualResult);
	}
	
	
	@Test
	public void testGetClosestMatchingString_case_sensitive() {
		// given
		String input = "a";
		Collection<String> alternatives = Arrays.asList("c","AA","bb");
		
		// when
		String actualResult = StringUtil.getClosestMatchingString(input, alternatives);
		
		// then
		Assert.assertEquals("AA",actualResult);
	}
}
