package nl.siegmann.ehcachetag;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCacheKeyMetaFactory implements CacheKeyMetaFactory {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCacheKeyMetaFactory.class);
	private Map<String, CacheKeyFactory> cacheKeyFactories = new HashMap<String, CacheKeyFactory>();
	
	public void init(String propertiesString) {
		Properties properties = parseProperties(propertiesString);
		for ( String factoryName: properties.stringPropertyNames()) {
			CacheKeyFactory cacheKeyFactory = createCacheKeyFactory(properties.getProperty(factoryName));
			if (cacheKeyFactory != null) {
				cacheKeyFactories.put(factoryName, cacheKeyFactory);
			}
		}
	}

	private Properties parseProperties(String propertiesString) {
		
		Properties properties = new Properties();
		try {
			properties.load(new StringReader(propertiesString));
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return properties;
	}
	

	private CacheKeyFactory createCacheKeyFactory(String cacheKeyFactoryClassName) {
		CacheKeyFactory result = null;
		try {
			result = (CacheKeyFactory) Class.forName(cacheKeyFactoryClassName).newInstance();
		} catch (InstantiationException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public CacheKeyFactory getCacheKeyFactory(String cacheKeyFactoryName) {
		return cacheKeyFactories.get(cacheKeyFactoryName);
	}
}
