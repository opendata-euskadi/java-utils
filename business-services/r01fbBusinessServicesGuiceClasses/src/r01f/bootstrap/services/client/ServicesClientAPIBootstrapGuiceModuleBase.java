package r01f.bootstrap.services.client;

import javax.inject.Singleton;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.Binder;
import com.google.inject.matcher.Matchers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.ServicesBootstrap;
import r01f.bootstrap.services.ServicesBootstrapUtil;
import r01f.bootstrap.services.ServicesClientProxyLazyLoaderGuiceMethodInterceptor;
import r01f.bootstrap.services.config.client.ServicesClientGuiceBootstrapConfig;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.inject.HasMoreBindings;
import r01f.model.metadata.HasTypesMetaData;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.reflection.ReflectionUtils;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.services.interfaces.ServiceInterface;

/**
 * This GUICE module is where the client-api bindings takes place
 *
 * This guice module is included from the bootstrap module: {@link ServicesBootstrap} (which is called when the injector is created)
 *
 * At this module some client-side bindings are done:
 * <ol>
 * 		<li>Client APIs: types that aggregates the services access</li>
 * 		<li>Model object extensions</li>
 * 		<li>Server services proxies (ie: REST, bean, ejb)</li>
 * </ol>
 *
 * The execution flow is something like:
 * <pre>
 * ClientAPI
 *    |----> ServicesClientProxy
 * 						|---------------[ Proxy between client and server services ]
 * 														  |
 * 														  |----- [ HTTP / RMI / Direct Bean access ]-------->[REAL server / core side Services implementation]
 * </pre>
 *
 * The API simply offers access to service methods to the client and frees it from the buzz of knowing how to deal with different
 * service implementations (REST, EJB, Bean...).
 * All the logic related to transforming client method-calls to core services method calls is done at the PROXIES.
 * There's one proxy per core service implementation (REST, EJB, Bean...)
 *
 * <b>See file services-architecture.txt :: there is an schema of the app high level architecture</b>
 * </pre>
 */
@Slf4j
@Accessors(prefix="_")
@EqualsAndHashCode				// This is important for guice modules
public abstract class ServicesClientAPIBootstrapGuiceModuleBase
		   implements ServicesClientBootstrapGuiceModule {	// this is a client guice bindings module
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS (all are set at bootstraping time at {@link ServicesBootstrap})
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Client API config
	 */
	@Getter private final ServicesClientGuiceBootstrapConfig _clientBootstrapCfg;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected ServicesClientAPIBootstrapGuiceModuleBase(final ServicesClientGuiceBootstrapConfig servicesClientBootstrapCfg) {
		_clientBootstrapCfg = servicesClientBootstrapCfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MODULE INTERFACE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		Binder theBinder = binder;


		// [0] - Find the model object types & bind it
		TypeMetaDataInspector.singleton()
							 .init(_clientBootstrapCfg.getClientApiAppCode());
		binder.bind(HasTypesMetaData.class)
			  .toInstance(TypeMetaDataInspector.singleton());

		// [1] - Bind the client config and properties to be injected as @XmlPropertiesForAppComponent("{clientAppCode}.client")
//		binder.bind(ServicesClientGuiceBootstrapConfig.class)
//			  .toInstance(_clientBootstrapCfg);
//		ServicesBootstrapUtil.bindXMLPropertiesForAppComponent(_clientBootstrapCfg.getClientApiAppCode(),AppComponent.forId("client"),
//															   AppComponent.forId("client"),
//															   theBinder);

		// [2] - Other module-specific bindings
		if (this instanceof HasMoreBindings) {
			((HasMoreBindings)this).configureMoreBindings(binder);
		}

		// [3] - Bind the Services proxy aggregator types as singletons
		//		 The services proxy aggregator instance contains fields for every fine-grained service proxy
		// 		 which are lazily created when accessed (see bindings at [1])
		_bindServiceProxiesAggregator(theBinder);

		// [4] - Bind the client API aggregator types as singletons
		//		 The ClientAPI is injected with a service proxy aggregator defined at [2]
		binder.bind(_clientBootstrapCfg.getClientApiType())
			  .in(Singleton.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SERVICES PROXY
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Binds the {@link ServiceProxiesAggregator} that MUST contain fields of types implementing {@link ServiceInterface} which are
	 * the concrete proxy implementation to the services
	 *
	 * The {@link ServiceInterface} fields of {@link ServiceProxiesAggregator} implementing type are LAZY loaded by
	 * {@link ServicesClientProxyLazyLoaderGuiceMethodInterceptor} which guesses what proxy implementation assign to the field:
	 * <ul>
	 * 		<li>If the {@link ServiceProxiesAggregator} extends {@link ServiceProxiesAggregatorForDefaultImpls}, the concrete {@link ServiceInterface}-implementing
	 * 			proxy instance is taken from the client properties XML file, so some service impls might be accessed using a BEAN proxy while others might be accessed
	 * 			using a REST proxy -depending on the properties file-</li>
	 * 		<li>If the {@link ServiceInterface} field's BEAN implementation is available this one will be assigned to the field no matter what type the aggregator is</li>
	 * </ul>
	 * @param binder
	 */
	private void _bindServiceProxiesAggregator(final Binder binder) {
		// a) create an instance of the ServiceInterfaceTypesToImplOrProxyMappings that maps
		//	  each service interface to either a proxy or the bean impl
		ServiceInterfaceTypesToImplOrProxyMappings serviceInterfaceTypesToImplOrProxyMappings = ReflectionUtils.createInstanceOf(_clientBootstrapCfg.getServiceInterfaceTypesToImplOrProxyMappingsType());

		// b) Inject all Map fields that matches the service interface types with the bean impl or proxy to be used
		//    (this Map fields are injected by MapBinders created at ServicesForAppModulePrivateGuiceModule)
		binder.requestInjection(serviceInterfaceTypesToImplOrProxyMappings);

		// c) Bind the interceptor to ServiceProxiesAggregator type's fine-grained method calls
		MethodInterceptor serviceProxyGetterInterceptor = new ServicesClientProxyLazyLoaderGuiceMethodInterceptor(serviceInterfaceTypesToImplOrProxyMappings);		// the method interceptor is feeded with a map of service interfaces to bean impl or proxy created below
		binder.bindInterceptor(Matchers.subclassesOf(ServiceProxiesAggregator.class),
							   Matchers.any(),
							   serviceProxyGetterInterceptor);

		// Bind every services proxy aggregator implementation
		log.info("[ServiceProxyAggregator] > {}",
				 _clientBootstrapCfg.getServicesProxiesAggregatorType());
		binder.bind(_clientBootstrapCfg.getServicesProxiesAggregatorType())
		      .in(Singleton.class);
	}
}
