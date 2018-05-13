package r01f.bootstrap.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.matcher.Matcher;
import com.google.inject.multibindings.MapBinder;

import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.client.ServiceInterfaceTypesToImplOrProxyMappings;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.exceptions.Throwables;
import r01f.patterns.Memoized;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.ReflectionUtils.FieldAnnotated;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.services.interfaces.ServiceInterface;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;

/**
 * A GUICE {@link MethodInterceptor} that lazy loads the {@link ServiceProxiesAggregator}'s sub type that provides access to the
 * proxy implementation depending on the {@link ServicesImpl}
 *
 * This type contains a cache that maps the {@link SubServiceInterface} to it's concrete implementation depending on the {@link ServicesImpl}
 */
@Slf4j
public class ServicesClientProxyLazyLoaderGuiceMethodInterceptor
  implements MethodInterceptor {
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Service interface type to bean impl or rest / ejb, etc proxy matchings (bindings)
	 * This type has a Map member for every core appCode / module which key is the service interface type and the value is the
	 * concrete instance of the service interface bean impl or proxy to be used
	 * 		- if the service bean implementation is available, the service interface is binded to the bean impl directly
	 *		- otherwise, the best suitable proxy to the service implementation is binded
	 * Those Map member are {@link MapBinder}s injected at {@link ServicesCoreForAppModulePrivateGuiceModule}{@link #_bindServiceProxiesAggregators(Binder)} method
	 *
	 * Since there's a {@link ServicesCoreForAppModulePrivateGuiceModule} private module for every core appCode / module,
	 * this type has a Map member for every core appCode / module
	 */
	private final ServiceInterfaceTypesToImplOrProxyMappings _serviceInterfaceTypesToImplOrProxyMappings;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public ServicesClientProxyLazyLoaderGuiceMethodInterceptor(final ServiceInterfaceTypesToImplOrProxyMappings serviceInterfaceTypesToImplOrProxyMappings) {
		_serviceInterfaceTypesToImplOrProxyMappings = serviceInterfaceTypesToImplOrProxyMappings;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The injected {@link ServiceInterfaceTypesToImplOrProxyMappings} can contain just a single injected Map<Class,ServiceInterface> field
	 * or multiple injected Map<Class,ServiceInterface> fields (one per-core module):
	 * <pre class='brush:java'>
	 * 		public class XXServiceInterfaceTypesToImplOrProxyMappings
  	 *        implements ServiceInterfaceTypesToImplOrProxyMappings {
	 *			@Inject @Named("yyy.mod1") @SuppressWarnings({ "rawtypes" })							// yyy.mod1 is a CORE module
     *			private Map<Class,ServiceInterface> _serviceInterfaceTypesToImplOrProxyForYYYMod1;
     *
     *			@Inject @Named("zzz.mod2") @SuppressWarnings({ "rawtypes" })							// zzz.mod2 is another CORE module
     *			private Map<Class,ServiceInterface> _serviceInterfaceTypesToImplOrProxyForZZZMod2;
     *		}
	 * </pre>
	 * This method justs JOINS all Map<Class,ServiceInterface> in a SINGLE one
	 */
	private Memoized<Map<Class<ServiceInterface>,ServiceInterface>> _allServiceInterfaceTypesToImplOrProxyMappings =
				new Memoized<Map<Class<ServiceInterface>,ServiceInterface>>() {
						@Override
						protected Map<Class<ServiceInterface>,ServiceInterface> supply() {
							return _flatMapServiceInterfaceTypesToImplOrProxyMappings(_serviceInterfaceTypesToImplOrProxyMappings);
						}

			   };
	@SuppressWarnings({ "unchecked"})
	private static Map<Class<ServiceInterface>,ServiceInterface> _flatMapServiceInterfaceTypesToImplOrProxyMappings(final ServiceInterfaceTypesToImplOrProxyMappings serviceInterfaceTypesToImplOrProxyMappings) {
		// find Map fields at the ServiceInterfaceTypesToImplOrProxyMappings instance
		Collection<Field> mapFields = _findFields(serviceInterfaceTypesToImplOrProxyMappings);

		// create a Map to collect all Maps from the _serviceInterfaceTypesToImplOrProxyMappings's Map fields
		Map<Class<ServiceInterface>,ServiceInterface> outMap = Maps.newHashMap();
		for (Field f : mapFields) {
			try {
				f.setAccessible(true);
				Map<Class<ServiceInterface>,ServiceInterface> mappings = null;
				try {
					mappings = (Map<Class<ServiceInterface>,ServiceInterface>)f.get(serviceInterfaceTypesToImplOrProxyMappings);
				} catch (ClassCastException ccEx) {
					throw new IllegalStateException(Strings.customized("Field {} at {} MUST be a Map<Class<ServiceInterface>,ServiceInterface>",
																	   f.getName(),serviceInterfaceTypesToImplOrProxyMappings.getClass()));
				}
				if (CollectionUtils.hasData(mappings)) outMap.putAll(mappings);
			} catch(Throwable th) {
				th.printStackTrace(System.out);
			}
		}
		return outMap;
	}
	@SuppressWarnings("unchecked")
	private static Collection<Field> _findFields(final ServiceInterfaceTypesToImplOrProxyMappings serviceInterfaceTypesToImplOrProxyMappings) {
		// find @Inject-annotated Map fields at the ServiceInterfaceTypesToImplOrProxyMappings instance
		FieldAnnotated<? extends Annotation>[] injectAnnFields = ReflectionUtils.fieldsAnnotated(serviceInterfaceTypesToImplOrProxyMappings.getClass(),
																								 com.google.inject.Inject.class,javax.inject.Inject.class);
		// ensure those fields are Map<Class<ServiceInterface>,ServiceInterface> fields
		Collection<Field> injectAnnotatedFields = Lists.newArrayList();
		if (CollectionUtils.hasData(injectAnnFields)) {
			for (FieldAnnotated<? extends Annotation> injectAnnField : injectAnnFields) {
				if (ReflectionUtils.isImplementing(injectAnnField.getField().getType(),Map.class)) {
					injectAnnotatedFields.add(injectAnnField.getField());
				} else {
					log.error("Type {} contains an @Inject-annotated field named {} that's NOT a Map<Class<ServiceInterface>,ServiceInterface>... IGNORED",
							  serviceInterfaceTypesToImplOrProxyMappings.getClass(),injectAnnField.getField().getName());
				}
			}
		}

		// Check that there's a Map annotated for every appCode / module
		if (CollectionUtils.isNullOrEmpty(injectAnnotatedFields)) throw new IllegalStateException(Throwables.message("{} instance does NOT have any @{} annotated Map<Class,ServiceInterface> fields for service interface type to bean impl or proxy bindings",
																									  	 			 serviceInterfaceTypesToImplOrProxyMappings.getClass(),Inject.class.getSimpleName()));
		return injectAnnotatedFields;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Intercept methods returning a ServiceInterface implementing type
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings({ "cast","unchecked" })
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		// Do not intercept the ServicesClientProxy base type methods
		if (!_isInterceptedMethodCall(invocation.getMethod())) return invocation.proceed();

		// If the return type is a ServiceInteface or a SubServiceInterface, do the lazy load if needed
		Object out = invocation.proceed();	// <-- this can return null if the ServiceInterface or SubServiceInterface was NOT created previously

		if (out != null) return out;		// the ServiceInterface was previously created

		// Type of the proxy aggregator
		ServiceProxiesAggregator serviceProxyAggregator = (ServiceProxiesAggregator)invocation.getThis();
		Class<? extends ServiceProxiesAggregator> serviceProxyAggregatorType = (Class<? extends ServiceProxiesAggregator>)serviceProxyAggregator.getClass();

		// the ServiceInterface or SubServiceInterface concrete type
		Class<?> returnType = invocation.getMethod().getReturnType();

		if (returnType != null && ReflectionUtils.isImplementing(returnType,
																 ServiceInterface.class)) {
			// Find the field at the aggregator type that contains the proxy
			Class<? extends ServiceInterface> serviceInterfaceType = (Class<? extends ServiceInterface>)returnType;
			Field serviceInterfaceBaseField = _findServiceInterfaceField(serviceProxyAggregatorType,
																	 	 serviceInterfaceType);

			// get the service impl (if available) or proxy instance from the injected MapBinder
			ServiceInterface serviceImplOrProxy = _allServiceInterfaceTypesToImplOrProxyMappings.get().get(serviceInterfaceType);
			if (serviceImplOrProxy != null) {
				ReflectionUtils.setFieldValue(serviceProxyAggregator,serviceInterfaceBaseField,
											  serviceImplOrProxy,
											  false);
				out = serviceImplOrProxy;
				log.info("[ServiceProxy aggregation] > {} field of type {} was not initialized on services proxy aggregator {} so an instance of {} was lazily created",
						 serviceInterfaceBaseField.getName(),serviceInterfaceType,serviceProxyAggregator.getClass(),serviceImplOrProxy.getClass());
			}
		}
		return out;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static Field _findServiceInterfaceField(final Class<? extends ServiceProxiesAggregator> servicesProxyAggregator,
											 		final Class<?> serviceType) {
		Field[] subServiceInterfaceFields = ReflectionUtils.fieldsOfType(servicesProxyAggregator,
																	  	 serviceType);
		if (CollectionUtils.isNullOrEmpty(subServiceInterfaceFields)) throw new IllegalStateException(Strings.customized("The proxy aggregator type {} does NOT have a field of type {}",
																													     servicesProxyAggregator,serviceType));
		if (subServiceInterfaceFields.length > 1) {
			//for (int i=0; i<serviceInterfaceFields.length; i++) System.out.println(">>>>>" + serviceInterfaceFields[i].getName());
			throw new IllegalStateException(Strings.customized("The proxy aggregator type {} have MORE THAN ONE field of type {}",
															   servicesProxyAggregator,serviceType));

		}
		return subServiceInterfaceFields[0];
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static Method[] NOT_INTERCEPTED_METHODS = new Method[] {
															// put here any methods at r01f.services.client.ServiceProxiesAggregator
															// that are NOT intercepted
//															ReflectionUtils.method(ServiceProxiesAggregator.class,
//																				   "getServicesImpl")
													  };
	/**
	 * Checks if a method should be intercepted
	 * (an alternative implementation could be to create a {@link Matcher} subtype)
	 * @return true if its an intercepted method
	 */
	private static boolean _isInterceptedMethodCall(final Method invokedMethod) {
		boolean outIntercepted = true;
		if (CollectionUtils.isNullOrEmpty(NOT_INTERCEPTED_METHODS)) return true;
		for (Method notInterceptedMethod : NOT_INTERCEPTED_METHODS) {
			if (invokedMethod.equals(notInterceptedMethod)) {
				outIntercepted = false;
				break;
			}
		}
		return outIntercepted;
	}
}
