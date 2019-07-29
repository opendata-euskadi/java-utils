package r01f.cache.redis;

import java.io.IOException;

import org.redisson.config.Config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.cache.DistributedCacheConfig;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;


@Accessors(prefix="_")
public class DistributedCacheRedisConfig
  implements DistributedCacheConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AppCode _appCode;
	@Getter private final AppComponent _appComponent;
			private final String _redisJsonConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Config getRedisConfig() throws IOException {
		return Config.fromJSON(_redisJsonConfig);		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REDIS CONFIG FILE: contains client and server data.
//	 https://www.baeldung.com/redis-redisson
/////////////////////////////////////////////////////////////////////////////////////////
	public DistributedCacheRedisConfig(final AppCode appCode,final AppComponent appComponent,
								       final String redisJsonConfig) {
		_appCode = appCode;
		_appComponent = appComponent;
		_redisJsonConfig = redisJsonConfig;
	}
	/*public static DistributedCacheRedisConfig createFrom(final XMLPropertiesForAppComponent cacheProps) {
		return DistributedCacheRedisConfig.createFrom(cacheProps,
										  		     "redis");
	}*/
	/*public static DistributedCacheRedisConfig createFrom(final XMLPropertiesForAppComponent cacheProps,
												              final String propsXPath) {
		log.warn(">>> Distributed Cache configFile by Component [{}] of app [{}] ",
				 cacheProps.getAppComponent(),cacheProps.getAppCode());
		
		String outHZXmlConfig = null;
		Node  hzConfigXMLNode  = cacheProps.propertyAt("/*").node();
		if (hzConfigXMLNode != null) {
			outHZXmlConfig = XMLStringSerializer.writeNode(hzConfigXMLNode,
													       Charset.defaultCharset());
		} else {
			
			 log.error("The hazelcast redis file is NOT valid!");
		}
		return new DistributedCacheRedisConfig(cacheProps.getAppCode(),cacheProps.getAppComponent(),
										           outHZXmlConfig);
	}*/
	
	@Override @SuppressWarnings("unchecked")
	public <C extends DistributedCacheConfig> C as(final Class<C> type) {	
		return (C) this;
	}
}
