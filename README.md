# EHCache Taglib
## Introduction

EHCacheTag is a taglib that caches fragments of a jsp page.  
It uses ehcache for the actual caching.

## Status

* Travis Build Status: [![Build Status](https://travis-ci.org/psiegman/ehcachetag.svg?branch=master)](https://travis-ci.org/psiegman/ehcachetag)

## Basic example

Here is a basic example fragment.  
The content is stored with the key 'test' in the cache 'ehcachtagCache'.

```jsp
<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>

<ect:cache key="test">
	Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
</ect:cache>
```

## Customizing the cache used
By default ehcachetag uses a cache named ehcachtagCache.    
This can simply be changed by using the cache attribute on the cachetag


More complex customization can done using the CacheTagModifiers

## How to customize the caching behaviour
Caching behaviour can be modified by using CacheTagModifiers.

CacheTagModifiers are called by the CacheTag before doing a cache lookup, before a cache update and after cache retrieval.  
See: [CacheTagModifier.java @ github](https://github.com/psiegman/ehcachetag/blob/master/ehcachetag/src/main/java/nl/siegmann/ehcachetag/cachetagmodifier/CacheTagModifier.java)

This way the caching behaviour like the cache key, the cached content, the cache used, etc can be modified.
EHCacheTag comes with a minimal beanfactory built-in to manage the CacheTagModifiers, but there is also the ehcache-spring module where the CacheTagModifiers can be managed by the Spring Framework.

The modifier system is set up in such a way that the changes in the jsp pages are as minimal as possible, and as much of the
work as posible is done in java code and web.xml configuration.

### Example: Locale-specific caching
In this example we add the end-user's locale to the cache key, so that the content is cached on a per locale-basis.

#### Create an implementation of the CacheTagModifier interface
The LocaleCacheTagModifier implements the beforeLookup method.
The beforeLookup method is called by the CacheTag before doing a lookup in the underlying caching system.

The LocaleCacheTagModifier implementation of the beforeLookup method gets the Locale from the pageContext request and adds it to the cache key.


LocaleCacheTagModifier.java:

```java
public class LocaleCacheTagModifier extends AbstractCacheTagModifier {

	@Override
	public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
		Locale locale = ((HttpServletRequest) pageContext.getRequest()).getLocale();
		Object cacheKey = new CompositeCacheKey(cacheTag.getKey(), locale);
		cacheTag.setKey(cacheKey);
	}
}
```

#### Add the modifier to the web.xml
Because this is the first CacheTagModifier in this project we need to add an extra ContextListener to the web.xml.

web.xml:

```xml
<listener>
    <listener-class>
		nl.siegmann.ehcachetag.EHCacheTagServletContextListener
    </listener-class>
</listener>
```

Next we add the LocaleCacheTagModifier to the config under the name of 'locale'.
This way we can refer to the modifier by its name 'locale' in the jsp:

web.xml:

```xml
<context-param>
	<param-name>ehcachetag.cacheTageModifierFactoryConfig</param-name>
	<param-value>
	locale=nl.siegmann.ehcachetag.cachetagmodifier.LocaleCacheTagModifier
	</param-value>
</context-param>
```

#### Use the modifier in a JSP page  
Here we use the LocaleCacheTagModifier.

Example.jsp:

```jsp
<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>

<ect:cache key="test" modifiers="locale">
	Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
</ect:cache>
```

## Customizing the CacheTagModifierFactory
Modifiers are by default managed by the DefaultCacheTagModifierFactory.
This ModifierFactory is a light-weight bean factory that enables you to run the ehcache tag system without Spring or any other bean factory.

However, if you do want your cachetagmodifiers managed by Spring or another bean factory then this is possible as follows:

1. Implement your own CacheTagModifierFactory.
2. Configure this in the web.xml like this:  

```xml
<context-param>
	<param-name>ehcachetag.cacheTageModifierFactory</param-name>
	<param-value>
		nl.siegmann.ehcachetag.cachetagmodifier.DefaultCacheTagModifierFactory
	</param-value>
</context-param>
```

## Customizing the default cache behaviour
If you add a Modifier with the name 'default' to the DefaultCacheTagModifierFactory config, then it will be used by default for every cache tag use.
## Customizing the cache manager
When you don't want to use the default cache manager that CacheManager.getInstance() provides, you can add a parameter in web.xml:

```xml
<context-param>
	<param-name>ehcachetag.cacheManagerName</param-name>
	<param-value>myCacheManager</param-value>
</context-param>
```
 
## References
[EHCache](http://ehcache.org/)
