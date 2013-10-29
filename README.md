# EHCache Taglib
## Introduction

A taglib that uses ehcache to cache parts of a jsp page.

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

## How to customize the tag behaviour
The behaviour of the cache tag can be changed using modifiers.  
Modifiers are java classes that are called before doing a cache lookup, before a cache update and after cache retrieval.

### Example Java source
This modifier updates the cache Key by combining the cacheKey from the tag with the Locale from the pageContext request.

	public class LocaleCacheTagModifier extends AbstractCacheTagModifier {
	
		@Override
		public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
			Locale locale = ((HttpServletRequest) pageContext.getRequest()).getLocale();
			Object cacheKey = new CompositeCacheKey(cacheTag.getKey(), locale);
			cacheTag.setKey(cacheKey);
		}
	}

### web.xml
Add the modifier to the web.xml

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

### JSP
And finally, how to use it in a JSP page  

	<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>
	
	<ect:cache key="test" modifiers="locale">
		Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
	</ect:cache>

## Customizing the CacheTagModifierFactory
Modifiers are by default managed by the DefaultCacheTagModifierFactory.

If you want a different implementation, for instance one where the Modifiers are managed by spring then you can configure this in the web.xml like this:  

	<context-param>
		<param-name>ehcachetag.cacheTageModifierFactory</param-name>
		<param-value>
			nl.siegmann.ehcachetag.cachetagmodifier.DefaultCacheTagModifierFactory
		</param-value>
	</context-param>

## Customizing the default cache behaviour
If you add a Modifier with the name 'default' to the DefaultCacheTagModifierFactory config, then it will be used by default for every cache tag use.