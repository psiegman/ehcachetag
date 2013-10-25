package nl.siegmann.ehcachetag.cachekeyfactories;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCacheKeyMetaFactory implements CacheKeyMetaFactory {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCacheKeyMetaFactory.class);
	private Map<String, CacheKeyFactory> cacheKeyFactories = new HashMap<String, CacheKeyFactory>();
	
	public void init(String propertiesString) {
		Map<String, Object> createBeansFromProperties = BeanFactory.createBeansFromProperties(propertiesString);
		
		for (Map.Entry<String, Object> mapEntry: createBeansFromProperties.entrySet()) {
			if (! (mapEntry.getValue() instanceof CacheKeyFactory)) {
				LOG.error("cacheKeyFactory \'" + mapEntry.getKey() + "\' must be an instance of " + CacheKeyFactory.class.getName() + " but is of unexpected type " + mapEntry.getValue().getClass().getName());
				continue;
			}
			cacheKeyFactories.put(mapEntry.getKey(), (CacheKeyFactory) mapEntry.getValue());
		}
	}

	@Override
	public CacheKeyFactory getCacheKeyFactory(String cacheKeyFactoryName) {
		return cacheKeyFactories.get(cacheKeyFactoryName);
	}

	@Override
	public void destroy() {
		for (CacheKeyFactory cacheKeyFactory: cacheKeyFactories.values()) {
			try {
				cacheKeyFactory.destroy();
			} catch(Exception e) {
				LOG.error(e.toString());
			}
		}
	}

	@Override
	public Set<Entry<String, CacheKeyFactory>> getCacheKeyFactories() {
		return cacheKeyFactories.entrySet();
	}
}
