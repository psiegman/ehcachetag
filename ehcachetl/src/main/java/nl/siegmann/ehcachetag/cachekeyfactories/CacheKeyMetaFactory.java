package nl.siegmann.ehcachetag.cachekeyfactories;

import java.util.Map.Entry;
import java.util.Set;

public interface CacheKeyMetaFactory {

	CacheKeyFactory getCacheKeyFactory(String cacheKeyFactoryName);

	Set<Entry<String, CacheKeyFactory>> getCacheKeyFactories();
	
	void init(String propertiesAsString);
	
	void destroy();
}
