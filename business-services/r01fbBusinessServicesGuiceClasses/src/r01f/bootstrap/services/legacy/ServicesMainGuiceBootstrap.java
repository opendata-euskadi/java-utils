package r01f.bootstrap.services.legacy;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.Module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.R01FBootstrapGuiceModule;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.bootstrap.services.client.ServicesClientBootstrapGuiceModule;
import r01f.bootstrap.services.core.BeanImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.RESTImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.bootstrap.services.core.ServletImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.reflection.ReflectionUtils;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppAndModule;
import r01f.services.interfaces.ServiceInterface;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Bootstraps a service-oriented guice-based application
 * Basic usage:
 * <pre class='brush:java'>
 *		Collection<Module> bootstrapModules = ServicesMainGuiceBootstrap.createFor(ServicesInitDataBuilder.createForClientApi(apiAppCode,apiModule)
 *						 								  												  .bootstrapedBy(MyBootstrapGuiceModule.class)
 *						 								  												  .findServiceInterfacesAtDefaultPackage()	// finds services interfaces at apiAppCode.api.apiModule.interfaces
 *						 								  												  .forServiceImplementations(CoreServiceImpl.of(coreAppCode,coreModule)
 *						 										  																				    .defaultImpl(ServicesImpl.REST))
 *						 								  												  .exposedByApiType(AA81CommonClientAPI.class)
 *						 								  												  .withNamedServiceHandler(AA81AppCodes.COMMON_APP_AND_MOD_STR)
 *						 								  												  .build())
 *																		 .loadBootstrapModuleInstances();
 *		Injector injector = Guice.createInjector(bootstrapModuleInstances);
 * </pre>
 *
 * A service-oriented application has the following basic components:
 * <ul>
 * 		<li>The services</li>
 * 		<li>The modules that bootstrap the client</li>
 * 		<li>The modules that bootstrap the core (server)<li>
 * </ul>
 *
 *
 * If xx is the client appCode and yy is the core appCode (they can be the same appCode)
 *
 * Client bootstraping
 * ===================
 * at the java package xx.client.internal create two types:
 * 1.- A type that will get injected with the {@link ServiceInterface} to bean impl or proxy
 * 	   	- if the {@link ServiceInterface} bean impl is available the system will bind the interface to this impl
 * 		- if not, the {@link ServiceInterface} will be binded to a proxy set at xx.client.properties.xml,
 * 		  for example a proxy to the REST {@link ServiceInterface} impl
 * 	    <pre class='brush:java'>
 *			public class XXServiceInterfaceTypesToImplOrProxyMappings
 *			  implements ServiceInterfaceTypesToImplOrProxyMappings {
 *
 *				@Inject @Named("yy.mymodule") @SuppressWarnings({ "rawtypes" })
 *				private Map<Class,ServiceInterface> _grantedBenefitsServiceInterfaceTypesToImplOrProxy;
 *			}
 * 		</pre>
 * 	   Is important that:
 * 			- There MUST exist a Map<Class,ServiceInterface> field for every core appCode / module combination
 * 			  set at xx.client.properties.xml
 * 			- Each Map<Class,ServiceInterface> field MUST be annotated with @Named("yy.my_module") where yy.my_module is
 * 			  the proxy's appCode/id at xx.client.properties.xml
 * 			  <pre class='brush:xml'>
 *				<proxies>
 *					<proxy appCode="yy" id="my_module" impl="REST">My Module</proxy>
 *					<proxy appCode="yy" id="my_otherModule" impl="REST">My other module</proxy>
 *				</proxies>
 * 			  </pre>
 *
 * 2.- A bootstrap guice module extending {@link ServicesClientAPIBootstrapGuiceModuleBase}
 * 		<pre class='brush:java'>
 *			public class XXClientBootstrapGuiceModule
 *			  	 extends ServicesClientAPIBootstrapGuiceModuleBase {	// this is a client guice bindings module
 *
 *				public XXClientBootstrapGuiceModule() {
 *					super(XXAppCode.API.code(),
 *						  new XXServiceInterfaceTypesToImplOrProxyMappings());
 *				}
 *				@Override
 *				protected void _configure(final Binder binder) {
 *					_bindModelObjectsMarshaller(binder);
 *					_bindModelObjectExtensionsModule(binder);
 *				}
 *				@Override @SuppressWarnings("unchecked")
 *				protected <U extends UserContext> U _provideUserContext() {
 *					XXMockUserContextProvider provider = new XXMockUserContextProvider();
 *					return (U)provider.get();
 *				}
 *			}
 * 		</pre>
 *
 *	<clientPackages>
 *		<!-- The package where the service interfaces are located -->
 *		<lookForServiceInterfacesAt>aa81f.api.common.interfaces</lookForServiceInterfacesAt>
 *		<!-- In order to bootstrap the client, the system will look for types extending ServicesClientAPIBootstrapGuiceModuleBase at the provided package -->
 *		<lookForClientBootstrapTypesAt>aa81f.client.common.internal</lookForClientBootstrapTypesAt>
 *		<!-- The package where the client api is located -->
 *		<lookForClientApiAggregatorTypeAt>aa81f.client.common.api</lookForClientApiAggregatorTypeAt>
 *	</clientPackages>
 *
 *	<!--
 *	Client Proxies to be used to access the services
 *	(see ServicesMainGuiceBootstrap.java for details about how services client APIs are
 *	 bound to the services core implementation through a proxy)
 *	Values:
 *		- REST	 : use HTTP to access the REST end-point that exposes the services layer functions
 *		- Servlet: a pseudo-REST http access end-point
 *		- Bean	 : use the services layer functions directly accessing the beans that encapsulates them
 *		- EJB 	 : use RMI to access the EJB end-point that exposes the services layer functions
 *		- Mock 	 : use a mock-ed services layer functions
 *	-->
 *	<proxies>
 *		<proxy appCode="aa81b" id="common" impl="REST">Granted Benefits core</proxy>
 *	</proxies>
 *
 *
 * SERVER/CORE BOOTSTRAPING
 * ========================
 * At the java package yy.internal there MUST exist a type extending {@link BeanImplementedServicesCoreBootstrapGuiceModuleBase}
 * that bootstraps the core side.
 * This guice module type contains the core's bindings, usually installing other guice modules as db persistence, searching, notifier, etc
 * For convenience, if the core is in charge of the DB persistence, the type might extend {@link BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase}
 * This type MUST be annotated with @ServicesCore with the core's module id
 * <pre class='brush:java'>
 *		@ServicesCore(moduleId="my_module",dependsOn=ServicesImpl.NULL) 	// see xx.client.properties.xml
 *		@EqualsAndHashCode(callSuper=true)									// This is important for guice modules
 *		public class YYServicesBootstrapGuiceModule
 *		     extends BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase {
 *
 *			public YYServicesBootstrapGuiceModule() {
 *				super(XXAppCode.API.code(),
 *					  new YYDBGuiceModule(YYServicesBootstrapGuiceModule.class),			// DB
 *					  new YYSearchGuiceModule(YYServicesBootstrapGuiceModule.class),		// search
 *					  Lists.<Module>newArrayList(new YYNotifierGuiceModule(YYServicesBootstrapGuiceModule.class)));
 *			}
 *		}
 * </pre>
 */
