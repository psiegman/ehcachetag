package nl.siegmann.ehcachetag;

public class EHCacheTagConstants {
	
	/**
	 * The web.xml config parameter that the EHCacheTagServletContextListener uses for the implementation class of the CacheTagModifierFactory.
	 */
	public static final String MODIFIER_FACTORY_CLASS_PARAM = "ehcachetag.cacheTageModifierFactory";
	
	/**
	 * The web.xml config parameter that the DefaultCacheTagModifierFactory uses for its configuration.
	 */
	public static final String MODIFIER_FACTORY_CONFIG_PARAM = "ehcachetag.cacheTageModifierFactoryConfig";

	/**
	 * The attribute name under which the CacheTagModifierFactory is stored in the web application context.
	 */
	public static final String MODIFIER_FACTORY_ATTRIBUTE = "ehcachetag.cacheTagModifierFactory";

	/**
	 * The web.xml config parameter that defines which CacheManager to use
	 * If not defined, use default CacheManager
	 */
	public static final String CACHE_MANAGER_NAME_PARAM = "ehcachetag.cacheManagerName";
	
	/**
	 * The parameter used to store the CacheManager in the ServletContext
	 */
	public static final String CACHE_MANAGER = "ehcachetag.cacheManager";

	/**
	 * The name of the default cache.
	 */
	public static final String DEFAULT_CACHE_NAME = "ehcachetagCache";
	
}
