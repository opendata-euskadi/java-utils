package r01f.bootstrap.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.client.ServicesClientBootstrapGuiceModule;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.bootstrap.services.config.client.ServicesClientGuiceBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenRESTExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenServletExposed;
import r01f.bootstrap.services.config.core.ServicesCoreGuiceBootstrapConfig;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.reflection.ReflectionException;
import r01f.reflection.ReflectionUtils;
import r01f.service.ServiceHandler;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesComponentImpl;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Utility type that encapsulates the services life cycle operations
 * <ul>
 * 	<li>Guice injector creation</li>
 * 	<li>Start / Stop of services that needs an explicit starting (ie Persistence services, thread pools, indexexers, etc)</li>
 * </ul>
 *
 * This type is mainly used at:
 * <ul>
 * 	<li>ServletContextListeners of web apps that controls the lifecycle of the app</li>
 * 	<li>Test init classes</li>
 * </ul>
 */
@Slf4j
public class ServicesBootstrapUtil {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the guice modules to do the bootstrapping
	 * @param servicesBootstrapCfg
	 * @return
	 */
	public static ServicesMainGuiceBootstrapCommonBindingModules getBootstrapGuiceModules(final ServicesBootstrapConfig... servicesBootstrapCfg) {
		if (CollectionUtils.isNullOrEmpty(servicesBootstrapCfg)) throw new IllegalArgumentException();
		return ServicesBootstrapUtil.getBootstrapGuiceModules(Arrays.asList(servicesBootstrapCfg));
	}
	/**
	 * Returns the guice modules to do the bootstrapping
	 * @param servicesBootstrapCfg
	 * @return
	 */
	public static ServicesMainGuiceBootstrapCommonBindingModules getBootstrapGuiceModules(final Collection<ServicesBootstrapConfig> servicesBootstrapCfg) {
		Collection<Module> bootstrapModules = new ServicesBootstrap(servicesBootstrapCfg)
																		.loadBootstrapModuleInstances();
		return new ServicesMainGuiceBootstrapCommonBindingModules(bootstrapModules);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ServicesMainGuiceBootstrapCommonBindingModules {
		private final Collection<Module> _bootstrapModules;

		public Collection<Module> withoutCommonBindingModules() {
			return _bootstrapModules;
		}
		public Iterable<Module> withCommonBindingModules(final Module... modules) {
			return CollectionUtils.hasData(modules) ? this.withCommonBindingModules(Arrays.asList(modules))
													: this.withoutCommonBindingModules();
		}
		public Iterable<Module> withCommonBindingModules(final Collection<Module> modules) {
			Iterable<Module> allBootstrapModuleInstances = CollectionUtils.hasData(modules) ? Iterables.concat(_bootstrapModules,
																											   modules)
																						    : _bootstrapModules;
			return allBootstrapModuleInstances;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOTSTRAP GUICE MODULES CREATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a client bootstrap guice module instance
	 * @param servicesClientBootstrapCfg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <G extends ServicesClientBootstrapGuiceModule> G createClientGuiceModuleInstance(final ServicesClientGuiceBootstrapConfig servicesClientBootstrapCfg) {
		try {
			G outMod =  ReflectionUtils.createInstanceOf((Class<G>)servicesClientBootstrapCfg.getClientBootstrapGuiceModuleType(),
												    	 new Class<?>[] { ServicesClientGuiceBootstrapConfig.class },
												    	 new Object[] { servicesClientBootstrapCfg });
			return outMod;
		} catch (ReflectionException refEx) {
			log.error("Could NOT create an instance of {} bootstrap guice module. The module MUST have {}-based constructor",
					  servicesClientBootstrapCfg.getClientBootstrapGuiceModuleType(),
					  ServicesClientGuiceBootstrapConfig.class);
			throw refEx;
		}
	}
	/**
	 * Creates a core bootstrap guice module instance
	 * @param servicesCoreBootstrapCfg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <G extends ServicesCoreBootstrapGuiceModule> G createCoreGuiceModuleInstance(final ServicesCoreGuiceBootstrapConfig<?,?> servicesCoreBootstrapCfg) {
		try {
			// find the constructor arg type
			Class<?> cfgType = null;
			if (servicesCoreBootstrapCfg.getImplType() == ServicesImpl.Bean) {
				cfgType = ServicesCoreBootstrapConfigWhenBeanExposed.class;
			} else if (servicesCoreBootstrapCfg.getImplType() == ServicesImpl.REST) {
				cfgType = ServicesCoreBootstrapConfigWhenRESTExposed.class;
			} else if (servicesCoreBootstrapCfg.getImplType() == ServicesImpl.Servlet) {
				cfgType = ServicesCoreBootstrapConfigWhenServletExposed.class;
			}
			// create the module
			G outMod =  ReflectionUtils.createInstanceOf((Class<G>)servicesCoreBootstrapCfg.getCoreBootstrapGuiceModuleType(),
												    	 new Class<?>[] { cfgType },
												    	 new Object[] { servicesCoreBootstrapCfg });
			return outMod;
		} catch (ReflectionException refEx) {
			log.error("Could NOT create an instance of {} bootstrap guice module. The module MUST have a {}-based constructor",
					  servicesCoreBootstrapCfg.getCoreBootstrapGuiceModuleType(),
					  ServicesCoreGuiceBootstrapConfig.class);
			throw refEx;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Starts services that needs to be started
	 * @param hasServiceHandlerTypes
	 * @param injector
	 */
	public static void startServices(final Injector injector) {
		if (injector == null) throw new IllegalStateException("Cannot start services: no injector present!");

		// Init JPA's Persistence Service, Lucene indexes and everything that has to be started
		// (see https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree)
		Collection<Key<? extends ServiceHandler>> serviceHandlerBindingKeys = _getServiceHandlersGuiceBindingKeys(injector);
		if (CollectionUtils.hasData(serviceHandlerBindingKeys)) {
			for (Key<? extends ServiceHandler> key : serviceHandlerBindingKeys) {
				ServiceHandler serviceHandler = injector.getInstance(key);
				log.warn("\t--START SERVICE using {} type: {}",ServiceHandler.class.getSimpleName(),key);
				try {
					serviceHandler.start();
				} catch(Throwable th) {
					log.error("Error starting service with ServiceHandler key={}: {}",key,th.getMessage(),th);
				}
			}
		}
	}
	/**
	 * Stops services that needs to be started
	 * @param hasServiceHandlerTypes
	 * @param injector
	 */
	public static void stopServices(final Injector injector) {
		if (injector == null) {
			log.warn("NO injector present... cannot stop services");
			return;
		}

		// Close JPA's Persistence Service, Lucene indexes and everything that has to be closed
		// (see https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree)
		Collection<Key<? extends ServiceHandler>> serviceHandlerBindingKeys = _getServiceHandlersGuiceBindingKeys(injector);
		if (CollectionUtils.hasData(serviceHandlerBindingKeys)) {
			for (Key<? extends ServiceHandler> key : serviceHandlerBindingKeys) {
				ServiceHandler serviceHandler = injector.getInstance(key);
				if (serviceHandler != null) {
					log.warn("\t--END SERVICE {} type: {}",ServiceHandler.class.getSimpleName(),key);
					try {
						serviceHandler.stop();
					} catch(Throwable th) {
						log.error("Error stopping service with ServiceHandler key={}: {}",key,th.getMessage(),th);
					}
				}
			}
		}
	}
	/**
	 * Binds a service handler type and exposes it if it's a private binder
	 * @param binder
	 * @param serviceHandlerType
	 * @param name
	 */
	public static void bindServiceHandler(final Binder binder,
										  final Class<? extends ServiceHandler> serviceHandlerType,final String name) {
		binder.bind(ServiceHandler.class)
			  .annotatedWith(Names.named(name))
			  .to(serviceHandlerType)
			  .in(Singleton.class);
		if (binder instanceof PrivateBinder) {
			PrivateBinder privateBinder = (PrivateBinder)binder;
			privateBinder.expose(Key.get(ServiceHandler.class,
										 Names.named(name)));	// expose the binding
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Introspects the injector bindings to find all binding keys for {@link ServiceHandler} types
	 * @param injector
	 * @return
	 */
	private static Collection<Key<? extends ServiceHandler>> _getServiceHandlersGuiceBindingKeys(final Injector injector) {
		List<Binding<ServiceHandler>> bindings = injector.findBindingsByType(TypeLiteral.get(ServiceHandler.class));

//		Map<Key<?>, Binding<?>> m = injector.getAllBindings();
//		for (Key<?> k : m.keySet()) System.out.println("...." + k);

		Collection<Key<? extends ServiceHandler>> outKeys = Lists.newArrayListWithExpectedSize(bindings.size());
		for (Binding<ServiceHandler> binding : bindings) {
			Key<? extends ServiceHandler> key = binding.getKey();
			outKeys.add(key);
		}
		return outKeys;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BIND XMLProperties component
/////////////////////////////////////////////////////////////////////////////////////////
	public static void bindXMLPropertiesForAppComponent(final AppCode appCode,final AppComponent component,
														final AppComponent bindingName,
														final Binder binder) {
		log.warn("{} {} properties are available for injection as a {} annotated with @{}(\"{}\")",
				 appCode,component,
				 XMLPropertiesForAppComponent.class.getSimpleName(),XMLPropertiesForAppComponent.class.getSimpleName(),
				 bindingName);
		binder.bind(XMLPropertiesForAppComponent.class)
			  .annotatedWith(new XMLPropertiesComponentImpl(bindingName.asString())) // @XMLPropertiesComponent("xx.client")
			  .toProvider(// the provider
					  	  new Provider<XMLPropertiesForAppComponent>() {
					  				@Inject
					  				private XMLProperties _props;	// injected properties

									@Override
									public XMLPropertiesForAppComponent get() {
										return _props.forAppComponent(appCode,
																	  component);
									}
			  			  })
			  .in(Singleton.class);

		// Expose xml properties binding
//		if (binder instanceof PrivateBinder) {
//			PrivateBinder pb = (PrivateBinder)binder;
//			pb.expose(Key.get(XMLPropertiesForAppComponent.class,
//					  new XMLPropertiesComponentImpl(bindingName.asString())));
//		}
	}
}
