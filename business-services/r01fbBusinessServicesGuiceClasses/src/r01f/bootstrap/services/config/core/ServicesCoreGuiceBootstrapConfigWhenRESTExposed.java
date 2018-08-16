package r01f.bootstrap.services.config.core;

import java.util.Collection;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesBootstrapConfigBuilder;
import r01f.bootstrap.services.config.core.ServicesClientProxyForCoreRESTExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenRESTExposed;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.bootstrap.services.config.core.ServicesCoreModuleExpositionForRESTImpl;
import r01f.bootstrap.services.config.core.ServicesCoreSubModuleBootstrapConfig;
import r01f.bootstrap.services.core.RESTImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.types.url.Host;
import r01f.types.url.UrlPath;


/**
 * @see ServicesBootstrapConfigBuilder
 */
@Accessors(prefix="_")
public class ServicesCoreGuiceBootstrapConfigWhenRESTExposed
	 extends ServicesCoreGuiceBootstrapConfigBase<ServicesCoreModuleExpositionForRESTImpl,
	 									  ServicesClientProxyForCoreRESTExposed> 
  implements ServicesCoreBootstrapConfigWhenRESTExposed {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesCoreGuiceBootstrapConfigWhenRESTExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
												      	   final ServicesCoreModuleExpositionForRESTImpl restExpCfg,
												      	   final ServicesClientProxyForCoreRESTExposed clientProxyCfg,
												      	   final ServicesCoreModuleEventsConfig eventHandling,
												      	   final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs,
												      	   final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule,
												      	   final boolean isolate) {
		super(coreAppCode,coreModule,
			  restExpCfg,clientProxyCfg,
			  eventHandling,
			  subModulesCfgs,
			  coreBootstrapGuiceModule,
			  isolate);
	}
	public ServicesCoreGuiceBootstrapConfigWhenRESTExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
														   final ServicesCoreModuleExpositionForRESTImpl restExpCfg,
														   final ServicesClientProxyForCoreRESTExposed clientProxyCfg,
														   final ServicesCoreModuleEventsConfig eventHandling,
														   final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs,
														   final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule) {
		this(coreAppCode,coreModule, 
			 restExpCfg,clientProxyCfg,
			 eventHandling,
			 subModulesCfgs,
			 coreBootstrapGuiceModule,
			 false);	// REST guice modules MUST NOT be binded as private modules; otherwise guice servlet filter cannot see REST resource bindings
	}
	public ServicesCoreGuiceBootstrapConfigWhenRESTExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
												      	   final Host restEndPointHost,final UrlPath restEndPointBasePath,
												      	   final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType,
												      	   final ServicesCoreModuleEventsConfig eventHandling,
												      	   final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs,
												      	   final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule,
												      	   final boolean isolate) {
		super(coreAppCode,coreModule,
			  new ServicesCoreModuleExpositionForRESTImpl(restEndPointHost,restEndPointBasePath),
			  new ServicesClientProxyForCoreRESTExposed(serviceProxyImplsBaseType),
			  eventHandling,
			  subModulesCfgs,
			  coreBootstrapGuiceModule,
			  isolate);
	}
	public ServicesCoreGuiceBootstrapConfigWhenRESTExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
												      	   final Host restEndPointHost,final UrlPath restEndPointBasePath,
												      	   final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType,
												      	   final ServicesCoreModuleEventsConfig eventHandling,
												      	   final Collection<ServicesCoreSubModuleBootstrapConfig<?>> subModulesCfgs,
												      	   final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule) {
		this(coreAppCode,coreModule,
			 new ServicesCoreModuleExpositionForRESTImpl(restEndPointHost,restEndPointBasePath),
			 new ServicesClientProxyForCoreRESTExposed(serviceProxyImplsBaseType),
			 eventHandling,
			 subModulesCfgs,
			 coreBootstrapGuiceModule,
			 false);	// REST guice modules MUST NOT be binded as private modules; otherwise guice servlet filter cannot see REST resource bindings
	}	
}
