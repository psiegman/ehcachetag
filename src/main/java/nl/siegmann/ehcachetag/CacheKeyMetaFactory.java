package nl.siegmann.ehcachetag;

public interface CacheKeyMetaFactory {

	CacheKeyFactory getCacheKeyFactory(String cacheKeyFactoryName);
}
