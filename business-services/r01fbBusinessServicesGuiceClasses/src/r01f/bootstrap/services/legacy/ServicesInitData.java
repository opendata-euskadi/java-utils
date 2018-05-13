package r01f.bootstrap.services.legacy;

import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Node;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.bootstrap.services.config.client.ServicesClientGuiceBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreGuiceBootstrapConfig;
import r01f.services.client.ClientAPI;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppAndModule;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.types.JavaPackage;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xml.XMLUtils;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Contains necessary data to initialize the services guice injector
 */
@Deprecated
@Slf4j
@Accessors(prefix="_")
class ServicesInitData {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter	protected final ClientApiAppCode _clientApiAppCode;
	@Getter	protected final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> _clientApiBootstrapType;
	@Getter	protected final JavaPackage _packageToLookForServiceInterfaces;
	@Getter	protected final Map<CoreAppAndModule,ServicesImpl> _coreAppAndModulesDefProxies;
	@Getter	protected final Class<? extends ClientAPI> _apiType;
	
	public Collection<CoreAppAndModule> getCoreAppAndModules() {
		return _coreAppAndModulesDefProxies != null ? _coreAppAndModulesDefProxies.keySet() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesInitData(final ClientApiAppCode clientApiAppCode,
							final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientApiBootstrapType,
							final JavaPackage packageToLookForServiceInterfaces,
							final Map<CoreAppAndModule,ServicesImpl> coreAppAndModulesDefProxies,
							final Class<? extends ClientAPI> apiType) {
		_clientApiAppCode = clientApiAppCode;
		_clientApiBootstrapType = clientApiBootstrapType;
		_packageToLookForServiceInterfaces = packageToLookForServiceInterfaces;
		_coreAppAndModulesDefProxies = coreAppAndModulesDefProxies;
		_apiType = apiType;
	}
	public ServicesInitData(final ServicesBootstrapConfig cfg) {
		ServicesClientGuiceBootstrapConfig guiceCfg = cfg.getClientConfigAs(ServicesClientGuiceBootstrapConfig.class);
		
		_clientApiAppCode = ServicesPackages.clientApiAppCodeFrom(guiceCfg.getClientBootstrapGuiceModuleType());
		_clientApiBootstrapType = guiceCfg.getClientBootstrapGuiceModuleType();
		_packageToLookForServiceInterfaces = ServicesPackages.serviceIntefacePackageFrom(guiceCfg.getClientBootstrapGuiceModuleType());
		_apiType = new ServicesClientAPIFinder()
							.findClientAPI(ServicesPackages.clientApiPackageFrom(guiceCfg.getClientBootstrapGuiceModuleType()));
	
		// guess the core impls
		Map<CoreAppAndModule,ServicesImpl> coreAppAndModuleServicesImpl = Maps.newLinkedHashMapWithExpectedSize(cfg.getCoreModulesConfig().size());
		for (ServicesCoreBootstrapConfig<?,?> coreCfg : cfg.getCoreModulesConfig()) {
			ServicesCoreGuiceBootstrapConfig<?,?> guiceCoreCfg = (ServicesCoreGuiceBootstrapConfig<?,?>)coreCfg;
			CoreAppAndModule coreAppAndModule = CoreAppAndModule.of(ServicesPackages.coreAppCodeFromCoreBootstrapModuleType(guiceCoreCfg.getCoreBootstrapGuiceModuleType()),
																	ServicesPackages.coreAppModuleFromCoreBootstrapModuleTypeOrThrow(guiceCoreCfg.getCoreBootstrapGuiceModuleType()));
			ServicesImpl serviceImpl = coreCfg.getExposition().getServiceImpl();
			
			coreAppAndModuleServicesImpl.put(coreAppAndModule,serviceImpl);
		}
	
		_coreAppAndModulesDefProxies = coreAppAndModuleServicesImpl;
	}
	public ServicesInitData(final ClientApiAppCode clientApiAppCode,
							final XMLPropertiesForAppComponent apiModuleProperties) {;
		// [0] - Load client bootstrapping info from the properties files
		// The package where the certain mandatory client types are located
		final JavaPackage pckgToLookForClientBootstrapType = apiModuleProperties.propertyAt("/client/clientPackages/lookForClientBootstrapTypesAt")
														  			 .asJavaPackage(new JavaPackage(Strings.customized("{}.client.internal",
																							      					   clientApiAppCode)));
		final JavaPackage pckToLookForServiceInterfaces = apiModuleProperties.propertyAt("/client/clientPackages/lookForServiceInterfacesAt")
													   			  .asJavaPackage(new JavaPackage(Strings.customized("{}.api.interfaces",
																							   					    clientApiAppCode)));
		final JavaPackage pckgToLookForClientApiAggregator = apiModuleProperties.propertyAt("/client/clientPackages/lookForClientApiAggregatorTypeAt")
													   	  			 .asJavaPackage(new JavaPackage(Strings.customized("{}.client.servicesproxy",
		  																					      					   clientApiAppCode)));


		// Find all core appCode / modules from {apiAppCode}.{apiComponent}.properties.xml
		final Collection<CoreAppAndModule> coreAppAndModules = apiModuleProperties.propertyAt("/client/proxies")
												  			    	  .asObjectList(new Function<Node,CoreAppAndModule>() {
																							@Override
																							public CoreAppAndModule apply(final Node node) {
																								final CoreAppCode coreAppCode = CoreAppCode.forId(XMLUtils.nodeAttributeValue(node,"appCode"));
																								final CoreModule module = CoreModule.forId(XMLUtils.nodeAttributeValue(node,"id"));
																								return CoreAppAndModule.of(coreAppCode,module);
																							}
													 					            });
		// Find all core appCode / modules default proxy from {apiAppCode}.client.properties.xml
		Map<CoreAppAndModule,ServicesImpl> coreAppAndModulesDefProxy = null;
		coreAppAndModulesDefProxy = Maps.toMap(coreAppAndModules,
											   new Function<CoreAppAndModule,ServicesImpl>() {
													@Override
													public ServicesImpl apply(final CoreAppAndModule coreAppAndModule) {
														final String propsXPath = Strings.customized("/client/proxies/proxy[@appCode='{}' and @id='{}']/@impl",
																				   			   		 coreAppAndModule.getAppCode(),
																				   			   		 coreAppAndModule.getModule());
														ServicesImpl configuredImpl = apiModuleProperties.propertyAt(propsXPath)
																								 		 .asEnumElement(ServicesImpl.class);
														if (configuredImpl == null) {
															log.warn("NO proxy impl for appCode/module={} configured at {}.client.properties.xml, {} is used by default",
																	 coreAppAndModule,clientApiAppCode,ServicesImpl.REST);
															configuredImpl = ServicesImpl.REST;
														}
														return configuredImpl;
													}
										});
		// a bit of log
		log.warn("[Client API: {}]",clientApiAppCode);
		log.warn("\tLocations:");
		log.warn("\t\t-   Search for bootstrap guice modules at: {}",pckgToLookForClientBootstrapType);
		log.warn("\t\t-        Search for service interfaces at: {}",pckToLookForServiceInterfaces);
		log.warn("\t\t-Search for client api aggregator type at: {}",pckgToLookForClientApiAggregator);
		log.warn("\tCore modules:");
		if (CollectionUtils.hasData(coreAppAndModulesDefProxy)) {
			for (final Map.Entry<CoreAppAndModule,ServicesImpl> me : coreAppAndModulesDefProxy.entrySet()) {
				log.warn("\t\t-{} > {}",me.getKey(),me.getValue());
			}
		}

		// [1] - Find the CLIENT API BOOTSTRAP guice module types
    	log.warn("[START]-Find CLIENT bootstrap modules at {}===========================================",
    			 pckgToLookForClientBootstrapType);
    	final ServicesClientBootstrapModulesFinder clientBootstrapModulesFinder = new ServicesClientBootstrapModulesFinder(pckgToLookForClientBootstrapType);
    	// Find the client bootstrap module type
		final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientAPIBootstrapModulesType = clientBootstrapModulesFinder.findClientBootstrapGuiceModuleTypes();
		log.warn("  [END]-Find CLIENT binding modules============================================");

		log.warn("[START]-Find CLIENT API at {}========================================================");
		final ServicesClientAPIFinder clientAPIFinder = new ServicesClientAPIFinder();
		// Find the client api
		final Class<? extends ClientAPI> clientAPIType = clientAPIFinder.findClientAPI(pckgToLookForClientApiAggregator);

		// Create the service definition
		_clientApiAppCode = clientApiAppCode;
		_clientApiBootstrapType = clientAPIBootstrapModulesType;
		_packageToLookForServiceInterfaces = pckToLookForServiceInterfaces;
		_coreAppAndModulesDefProxies = coreAppAndModulesDefProxy;
		_apiType = clientAPIType;
	}
}
