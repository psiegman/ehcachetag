# EHCache Taglib
## Introduction

EHCache taglib is a taglib that provides a cache tag for jsp pages backed by ehcache.

## Basic example

Simple test tag, stores the content with the key 'test'.

	<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>
	
	<ect:cache key="test">
		Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
	</ect:cache>


## Customizing the cache used
By default ehcachetag uses a cache named ehcachtagCache.  
This can be changed in several ways.

- You can use the cache attribute on the cachetag
- You can add a modifier that updates the cache name (see "How to customize tag behaviour" for this)

## How to customize the caching behaviour
The cache key and the cached content can be modified before doing a cache lookup, before a cache update and after cache retrieval.  
This way the caching behaviour can be changed from the defaults.  

The modifier system is set up in such a way that the changes in the jsp pages are as minimal as possible, and as much of the
work as posible is done in java code and web.xml configuration.

### Example: Locale-specific caching
In this example we add the end-user's locale to the cache key, so that the content is cached on a per locale-basis.

#### Create an implementation of the CacheTagModifier interface
This modifier updates the cache Key by combining the cacheKey from the tag with the 

Locale from the pageContext request.

[CacheTagModifier.java @ github](https://github.com/psiegman/ehcachetag/blob/master/ehcachetag/src/main/java/nl/siegmann/ehcachetag/cachetagmodifier/CacheTagModifier.java)

LocaleCacheTagModifier.java:

	public class LocaleCacheTagModifier extends AbstractCacheTagModifier {
	
		@Override
		public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
			Locale locale = ((HttpServletRequest) pageContext.getRequest()).getLocale();
			Object cacheKey = new CompositeCacheKey(cacheTag.getKey(), locale);
			cacheTag.setKey(cacheKey);
		}
	}

#### Add the modifier to the web.xml
web.xml:

    <listener>
        <listener-class>
			nl.siegmann.ehcachetag.EHCacheTagServletContextListener
        </listener-class>
    </listener>

	<context-param>
		<param-name>ehcachetag.cacheTageModifierFactoryConfig</param-name>
		<param-value>
		locale=nl.siegmann.ehcachetag.cachetagmodifier.LocaleCacheTagModifier
		</param-value>
	</context-param>

#### Use the modifier in a JSP page  
Example.jsp:

	<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>
	
	<ect:cache key="test" modifiers="locale">
		Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
	</ect:cache>

## Customizing the CacheTagModifierFactory
Modifiers are by default managed by the DefaultCacheTagModifierFactory.
This ModifierFactory is a light-weight bean factory that enables you to run the ehcache tag system without Spring or any other bean factory.

However, if you do want your cachetagmodifiers managed by Spring or another bean factory then this is possible as follows:
1. Implement your own CacheTagModifierFactory.
2. Configure this in the web.xml like this:  

	<context-param>
		<param-name>ehcachetag.cacheTageModifierFactory</param-name>
		<param-value>
			nl.siegmann.ehcachetag.cachetagmodifier.DefaultCacheTagModifierFactory
		</param-value>
	</context-param>

## Customizing the default cache behaviour
If you add a Modifier with the name 'default' to the DefaultCacheTagModifierFactory config, then it will be used by default for every cache tag use.
