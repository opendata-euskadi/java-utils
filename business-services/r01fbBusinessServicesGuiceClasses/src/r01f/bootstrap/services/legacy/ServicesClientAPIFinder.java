package r01f.bootstrap.services.legacy;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import r01f.exceptions.Throwables;
import r01f.reflection.ReflectionUtils;
import r01f.services.client.ClientAPI;
import r01f.services.client.ClientAPIImplBase;
import r01f.services.client.ServiceProxiesAggregator;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;

@Deprecated
@RequiredArgsConstructor
class ServicesClientAPIFinder {
/////////////////////////////////////////////////////////////////////////////////////////
//  CLIENT API AGGREGATORS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a {@link Set} that contains {@link ClientAPIDef} objects that relates the {@link ClientAPI} interfaces 
	 * with it's implementations ({@link ClientAPIForBeanServices}, {@link ClientAPIForEJBServices}, {@link ClientAPIForRESTServices}, etc)
	 * and also the default impl ({@link ClientAPIForDefaultServices}) specified at the [apiAppCode].client.properties.xml
	 * @param packageToLookForClientApi
	 * @return
	 */
	public Class<? extends ClientAPI> findClientAPI(final JavaPackage packageToLookForClientApi) {
		// Find all client apis
		List<JavaPackage> pckgs = Lists.newArrayListWithExpectedSize(2);
		pckgs.add(packageToLookForClientApi);	
		pckgs.add(JavaPackage.of(ClientAPI.class));
		Set<Class<? extends ClientAPI>> clientAPIImplTypes = ServicesPackages.findSubTypesAt(ClientAPI.class,
																						     pckgs,
																						     this.getClass().getClassLoader());
		if (CollectionUtils.isNullOrEmpty(clientAPIImplTypes)) throw new IllegalStateException(Throwables.message("NO types extending {} was found at {}",
																											      ClientAPI.class,packageToLookForClientApi));
		Collection<Class<? extends ClientAPI>> clientApiTypes = FluentIterable.from(clientAPIImplTypes)
																	 .filter(new Predicate<Class<? extends ClientAPI>>() {
																					@Override
																					public boolean apply(final Class<? extends ClientAPI> clientAPIType) {
																						return ReflectionUtils.isInstanciable(clientAPIType);	// ignore interfaces or abstract types
																					}
																	 		 })
																	 .toSet();
		if (CollectionUtils.isNullOrEmpty(clientApiTypes)) throw new IllegalStateException(Throwables.message("NO instanciable types extending {} was found at {}",
																										  ClientAPI.class,packageToLookForClientApi));
		if (clientApiTypes.size() != 1) throw new IllegalStateException(Throwables.message("More than a single {} type was found at {}",
																					   ClientAPI.class,packageToLookForClientApi));
		return Iterables.getOnlyElement(clientApiTypes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PROXY AGGREGATORS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Uses the client api type to guess the proxy aggregator type
	 * Remember that client api type extends {@link ClientAPIImplBase} and must be parameterized with
	 * a type extending {@link ServiceProxiesAggregator}
	 * <pre class='brush:java'>
	 * 		public class MyClientApi
	 * 			 extends ClientAPIBase<MyClientProxyAggregator> {
	 * 			...
	 * 		} 
	 * </pre>
	 * @param clientAPIType
	 * @return
	 */
	@SuppressWarnings({ "unchecked","static-method" })
	public Class<? extends ServiceProxiesAggregator> findClientAPIProxyAggregatorType(final Class<? extends ClientAPI> clientAPIType) {
		ParameterizedType t = (ParameterizedType)clientAPIType.getGenericSuperclass(); // ClientAPIBase<T extends ServiceProxiesAggregator>
		Class<?> clazz = (Class<?>)t.getActualTypeArguments()[0]; // Class<? extends ServiceProxiesAggregator>
		if (clazz == null) throw new IllegalArgumentException(clientAPIType + " MUST extends " + ClientAPIImplBase.class + " with a type parameter that extends " + ServiceProxiesAggregator.class);
		Class<? extends ServiceProxiesAggregator> proxyType = (Class<? extends ServiceProxiesAggregator>)clazz;
		return proxyType;
	}
	public Class<? extends ServiceProxiesAggregator> findClientAPIProxyAggregatorType(final JavaPackage packageToLookForServiceProxiesAggregatorType) {
		// Find all ServiceProxiesAggregatorImpl interface subtypes
		List<JavaPackage> pckgs = Lists.newArrayListWithExpectedSize(2);
		pckgs.add(packageToLookForServiceProxiesAggregatorType);	
		pckgs.add(JavaPackage.of(ServiceProxiesAggregator.class));
		Set<Class<? extends ServiceProxiesAggregator>> proxyImplTypes = ServicesPackages.findSubTypesAt(ServiceProxiesAggregator.class,
																										pckgs,
																										this.getClass().getClassLoader());
		if (CollectionUtils.isNullOrEmpty(proxyImplTypes)) throw new IllegalStateException(Throwables.message("NO type extending {} was found at package {}",
																											  ServiceProxiesAggregator.class,
																											  packageToLookForServiceProxiesAggregatorType));
		Collection<Class<? extends ServiceProxiesAggregator>> proxyAggrTypes = FluentIterable.from(proxyImplTypes)
																					 .filter(new Predicate<Class<? extends ServiceProxiesAggregator>>() {
																									@Override
																									public boolean apply(final Class<? extends ServiceProxiesAggregator> proxyAggregatorImplType) {
																										return ReflectionUtils.isInstanciable(proxyAggregatorImplType);	// ignore interfaces
																									}
																					 		 })
																					 .toSet();
		if (CollectionUtils.isNullOrEmpty(proxyAggrTypes)) throw new IllegalStateException(Throwables.message("NO instanciable type extending {} was found at package {}",
																											  ServiceProxiesAggregator.class,
																											  packageToLookForServiceProxiesAggregatorType));
		if (proxyAggrTypes.size() != 1) throw new IllegalStateException(Throwables.message("More than a single {} type was found at {}",
																						   ServiceProxiesAggregator.class,packageToLookForServiceProxiesAggregatorType));
		return Iterables.getOnlyElement(proxyAggrTypes);
	}
}
