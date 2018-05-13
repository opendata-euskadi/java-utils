package r01f.bootstrap.services.config.client;

import r01f.bootstrap.services.config.ServicesConfigObject;
import r01f.services.client.ClientAPI;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.interfaces.ServiceInterface;

/**
 * @see ServicesClientBootstrapConfigBuilder
 */
public interface ServicesClientBootstrapConfig
		 extends ServicesConfigObject {

	public ClientApiAppCode getClientApiAppCode();
	public Class<? extends ClientAPI> getClientApiType();
	public Class<? extends ServiceInterface> getServiceInterfacesBaseType();
	public Class<? extends ServiceProxiesAggregator> getServicesProxiesAggregatorType();
}
