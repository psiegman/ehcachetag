package nl.siegmann.ehcachetag.cachekeyfactories;

import java.io.Serializable;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An object that can be used as a cache key where all components are used for hashCode and equals calculation.
 */
public class CompositeCacheKey implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8907654331874070103L;

	private Serializable[] keyComponents;
	
	public CompositeCacheKey(Serializable... keyComponents) {
		this.keyComponents = keyComponents;
	}

	public boolean equals(Object other) {
		return ArrayUtils.isEquals(keyComponents, ((CompositeCacheKey) other).keyComponents);
	}

	public int hashCode() {
		return ArrayUtils.hashCode(keyComponents);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
