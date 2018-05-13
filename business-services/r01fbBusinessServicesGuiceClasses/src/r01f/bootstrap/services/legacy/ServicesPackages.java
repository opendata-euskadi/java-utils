package r01f.bootstrap.services.legacy;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.exceptions.Throwables;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.scanner.SubTypeOfScanner;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppAndModule;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.services.interfaces.ServiceInterface;
import r01f.types.JavaPackage;
import r01f.util.types.Strings;

@Deprecated
class ServicesPackages {
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
	public static <T> Set<Class<? extends T>> findSubTypesAt(final Class<T> superType,
													  		 final List<JavaPackage> pckgNames,
													  		 final ClassLoader otherClassLoader) {
		return SubTypeOfScanner.findSubTypesAt(superType,
											   pckgNames,
											   otherClassLoader);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern COMMON_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN = Pattern.compile("^(.+)\\.client\\.*");
	private static final Pattern STANDARD_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN1 = Pattern.compile("^(.+)\\.client\\.internal$");
	private static final Pattern STANDARD_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN2 = Pattern.compile("^(.+)\\.client\\.([^.]+)\\.internal$");
	private static final Pattern STANDARD_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN3 = Pattern.compile("^(.+)\\.client\\.internal\\.([^.]+)$");
	
	/**
	 * Guess the client api app code from the client api bootstrap module
	 * @param clientApiBootstrapModule
	 * @return
	 */
	public static <S extends ServicesClientAPIBootstrapGuiceModuleBase> ClientApiAppCode clientApiAppCodeFrom(final Class<S> clientApiBootstrapModule) {
		ClientApiAppCode outAppCode = null;
		
		String bootstrapModulePckg = clientApiBootstrapModule.getPackage().getName();
		Matcher m = COMMON_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN.matcher(bootstrapModulePckg);
		if (m.find()) {
			outAppCode = ClientApiAppCode.forId(m.group(1));
		} 
		if (outAppCode == null) throw new IllegalArgumentException("The client bootstrap module " + clientApiBootstrapModule + " is NOT at an standard package location (" + COMMON_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN + ") so the client api appCode cannot be guessed");
		return outAppCode;
	}
 	/**
 	 * Guess the service interfaces package from the client api bootstrap module
 	 * @param clientApiBootstrapModule
 	 * @return
 	 */
 	public static <S extends ServicesClientAPIBootstrapGuiceModuleBase> JavaPackage clientApiPackageFrom(final Class<S> clientApiBootstrapModule) {
		// client bootstrap modules at standard locations have a package like:
		//		- if the client module is the default one: 		{clientApiAppCode}.client.internal
		//		- if the client module is NOT the default one: 	{clientApiAppCode}.client.{clientApiModule}.internal
		// so the client api appCode/module can be extracted from the package name (if it's an standard one)
		JavaPackage outPckg = null;
		
		String pckgName = clientApiBootstrapModule.getPackage().getName();
		Matcher m = STANDARD_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN1.matcher(pckgName);
		if (m.find()) {
			outPckg = JavaPackage.of(Strings.customized("{}.client.api",
														m.group(1)));
		} else {
			m = STANDARD_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN2.matcher(pckgName);
			if (m.find()) {
				outPckg = JavaPackage.of(Strings.customized("{}.client.{}.api",
															m.group(1),m.group(2)));
			} else {
				m = STANDARD_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN3.matcher(pckgName);
				if (m.find()) {
					outPckg = JavaPackage.of(Strings.customized("{}.client.api.{}",
															m.group(1),m.group(2)));
				}
			}
		}
		if (outPckg == null) throw new IllegalArgumentException("The client bootstrap module " + clientApiBootstrapModule + " is NOT at an standard package location so the client api appCode/module cannot be guessed");
		return outPckg;
	}
 	/**
 	 * Guess the service interfaces package from the client api bootstrap module
 	 * @param clientApiBootstrapModule
 	 * @return
 	 */
 	public static <S extends ServicesClientAPIBootstrapGuiceModuleBase> JavaPackage serviceIntefacePackageFrom(final Class<S> clientApiBootstrapModule) {
		// client bootstrap modules at standard locations have a package like:
		//		- if the client module is the default one: 		{clientApiAppCode}.client.internal
		//		- if the client module is NOT the default one: 	{clientApiAppCode}.client.{clientApiModule}.internal
		// so the client api appCode/module can be extracted from the package name (if it's an standard one)
		JavaPackage outPckg = null;
		
		String pckgName = clientApiBootstrapModule.getPackage().getName();
		Matcher m = STANDARD_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN1.matcher(pckgName);
		if (m.find()) {
			outPckg = JavaPackage.of(Strings.customized("{}.client.api.interfaces",
														m.group(1)));
		} else {
			m = STANDARD_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN2.matcher(pckgName);
			if (m.find()) {
				outPckg = JavaPackage.of(Strings.customized("{}.client.{}.api.interfaces",
															m.group(1),m.group(2)));
			} else {
				m = STANDARD_CLIENT_BOOTSTRAP_MODULE_PACKAGE_PATTERN3.matcher(pckgName);
				if (m.find()) {
					outPckg = JavaPackage.of(Strings.customized("{}.client.api.{}.interfaces",
															m.group(1),m.group(2)));
				}
			}
		}
		if (outPckg == null) throw new IllegalArgumentException("The client bootstrap module " + clientApiBootstrapModule + " is NOT at an standard package location so the client api appCode/module cannot be guessed");
		return outPckg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CORE
/////////////////////////////////////////////////////////////////////////////////////////	
	public static JavaPackage coreGuiceModulePackage(final CoreAppCode coreAppCode) {
		return new JavaPackage(Strings.customized("{}.internal",
					  			  				  coreAppCode.getId()));
	}	
	public static JavaPackage coreServicesPackage(final CoreAppCode coreAppCode) {
		return new JavaPackage(Strings.customized("{}.services",
					  			  				  coreAppCode));
	}
	/**
	 * Guess the core appCode/module from the service interface type
	 * Note that the service interface type MUST be annotated with the @{@link ServiceInterfaceFor} annotation that sets the appCode / module
	 * @param serviceInterface
	 * @return
	 */
	public static <S extends ServiceInterface> CoreAppAndModule coreAppAndModuleFromServiceInterfaceType(final Class<S> serviceInterface) {
		ServiceInterfaceFor serviceIfaceForAnnot = ReflectionUtils.typeAnnotation(serviceInterface,ServiceInterfaceFor.class);
		if (serviceIfaceForAnnot == null
		 || Strings.isNullOrEmpty(serviceIfaceForAnnot.appCode())
		 || Strings.isNullOrEmpty(serviceIfaceForAnnot.module())) {
			throw new IllegalStateException(Throwables.message("Service interface {} is NOT annotated with @{} or the appCode / module annotation's attributes are NOT set",
															    serviceInterface,ServiceInterfaceFor.class));
		}
		CoreAppCode coreAppCode = CoreAppCode.forId(serviceIfaceForAnnot.appCode());
		CoreModule module = CoreModule.forId(serviceIfaceForAnnot.module());
		return CoreAppAndModule.of(coreAppCode,module);
	}
	/**
	 * Returns the appCode from the services core bootstrap type
	 * (beware that the appCode is considered to be anything BEFORE the "internal" token at the core bootstrap type package)
	 * @param coreBootstrapType
	 * @return
	 */
	public static CoreAppCode coreAppCodeFromCoreBootstrapModuleType(final Class<? extends ServicesCoreBootstrapGuiceModule> coreBootstrapType) {
		String[] pckgSplitted = coreBootstrapType.getPackage().getName().split("\\.");
		StringBuilder appCode = new StringBuilder();
		for (String pckgToken : pckgSplitted) {
			if (pckgToken.equals("internal")) break;	// anything BEFORE internal
			if (appCode.length() > 0) appCode.append(".");
			appCode.append(pckgToken);
		}
		return CoreAppCode.forId(appCode.toString());		// the appCode is extracted from the package
	}
	/**
	 * Returns the appComponent from the services core bootstrap type
	 * @param coreBootstrapType
	 * @return
	 */
	public static CoreModule coreAppModuleFromCoreBootstrapModuleTypeOrNull(final Class<? extends ServicesCoreBootstrapGuiceModule> coreBootstrapType) {
		return _coreAppModuleFromCoreBootstrapModuleType(coreBootstrapType,
														 null,
														 false);
	}
	/**
	 * Returns the appComponent from the services core bootstrap type
	 * @param coreBootstrapType
	 * @param suffix
	 * @return
	 */
	public static CoreModule coreAppModuleFromCoreBootstrapModuleTypeOrNull(final Class<? extends ServicesCoreBootstrapGuiceModule> coreBootstrapType,
																		   	final String suffix) {
		return _coreAppModuleFromCoreBootstrapModuleType(coreBootstrapType,
														 suffix,
														 false);
	}
	/**
	 * Returns the appComponent from the services core bootstrap type
	 * @param coreBootstrapType
	 * @return
	 */
	public static CoreModule coreAppModuleFromCoreBootstrapModuleTypeOrThrow(final Class<? extends ServicesCoreBootstrapGuiceModule> coreBootstrapType) {
		return _coreAppModuleFromCoreBootstrapModuleType(coreBootstrapType,
														 null,
														 true);
	}
	/**
	 * Returns the appComponent from the services core bootstrap type
	 * @param coreBootstrapType
	 * @param suffix
	 * @return
	 */
	public static CoreModule coreAppModuleFromCoreBootstrapModuleTypeOrThrow(final Class<? extends ServicesCoreBootstrapGuiceModule> coreBootstrapType,
																			 final String suffix) {
		return _coreAppModuleFromCoreBootstrapModuleType(coreBootstrapType,
														 suffix,
														 true);
	}
	/**
	 * Returns the appComponent from the services core bootstrap type
	 * @param coreBootstrapType
	 * @return
	 */
	private static CoreModule _coreAppModuleFromCoreBootstrapModuleType(final Class<? extends ServicesCoreBootstrapGuiceModule> coreBootstrapType,
																	    final String suffix,
																	    final boolean strict) {
		ServicesCore serviceCoreAnnot = ReflectionUtils.typeAnnotation(coreBootstrapType,
																	   ServicesCore.class);
		String modId = serviceCoreAnnot != null ? serviceCoreAnnot.moduleId()
												: null;
		if (strict && Strings.isNullOrEmpty(modId)) throw new IllegalStateException(Throwables.message("{} core bootstrap type is NOT annotated with @{} or the annotation's moduleId property is NOT set",
																					 		 		   coreBootstrapType,ServicesCore.class.getName()));
		if (modId == null) return null;
		
		return suffix != null ? CoreModule.forId(modId + "." + suffix)
							  : CoreModule.forId(modId);
		
	}
}
