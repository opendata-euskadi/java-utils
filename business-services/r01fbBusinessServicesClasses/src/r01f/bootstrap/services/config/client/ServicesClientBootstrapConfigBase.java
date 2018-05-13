package r01f.bootstrap.services.config.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.services.client.ClientAPI;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.Strings;

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
	@Getter private final ClientApiAppCode _clientApiAppCode;
	/**
	 * The client api that exposes the fine-grained services interfaces
	 */
	@Getter private final Class<? extends ClientAPI> _clientApiType;
	/**
	 * The java package where the services interfaces can be found
	 */
	@Getter private final Class<? extends ServiceInterface> _serviceInterfacesBaseType;
	/**
	 * A type that aggregates the fine-grained services proxies
	 */
	@Getter private final Class<? extends ServiceProxiesAggregator> _servicesProxiesAggregatorType;
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
