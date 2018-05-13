package r01f.cache;

import java.nio.charset.Charset;

import org.w3c.dom.Node;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryXmlConfig;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.xml.XMLStringSerializer;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class DistributedCacheConfig
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AppCode _appCode;
	@Getter private final AppComponent _appComponent;
			private final String _hzXmlConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public Config getHZConfig() {
		return new InMemoryXmlConfig(_hzXmlConfig);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	HAZELCAST CONFIG FILE: contains client and server data.
//	In order to complain with R01FB architecture this file will be located at server (P12GN)
//	but read from client (P12MC) -P12MC is shared between ALL internal client projects of payment gateway -
//
//		http://docs.hazelcast.org/docs/3.5/manual/html/configuringhazelcast.html
//		http://docs.hazelcast.org/docs/3.4/manual/html/configurationoverview.html
/////////////////////////////////////////////////////////////////////////////////////////
	public DistributedCacheConfig(final AppCode appCode,final AppComponent appComponent,
								  final String hzXmlConfig) {
		_appCode = appCode;
		_appComponent = appComponent;
		_hzXmlConfig = hzXmlConfig;
	}
	public static DistributedCacheConfig createFrom(final XMLPropertiesForAppComponent cacheProps) {
		return DistributedCacheConfig.createFrom(cacheProps,
										  		 "hazelcast");
	}
	public static DistributedCacheConfig createFrom(final XMLPropertiesForAppComponent cacheProps,
												    final String propsXPath) {
		log.warn(">>> Distributed Cache configFile by Component [{}] of app [{}] ",
				 cacheProps.getAppComponent(),cacheProps.getAppCode());
		
		String outHZXmlConfig = null;
		Node  hzConfigXMLNode  = cacheProps.propertyAt("/*").node();
		if (hzConfigXMLNode != null) {
			outHZXmlConfig = XMLStringSerializer.writeNode(hzConfigXMLNode,
													       Charset.defaultCharset());
		} else {
			//throw new IllegalStateException("The hazelcast config file is NOT valid!");
		}
		return new DistributedCacheConfig(cacheProps.getAppCode(),cacheProps.getAppComponent(),
										  outHZXmlConfig);
	}
}
