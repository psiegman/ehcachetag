package nl.siegmann.ehcachetag.cachetagmodifier;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An object that can be used as a cache key where all components are used for hashCode and equals calculation.
 */
public class CompositeCacheKey {
	
	private Object[] keyComponents;
	
	public CompositeCacheKey(Object... keyComponents) {
		this.keyComponents = keyComponents;
	}

	public boolean equals(Object other) {
		return ArrayUtils.isEquals(keyComponents, ((CompositeCacheKey) other).keyComponents);
	}

	public int hashCode() {
		return ArrayUtils.hashCode(keyComponents);
	}

	public String toString() {
		return new ToStringBuilder(this).append("keyComponents", keyComponents).toString();
	}
}
