package r01f.bootstrap.services;

import java.util.Map;

import com.google.inject.Binder;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenRESTExposed;
import r01f.bootstrap.services.config.core.ServicesCoreGuiceBootstrapConfig;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.services.ServiceMatcher;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * When more than a single coreAppCode / module is found in the classpath there's a big chance for a collision of 
 * binded resources like JPA's EntityManager that MUST be binded at guice's {@link PrivateModule}s 
 * (see guice multiple persist modules at https://github.com/google/guice/wiki/GuicePersistMultiModules)
 * 
 * The solution is isolate core bindings for every coreAppCode / module at a separate private module and expose only 
 * the public service interface implementations.
 */
@Slf4j
@RequiredArgsConstructor
public class ServicesCoreBootstrapPrivateGuiceModule 
     extends PrivateModule {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * client config
	 */
	private final ServicesClientBootstrapConfig _clientConfig;
	/**
	 * Core config
	 */
	private final ServicesCoreGuiceBootstrapConfig<?,?> _coreModuleCfg;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings({"rawtypes"})
	protected void configure() {
		Binder privateBinder = this.binder();
		
		log.warn("[START]-Binding PRIVATE CORE guice module for {}/{} using {}",
				 _coreModuleCfg.getCoreAppCode(),_coreModuleCfg.getCoreModule(),
				 _coreModuleCfg.getCoreBootstrapGuiceModuleType());
		
		// [1] - Create the core bootstrap guice module and install it as PRIVATE module
		// 		 BEWARE!!!	do NOT install the REST core buide modules (they're binded at ServicesMainGuiceBootstrap, otherwise they're not visible
		// 					to the outside world and so the Guice Servlet filter cannot see REST resources)
		ServicesCoreBootstrapGuiceModule coreBootstrapModule = ServicesBootstrapUtil.createCoreGuiceModuleInstance(_coreModuleCfg);
		privateBinder.install(coreBootstrapModule);
			
		log.warn("  [END]-Binding PRIVATE CORE guice module for {}/{}: {}",
				 _coreModuleCfg.getCoreAppCode(),_coreModuleCfg.getCoreModule(),
				 _coreModuleCfg.getCoreBootstrapGuiceModuleType());
		
		
		// [2] - Find a match for each service interfaces to a bean impl or a proxy
		Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> serviceIfaceTypeToImplOrProxyType = _serviceInterfaceToImplOrProxy();
		
		// [3] - Bind service interfaces to a proxy or impl
		for (Map.Entry<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> me : serviceIfaceTypeToImplOrProxyType.entrySet()) {
			Class<? extends ServiceInterface> iface = me.getKey();
			Class<? extends ServiceInterface> implOrProxy = me.getValue();
			
			// bind the service impl or proxy as singleton
			_bindServiceInterfaceToImplOrProxy(privateBinder,
											   _captureType(iface),_captureSubType(implOrProxy));	
		}
		
		// [4] - Expose the service interface types to bean impl or proxy types as:
		//		 [a] - A MapBinder that binds the service interface type to the bean impl or proxy instance
		//			   This MapBinder is used at the API's proxy aggregator to inject the correct service interface bean impl or proxy 
		//			   (see ServicesClientProxyLazyLoaderGuiceMethodInterceptor)
		//		 [b] - A direct bind of the service intereface type to the bean impl or proxy type
		Named mapBinderNamed = Names.named(Strings.customized("{}.{}",
															  _coreModuleCfg.getCoreAppCode(),_coreModuleCfg.getCoreModule()));
		MapBinder<Class,ServiceInterface> serviceIfaceTypeToImplOrProxyBinder = MapBinder.newMapBinder(privateBinder,
																				 				 	   Class.class,ServiceInterface.class,
																				 				 	   mapBinderNamed);
		int numBindings = 0;
		if (CollectionUtils.hasData(serviceIfaceTypeToImplOrProxyType)) {
			for (Map.Entry<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> me : serviceIfaceTypeToImplOrProxyType.entrySet()) {
				// a an interface to impl / proxy binding to the Map used at ServicesClientProxyLazyLoaderGuiceMethodInterceptor
				serviceIfaceTypeToImplOrProxyBinder.addBinding(me.getKey())
								 			 	   .to(me.getValue());
				// expose the service interface binding
				this.expose(me.getKey());				
				numBindings++;
			}
		}			
		// expose the MapBinder to be injected and used at ServicesClientProxyLazyLoaderGuiceMethodInterceptor
		this.expose(new TypeLiteral<Map<Class,ServiceInterface>>() { /* nothing */ })
			.annotatedWith(mapBinderNamed);
		log.warn("\t{} service interface to bean impl or proxy bindings exposed as @Named({}.{})",
				 numBindings,
				 _coreModuleCfg.getCoreAppCode(),_coreModuleCfg.getCoreModule());
		
		// [5] - Expose bindings outside the private module if the core bootstrap guice module exposes any of it's bindings
		if (coreBootstrapModule instanceof ServicesCoreBootstrapGuiceModuleExposesBindings) {
			ServicesCoreBootstrapGuiceModuleExposesBindings exposes = (ServicesCoreBootstrapGuiceModuleExposesBindings)coreBootstrapModule;
			exposes.exposeBindings(this.binder());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Find a match for each service interfaces to a bean impl or a proxy
	 * @return
	 */
	private Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> _serviceInterfaceToImplOrProxy() {
		log.warn("[START]-Binding service interfaces to proxy or impl for {}/{}",
				 _coreModuleCfg.getCoreAppCode(),_coreModuleCfg.getCoreModule());
		log.warn("\t- service interface MUST extend {}",
				 _clientConfig.getServiceInterfacesBaseType());
		
		ServiceMatcher serviceMatcher = new ServiceMatcher(this.getClass().getClassLoader());
		
		Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> serviceIfaceTypeToImplOrProxyType = null;
		if (_coreModuleCfg.getExposition().getServiceImpl() == ServicesImpl.Bean) {
			log.warn("\t- core bean service interface implementations MUST extend {}",
					 _coreModuleCfg.as(ServicesCoreBootstrapConfigWhenBeanExposed.class)
					 			   .getExposition().getCoreServicesBaseType());
		    
			serviceIfaceTypeToImplOrProxyType = serviceMatcher.findServiceInterfaceMatchings(_clientConfig.getServiceInterfacesBaseType(),
		    																				 null,		// no proxy
		    																				 _coreModuleCfg.as(ServicesCoreBootstrapConfigWhenBeanExposed.class)
		    																				 			   .getExposition().getCoreServicesBaseType());
		} 
		else if (_coreModuleCfg.getExposition().getServiceImpl() == ServicesImpl.REST) {
			log.warn("\t- rest service interface proxy implementations MUST extend {}",
					 _coreModuleCfg.as(ServicesCoreBootstrapConfigWhenRESTExposed.class)
					 					.getClientProxyConfig()
					 						.getServiceProxyImplsBaseType());
			
		    serviceIfaceTypeToImplOrProxyType = serviceMatcher.findServiceInterfaceMatchings(_clientConfig.getServiceInterfacesBaseType(),
		    																				 _coreModuleCfg.as(ServicesCoreBootstrapConfigWhenRESTExposed.class)
																							 					.getClientProxyConfig()
																							 						.getServiceProxyImplsBaseType(),	
		    																				 null);		// no bean impl
		}
		log.warn(ServiceMatcher.serviceInterfaceMatchingsDebugInfoFor(serviceIfaceTypeToImplOrProxyType));
		return serviceIfaceTypeToImplOrProxyType;
	}
	private static <S extends ServiceInterface> void _bindServiceInterfaceToImplOrProxy(final Binder binder,
																				   	    final Class<S> serviceInterfaceType,
																				   	    final Class<? extends S> serviceInterfaceImpOrProxyType) {
		// bind the service impl or proxy as singleton
		binder.bind(serviceInterfaceImpOrProxyType)
			  .in(Singleton.class);
		// bind the interface to the impl or proxy
		binder.bind(serviceInterfaceType)
			  .to(serviceInterfaceImpOrProxyType);	
	}
	@SuppressWarnings("unchecked")
	private static <S extends ServiceInterface> Class<S> _captureType(final Class<? extends ServiceInterface> type) {
		return (Class<S>)type;
	}
	@SuppressWarnings("unchecked")
	private static <S extends ServiceInterface> Class<? extends S> _captureSubType(final Class<? extends ServiceInterface> type) {
		return (Class<? extends S>)type;
	}
}
