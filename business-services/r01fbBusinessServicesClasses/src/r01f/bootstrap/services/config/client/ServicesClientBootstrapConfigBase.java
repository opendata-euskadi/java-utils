package r01f.bootstrap.services.config.client;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.client.ClientAPI;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * @see ServicesClientBootstrapConfigBuilder
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
abstract class ServicesClientBootstrapConfigBase 
    implements ServicesClientBootstrapConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The client app code
	 */
	@Getter protected final ClientApiAppCode _clientApiAppCode;
	/**
	 * The client api that exposes the fine-grained services interfaces
	 */
	@Getter protected final Class<? extends ClientAPI> _clientApiType;
	/**
	 * The java package where the services interfaces can be found
	 */
	@Getter protected final Class<? extends ServiceInterface> _serviceInterfacesBaseType;
	/**
	 * A type that aggregates the fine-grained services proxies
	 */
	@Getter protected final Class<? extends ServiceProxiesAggregator> _servicesProxiesAggregatorType;
	/**
	 * any client sub-module config
	 */
	@Getter protected final Collection<ServicesClientSubModuleBootstrapConfig<?>> _subModulesCfgs;
/////////////////////////////////////////////////////////////////////////////////////////
//  SUB-MODULE CONFIGS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <CFG extends ContainsConfigData> CFG getSubModuleConfigFor(final AppComponent component) {
		if (CollectionUtils.isNullOrEmpty(_subModulesCfgs)) throw new IllegalStateException("NO sub-modules config was set!");
		ServicesClientSubModuleBootstrapConfig<CFG> subCfg = (ServicesClientSubModuleBootstrapConfig<CFG>)FluentIterable.from(_subModulesCfgs)
																	 .filter(new Predicate<ServicesClientSubModuleBootstrapConfig<?>>() {
																					@Override
																					public boolean apply(final ServicesClientSubModuleBootstrapConfig<?> cfg) {
																						return cfg.getComponent().is(component);
																					}
																	 		 })
																	 .first().orNull();
		return subCfg != null ? subCfg.getConfig() : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} client api {} for service interfaces aggregated at {} and extending {}",
								  _clientApiAppCode,
								  _clientApiType,
								  _servicesProxiesAggregatorType,
								  _serviceInterfacesBaseType);
	}
}
