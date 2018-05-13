package r01f.bootstrap.services.config.core;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
@RequiredArgsConstructor
abstract class ServicesCoreBootstrapConfigBase<E extends ServicesCoreModuleExposition,
											P extends ServicesClientProxyToCoreServices> 
    implements ServicesCoreBootstrapConfig<E,P> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The core app code
	 */
	@Getter protected final CoreAppCode _coreAppCode;
	/**
	 * The core module
	 */
	@Getter protected final CoreModule _coreModule;
	/**
	 * How is this module exposed to the client API
	 */
	@Getter protected final E _exposition;
	/**
	 * Client proxy to core exposition
	 */
	@Getter protected final P _clientProxyConfig;
	/**
	 * How are core events handleds
	 */
	@Getter protected final ServicesCoreModuleEventsConfig _eventHandling;
	/**
	 * Sub-modules config
	 */
	@Getter protected final Collection<ServicesCoreSubModuleBootrapConfig<?>> _subModulesConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <C extends ServicesCoreBootstrapConfig<?,?>> C as(final Class<C> type) {
		return (C)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ServicesImpl getImplType() {
		if (_exposition instanceof ServicesCoreModuleExpositionForBeanImpl) {
			return ServicesImpl.Bean;
		} else if (_exposition instanceof ServicesCoreModuleExpositionForRESTImpl) {
			return ServicesImpl.REST;
		} else if (_exposition instanceof ServicesCoreModuleExpositionForServletImpl) {
			return ServicesImpl.Servlet;
		} 
		throw new IllegalStateException("Illegal exposition type: " + _exposition.getClass());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <CFG extends ContainsConfigData> CFG getSubModuleConfigFor(final AppComponent component) {
		if (CollectionUtils.isNullOrEmpty(_subModulesConfig)) throw new IllegalStateException("NO sub-modules config was set!");
		ServicesCoreSubModuleBootrapConfig<CFG> subCfg = (ServicesCoreSubModuleBootrapConfig<CFG>)FluentIterable.from(_subModulesConfig)
																	 .filter(new Predicate<ServicesCoreSubModuleBootrapConfig<?>>() {
																					@Override
																					public boolean apply(final ServicesCoreSubModuleBootrapConfig<?> cfg) {
																						return cfg.getComponent().is(component);
																					}
																	 		 })
																	 .first().orNull();
		return subCfg != null ? subCfg.getConfig() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{}/{} as {}; {}",
								  _coreAppCode,_coreModule,
				   				  _exposition.debugInfo(),
				   				  _eventHandling != null ? _eventHandling.debugInfo() : "");
	}
}
