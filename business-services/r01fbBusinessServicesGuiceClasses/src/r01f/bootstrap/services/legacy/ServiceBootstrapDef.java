package r01f.bootstrap.services.legacy;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.bootstrap.services.core.BeanImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.RESTImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.bootstrap.services.core.ServletImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppAndModule;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.collections.CollectionUtils;

@Deprecated
@Accessors(prefix="_")
@RequiredArgsConstructor
class ServiceBootstrapDef {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * API app code
	 */
	@Getter private final ClientApiAppCode _clientApiAppCode;
	/**
	 * Core app code and module
	 */
	@Getter private final CoreAppAndModule _coreAppCodeAndModule;
	/**
	 * Default proxy impl (REST, EJB, etc)
	 */
	@Getter private final ServicesImpl _defaultProxyImpl;
	/**
	 * Type of the core bootstrap modules
	 */
	@Getter private Collection<Class<ServicesCoreBootstrapGuiceModule>> _coreBeanBootstrapModuleTypes;
	/**
	 * Type of the REST core bootstrap modules
	 */
	@Getter private Collection<Class<ServicesCoreBootstrapGuiceModule>> _coreRESTBootstrapModuleTypes;
	/**
	 * Type of the Servlet core bootstrap modules
	 */
	@Getter private Collection<Class<ServicesCoreBootstrapGuiceModule>> _coreServletBootstrapModuleTypes;
	/**
	 * Type of the EJB core bootstrap modules
	 */
	@Getter private Collection<Class<ServicesCoreBootstrapGuiceModule>> _coreEJBBootstrapModuleTypes;
	/**
	 * The service interface types to impl and proxy binding def
	 */
	@Getter private Collection<ServiceToImplAndProxyDef<? extends ServiceInterface>> _serviceInterfacesToImplAndProxiesDefs;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public void addCoreBeanBootstrapModuleType(final Class<? extends BeanImplementedServicesCoreBootstrapGuiceModuleBase> moduleType) {
		if (_coreBeanBootstrapModuleTypes == null) _coreBeanBootstrapModuleTypes = Lists.newArrayList();
		Object moduleTypeObj = moduleType;
		Class<ServicesCoreBootstrapGuiceModule> beanModuleType = (Class<ServicesCoreBootstrapGuiceModule>)moduleTypeObj;
		_coreBeanBootstrapModuleTypes.add(beanModuleType);
	}
	@SuppressWarnings("unchecked")
	public void addCoreRESTBootstrapModuleType(final Class<? extends RESTImplementedServicesCoreBootstrapGuiceModuleBase> moduleType) {
		if (_coreRESTBootstrapModuleTypes == null) _coreRESTBootstrapModuleTypes = Lists.newArrayList();
		Object moduleTypeObj = moduleType;
		Class<ServicesCoreBootstrapGuiceModule> restModuleType = (Class<ServicesCoreBootstrapGuiceModule>)moduleTypeObj;
		_coreRESTBootstrapModuleTypes.add(restModuleType);
	}
	@SuppressWarnings("unchecked")
	public void addCoreServletBootstrapModuleType(final Class<? extends ServletImplementedServicesCoreBootstrapGuiceModuleBase> moduleType) {
		if (_coreServletBootstrapModuleTypes == null) _coreServletBootstrapModuleTypes = Lists.newArrayList();
		Object moduleTypeObj = moduleType;
		Class<ServicesCoreBootstrapGuiceModule> servletModuleType = (Class<ServicesCoreBootstrapGuiceModule>)moduleTypeObj; 
		_coreServletBootstrapModuleTypes.add(servletModuleType);
	}
	public void setServiceInterfacesToImplAndProxiesDefs(final Collection<ServiceToImplAndProxyDef<? extends ServiceInterface>> serviceInterfacesToImplAndProxiesDefs) {
		if (CollectionUtils.isNullOrEmpty(serviceInterfacesToImplAndProxiesDefs)) {
//			throw new IllegalStateException(Throwables.message("The core module {} is NOT accesible via a client-API service interface: there's NO client API service interface to impl and/or proxy binding for {}; check that the {} types @{} annotation appCode and module attributes are correct (they MUST match the ones in {}.client.properties.xml)",
//															   _coreAppCodeAndModule,
//															   _coreAppCodeAndModule,
//															   ServiceInterface.class.getName(),ServiceInterfaceFor.class.getSimpleName(),
//															   _coreAppCodeAndModule.getAppCode()));
		}
		_serviceInterfacesToImplAndProxiesDefs = serviceInterfacesToImplAndProxiesDefs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * do NOT install the REST core modules (they're binded at ServicesMainGuiceBootstrap, otherwise they're not visible
     * to the outside world and Guice Servlet filter cannot see REST resources)
	 * @return an instance of every guice module that must be installed into a private module to be isolated from other modules
	 */
	public Collection<ServicesCoreBootstrapGuiceModule> getPrivateBootstrapGuiceModuleInstances() {
		Collection<ServicesCoreBootstrapGuiceModule> outModuleInstances = Lists.newArrayList();
		if (CollectionUtils.hasData(_coreBeanBootstrapModuleTypes)) {
			Collection<ServicesCoreBootstrapGuiceModule> modInstances = _createModuleInstancesOf(_coreBeanBootstrapModuleTypes);
			outModuleInstances.addAll(modInstances);
		}
		if (CollectionUtils.hasData(_coreEJBBootstrapModuleTypes)) {
			Collection<? extends ServicesCoreBootstrapGuiceModule> modInstances = _createModuleInstancesOf(_coreEJBBootstrapModuleTypes);
			outModuleInstances.addAll(modInstances);
		}
		return outModuleInstances;
	}
	public Collection<ServicesCoreBootstrapGuiceModule> getPublicBootstrapGuiceModuleInstances() {
		Collection<ServicesCoreBootstrapGuiceModule> outModuleInstances = Lists.newArrayList();
		if (CollectionUtils.hasData(_coreRESTBootstrapModuleTypes)) {
			Collection<? extends ServicesCoreBootstrapGuiceModule> modInstances = _createModuleInstancesOf(_coreRESTBootstrapModuleTypes);
			outModuleInstances.addAll(modInstances);
		}
		if (CollectionUtils.hasData(_coreServletBootstrapModuleTypes)) {
			Collection<? extends ServicesCoreBootstrapGuiceModule> modInstances = _createModuleInstancesOf(_coreServletBootstrapModuleTypes);
			outModuleInstances.addAll(modInstances);
		}
		return outModuleInstances;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static Collection<ServicesCoreBootstrapGuiceModule> _createModuleInstancesOf(final Collection<Class<ServicesCoreBootstrapGuiceModule>> modulesTypes) {
		return FluentIterable.from(modulesTypes)
							 .transform(new Function<Class<ServicesCoreBootstrapGuiceModule>,ServicesCoreBootstrapGuiceModule>() {
												@Override 
												public ServicesCoreBootstrapGuiceModule apply(final Class<ServicesCoreBootstrapGuiceModule> type) {
													return (ServicesCoreBootstrapGuiceModule)ServicesLifeCycleUtil.createGuiceModuleInstance(type);
												}
							 			})
							 .toList();
	}
}
