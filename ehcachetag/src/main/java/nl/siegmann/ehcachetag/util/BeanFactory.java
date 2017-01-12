package nl.siegmann.ehcachetag.util;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates Beans from properties, including the setting of String properties.
 * <p/>
 * Example input:<br/>
 * <pre>
 * demoBean=com.example.DemoBean?message=hi&foo=bar
 * secondDemoBean=com.example.SecondDemoBean?locale=en_US&color=red
 * </pre>
 * @author paul
 *
 */
public class BeanFactory {
	
	public static final String QUERY_STRING_CHARACTER_ENCODING = "UTF-8";
	
	private static final Logger LOG = LoggerFactory.getLogger(BeanFactory.class);
	private static final PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();

	public static Map<String, Object> createBeansFromProperties(String propertiesString) {
		Map<String, Object> result = new HashMap<String, Object>();
		Properties properties = parseProperties(propertiesString);
		for ( String beanName: properties.stringPropertyNames()) {
			Object bean = BeanFactory.createBean(properties.getProperty(beanName));
			if (bean != null) {
				result.put(beanName, bean);
			}
		}
		return result;
	}


	private static Properties parseProperties(String propertiesString) {
		
		Properties result = new Properties();
		if (StringUtils.isBlank(propertiesString)) {
			return result;
		}
		try {
			result.load(new StringReader(propertiesString));
		} catch (IOException e) {
			LOG.error(e.toString());
		}
		return result;
	}
	
	public static <T> T  createBean(String beanCreateRequest) {
		
		// create bean
		String classNameString = StringUtils.substringBefore(beanCreateRequest, "?");
		T result = createBeanInstance(classNameString);
		if (result == null) {
			return result;
		}
		
		// initialize bean
		String queryString = StringUtils.substringAfterLast(beanCreateRequest, "?");
		if (StringUtils.isNotBlank(queryString)) {
			result = initializeBean(result, queryString);
		}

		return result;
	}
	
	/**
	 * Sets all the bean properties, returning null if anything goes wrong.
	 * 
	 * @param bean
	 * @param queryString
	 * @return the given bean with all the bean properties set, null if anything went wrong.
	 */
	private static <T> T initializeBean(T bean,
			String queryString) {
		
		T result = null;
		Map<String, String> splitQuery;
		try {
			splitQuery = parseQueryString(queryString);
			for (Map.Entry<String, String> keyValue: splitQuery.entrySet()) {
				setProperty(bean, keyValue.getKey(), keyValue.getValue());
			}
			result = bean;
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.toString());
		} catch (SecurityException e) {
			LOG.error(e.toString());
		} catch (IllegalAccessException e) {
			LOG.error(e.toString());
		} catch (IllegalArgumentException e) {
			LOG.error(e.toString());
		} catch (InvocationTargetException e) {
			LOG.error(e.toString());
		} catch (NoSuchMethodException e) {
			LOG.error(e.toString());
		}
		return result;
	}
	
	private static void setProperty(Object bean, String propertyName, String propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		// apache commons BeanUtilsBean silently ignores missing properties, so we do the work ourselves
		PropertyDescriptor propertyDescriptor = propertyUtilsBean.getPropertyDescriptor(bean, propertyName);
		
		if (propertyDescriptor == null) {
			throw new IllegalArgumentException("Property " + propertyName + " not found on class " + bean.getClass().getName());
		}
		ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
		convertUtilsBean.register(true, true, 0);
		
		Object value = convertUtilsBean.convert(propertyValue, propertyDescriptor.getPropertyType());
		
		propertyUtilsBean.setProperty(bean, propertyName, value);
	}

	@SuppressWarnings("unchecked")
	private static <T> T createBeanInstance(String beanClassName) {
		T result = null;
		try {
			result = (T) Class.forName(beanClassName).newInstance();
		} catch (InstantiationException e) {
			LOG.error(e.toString());
		} catch (IllegalAccessException e) {
			LOG.error(e.toString());
		} catch (ClassNotFoundException e) {
			LOG.error(e.toString());
		}
		return result;
	}

	private static Map<String, String> parseQueryString(String queryString) throws UnsupportedEncodingException {
		Map<String, String> result = new HashMap<String, String>();
		String[] pairs = queryString.split("&");
		for (String pair : pairs) {
			int equalsPos = pair.indexOf("=");
			String key = urlDecode(pair.substring(0, equalsPos));
			String value = urlDecode(pair.substring(equalsPos + 1));
			result.put(key, value);
		}
		return result;
	}
	
	private static String urlDecode(String input) throws UnsupportedEncodingException {
		return URLDecoder.decode(input, QUERY_STRING_CHARACTER_ENCODING);
	}
}

