package r01f.bootstrap.services.config.core;

import lombok.experimental.Accessors;

@Accessors(prefix="_")
public interface ServicesCoreBootstrapConfigWhenBeanExposed
	 	 extends ServicesCoreBootstrapConfig<ServicesCoreModuleExpositionForBeanImpl,
	 									     ServicesClientProxyForCoreBeanExposed> {
	// just a marker interface
}
