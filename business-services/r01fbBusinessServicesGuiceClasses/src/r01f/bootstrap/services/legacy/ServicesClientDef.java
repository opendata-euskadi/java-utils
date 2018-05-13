package r01f.bootstrap.services.legacy;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.client.ServiceInterfaceTypesToImplOrProxyMappings;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.services.ids.ServiceIDs.CoreAppAndModule;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.types.JavaPackage;

@Deprecated
@Accessors(prefix="_")
  class ServiceClientDef
extends ServicesInitData {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final ServiceInterfaceTypesToImplOrProxyMappings _serviceInterfacesToImplOrProxyMappings;
	@Getter private final Class<? extends ServiceProxiesAggregator> _servicesProxiesAggregatorType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServiceClientDef(final ServicesInitData initData) {
		super(initData.getClientApiAppCode(),
			  initData.getClientApiBootstrapType(),
			  initData.getPackageToLookForServiceInterfaces(),
			  initData.getCoreAppAndModulesDefProxies(),
			  initData.getApiType());
		// Find the type that holds a Map that binds the service interface to the proxy or implementation
		// (note that this type MUST be at the same package as the client api)
    	final ServicesClientBootstrapModulesFinder clientBootstrapModulesFinder = new ServicesClientBootstrapModulesFinder(JavaPackage.of(initData.getClientApiBootstrapType()));
		final ServiceInterfaceTypesToImplOrProxyMappings serviceInterfaceTypesToImplOrProxyMappings = clientBootstrapModulesFinder.findServiceInterfaceTypesToImplOrProxyMappingsFor(initData.getCoreAppAndModules());

		// Find the type that aggregates all service interface proxies
		final ServicesClientAPIFinder clientAPIFinder = new ServicesClientAPIFinder();
		final Class<? extends ServiceProxiesAggregator> proxyAggregatorType = clientAPIFinder.findClientAPIProxyAggregatorType(initData.getApiType());
		
		_serviceInterfacesToImplOrProxyMappings = serviceInterfaceTypesToImplOrProxyMappings;
		_servicesProxiesAggregatorType = proxyAggregatorType;
	}
	public Collection<CoreAppAndModule> getCoreAppAndModules() {
		return _coreAppAndModulesDefProxies != null ? _coreAppAndModulesDefProxies.keySet() : null;
	}
	public Collection<CoreAppCode> getCoreAppCodes() {
		final Collection<CoreAppCode> outCoreApps = Lists.newArrayList();
		for (final CoreAppAndModule appAndMod : this.getCoreAppAndModules()) {
			if (!outCoreApps.contains(appAndMod.getAppCode())) outCoreApps.add(appAndMod.getAppCode());
		}
		return outCoreApps;
	}
}