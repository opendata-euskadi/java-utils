package r01f.bootstrap.services.config.client;

import r01f.bootstrap.services.client.ServiceInterfaceTypesToImplOrProxyMappings;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfig;

/**
 * @see ServicesClientBootstrapConfigBuilder
 */
public interface ServicesClientGuiceBootstrapConfig
		 extends ServicesClientBootstrapConfig {
	public Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> getServiceInterfaceTypesToImplOrProxyMappingsType();
	public Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> getClientBootstrapGuiceModuleType();
}
