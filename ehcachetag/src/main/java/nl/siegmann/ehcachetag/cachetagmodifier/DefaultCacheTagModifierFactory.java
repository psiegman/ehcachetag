package nl.siegmann.ehcachetag.cachetagmodifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import nl.siegmann.ehcachetag.EHCacheTagConstants;
import nl.siegmann.ehcachetag.util.BeanFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default cacheKeyMetaFactory.
 * <p/>
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
public class DefaultCacheTagModifierFactory implements CacheTagModifierFactory {

	public static final String DEFAULT_CACHETAG_MODIFIER_NAME = "default";
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCacheTagModifierFactory.class);
	private Map<String, CacheTagModifier> cacheTagModifiers = new HashMap<String, CacheTagModifier>();
	private CacheTagModifier defaultCacheTagModifier;
	
	public void init(ServletContext servletContext) {

		// get the configuration of the cacheKeyMetaFactory
		String propertiesString = servletContext.getInitParameter(EHCacheTagConstants.MODIFIER_FACTORY_CONFIG_PARAM);

		Map<String, Object> createBeansFromProperties = BeanFactory.createBeansFromProperties(propertiesString);
		
		for (Map.Entry<String, Object> mapEntry: createBeansFromProperties.entrySet()) {
			if (! (mapEntry.getValue() instanceof CacheTagModifier)) {
				LOG.error("cacheTagPreProcessor \'" + mapEntry.getKey() + "\' must be an instance of " + CacheTagModifier.class.getName() + " but is of unexpected type " + mapEntry.getValue().getClass().getName());
				continue;
			}
			CacheTagModifier cacheTagPreProcessor = (CacheTagModifier) mapEntry.getValue();
			cacheTagPreProcessor.init(servletContext);
			
			String name = mapEntry.getKey();
			if (DEFAULT_CACHETAG_MODIFIER_NAME.equalsIgnoreCase(name)) {
				defaultCacheTagModifier = cacheTagPreProcessor;
			}
			cacheTagModifiers.put(mapEntry.getKey(), cacheTagPreProcessor);
		}
		if (defaultCacheTagModifier == null) {
			defaultCacheTagModifier = CacheTagModifier.NULL_CACHETAG_MODIFIER;
		}
	}

	@Override
	public CacheTagModifier getCacheTagModifier(String cacheTagModifierName) {
		if (StringUtils.isBlank(cacheTagModifierName) || DEFAULT_CACHETAG_MODIFIER_NAME.equals(cacheTagModifierName)) {
			return defaultCacheTagModifier;
		}
		
		return cacheTagModifiers.get(cacheTagModifierName);
	}

	@Override
	public Collection<String> getCacheTagModifierNames() {
		return cacheTagModifiers.keySet();
	}
	
	@Override
	public void destroy() {
		for (CacheTagModifier cacheTagPreProcessor: cacheTagModifiers.values()) {
			try {
				cacheTagPreProcessor.destroy();
			} catch(Exception e) {
				LOG.error(e.toString());
			}
		}
	}
}
