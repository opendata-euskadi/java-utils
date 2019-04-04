package r01f.ejie.nora;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.types.url.Url;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class NORAServiceConfig 
  implements ContainsConfigData {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Url _wsEndpointUrl;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NORAServiceConfig(final Url wsEndpointUrl) {
		_wsEndpointUrl = wsEndpointUrl;
	}
	public static NORAServiceConfig from(final XMLPropertiesForAppComponent props) {
		// http://svc.extra.integracion.jakina.{env}/ctxapp/t17iApiWS		wehere env=ejiedes.net | ejiepru.net | ejgvdns
		// http://svc.inter.integracion.jakina.{env}/ctxapp/t17iApiWS		wehere env=ejiedes.net | ejiepru.net | ejgvdns
		Url wsEndpointUrl = props.propertyAt("geo/nora/webservice/endpointUrl/")
								 .asUrl("http://svc.inter.integracion.jakina.ejiedes.net/ctxapp/t17iApiWS");	
																												
		return new NORAServiceConfig(wsEndpointUrl);
	}
}
