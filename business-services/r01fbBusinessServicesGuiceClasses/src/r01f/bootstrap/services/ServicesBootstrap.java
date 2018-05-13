package r01f.bootstrap.services;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.Module;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.R01FBootstrapGuiceModule;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.client.ServicesClientGuiceBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreGuiceBootstrapConfig;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
@Slf4j
@RequiredArgsConstructor
public class ServicesBootstrap {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Bootstrap configs
	 */
	private final Collection<ServicesBootstrapConfig> _bootstrapCfgs;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
  	 * Load bootstrap module instances
	 *	- If there's more than a single api appCode a private module for every api appCode is returned so
	 *	  there's NO conflict between each api appCode
	 *	- If there's a single api appCode there's no need to isolate every api appcode in it's own private module
	 * @return
	 */
	Collection<Module> loadBootstrapModuleInstances() {
		// [1] - Bootstrap every config
		log.warn("\n\n\n\n");
		log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		log.warn("BOOTSTRAPING");
		log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		final List<Module> bootstrapModules = Lists.newArrayList();
		for (final ServicesBootstrapConfig bootstrapCfg  : _bootstrapCfgs) {
			final Collection<Module> clientAndCodeBootstrap = _createClientAndCoreBootstrapModules(bootstrapCfg);

			if (CollectionUtils.hasData(clientAndCodeBootstrap)) bootstrapModules.addAll(clientAndCodeBootstrap);
		}
		// [2] - Add the mandatory R01F guice modules
		bootstrapModules.add(0,new R01FBootstrapGuiceModule());

		return bootstrapModules;
	}
	/**
	 * Creates a module for the API appCode that gets installed with:
	 * 	- A module with the client API bindings
	 *	- A private module with the core bindings for each core app module
	 * @param serviceClientDef
	 * @param serviceIfacesToImplAndProxiesByAppModule
	 * @param coreBootstrapModulesTypesByAppAndModule
	 * @return
	 */
	private Collection<Module> _createClientAndCoreBootstrapModules(final ServicesBootstrapConfig servicesBootstrapCfg) {
		// contains all the guice modules to be bootstraped: client & core
		final List<Module> bootstrapModuleInstances = Lists.newArrayList();

		// a) Add the CLIENT bootstrap guice module
		if (servicesBootstrapCfg.getClientConfig() != null) {
			ServicesClientGuiceBootstrapConfig clientBootstrapCfg = servicesBootstrapCfg.getClientConfigAs(ServicesClientGuiceBootstrapConfig.class);
			final ServicesClientAPIBootstrapGuiceModuleBase clientModule = ServicesBootstrapUtil.createClientGuiceModuleInstance(clientBootstrapCfg);
			bootstrapModuleInstances.add(0,clientModule);	// insert first!
		} else {
			log.warn("NO client will be bootstrapped!");
		}

		// b) - Add a module for each CORE appCode / module
		for (final ServicesCoreBootstrapConfig<?,?> coreModuleCfg : servicesBootstrapCfg.getCoreModulesConfig()) {
			// Each core bootstrap modules (the ones implementing BeanImplementedServicesCoreGuiceModuleBase) for every core appCode / module
			// SHOULD reside in it's own private guice module in order to avoid bindings collisions
			// (ie JPA's guice persist modules MUST reside in separate private guice modules -see https://github.com/google/guice/wiki/GuicePersistMultiModules-)
			// ... BUT the REST or Servlet core bootstrap modules (the ones extending RESTImplementedServicesCoreGuiceModuleBase) MUST be binded here
			// in order to let the world see (specially the Guice Servlet filter) see the REST resources bindings
			ServicesCoreGuiceBootstrapConfig<?,?> guiceCoreModuleCfg = (ServicesCoreGuiceBootstrapConfig<?,?>)coreModuleCfg;
			
			if (guiceCoreModuleCfg.getCoreBootstrapGuiceModuleType() == null) {
				log.error("Could NOT bootstrap core module {}/{} since the guice module type is null",
						  coreModuleCfg.getCoreAppCode(),coreModuleCfg.getCoreModule());
				continue;
			}

			Module coreGuiceModule = null;							
			if (guiceCoreModuleCfg.isIsolate()) {
				if (servicesBootstrapCfg.getClientConfig() == null) throw new IllegalStateException("Only modules with client can be isolated!");
				
				// isolated core module
				coreGuiceModule = new ServicesCoreBootstrapPrivateGuiceModule(servicesBootstrapCfg.getClientConfig(),
																			  guiceCoreModuleCfg);	
			} else {
				// not isolated core module
				coreGuiceModule = ServicesBootstrapUtil.createCoreGuiceModuleInstance(guiceCoreModuleCfg);								
			}
			bootstrapModuleInstances.add(coreGuiceModule);
		}
		
		// c) return
		return bootstrapModuleInstances;
	}
}
