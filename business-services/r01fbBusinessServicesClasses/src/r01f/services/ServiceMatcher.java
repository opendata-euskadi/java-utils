package r01f.services;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.outline.TypeOutline;
import r01f.reflection.scanner.SubTypeOfScanner;
import r01f.services.core.CoreService;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.services.interfaces.ServiceInterface;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.types.JavaPackage;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class ServiceMatcher {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final ClassLoader _classLoader;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ServiceMatcher() {
		// default non-args constructor
		this(Thread.currentThread().getContextClassLoader());
	}
	public ServiceMatcher(final ClassLoader classLoader) {
		_classLoader = classLoader;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds subTypes of a given type scanning at given packages
	 * Used at:
	 * 		{@link ServicesClientAPIFinder}#findClientAPIProxyAggregatorTypes()
	 * 		{@link ServicesCoreBootstrapModulesFinder}#_findCoreBootstrapGuiceModuleTypesByAppModule
	 * 		{@link ServicesClientBootstrapModulesFinder}
	 * 		{@link ServicesClientInterfaceToImplAndProxyFinder}.ServiceInterfaceImplementingTypes
	 * @param superType
	 * @param pckgNames
	 * @return
	 */
	private static <T> Set<Class<? extends T>> _findSubTypesOf(final Class<T> superType,
															   final Collection<JavaPackage> pckgNames,
													  		   final ClassLoader otherClassLoader) {
		return SubTypeOfScanner.findSubTypesAt(superType,
											   pckgNames,
											   otherClassLoader);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds {@link ServiceInterface}-implementing interfaces
	 * @param serviceInterfacesPckg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<Class<? extends ServiceInterface>> findServiceInterfaceTypes(final Class<? extends ServiceInterface> serviceInterfacesBaseType) {
		log.warn("[1]: find service interface extending {}",
				 serviceInterfacesBaseType);

		// [0] - java packages where look for service interface types (required by org.reflections)
		Collection<JavaPackage> javaPackagesContainingServiceInterfaceTypes = _packagesWhereToLookForTypesExtending(serviceInterfacesBaseType);

		// [1] - do find
		Set<Class<? extends ServiceInterface>> serviceInterfaceTypes = _findSubTypesOf((Class<ServiceInterface>)serviceInterfacesBaseType,
																			    	   javaPackagesContainingServiceInterfaceTypes,		// ServiceInterface.class
																			    	   _classLoader);
		// [2] - the found service interface set contains also the R01F base service interfaces; remove them
		//		 ... also remove interface types NOT annotated with @ExposedServiceInterface (this is because sometimes there are BASE service interfaces
		//			 that we do NOT want to be detected as REAL -final- service interfaces; these BASE service interfaces are NOT annotated
		//			 with @ExposedServiceInterface)
		serviceInterfaceTypes = FluentIterable.from(serviceInterfaceTypes)
											  .filter(new Predicate<Class<? extends ServiceInterface>>() {
															@Override
															public boolean apply(final Class<? extends ServiceInterface> serviceInterface) {
																// service interfaces MUST be annotated with @ExposedServiceInterface
																return ReflectionUtils.typeAnnotation(serviceInterface,ExposedServiceInterface.class) != null;		// it's annotated with @ServiceInterfaceFor
															}
											  		  })
											  .toSet();
		// [2] - checkings
		if (CollectionUtils.hasData(serviceInterfaceTypes)) {
			for(Class<? extends ServiceInterface> serviceInterfaceType : serviceInterfaceTypes) {
				if (!ReflectionUtils.isInterface(serviceInterfaceType)) throw new IllegalStateException(String.format("%s is NOT a valid %s: it MUST be an interface",
																													  serviceInterfaceType,ServiceInterface.class.getSimpleName()));
			}
		}
		// [3] - Return
		if (CollectionUtils.isNullOrEmpty(serviceInterfaceTypes)) throw new IllegalStateException("Could NOT find any " + ServiceInterface.class.getSimpleName() + " types extending " + serviceInterfacesBaseType + " at java packages " + javaPackagesContainingServiceInterfaceTypes +
																								  " ensure that the intefacees extends " + ServiceInterface.class + " and are annotated with @" + ExposedServiceInterface.class.getSimpleName());
		if (log.isDebugEnabled()) log.debug("{}",serviceInterfaceTypes);
		return serviceInterfaceTypes;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * First it finds the {@link ServiceInterface}-extending interfaces and then tries to find
	 * the best {@link ServiceInterface} matching: if a {@link CoreService} impl is available it matches this one,
	 * but if it's not, it tries to match a {@link ServiceProxyImpl} one; if none is available an exception is thrown
	 * @param serviceInterfacesPckg
	 * @param serviceProxiesPckg
	 * @param coreImplsPckg
	 * @return
	 */
	public Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> findServiceInterfaceMatchings(final Class<? extends ServiceInterface> serviceInterfacesBaseType,
																						 						  final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType,
																						 						  final Class<? extends CoreService> coreServicesBaseType) {
		// [1] - Find service interfaces
		Set<Class<? extends ServiceInterface>> serviceInterfaceTypes = this.findServiceInterfaceTypes(serviceInterfacesBaseType);

		// [2] - Match
		return this.findServiceInterfaceMatchings(serviceInterfaceTypes,
												  serviceProxyImplsBaseType,
												  coreServicesBaseType);
	}
	/**
	 * Tries to find the best {@link ServiceInterface} matching: if a {@link CoreService} impl is available it matches this one,
	 * but if it's not, it tries to match a {@link ServiceProxyImpl} one; if none is available an exception is thrown
	 * @param serviceInterfaceTypes
	 * @param serviceProxiesPckg
	 * @param coreImplsPckg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> findServiceInterfaceMatchings(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																						 						  final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType,
																						 						  final Class<? extends CoreService> coreServiceBaseType) {
		log.warn("[2]: find matching for {} service interfaces: proxies MUST extend {} / core bean MUST extend {}",
				  serviceInterfaceTypes.size(),
				  serviceProxyImplsBaseType,// proxies
				  coreServiceBaseType);		// core impls

		// [1] - Find service interface to proxy matchings
		Map<Class<? extends ServiceInterface>,Class<? extends ServiceProxyImpl>> serviceInterfacesToProxyMatchs = serviceProxyImplsBaseType != null
																														? this.findServiceInterfaceToProxyMatch(serviceInterfaceTypes,
																																								serviceProxyImplsBaseType)
																														: null;	// no proxies
		log.warn("\t - found {} proxy impls extending {}",
				 serviceInterfacesToProxyMatchs != null ? serviceInterfacesToProxyMatchs.size() : 0,
				 serviceProxyImplsBaseType);

		// [2] - Find service interface to core matchings
		Map<Class<? extends ServiceInterface>,Class<? extends CoreService>> serviceInterfacesToCoreImplMatchs = coreServiceBaseType != null
																														? this.findServiceInterfaceToCoreImplMatch(serviceInterfaceTypes,
																																					     		   coreServiceBaseType)
																														: null;			// no bean impls
		log.warn("\t - found {} core impls extending {}",
				 serviceInterfacesToCoreImplMatchs != null ? serviceInterfacesToCoreImplMatchs.size() : 0,
				 coreServiceBaseType);
		// [3] - mix both
		Map<Class<? extends ServiceInterface>,Class<? extends ServiceInterface>> outMatchings = Maps.newHashMapWithExpectedSize(serviceInterfaceTypes.size());
		for (Class<? extends ServiceInterface> serviceInterfaceType : serviceInterfaceTypes) {
			Class<? extends ServiceInterface> matchedType = null;

			// a) try to find a core impl type
			if (matchedType == null) {
				Class<? extends CoreService> coreImplType = serviceInterfacesToCoreImplMatchs != null ? serviceInterfacesToCoreImplMatchs.get(serviceInterfaceType)
																									  : null;
				if (coreImplType != null) matchedType = (Class<? extends ServiceInterface>)coreImplType;
			}
			// b) try to find a proxy type
			if (matchedType == null) {
				Class<? extends ServiceProxyImpl> proxyType = serviceInterfacesToProxyMatchs != null ? serviceInterfacesToProxyMatchs.get(serviceInterfaceType)
																									 : null;
				if (proxyType != null) matchedType = (Class<? extends ServiceInterface>)proxyType;
			}
			// c) check
			if (matchedType == null) {
				log.warn(ServiceMatcher.serviceInterfaceMatchingsDebugInfoFor(serviceInterfacesToCoreImplMatchs));
				log.warn(ServiceMatcher.serviceInterfaceMatchingsDebugInfoFor(serviceInterfacesToProxyMatchs));
				throw new IllegalStateException(String.format("Could NOT find a matching for %s-implementing type %s, either as %s neither as %s",
															  ServiceInterface.class.getSimpleName(),serviceInterfaceType,
															  CoreService.class.getSimpleName(),ServiceProxyImpl.class.getSimpleName()));
			}
			// c) add to the out matchings
			outMatchings.put(serviceInterfaceType,matchedType);
		}
		// [4] - Return
		return outMatchings;
	}
	/**
	 * First it finds the {@link ServiceInterface}-extending interfaces and then tries to find
	 * each service proxy to the the correspondent service implementation
	 * The proxy finding is restricted to the given package
	 * @param serviceInterfacesPckg
	 * @param serviceProxiesPckg
	 * @return
	 */
	public Map<Class<? extends ServiceInterface>,Class<? extends ServiceProxyImpl>> findServiceInterfaceToProxyMatch(final Class<? extends ServiceInterface> serviceInterfacesBaseType,
																													 final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType) {
		// [1] - Find service interfaces
		Set<Class<? extends ServiceInterface>> serviceInterfaceTypes = this.findServiceInterfaceTypes(serviceInterfacesBaseType);

		// [2] - Match
		return this.findServiceInterfaceToProxyMatch(serviceInterfaceTypes,
												  	 serviceProxyImplsBaseType);
	}
	/**
	 * Given a collection of service interfaces, this method finds each service proxy to the the correspondent service implementation
	 * The proxy finding is restricted to the given package
	 * @param serviceInterfaceTypes
	 * @param serviceProxiesPckg
	 * @return
	 */
	public Map<Class<? extends ServiceInterface>,Class<? extends ServiceProxyImpl>> findServiceInterfaceToProxyMatch(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																													 final Class<? extends ServiceProxyImpl> serviceProxyImplsBaseType) {
		Collection<JavaPackage> javaPackagesWhereLookForProxyImpls = _packagesWhereToLookForTypesExtending(serviceProxyImplsBaseType);
		return _findServiceInterfaceMatchings(serviceInterfaceTypes,
											  ServiceProxyImpl.class,
											  javaPackagesWhereLookForProxyImpls);
	}
	/**
	 * First it finds the {@link ServiceInterface}-extending interfaces and then tries to find
	 * each service proxy to the the correspondent service implementation
	 * The proxy finding is restricted to the given package
	 * @param serviceInterfacesPckg
	 * @param coreServicesPckg
	 * @return
	 */
	public Map<Class<? extends ServiceInterface>,Class<? extends CoreService>> findServiceInterfaceToCoreImplMatch(final Class<? extends ServiceInterface> serviceInterfacesBaseType,
																												   final Class<? extends CoreService> coreServiceBaseType) {
		// [1] - Find service interfaces
		Set<Class<? extends ServiceInterface>> serviceInterfaceTypes = this.findServiceInterfaceTypes(serviceInterfacesBaseType);
		// [2] - Match
		return this.findServiceInterfaceToCoreImplMatch(serviceInterfaceTypes,
					 								    coreServiceBaseType);
	}
	/**
	 * Given a collection of service interfaces, this method finds each service proxy to the the correspondent service implementation
	 * The proxy finding is restricted to the given package
	 * @param serviceInterfaceTypes
	 * @param coreServicesPckg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Class<? extends ServiceInterface>,Class<? extends CoreService>> findServiceInterfaceToCoreImplMatch(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																												   final Class<? extends CoreService> coreServicesBaseType) {
		Collection<JavaPackage> javaPackagesWhereLookForCoreServices = _packagesWhereToLookForTypesExtending(coreServicesBaseType);
		return _findServiceInterfaceMatchings(serviceInterfaceTypes,
											  (Class<CoreService>)coreServicesBaseType, 	// CoreService.class,
											  javaPackagesWhereLookForCoreServices);
	}
	private <T> Map<Class<? extends ServiceInterface>,Class<? extends T>> _findServiceInterfaceMatchings(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																										 final Class<T> type,
																										 final Collection<JavaPackage> pckgs) {
		return _findServiceInterfaceMatchings(serviceInterfaceTypes,
											  type,
											  pckgs,
											  true);	// strict mode: throw if a service iface matching is not found
	}
	private <T> Map<Class<? extends ServiceInterface>,Class<? extends T>> _findServiceInterfaceMatchings(final Set<Class<? extends ServiceInterface>> serviceInterfaceTypes,
																										 final Class<T> type,
																										 final Collection<JavaPackage> pckgs,
																										 final boolean strict) {
		// [1] - do find type implementing types
		Set<Class<? extends T>> typeImplTypes = _findSubTypesOf(type,
															    pckgs,
																this.getClass().getClassLoader());
		// [2] - Filter abstract or interface types
		typeImplTypes = FluentIterable.from(typeImplTypes)
									  .filter(new Predicate<Class<? extends T>>() {
													@Override
													public boolean apply(final Class<? extends T> implType) {
														return ReflectionUtils.isInstanciable(implType);
													}
									  		  })
									  .toSet();
		// [3] - Checkings
    	if (CollectionUtils.isNullOrEmpty(typeImplTypes)
    	 && CollectionUtils.hasData(serviceInterfaceTypes)) throw new IllegalStateException(String.format("Could NOT find any type implementing %s at %s for service interfaces: %s",
    			 																					  	  type,pckgs,serviceInterfaceTypes));

		// [4] - Match each type to it's corresponding ServiceInterface type
    	//		 (note that a service proxy could proxy for more than a single service interface)
    	Map<Class<? extends ServiceInterface>,Class<? extends T>> outServiceInterfaceMatchings = Maps.newHashMapWithExpectedSize(serviceInterfaceTypes.size());
		for (Class<? extends T> typeImplType : typeImplTypes) {
			boolean matched = false;
			for (Class<? extends ServiceInterface> serviceInterfaceType : serviceInterfaceTypes) {
				if (ReflectionUtils.isImplementing(typeImplType,
												   serviceInterfaceType)) {
					if (outServiceInterfaceMatchings.containsKey(serviceInterfaceType))	throw new IllegalStateException(String.format("There're TWO %s-implementing types that implements the SAME %s (%s): %s and %s, " +
												   																					  "this is usually the case for BASE service interfaces; " +
												   																					  "annotate EXPOSED service interfaces with @{} and DO NOT annotate the BASE service interface",
																	  																  type.getSimpleName(),ServiceInterface.class.getSimpleName(),
																	  																  serviceInterfaceType,
																	  																  typeImplType,outServiceInterfaceMatchings.get(serviceInterfaceType),
																	  																  ExposedServiceInterface.class.getSimpleName()));
					outServiceInterfaceMatchings.put(serviceInterfaceType,
											 		 typeImplType);
					matched = true;
				}
			}
			if (strict && !matched) throw new IllegalStateException(String.format("There's NO %s-implementing type for the %s %s",
																				  ServiceInterface.class.getSimpleName(),
																				  type.getSimpleName(),typeImplType));
		}
		// [3] - Return
		return outServiceInterfaceMatchings;
	}
	private static Collection<JavaPackage> _packagesWhereToLookForTypesExtending(final Class<?> iface) {
		// BEWARE!
		// org.reflections is used to scan subtypes of CoreService. This library requires
		// ALL the packages in the type hierarchy to be given to the scan methods:
		// <pre class='brush:java'>
		//  		CoreService
		//  			|-- interface 1
		//  					|--  interface 2
		//  							|-- all the core service impl
		// </pre>
		// The packages where CoreService, interface 1 and interface 2 resides MUST be handed
		// to the subtypeOfScan method of org.reflections

		// find the hierarchy between the given interface and CoreService
		TypeOutline typeOutline = new TypeOutline(iface);
		log.warn("{} type hierarchy outline:\n{}",
			     iface,typeOutline.debugInfo());
		return FluentIterable.from(typeOutline.getNodesFromGeneralToSpezialized())
						.transform(new Function<Class<?>,JavaPackage>() {
											@Override
											public JavaPackage apply(final Class<?> type) {
												return JavaPackage.of(type);
											}
								   })
						.filter(new Predicate<JavaPackage>() {
											@Override
											public boolean apply(final JavaPackage pckg) {
												return !pckg.isJavaLang();
											}
								})
						.toSet();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Prints debug info about service interface matchings
	 * @param matchings
	 * @return
	 */
	public static <T> String serviceInterfaceMatchingsDebugInfoFor(final Map<Class<? extends ServiceInterface>,Class<? extends T>> matchings) {
		StringBuilder dbg = new StringBuilder();

		dbg.append(ServiceInterface.class).append(" to ").append(ServiceProxyImpl.class).append(" or ").append(CoreService.class).append(" matchings: ")
		   .append(CollectionUtils.hasData(matchings) ? matchings.size() : 0).append(" items\n");

		if (CollectionUtils.isNullOrEmpty(matchings)) return Strings.customized("NO %s matchings",
																			    ServiceInterface.class.getSimpleName());

		for (Iterator<Map.Entry<Class<? extends ServiceInterface>,Class<? extends T>>> meIt = matchings.entrySet().iterator(); meIt.hasNext(); ) {
			Map.Entry<Class<? extends ServiceInterface>,Class<? extends T>> me = meIt.next();

			Class<? extends ServiceInterface> serviceInterfaceType = me.getKey();
			Class<? extends T> proxyOrImplType = me.getValue();

			boolean isProxy = false;
			if (ReflectionUtils.isSubClassOf(proxyOrImplType,ServiceProxyImpl.class)) {
				isProxy = true;
			}
			else if (ReflectionUtils.isSubClassOf(proxyOrImplType,CoreService.class)) {
				isProxy = false;
			}

			dbg.append("\t-").append(serviceInterfaceType).append(" >").append(isProxy ? "PROXY" : "").append("> ").append(proxyOrImplType);
			if (meIt.hasNext()) dbg.append("\n");
		}
		return dbg.toString();
	}
}