@Deprecated
@Slf4j
class ServicesMainGuiceBootstrap {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	// Client definitions
	private Collection<ServiceClientDef> _clientDefs;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	private ServicesMainGuiceBootstrap(final ServiceClientDef... defs) {
		_clientDefs = Arrays.asList(defs);
	}
	private ServicesMainGuiceBootstrap(final ServicesInitData... initData) {
		// Transform the init data to client defs
		final Collection<ServiceClientDef> clientDefs = Lists.newArrayListWithExpectedSize(initData.length);

		for (final ServicesInitData currInitData : initData) {
			clientDefs.add(new ServiceClientDef(currInitData));
		}
		_clientDefs = clientDefs;
	}
	private ServicesMainGuiceBootstrap(final ClientApiAppCode... apiAppCodes) {
		// The service client defs are loaded from a properties file
		final Collection<ServiceClientDef> clientDefs = Lists.newArrayListWithExpectedSize(apiAppCodes.length);

		for (final ClientApiAppCode apiAppCode : apiAppCodes) {
			final XMLPropertiesForApp apiProps = XMLPropertiesBuilder.createForApp(apiAppCode.asAppCode())
									 						  .notUsingCache();
			final XMLPropertiesForAppComponent apiModuleProperties = apiProps.forComponent(AppComponent.forId("client"));


			// Create the service definition
			final ServiceClientDef def = new ServiceClientDef(new ServicesInitData(apiAppCode,
																 				   apiModuleProperties));
			clientDefs.add(def);
		} // for
		_clientDefs = clientDefs;
	}
	public static ServicesMainGuiceBootstrap createForApi(final ClientApiAppCode... apiAppCodes) {
		return new ServicesMainGuiceBootstrap(apiAppCodes);
	}
	public static ServicesMainGuiceBootstrap createForApi(final Collection<ClientApiAppCode> apiAppCodes) {
		return new ServicesMainGuiceBootstrap(apiAppCodes.toArray(new ClientApiAppCode[apiAppCodes.size()]));
	}
	public static ServicesMainGuiceBootstrap createFor(final ServicesInitData... initData) {
		if (CollectionUtils.isNullOrEmpty(initData)) throw new IllegalArgumentException();
		return new ServicesMainGuiceBootstrap(initData);
	}
	public static ServicesMainGuiceBootstrap createFor(final Collection<ServicesInitData> defs) {
		if (CollectionUtils.isNullOrEmpty(defs)) throw new IllegalArgumentException();
		return new ServicesMainGuiceBootstrap(defs.toArray(new ServicesInitData[defs.size()]));
	}
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
		// [1] - Find the client & core bootstrap modules
		log.warn("\n\n\n\n");
		log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		log.warn("PHASE I: CONFIGURING {} CLIENT APIs",_clientDefs.size());
		log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		final Collection<ClientAndCoreBootstrapData> bootstrapDatas = Lists.newArrayList();
		for (final ServiceClientDef clientDef : _clientDefs) {
			final ClientAndCoreBootstrapData currClientDefBootstrapData = _bootstrapGuiceModuleFor(clientDef);

			bootstrapDatas.add(currClientDefBootstrapData);
		}
		log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		log.warn("\n\n\n\n");

