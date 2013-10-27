# EHCache Taglib
## Introduction

A taglib that uses ehcache to cache parts of a jsp page.

## Basic example

Simple test tag, stores the content with the key 'test'.

	<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>
	
	<ect:cache key="test">
		Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
	</ect:cache>


## Customizing the default cache

ehcache.xml

	<cache name="ehcachetagCache" eternal="false"
		maxElementsInMemory="10000" overflowToDisk="false" diskPersistent="false"
		timeToIdleSeconds="0" timeToLiveSeconds="10"
		memoryStoreEvictionPolicy="LRU" statistics="true" />

## Using a simple keyFactory

How cacheKeys are generated can be customized. In this example the cacheKey consists of both the tag cache key and the 'p' request parameter.

### Java source
Create the java class to generate the cacheKey:  

	public class RequestParameterCacheKeyFactory extends AbstractPageCacheKeyFactory {

		private String parameter;
	
		@Override
		public Object createCacheKey(Object tagCacheKey, PageContext pageContext) {
			Object parameterValue = ((HttpServletRequest) pageContext.getRequest()).getParameter(parameter);
			if (parameterValue == null) {
				return null;
			}
			return new CompositeCacheKey(tagCacheKey, parameterValue);
		}

		...
	}

### web.xml
Add the cacheKeyFactory to the web.xml

	<context-param>
		<param-name>ehcachetag.cacheKeyMetaFactoryConfig</param-name>
		<param-value>
		param=nl.siegmann.ehcachetag.cachekeyfactories.RequestParameterCacheKeyFactory?parameter=p
		</param-value>
	</context-param>

### JSP
And finally, how to use it in a JSP page  

	<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>
	
	<ect:cache key="test" keyFactory="pparam">
		Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
	</ect:cache>

## Customizing the CacheKeyMetaFactory
CacheKeys are created by CacheKeyFactories.
By default the CacheTag uses the DefaultCacheKeyMetaFactory for this.
If you want a different implementation, for instance one where the CacheKeyFactories are managed by spring then you can configure this in the web.xml like this:  

	<context-param>
        <param-name>ehcachetag.cacheKeyMetaFactoryClass</param-name>
        <param-value>nl.siegmann.ehcachetag.DefaultCacheKeyMetaFactory</param-value>
    </context-param>
