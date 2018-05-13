package r01f.bootstrap.services.config.client;

import r01f.bootstrap.services.client.ServiceInterfaceTypesToImplOrProxyMappings;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.services.client.ClientAPI;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.interfaces.ServiceInterface;

  class ServicesClientGuiceBootstrapConfigImpl 
extends ServicesClientGuiceBootstrapConfigBase {

	public ServicesClientGuiceBootstrapConfigImpl(final ClientApiAppCode clientApiAppCode,
											 	  final Class<? extends ClientAPI> clientApiType,
											 	  final Class<? extends ServiceInterface> serviceInterfaceBaseType,
											 	  final Class<? extends ServiceProxiesAggregator> servicesProxiesAggregatorType,
											 	  final Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> serviceInterfaceTypesToImplOrProxyMappingsType,
											 	  final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientBootstrapGuiceModuleType) {
		super(clientApiAppCode,
			  clientApiType, 
			  serviceInterfaceBaseType, 
			  servicesProxiesAggregatorType,
			  serviceInterfaceTypesToImplOrProxyMappingsType,
			  clientBootstrapGuiceModuleType);
	}
}
