package nl.siegmann.ehcachetag.cachekeyfactories;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;

import nl.siegmann.ehcachetag.EHCacheTagConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default cacheKeyMetaFactory.
 * 
 * On init the following happens:<br/>
 * <ol>
 * <li>Get the properties from the servletContext init parameter named 'ehcachetag.cacheKeyMetaFactoryConfig'</li>
 * <li>This parameter value is assumed to be in the form of {factoryName}={factoryClass}?{attributeName1}={attributeValue1}&...<br/>
 * Example:<br/>
 * <pre>
 * demoBean=com.example.DemoBean?message=hi&foo=bar
 * secondDemoBean=com.example.SecondDemoBean?locale=en_US&color=red
 * </pre>
 * </li>
 * <li>All the attributes will be set. If anything goes wrong an error is logged and that particular factory is not created or discarded</li>
 * <li>On the CacheKeyFactory the init method will be called with the ServletContext as parameter</li>
 * </ol>
 * @author paul
 *
 */
public class DefaultCacheKeyMetaFactory implements CacheKeyMetaFactory {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCacheKeyMetaFactory.class);
	private Map<String, CacheKeyFactory> cacheKeyFactories = new HashMap<String, CacheKeyFactory>();
	
	public void init(ServletContext servletContext) {

		// get the configuration of the cacheKeyMetaFactory
		String propertiesString = servletContext.getInitParameter(EHCacheTagConstants.METAFACTORY_CONFIG_PARAM_NAME);

		Map<String, Object> createBeansFromProperties = BeanFactory.createBeansFromProperties(propertiesString);
		
		for (Map.Entry<String, Object> mapEntry: createBeansFromProperties.entrySet()) {
			if (! (mapEntry.getValue() instanceof CacheKeyFactory)) {
				LOG.error("cacheKeyFactory \'" + mapEntry.getKey() + "\' must be an instance of " + CacheKeyFactory.class.getName() + " but is of unexpected type " + mapEntry.getValue().getClass().getName());
				continue;
			}
			CacheKeyFactory cacheKeyFactory = (CacheKeyFactory) mapEntry.getValue();
			cacheKeyFactory.init(servletContext);
			cacheKeyFactories.put(mapEntry.getKey(), cacheKeyFactory);
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
