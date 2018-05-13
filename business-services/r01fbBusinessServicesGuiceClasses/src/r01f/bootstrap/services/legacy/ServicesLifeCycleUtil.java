package r01f.bootstrap.services.legacy;


import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.ServicesBootstrapUtil;
import r01f.reflection.ReflectionException;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.collections.CollectionUtils;

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
@Deprecated
@Slf4j
  class ServicesLifeCycleUtil 
extends ServicesBootstrapUtil {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the guice modules to do the bootstrapping
	 * @param servicesInitData
	 * @return
	 */
	public static ServicesMainGuiceBootstrapCommonBindingModules getBootstrapGuiceModules(final ServicesInitData... servicesInitData) {
		if (CollectionUtils.isNullOrEmpty(servicesInitData)) throw new IllegalArgumentException();
		return ServicesLifeCycleUtil.getBootstrapGuiceModules(Arrays.asList(servicesInitData));
	}
	/**
	 * Returns the guice modules to do the bootstrapping
	 * @param servicesInitData
	 * @return
	 */
	public static ServicesMainGuiceBootstrapCommonBindingModules getBootstrapGuiceModules(final Collection<ServicesInitData> servicesInitData) {
		Collection<Module> bootstrapModules = ServicesMainGuiceBootstrap.createFor(servicesInitData)
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
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a client bootstrap guice module instance
	 * @param servicesClientBootstrapCfg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Module createGuiceModuleInstance(final Class<? extends Module> moduleType) {
		try {
			Module outMod =  ReflectionUtils.createInstanceOf(moduleType,
												    	 	  new Class<?>[] {  },
												    	 	  new Object[] {  });
			return outMod;
		} catch (ReflectionException refEx) {																					
			log.error("Could NOT create an instance of {} bootstrap guice module. The module MUST have a no-args constructor",
					  moduleType);
			throw refEx;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates the guice injector
	 * @param servicesInitData
	 * @return
	 */
	public static ServicesLifeCycleInjectorCommonBindingModules createGuiceInjector(final ServicesInitData... servicesInitData) {
		if (CollectionUtils.isNullOrEmpty(servicesInitData)) throw new IllegalArgumentException();
		return new ServicesLifeCycleInjectorCommonBindingModules(Arrays.asList(servicesInitData));
	}
	/**
	 * Creates the guice injector
	 * @param servicesInitData
	 * @return
	 */
	public static ServicesLifeCycleInjectorCommonBindingModules createGuiceInjector(final Collection<ServicesInitData> servicesInitData) {
		return new ServicesLifeCycleInjectorCommonBindingModules(servicesInitData);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class ServicesLifeCycleInjectorCommonBindingModules {
		private final Collection<ServicesInitData> _servicesInitData;
		
		public Injector withoutCommonBindingModules() {
			return Guice.createInjector(ServicesLifeCycleUtil.getBootstrapGuiceModules(_servicesInitData)
															 .withoutCommonBindingModules());
		}
		public Injector withCommonBindingModules(final Module... modules) {
			return Guice.createInjector(ServicesLifeCycleUtil.getBootstrapGuiceModules(_servicesInitData)
															 .withCommonBindingModules(modules));
		}
		public Injector withCommonBindingModules(final Collection<Module> modules) {
			return Guice.createInjector(ServicesLifeCycleUtil.getBootstrapGuiceModules(_servicesInitData)
															 .withCommonBindingModules(modules));
		}
	}
}
