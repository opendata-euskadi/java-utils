package r01f.bootstrap.services.config.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.bootstrap.services.client.ServiceInterfaceTypesToImplOrProxyMappings;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.patterns.IsBuilder;
import r01f.reflection.scanner.SubTypeOfScanner;
import r01f.services.client.ClientAPI;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.interfaces.ServiceInterface;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;


/**
 * Builder for ServicesConfig
 * Usage: 
 * <pre class='brush:java'>

 * </pre>
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServicesClientBootstrapConfigBuilder 
	       implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServicesClientBootstrapConfigBuilderApiTypeStep forClientApiAppCode(final ClientApiAppCode appCode) {
		return new ServicesClientBootstrapConfigBuilder() { /* nothing */ }
					.new ServicesClientBootstrapConfigBuilderApiTypeStep(appCode);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesClientBootstrapConfigBuilderApiTypeStep {
		private final ClientApiAppCode _clientApiAppCode;
		
		public ServicesClientBootstrapConfigBuilderServiceInterfacesStep exposingApi(Class<? extends ClientAPI> clientApiType) {
			return new ServicesClientBootstrapConfigBuilderServiceInterfacesStep(_clientApiAppCode,
																				 clientApiType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesClientBootstrapConfigBuilderServiceInterfacesStep {
		private final ClientApiAppCode _clientApiAppCode;
		private final Class<? extends ClientAPI> _clientApiType;

		public ServicesClientBootstrapConfigBuilderProxiesAggregatorStep ofServiceInterfacesExtending(final Class<? extends ServiceInterface> serviceInterfaceBaseType) {
			return new ServicesClientBootstrapConfigBuilderProxiesAggregatorStep(_clientApiAppCode,
																			     _clientApiType,
																			     serviceInterfaceBaseType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesClientBootstrapConfigBuilderProxiesAggregatorStep {
		private final ClientApiAppCode _clientApiAppCode;
		private final Class<? extends ClientAPI> _clientApiType;
		private final Class<? extends ServiceInterface> _serviceInterfacesBaseType;
		
		public ServicesClientBootstrapConfigBuilderGuiceModuleStep withProxiesToCoreImplAggregatedAt(final Class<? extends ServiceProxiesAggregator> servicesProxiesAggregatorType) {
			return new ServicesClientBootstrapConfigBuilderGuiceModuleStep(_clientApiAppCode,
																		   _clientApiType,
																		   _serviceInterfacesBaseType,
																		   servicesProxiesAggregatorType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesClientBootstrapConfigBuilderGuiceModuleStep {
		private final ClientApiAppCode _clientApiAppCode;
		private final Class<? extends ClientAPI> _clientApiType;
		private final Class<? extends ServiceInterface> _serviceInterfacesBaseType;
		private final Class<? extends ServiceProxiesAggregator> _servicesProxiesAggregatorType;
		
		public ServicesClientBootstrapConfigBuilderBuildStep bootstrappedWith(final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientBootstrapGuiceModuleType) {
			Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> serviceInterfaceTypesToImplOrProxyMappingsType = _findServiceInterfaceToImplOrProxyMappingsFor(clientBootstrapGuiceModuleType);
			return new ServicesClientBootstrapConfigBuilderBuildStep(_clientApiAppCode,
																	 _clientApiType,
																	 _serviceInterfacesBaseType,
																	 _servicesProxiesAggregatorType,
																	 serviceInterfaceTypesToImplOrProxyMappingsType,
																	 clientBootstrapGuiceModuleType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesClientBootstrapConfigBuilderBuildStep {
		private final ClientApiAppCode _clientApiAppCode;
		private final Class<? extends ClientAPI> _clientApiType;
		private final Class<? extends ServiceInterface> _serviceInterfacesBaseType;
		private final Class<? extends ServiceProxiesAggregator> _servicesProxiesAggregatorType;
		private final Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> _serviceInterfaceTypesToImplOrProxyMappingsType;
		private final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> _clientBootstrapGuiceModuleType;
		private Collection<ServicesClientSubModuleBootstrapConfig<?>> _subModulesCfgs;
		
		public ServicesClientBootstrapConfigBuilderBuildStep withSubModulesConfigs(final ServicesClientSubModuleBootstrapConfig<?>... subModulesCfgs) {
			_subModulesCfgs = CollectionUtils.hasData(subModulesCfgs) ? Lists.newArrayList(subModulesCfgs) : null;
			return this;
		}
		public ServicesClientGuiceBootstrapConfig build() {
			return new ServicesClientGuiceBootstrapConfigImpl(_clientApiAppCode,
															  _clientApiType,
															  _serviceInterfacesBaseType,
															  _servicesProxiesAggregatorType,
															  _serviceInterfaceTypesToImplOrProxyMappingsType,
															  _clientBootstrapGuiceModuleType,
															  _subModulesCfgs);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> _findServiceInterfaceToImplOrProxyMappingsFor(final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientBootstrapGuiceModuleType) {
		List<JavaPackage> pckgs = Lists.newArrayListWithExpectedSize(2);
		pckgs.add(JavaPackage.of(ServiceInterfaceTypesToImplOrProxyMappings.class));	// beware to include also the package where ServiceInterfaceTypesToImplOrProxyMappings is
		pckgs.add(JavaPackage.of(clientBootstrapGuiceModuleType));
		Set<Class<? extends ServiceInterfaceTypesToImplOrProxyMappings>> ts = SubTypeOfScanner.findSubTypesAt(ServiceInterfaceTypesToImplOrProxyMappings.class,
																											  pckgs,
																											  this.getClass().getClassLoader());
		if (CollectionUtils.isNullOrEmpty(ts)) throw new IllegalStateException("Did NOT found a type extending " + ServiceInterfaceTypesToImplOrProxyMappings.class.getSimpleName() + " at " + JavaPackage.of(clientBootstrapGuiceModuleType));
		if (ts.size() > 1) throw new IllegalStateException("There MUST be a single type extending " + ServiceInterfaceTypesToImplOrProxyMappings.class.getSimpleName() + " at " + JavaPackage.of(clientBootstrapGuiceModuleType) + " found: " + ts);
		Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> t = Iterables.getOnlyElement(ts);	
		return t;
	}
}
