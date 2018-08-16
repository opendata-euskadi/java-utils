package r01f.bootstrap.services.config.client;

import java.util.Collection;

import com.google.inject.multibindings.MapBinder;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.ServicesCoreBootstrapPrivateGuiceModule;
import r01f.bootstrap.services.client.ServiceInterfaceTypesToImplOrProxyMappings;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.services.client.ClientAPI;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.Strings;

/**
 * @see ServicesClientBootstrapConfigBuilder
 */
@Accessors(prefix="_")
abstract class ServicesClientGuiceBootstrapConfigBase 
	   extends ServicesClientBootstrapConfigBase
    implements ServicesClientGuiceBootstrapConfig {

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Service interface type to bean impl or rest / ejb, etc proxy matchings (bindings) 
	 * This type instance has a Map member for every core appCode / module which key is the service interface type and the value is the
	 * concrete instance of the service interface bean impl or proxy to be used
	 * 		- if the service bean implementation is available, the service interface is binded to the bean impl directly
	 *		- otherwise, the best suitable proxy to the service implementation is binded
	 * Those Map member are {@link MapBinder}s injected at {@link ServicesCoreBootstrapPrivateGuiceModule}
	 * 
	 * Since there's a {@link ServicesCoreForAppModulePrivateGuiceModule} private module for every core appCode / module,
	 * this type has a Map member for every core appCode / module
	 */
	@Getter private final Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> _serviceInterfaceTypesToImplOrProxyMappingsType;
	/**
	 * The guice module that bootstraps the client 
	 */
	@Getter private final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> _clientBootstrapGuiceModuleType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServicesClientGuiceBootstrapConfigBase(final ClientApiAppCode clientApiAppCode,
												  final Class<? extends ClientAPI> clientApiType,
												  final Class<? extends ServiceInterface> serviceInterfaceBaseType,
												  final Class<? extends ServiceProxiesAggregator> servicesProxiesAggregatorType,
												  final Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> serviceInterfaceTypesToImplOrProxyMappingsType,
												  final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientBootstrapGuiceModuleType) {
		this(clientApiAppCode,
			 clientApiType,
			 serviceInterfaceBaseType,
			 servicesProxiesAggregatorType,
			 serviceInterfaceTypesToImplOrProxyMappingsType,
			 clientBootstrapGuiceModuleType,
			 null);
	}
	public ServicesClientGuiceBootstrapConfigBase(final ClientApiAppCode clientApiAppCode,
												  final Class<? extends ClientAPI> clientApiType,
												  final Class<? extends ServiceInterface> serviceInterfaceBaseType,
												  final Class<? extends ServiceProxiesAggregator> servicesProxiesAggregatorType,
												  final Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> serviceInterfaceTypesToImplOrProxyMappingsType,
												  final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientBootstrapGuiceModuleType,
												  final Collection<ServicesClientSubModuleBootstrapConfig<?>> subModulesCfgs) {
		super(clientApiAppCode, 
			  clientApiType, 
			  serviceInterfaceBaseType,servicesProxiesAggregatorType,
			  subModulesCfgs);
		_serviceInterfaceTypesToImplOrProxyMappingsType = serviceInterfaceTypesToImplOrProxyMappingsType;
		_clientBootstrapGuiceModuleType = clientBootstrapGuiceModuleType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} bootstraped by {}",
								  super.debugInfo(),
								  _clientBootstrapGuiceModuleType);
	}
}
