package r01f.bootstrap.services.config.core;

import lombok.experimental.Accessors;

@Accessors(prefix="_")
public interface ServicesCoreBootstrapConfigWhenServletExposed
	     extends ServicesCoreBootstrapConfig<ServicesCoreModuleExpositionForServletImpl,
	 									     ServicesClientProxyForCoreServletExposed> {
	// just a marker interface
}
