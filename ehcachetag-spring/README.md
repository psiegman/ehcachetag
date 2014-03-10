# EHCacheTag Spring
This extension to the ehcache tag allows you to create and configure cachetag modifiers using the spring framework.


## Howto Use

Configure the web.xml to use the SpringCacheTagModifier.  
From then on any bean in the Spring ApplicationContext that implements the nl.siegmann.ehcachetag.cachetagmodifier.CacheTagModifier interface is available as a CacheTagModifier in the cache tag.

### Web.xml
In the web.xml configure the spring cachetag modifier.

web.xml:


	<context-param>
		<param-name>ehcachetag.cacheTageModifierFactory</param-name>
		<param-value>
			nl.siegmann.ehcachetag.cachetagmodifier.SpringCacheTagModifierFactory
		</param-value>
	</context-param>

    <listener>
        <listener-class>
			nl.siegmann.ehcachetag.EHCacheTagServletContextListener
        </listener-class>
    </listener>

### spring.xml example

	<bean id="localeModifier" class="nl.siegmann.ehcachetag.cachetagmodifier.LocaleCacheTagModifier"/>

### jsp example

	<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>
	
	<ect:cache key="test" modifiers="localeModifier">
		Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
	</ect:cache>

