package r01f.bootstrap.services.config.core;

import lombok.experimental.Accessors;


@Accessors(prefix="_")
public interface ServicesCoreBootstrapConfigWhenRESTExposed
	     extends ServicesCoreBootstrapConfig<ServicesCoreModuleExpositionForRESTImpl,
	 									  	 ServicesClientProxyForCoreRESTExposed> {
	// just a marker interface
}
