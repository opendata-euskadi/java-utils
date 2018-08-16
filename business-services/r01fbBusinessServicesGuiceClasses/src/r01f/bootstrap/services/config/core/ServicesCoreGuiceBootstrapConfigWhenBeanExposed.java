package r01f.bootstrap.services.config.core;

import java.util.Collection;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesBootstrapConfigBuilder;
import r01f.bootstrap.services.config.core.ServicesClientProxyForCoreBeanExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.bootstrap.services.config.core.ServicesCoreModuleExpositionForBeanImpl;
import r01f.bootstrap.services.config.core.ServicesCoreSubModuleBootstrapConfig;
import r01f.bootstrap.services.core.BeanImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.services.core.CoreService;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;

/**
 * @see ServicesBootstrapConfigBuilder
 */
@Accessors(prefix="_")
public class ServicesCoreGuiceBootstrapConfigWhenBeanExposed
	 extends ServicesCoreGuiceBootstrapConfigBase<ServicesCoreModuleExpositionForBeanImpl,
	 									          ServicesClientProxyForCoreBeanExposed> 
  implements ServicesCoreBootstrapConfigWhenBeanExposed {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesCoreGuiceBootstrapConfigWhenBeanExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
												   	       final Class<? extends CoreService> coreServicesBaseType,
												   	       final ServicesCoreModuleEventsConfig eventHandling,
												   	       final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs,
												   	       final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule,
												   	       final boolean isolate) {
		super(coreAppCode,coreModule,
			  new ServicesCoreModuleExpositionForBeanImpl(coreServicesBaseType),
			  new ServicesClientProxyForCoreBeanExposed(),
			  eventHandling,
			  subModulesCfgs,
			  coreBootstrapGuiceModule,
			  isolate);
	}
	public ServicesCoreGuiceBootstrapConfigWhenBeanExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
												      	   final ServicesCoreModuleExpositionForBeanImpl exposition,
												      	   final ServicesCoreModuleEventsConfig eventHandling,
												      	   final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs,
												      	   final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule) {
		super(coreAppCode,coreModule,
			  exposition,
			  new ServicesClientProxyForCoreBeanExposed(),
			  eventHandling,
			  subModulesCfgs,
			  coreBootstrapGuiceModule,
			  true);		// usually bean core modules contains DB modules (jpa) that MUST be binded in a private module
	}
}