		// [2] - Check the core modules dependencies
		// TODO check core module dependencies

		// [3] - Create a bundle module for every client - core
		log.warn("\n\n\n\n");
		log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		log.warn("PHASE II: BOOTSTRAPING");
		log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		final List<ClientApiAppCode> bootstrappedClientAppModules = Lists.newArrayList();
		final List<Module> bootstrapModules = Lists.newArrayList();
		for (final ClientAndCoreBootstrapData bootstrapData  : bootstrapDatas) {
			final boolean haveToBootstrapClient = !bootstrappedClientAppModules.contains(bootstrapData.getServiceClientDef().getClientApiAppCode());	// avoid bootstrapping the same client module twice
			final Module clientAndCodeBootstrap = _createClientAndCoreBootstrapModule(bootstrapData,
																					  haveToBootstrapClient);
			bootstrappedClientAppModules.add(bootstrapData.getServiceClientDef().getClientApiAppCode());			// avoid bootstrapping the same client module twice

			if (clientAndCodeBootstrap != null) bootstrapModules.add(clientAndCodeBootstrap);
		}
		log.warn(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		log.warn("\n\n\n\n");

		// [4] - Add the mandatory R01F guice modules
		bootstrapModules.add(0,new R01FBootstrapGuiceModule());

		return bootstrapModules;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	private static class ClientAndCoreBootstrapData {
		@Getter private final ServiceClientDef _serviceClientDef;
		@Getter private final Map<CoreAppAndModule,
     	    			          Set<ServiceToImplAndProxyDef<? extends ServiceInterface>>> _serviceIfacesToImplAndProxiesByAppModule;
		@Getter private final Map<CoreAppAndModule,
	        			          Collection<Class<? extends ServicesCoreBootstrapGuiceModule>>> _coreBootstrapModulesTypesByAppAndModule;
	}
	/**
	 * Creates a guice module that encapsulates all bindings for an api app code
	 * @param apiAppCode
	 * @return
	 */
	private static ClientAndCoreBootstrapData _bootstrapGuiceModuleFor(final ServiceClientDef serviceClientDef) {
		// [1] - A bit of logging
		log.warn("///////////////////////////////////////////////////////////////////////");
		log.warn("[CONFIGURING {} CLIENT: {} CORE MODULES {}]",
				  serviceClientDef.getClientApiAppCode(),
				  serviceClientDef.getCoreAppAndModules() != null ? serviceClientDef.getCoreAppAndModules().size() : 0,
				  serviceClientDef.getCoreAppAndModules());
		log.warn("{} Client api {} will be bootstrapped with {}",
				 serviceClientDef.getClientApiAppCode(),
				 serviceClientDef.getApiType(),
				 serviceClientDef.getClientApiBootstrapType());
		log.warn("Service proxies will be looked for at: {}",
				 serviceClientDef.getServicesProxiesAggregatorType().getPackage().getName());


		// [2] - Find the CORE (server) bootstrap guice module types for given cores
		//		 for each app/component combination there might be multiple Bean/REST/EJB/Servlet, etc core bootstrap modules
    	log.warn("[START]-Find CORE binding modules==============================================");
    	final ServicesCoreBootstrapModulesFinder coreBootstrapModulesFinder = new ServicesCoreBootstrapModulesFinder(serviceClientDef.getCoreAppAndModules());
		final Map<CoreAppAndModule,
				  Collection<Class<? extends ServicesCoreBootstrapGuiceModule>>> coreBootstrapModulesTypesByAppAndModule = coreBootstrapModulesFinder.findBootstrapGuiceModuleTypes();
		log.warn("  [END]-Find CORE binding modules==============================================");


		// [3] - Find the client-api service interface to proxy and / or impls matchings
		//		 now every client-api defined service interface is matched to a proxy implementation
		log.warn("[START]-Find ServiceInterface to bean impl/proxy matchings ================");
		final ServicesClientInterfaceToImplAndProxyFinder serviceIfaceToImplAndProxiesFinder = new ServicesClientInterfaceToImplAndProxyFinder(serviceClientDef.getPackageToLookForServiceInterfaces(),
																																		 	   JavaPackage.of(serviceClientDef.getServicesProxiesAggregatorType()),
																																		 	   serviceClientDef.getCoreAppAndModulesDefProxies());
		final Map<CoreAppAndModule,
				  Set<ServiceToImplAndProxyDef<? extends ServiceInterface>>> serviceIfacesToImplAndProxiesByAppModule = serviceIfaceToImplAndProxiesFinder.findServiceInterfacesToImplAndProxiesBindings(serviceClientDef.getCoreAppCodes());
		log.warn("  [END]-Find ServiceInterface to bean impl/proxy matchings ================");

		return new ClientAndCoreBootstrapData(serviceClientDef,
											  serviceIfacesToImplAndProxiesByAppModule,
											  coreBootstrapModulesTypesByAppAndModule);
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
	private static Module _createClientAndCoreBootstrapModule(final ClientAndCoreBootstrapData bootstrapData,
															  final boolean bootstrapClient) {
		log.warn("[START] Creating CLIENT & CORE MODULES");
		final Module outModule = new Module() {
					@Override
					public void configure(final Binder binder) {
						log.warn("///////////////////////////////////////////////////////////////////////");
						log.warn("[BOOTSTRAPING {} CLIENT: {} CORE MODULES {}]",
								  bootstrapData.getServiceClientDef().getClientApiAppCode(),
								  bootstrapData.getServiceClientDef().getCoreAppAndModules() != null ? bootstrapData.getServiceClientDef().getCoreAppAndModules().size() : 0,
								  bootstrapData.getServiceClientDef().getCoreAppAndModules());
						log.warn("{} Client api {} will be bootstrapped with {}",
								 bootstrapData.getServiceClientDef().getClientApiAppCode(),
								 bootstrapData.getServiceClientDef().getApiType(),
								 bootstrapData.getServiceClientDef().getClientApiBootstrapType());
						// contains all the guice modules to be bootstraped: client & core
						final List<Module> bootstrapModuleInstances = Lists.newArrayList();

						// a) Add the CLIENT bootstrap guice module
						if (bootstrapClient) {
							final ServicesClientAPIBootstrapGuiceModuleBase clientModule = (ServicesClientAPIBootstrapGuiceModuleBase)ServicesLifeCycleUtil.createGuiceModuleInstance(bootstrapData.getServiceClientDef().getClientApiBootstrapType());

							bootstrapModuleInstances.add(0,clientModule);	// insert first!
						}

						// b) - Add a private module for each appCode / module stack: service interface --> proxy --> impl (rest / bean / etc)
						//	    this way, each appCode / module is independent (isolated)
						final Collection<ServiceBootstrapDef> coreBootstrapGuiceModuleDefs = _coreBootstrapGuiceModuleDefsFrom(bootstrapData.getServiceClientDef(),
																													 	 	   bootstrapData.getCoreBootstrapModulesTypesByAppAndModule(),
																													 	 	   bootstrapData.getServiceIfacesToImplAndProxiesByAppModule());
						for (final ServiceBootstrapDef bootstrapCoreModDef : coreBootstrapGuiceModuleDefs) {
							// Each core bootstrap modules (the ones implementing BeanImplementedServicesCoreGuiceModuleBase) for every core appCode / module
							// SHOULD reside in it's own private guice module in order to avoid bindings collisions
							// (ie JPA's guice persist modules MUST reside in separate private guice modules -see https://github.com/google/guice/wiki/GuicePersistMultiModules-)
							final Module coreAppAndModulePrivateGuiceModule = new ServicesCoreForAppModulePrivateGuiceModule(bootstrapCoreModDef);
							bootstrapModuleInstances.add(coreAppAndModulePrivateGuiceModule);

							// ... BUT the REST or Servlet core bootstrap modules (the ones extending RESTImplementedServicesCoreGuiceModuleBase) MUST be binded here
							// in order to let the world see (specially the Guice Servlet filter) see the REST resources bindings
							final Collection<? extends ServicesCoreBootstrapGuiceModule> restCoreAppAndModuleGuiceModules = bootstrapCoreModDef.getPublicBootstrapGuiceModuleInstances();
							bootstrapModuleInstances.addAll(restCoreAppAndModuleGuiceModules);
						}

						// c) - Install the modules
						final Binder theBinder = binder;
						if (CollectionUtils.hasData(bootstrapModuleInstances)) {

							boolean clientBindingLogged = false;
							boolean coreBindingLogged = false;

							for (final Module module : bootstrapModuleInstances) {
								// a bit of log
								if (!clientBindingLogged && module instanceof ServicesClientBootstrapGuiceModule) {
									clientBindingLogged = true;
									log.warn("[Bind CLIENT Modules] {}");
									log.warn("=============================");
									clientBindingLogged = true;
								} else if (!coreBindingLogged && module instanceof ServicesCoreForAppModulePrivateGuiceModule) {
									log.warn("[Bind PRIVATE CORE Modules]");
									log.warn("=============================");
									coreBindingLogged = true;
								}
								// DO validate
								if (module instanceof ValidatedServicesGuiceModule)
									((ValidatedServicesGuiceModule)module).validate();
								// DO the install
								theBinder.install(module);

							}
						}
					}
				};
		log.warn("[END] Creating CLIENT & CORE MODULES");
		return outModule;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a list of the definition of core a module to be bootstrapped (a collection of {@link ServiceBootstrapDef} objects that encapsulates all data
	 * needed to bootstrap a core module)
	 * It's important to note that if a core module bootstrap type is NOT found, the DEFAULT proxy (see client config) is used
	 * @param serviceClientDef
	 * @param coreBootstrapModulesTypesByAppAndModule
	 * @param serviceInterfacesToImplAndProxyByAppModule
	 * @return
	 */
	@SuppressWarnings({ "unchecked","null" })
	private static Collection<ServiceBootstrapDef> _coreBootstrapGuiceModuleDefsFrom(final ServiceClientDef serviceClientDef,
																		  		 	 final Map<CoreAppAndModule,Collection<Class<? extends ServicesCoreBootstrapGuiceModule>>> coreBootstrapModulesTypesByAppAndModule,
																		  		 	 final Map<CoreAppAndModule,Set<ServiceToImplAndProxyDef<? extends ServiceInterface>>> serviceInterfacesToImplAndProxyByAppModule) {
		final Map<CoreAppAndModule,ServiceBootstrapDef> outSrvcBootstrapDefs = Maps.newHashMap();

		// [1]: Configure the definitions only with the default proxy
		for (final CoreAppAndModule coreAppAndComponent : serviceClientDef.getCoreAppAndModules()) {
			log.warn("/----------------------------------------------------------------------------------------------------------------------------\\");
			final ServiceBootstrapDef modDef = new ServiceBootstrapDef(serviceClientDef.getClientApiAppCode(),
															     	   coreAppAndComponent,
															     	   serviceClientDef.getCoreAppAndModulesDefProxies().get(coreAppAndComponent));
			log.warn("API MODULE {} to CORE MODULE {} using proxy={}",
					 modDef.getClientApiAppCode(),modDef.getCoreAppCodeAndModule(),
					 modDef.getDefaultProxyImpl());

			// check if the core bootstrap module is present
			final Collection<Class<? extends ServicesCoreBootstrapGuiceModule>> coreBootstrapGuiceModulesTypes = CollectionUtils.hasData(coreBootstrapModulesTypesByAppAndModule) ? coreBootstrapModulesTypesByAppAndModule.get(coreAppAndComponent)
																																									        	  : null;

			// the core bootstrap module is present
			if (CollectionUtils.hasData(coreBootstrapGuiceModulesTypes)) {
				log.warn("\t\tcore bootstrap modules detected: ");
				// divide the core bootstrap guice modules by type
				for (final Class<? extends ServicesCoreBootstrapGuiceModule> coreBootstrapGuiceModuleType : coreBootstrapGuiceModulesTypes) {
					log.warn("\t\t\t- {}",coreBootstrapGuiceModuleType);
					if (ReflectionUtils.isImplementing(coreBootstrapGuiceModuleType,
													   BeanImplementedServicesCoreBootstrapGuiceModuleBase.class)) {
						modDef.addCoreBeanBootstrapModuleType((Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase>)coreBootstrapGuiceModuleType);
					} else if (ReflectionUtils.isImplementing(coreBootstrapGuiceModuleType,
															  RESTImplementedServicesCoreBootstrapGuiceModuleBase.class)) {
						modDef.addCoreRESTBootstrapModuleType((Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase>)coreBootstrapGuiceModuleType);
					} else if (ReflectionUtils.isImplementing(coreBootstrapGuiceModuleType,
															  ServletImplementedServicesCoreBootstrapGuiceModuleBase.class)) {
						modDef.addCoreServletBootstrapModuleType((Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase>)coreBootstrapGuiceModuleType);
					} else {
						throw new IllegalArgumentException("Unsupported bootstrap guice module type: " + coreBootstrapGuiceModuleType);
					}
				}

				// set the service interface to impl and proxy binding definition
				if (serviceInterfacesToImplAndProxyByAppModule.get(coreAppAndComponent) == null) {
					log.warn("BEWARE!!!!! The core module {} is NOT accesible via a client-API service interface: " +
							 "there's NO client API service interface to impl and/or proxy binding for {}; " +
							 "check that the types implementing {} has the @{} annotation and the appCode/module attributes match the coreAppCode/module " +
							 "This is usually an ERROR except on coreAppCode/modules that do NOT expose anything to a client-api (ie the Servlet modules)",
							 coreAppAndComponent,
							 coreAppAndComponent,
							 ServiceInterface.class.getName(),ServiceInterfaceFor.class.getSimpleName(),
							 coreAppAndComponent.getAppCode());
				} else {
					modDef.setServiceInterfacesToImplAndProxiesDefs(serviceInterfacesToImplAndProxyByAppModule.get(coreAppAndComponent));

					// a bit of logging
					if (serviceInterfacesToImplAndProxyByAppModule.get(coreAppAndComponent) != null) {
						for (final ServiceToImplAndProxyDef<? extends ServiceInterface> serviceToImplAndProxyDef : serviceInterfacesToImplAndProxyByAppModule.get(coreAppAndComponent)) {
							log.warn("\t\tservice interface: {}",serviceToImplAndProxyDef.debugInfo());
						}
					}
				}
			}
			// the core bootstrap module is NOT present: set the service interface to impl and proxy binding definition
			else {
				log.warn("\t\tNO core bootstrap modules detected; using default proxy");
				modDef.setServiceInterfacesToImplAndProxiesDefs(serviceInterfacesToImplAndProxyByAppModule.get(coreAppAndComponent));

				// a bit of logging
				if (serviceInterfacesToImplAndProxyByAppModule.get(coreAppAndComponent) != null) {
					for (final ServiceToImplAndProxyDef<? extends ServiceInterface> serviceToImplAndProxyDef : serviceInterfacesToImplAndProxyByAppModule.get(coreAppAndComponent)) {
						log.warn("\t\tservice interface: {}",serviceToImplAndProxyDef.debugInfo());
					}
				}
			}

			// finally add the def to the map
			outSrvcBootstrapDefs.put(coreAppAndComponent,modDef);
			log.warn("\\----------------------------------------------------------------------------------------------------------------------------/");
		}
		return outSrvcBootstrapDefs.values();
	}
}
