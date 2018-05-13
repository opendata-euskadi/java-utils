package r01f.bootstrap.services.config.core;

import java.util.Collection;

import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesBootstrapConfigBuilder;
import r01f.bootstrap.services.config.core.ServicesClientProxyForCoreServletExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenServletExposed;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.bootstrap.services.config.core.ServicesCoreModuleExpositionForServletImpl;
import r01f.bootstrap.services.config.core.ServicesCoreSubModuleBootrapConfig;
import r01f.bootstrap.services.core.ServletImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.types.url.Host;
import r01f.types.url.UrlPath;

/**
 * @see ServicesBootstrapConfigBuilder
 */
@Accessors(prefix="_")
public class ServicesCoreGuiceBootstrapConfigWhenServletExposed
	 extends ServicesCoreGuiceBootstrapConfigBase<ServicesCoreModuleExpositionForServletImpl,
	 									  ServicesClientProxyForCoreServletExposed> 
  implements ServicesCoreBootstrapConfigWhenServletExposed {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesCoreGuiceBootstrapConfigWhenServletExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
												         	  final ServicesCoreModuleExpositionForServletImpl servletExpCfg,
												         	  final ServicesClientProxyForCoreServletExposed servletClientProxyCfg,
												         	  final ServicesCoreModuleEventsConfig eventHandling,
												         	  final Collection<ServicesCoreSubModuleBootrapConfig<?>> subModulesCfgs,
												         	  final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule,
												         	  final boolean isolate) {
		super(coreAppCode,coreModule,
			  servletExpCfg,servletClientProxyCfg,
			  eventHandling,
			  subModulesCfgs,
			  coreBootstrapGuiceModule,
			  isolate);
	}
	public ServicesCoreGuiceBootstrapConfigWhenServletExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
												      	 	  final ServicesCoreModuleExpositionForServletImpl servletExpCfg,	
												      	 	  final ServicesClientProxyForCoreServletExposed servletClientProxyCfg,
												      	 	  final ServicesCoreModuleEventsConfig eventHandling,
												      	 	  final Collection<ServicesCoreSubModuleBootrapConfig<?>>  subModulesCfgs,
												      	 	  final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule) {
		this(coreAppCode,coreModule, 
			 servletExpCfg,servletClientProxyCfg,
			 eventHandling,
			 subModulesCfgs,
			 coreBootstrapGuiceModule,
			 false);	// Servlet guice modules MUST NOT be binded as private modules; otherwise guice servlet filter cannot see Servlet resource bindings
	}
	public ServicesCoreGuiceBootstrapConfigWhenServletExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,
												      	 	  final Host servletEndPointHost,final UrlPath servletPath,
												      	 	  final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType, 
												      	 	  final ServicesCoreModuleEventsConfig eventHandling,
												      	 	  final Collection<ServicesCoreSubModuleBootrapConfig<?>> subModulesCfgs,
												      	 	  final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule,
												      	 	  final boolean isolate) {
		super(coreAppCode,coreModule,
			  new ServicesCoreModuleExpositionForServletImpl(servletEndPointHost,servletPath),
			  new ServicesClientProxyForCoreServletExposed(serviceProxyImplsBaseType),
			  eventHandling,
			  subModulesCfgs,
			  coreBootstrapGuiceModule,
			  isolate);
	}
	public ServicesCoreGuiceBootstrapConfigWhenServletExposed(final CoreAppCode coreAppCode,final CoreModule coreModule,												         final Host servletEndPointHost,final UrlPath servletPath,
												         	  final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType,
												         	  final ServicesCoreModuleEventsConfig eventHandling,
												         	  final Collection<ServicesCoreSubModuleBootrapConfig<?>> subModulesCfgs,
												         	  final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModule) {
		super(coreAppCode,coreModule,
			  new ServicesCoreModuleExpositionForServletImpl(servletEndPointHost,servletPath),
			  new ServicesClientProxyForCoreServletExposed(serviceProxyImplsBaseType),
			  eventHandling,
			  subModulesCfgs,
			  coreBootstrapGuiceModule,
			  false);	// Servlet guice modules MUST NOT be binded as private modules; otherwise guice servlet filter cannot see Servlet resource bindings

	}
}
