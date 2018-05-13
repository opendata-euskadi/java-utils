package r01f.bootstrap.services.legacy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.patterns.IsBuilder;
import r01f.services.client.ClientAPI;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppAndModule;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;

@Deprecated
@NoArgsConstructor(access=AccessLevel.PRIVATE)
  abstract class ServicesInitDataBuilder
implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static Collection<ServicesInitData> multiple(final ServicesInitData... initData) {
		if (CollectionUtils.isNullOrEmpty(initData)) throw new IllegalArgumentException();
		return Arrays.asList(initData);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServicesInitDataBuilderApiBootstrapTypeStep createForClientApi(final ClientApiAppCode clientApiAppCode) {
		return new ServicesInitDataBuilder() { /* nothing */ }
					.new ServicesInitDataBuilderApiBootstrapTypeStep(clientApiAppCode);
	}
	public static ServicesInitDataBuilderServiceInterfacesPackageStep createForBootstrapModule(final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientApiBootstrapType) {
		return ServicesInitDataBuilder.createForClientApi(ServicesPackages.clientApiAppCodeFrom(clientApiBootstrapType))
									  .bootstrapedBy(clientApiBootstrapType);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesInitDataBuilderApiBootstrapTypeStep {
		private final ClientApiAppCode _clientApiAppCode;
		
		public ServicesInitDataBuilderServiceInterfacesPackageStep bootstrapedBy(final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> clientApiBootstrapType) {
			return new ServicesInitDataBuilderServiceInterfacesPackageStep(_clientApiAppCode,
																		   clientApiBootstrapType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesInitDataBuilderServiceInterfacesPackageStep {
		private final ClientApiAppCode _clientApiAppCode;
		private final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> _clientApiBootstrapType;
		
		public ServicesInitDataBuilderCoreModulesStep findServiceInterfacesAtPackage(final String packageToLookForServiceInterfaces) {
			return new ServicesInitDataBuilderCoreModulesStep(_clientApiAppCode,
															  _clientApiBootstrapType,
															  new JavaPackage(packageToLookForServiceInterfaces));
		}
		public ServicesInitDataBuilderCoreModulesStep findServiceInterfacesAtPackage(final JavaPackage packageToLookForServiceInterfaces) {
			return new ServicesInitDataBuilderCoreModulesStep(_clientApiAppCode,
															  _clientApiBootstrapType,
															  packageToLookForServiceInterfaces);
		}
		public ServicesInitDataBuilderCoreModulesStep findServiceInterfacesAtPackage(final Package packageToLookForServiceInterfaces) {
			return new ServicesInitDataBuilderCoreModulesStep(_clientApiAppCode,
															  _clientApiBootstrapType,
															  new JavaPackage(packageToLookForServiceInterfaces));
		}
		public ServicesInitDataBuilderCoreModulesStep findServiceInterfacesAtDefaultPackage() {
			return this.findServiceInterfacesAtPackage(ServicesPackages.serviceIntefacePackageFrom(_clientApiBootstrapType));
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesInitDataBuilderCoreModulesStep {
		private final ClientApiAppCode _clientApiAppCode;
		private final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> _clientApiBootstrapType;
		private final JavaPackage _packageToLookForServiceInterfaces;
		
		public ServicesInitDataBuilderClientApiStep proxiedTo(final CoreServiceImpl... coreServiceImpl) {
			if (CollectionUtils.isNullOrEmpty(coreServiceImpl)) throw new IllegalArgumentException("Core services impls cannot be null!!");
			Map<CoreAppAndModule,ServicesImpl> coreAppAndModulesDefProxies = Maps.newHashMapWithExpectedSize(coreServiceImpl.length);
			for (CoreServiceImpl impl : coreServiceImpl) {
				coreAppAndModulesDefProxies.put(impl.getCoreAppAndModule(),
												impl.getDefaultImpl());
			}
			return new ServicesInitDataBuilderClientApiStep(_clientApiAppCode,
															_clientApiBootstrapType,
															_packageToLookForServiceInterfaces,
															coreAppAndModulesDefProxies);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesInitDataBuilderClientApiStep {
		private final ClientApiAppCode _clientApiAppCode;
		private final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> _clientApiBootstrapType;
		private final JavaPackage _packageToLookForServiceInterfaces;
		private final Map<CoreAppAndModule,ServicesImpl> _coreAppAndModulesDefProxies;
		
		public ServicesInitDataBuilderBuildStep exposedByApiType(final Class<? extends ClientAPI> apiType) {
			return new ServicesInitDataBuilderBuildStep(_clientApiAppCode,
														_clientApiBootstrapType,
														_packageToLookForServiceInterfaces,
														_coreAppAndModulesDefProxies,
													    apiType);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesInitDataBuilderBuildStep {
		private final ClientApiAppCode _clientApiAppCode;
		private final Class<? extends ServicesClientAPIBootstrapGuiceModuleBase> _clientApiBootstrapType;
		private final JavaPackage _packageToLookForServiceInterfaces;
		private final Map<CoreAppAndModule,ServicesImpl> _coreAppAndModulesDefProxies;
		private final Class<? extends ClientAPI> _apiType;
		
		public ServicesInitData build() {
			return new ServicesInitData(_clientApiAppCode,
										_clientApiBootstrapType,
										_packageToLookForServiceInterfaces,
										_coreAppAndModulesDefProxies,
									    _apiType);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)	
	public static class CoreServiceImpl {
		@Getter private final CoreAppAndModule _coreAppAndModule;
		@Getter private final ServicesImpl _defaultImpl;
		
		public static CoreServiceBuilderImplStep of(final CoreAppAndModule coreAppAndModule) {
			return new CoreServiceBuilderImplStep(coreAppAndModule);
		}
		public static CoreServiceBuilderImplStep of(final CoreAppCode coreAppCode,final CoreModule coreModule) {
			return new CoreServiceBuilderImplStep(CoreAppAndModule.of(coreAppCode,coreModule));
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class CoreServiceBuilderImplStep {
		private final CoreAppAndModule _coreAppAndModule;
		public CoreServiceImpl usingDefaultProxy(final ServicesImpl impl) {
			return new CoreServiceImpl(_coreAppAndModule,
									   impl);
		}
	}
}
