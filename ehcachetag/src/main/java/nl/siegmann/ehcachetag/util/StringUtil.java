package nl.siegmann.ehcachetag.util;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

public class StringUtil {

	public static String getClosestMatchingString(String input, Collection<String> alternatives) {
		String current = null;
		int minDistance = Integer.MAX_VALUE;
		for (String alternative: alternatives) {
			if (current == null) {
				current = alternative;
				continue;
			}
			int currentDistance = StringUtils.getLevenshteinDistance(input, alternative);
			if (currentDistance < minDistance) {
				minDistance = currentDistance;
				current = alternative;
			}
		}
		return current;
	}

}
