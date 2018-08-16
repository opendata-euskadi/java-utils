package r01f.bootstrap.services.config.core;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.bootstrap.services.core.BeanImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.RESTImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.ServletImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.patterns.IsBuilder;
import r01f.services.core.CoreService;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.types.url.Host;
import r01f.types.url.UrlPath;
import r01f.util.types.collections.CollectionUtils;


/**
 * Builder for ServicesConfig
 * Usage: 
 * <pre class='brush:java'>
 * </pre>
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServicesCoreBootstrapConfigBuilder 
	       implements IsBuilder {
//////////////////////////////////////////////////////////////////////////////
//  CORE BEAN
//////////////////////////////////////////////////////////////////////////////
	public static ServicesConfigBuilderCOREBootstapTypeStep forCoreAppAndModule(final CoreAppCode coreAppCode,final CoreModule coreMod) {
		return new ServicesCoreBootstrapConfigBuilder() { /* nothing */ }
				.new ServicesConfigBuilderCOREBootstapTypeStep(coreAppCode,coreMod);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderCOREBootstapTypeStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		
		public ServicesConfigBuilderBeanCOREBootstapGuiceModuleStep beanImplemented() {
			return new ServicesConfigBuilderBeanCOREBootstapGuiceModuleStep(_coreAppCode,_coreModule);
		}
		public ServicesConfigBuilderRESTCOREBootstapGuiceModuleStep restImplemented() {
			return new ServicesConfigBuilderRESTCOREBootstapGuiceModuleStep(_coreAppCode,_coreModule);
		}
		public ServicesConfigBuilderServletCOREBootstapGuiceModuleStep servletImplemented() {
			return new ServicesConfigBuilderServletCOREBootstapGuiceModuleStep(_coreAppCode,_coreModule);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE BEAN
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderBeanCOREBootstapGuiceModuleStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		
		public ServicesConfigBuilderBeanCOREBootstapServicesImplStep bootstrappedBy(final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType) {
			return new ServicesConfigBuilderBeanCOREBootstapServicesImplStep(_coreAppCode,_coreModule,
																			 coreBootstrapGuiceModuleType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderBeanCOREBootstapServicesImplStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		
		public ServicesConfigBuilderBeanCOREBootstapEventHandlingStep findServicesExtending(final Class<? extends CoreService> servicesImplIfaceType) {
			return new ServicesConfigBuilderBeanCOREBootstapEventHandlingStep(_coreAppCode,_coreModule,
																			  _coreBootstrapGuiceModuleType,
																			  servicesImplIfaceType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderBeanCOREBootstapEventHandlingStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		protected final Class<? extends CoreService> _coreServicesBaseType;
		
		public ServicesConfigBuilderBeanCOREBootstapSubModuleStep backgroundEventsHandledWith(final ServicesCoreModuleEventsConfig eventHandlingCfg) {
			return new ServicesConfigBuilderBeanCOREBootstapSubModuleStep(_coreAppCode,_coreModule,
																			_coreBootstrapGuiceModuleType,
																			_coreServicesBaseType,
																			eventHandlingCfg);
		}
		public ServicesConfigBuilderBeanCOREBootstapSubModuleStep noBackgroundEvents() {
			return new ServicesConfigBuilderBeanCOREBootstapSubModuleStep(_coreAppCode,_coreModule,
																		  _coreBootstrapGuiceModuleType,
																		  _coreServicesBaseType,
																		  null);		// no event handling			
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderBeanCOREBootstapSubModuleStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		protected final Class<? extends CoreService> _coreServicesBaseType;
		protected final ServicesCoreModuleEventsConfig _eventHandling;
		
		public ServicesConfigBuilderBeanCOREBootstapModuleBuildStep withSubModulesConfigs(final ServicesCoreSubModuleBootstrapConfig<?>... subModulesCfgs) {
			return new ServicesConfigBuilderBeanCOREBootstapModuleBuildStep(_coreAppCode,_coreModule,
															   			    _coreBootstrapGuiceModuleType,
															   			    _coreServicesBaseType,
															   			    _eventHandling,
															   			    CollectionUtils.hasData(subModulesCfgs) ? Lists.newArrayList(subModulesCfgs) : null);
		}
		public ServicesConfigBuilderBeanCOREBootstapModuleBuildStep withoutSubModules() {
			return new ServicesConfigBuilderBeanCOREBootstapModuleBuildStep(_coreAppCode,_coreModule,
															   			    _coreBootstrapGuiceModuleType,
															   			    _coreServicesBaseType,
															   			    _eventHandling,
															   			    null);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderBeanCOREBootstapModuleBuildStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		protected final Class<? extends CoreService> _coreServicesBaseType;
		protected final ServicesCoreModuleEventsConfig _eventHandling;
		protected final Collection<ServicesCoreSubModuleBootstrapConfig<?>> _subModulesCfgs;
		
		public ServicesCoreBootstrapConfigWhenBeanExposed build() {
			return new ServicesCoreGuiceBootstrapConfigWhenBeanExposed(_coreAppCode,_coreModule,
															   	  	   _coreServicesBaseType,
															   	  	   _eventHandling,
															   	  	   _subModulesCfgs,
															   	  	   _coreBootstrapGuiceModuleType,
															   	  	   true);	// isolated by default
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE REST
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderRESTCOREBootstapGuiceModuleStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		
		public ServicesConfigBuilderRESTCOREBootstapEndPointStep bootstrappedBy(final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType) {
			return new ServicesConfigBuilderRESTCOREBootstapEndPointStep(_coreAppCode,_coreModule,
																		 coreBootstrapGuiceModuleType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderRESTCOREBootstapEndPointStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		
		public ServicesConfigBuilderRESTCOREBootstapClientProxiesStep exposedAt(final Host restEndPointHost,final UrlPath restEndPointBasePath) {
			return new ServicesConfigBuilderRESTCOREBootstapClientProxiesStep(_coreAppCode,_coreModule,
																			  _coreBootstrapGuiceModuleType,
																			  restEndPointHost,restEndPointBasePath);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderRESTCOREBootstapClientProxiesStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		protected final Host _restEndPointHost;
		protected final UrlPath _restEndPointBasePath;
		
		public ServicesConfigBuilderRESTCOREBootstapBuildStep findClientProxiesExtending(final Class<? extends ServiceProxyImpl> serviceProxyBaseType) {
			return new ServicesConfigBuilderRESTCOREBootstapBuildStep(_coreAppCode,_coreModule,
																	  _coreBootstrapGuiceModuleType,
																	  _restEndPointHost,_restEndPointBasePath,
																	  serviceProxyBaseType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderRESTCOREBootstapBuildStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		protected final Host _restEndPointHost;
		protected final UrlPath _restEndPointBasePath;
		protected final Class<? extends ServiceProxyImpl> _serviceProxyImplsBaseType;
		
		public ServicesCoreBootstrapConfigWhenRESTExposed build() {
			return new ServicesCoreGuiceBootstrapConfigWhenRESTExposed(_coreAppCode,_coreModule,
															   	  	   _restEndPointHost,_restEndPointBasePath,
															   	  	   _serviceProxyImplsBaseType,
															   	  	   null,	// no event handling
															   	  	   null, // no sub-modules
															   	  	   _coreBootstrapGuiceModuleType);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  CORE SERVLET 
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderServletCOREBootstapGuiceModuleStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		
		public ServicesConfigBuilderServletCOREBootstapEndPointStep bootstrappedBy(final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> coreBootstrapGuiceModuleType) {
			return new ServicesConfigBuilderServletCOREBootstapEndPointStep(_coreAppCode,_coreModule,
																		    coreBootstrapGuiceModuleType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderServletCOREBootstapEndPointStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		
		public ServicesConfigBuilderServletCOREBootstapClientProxiesStep exposedAt(final Host servletEndPointHost,final UrlPath servletPath) {
			return new ServicesConfigBuilderServletCOREBootstapClientProxiesStep(_coreAppCode,_coreModule,
																			     _coreBootstrapGuiceModuleType,
																			     servletEndPointHost,servletPath);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderServletCOREBootstapClientProxiesStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		protected final Host _servletEndPointHost;
		protected final UrlPath _servletPath;
		
		public ServicesConfigBuilderServletCOREBootstapBuildStep findClientProxiesExtending(final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType) {
			return new ServicesConfigBuilderServletCOREBootstapBuildStep(_coreAppCode,_coreModule,
																	     _coreBootstrapGuiceModuleType,
																	     _servletEndPointHost,_servletPath,
																	     serviceProxyImplsBaseType);
		}
		public ServicesCoreBootstrapConfigWhenServletExposed build() {
			return new ServicesConfigBuilderServletCOREBootstapBuildStep(_coreAppCode,_coreModule,
																	     _coreBootstrapGuiceModuleType,
																	     _servletEndPointHost,_servletPath,
																	     null)
							.build();
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesConfigBuilderServletCOREBootstapBuildStep {
		protected final CoreAppCode _coreAppCode;
		protected final CoreModule _coreModule;
		protected final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> _coreBootstrapGuiceModuleType;
		protected final Host _servletEndPointHost;
		protected final UrlPath _servletPath;
		protected final Class<? extends ServiceProxyImpl> _serviceProxyImplsBaseType;
		
		public ServicesCoreBootstrapConfigWhenServletExposed build() {
			return new ServicesCoreGuiceBootstrapConfigWhenServletExposed(_coreAppCode,_coreModule,
															      	 	  _servletEndPointHost,_servletPath,
															      	 	  _serviceProxyImplsBaseType,
															      	 	  null,	// no event handling
															      	 	  null,	// no sub-modules
															      	 	  _coreBootstrapGuiceModuleType);
		}
	}
}
