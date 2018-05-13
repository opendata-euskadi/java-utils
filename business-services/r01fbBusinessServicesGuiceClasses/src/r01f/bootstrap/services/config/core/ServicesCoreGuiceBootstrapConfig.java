package r01f.bootstrap.services.config.core;

import r01f.bootstrap.services.config.core.ServicesClientProxyToCoreServices;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreModuleExposition;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;

/**
 * @see ServicesCoreBootstrapConfigBuilder
 */
public interface ServicesCoreGuiceBootstrapConfig<E extends ServicesCoreModuleExposition,
										  		  P extends ServicesClientProxyToCoreServices> 
         extends ServicesCoreBootstrapConfig<E,P> {
	
	public Class<? extends ServicesCoreBootstrapGuiceModule> getCoreBootstrapGuiceModuleType();
	public boolean isIsolate();
}
