package r01f.reflection.scanner;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import r01f.types.JavaPackage;
import r01f.util.types.collections.CollectionUtils;


@Slf4j
public class SubTypeOfScanner {
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
													  		 final Collection<JavaPackage> pckgNames) {
		return SubTypeOfScanner.findSubTypesAt(superType,
											   pckgNames,
											   null,	// the default scanner filter will apply
											   SubTypeOfScanner.class.getClassLoader());
	}
	/**
	 * Finds subTypes of a given type scanning at given packages
	 * Used at:
	 * 		{@link ServicesClientAPIFinder}#findClientAPIProxyAggregatorTypes()
	 * 		{@link ServicesCoreBootstrapModulesFinder}#_findCoreBootstrapGuiceModuleTypesByAppModule
	 * 		{@link ServicesClientBootstrapModulesFinder}
	 * 		{@link ServicesClientInterfaceToImplAndProxyFinder}.ServiceInterfaceImplementingTypes
	 * @param superType
	 * @param pckgNames
	 * @param otherClassLoader
	 * @return
	 */
	public static <T> Set<Class<? extends T>> findSubTypesAt(final Class<T> superType,
													  		 final Collection<JavaPackage> pckgNames,
													  		 final ClassLoader otherClassLoader) {
		return SubTypeOfScanner.findSubTypesAt(superType,
											   pckgNames,
											   null,	// the default scanner filter will apply
											   otherClassLoader);
	}
	/**
	 * Finds subTypes of a given type scanning at given packages
	 * @param superType
	 * @param pckgs
	 * @param scannerFilter
	 * @param otherClassLoader
	 * @return
	 */
	public static <T> Set<Class<? extends T>> findSubTypesAt(final Class<T> superType,
													  		 final Collection<JavaPackage> pckgs,
													  		 final Predicate<JavaPackage> scannerFilter,
													  		 final ClassLoader otherClassLoader) {
		log.info("...finding subtypes of {} at packages {} (BEWARE that every type between the type to be found and the supertype MUST be accesible in the package names list)",
				 superType,pckgs);
		Collection<URL> pckgUrls = _urlsForPackages(pckgs,
											  		otherClassLoader);
		
		Set<Class<? extends T>> outSubTypes = null;
		if (CollectionUtils.isNullOrEmpty(pckgUrls)) {
			log.error("Could NOT get any URL for packages {} from any classloader!!!",pckgs);
			// The org.reflections' ClasspathHelper.forPackage method, at the end does: 
	        // 		for (ClassLoader classLader : ClasspathHelper.classLoaders()) {
			//			Enumeration<URL> urls = classLoader.getResources(<change package dots for />);
			//			if (urls != null) convert the enumeration into a Collection<URL> and return
			// 		}
			// BUT for an unknown reason, when the package is INSIDE a JAR at APP-INF/lib,
			// classLoader.getResources(pckgName) returns null
			// WHAT TO DO???
			// see:
			//		http://www.javaworld.com/article/2077352/java-se/smartly-load-your-properties.html
			//		http://stackoverflow.com/questions/676250/different-ways-of-loading-a-file-as-an-inputstream
			// 		https://github.com/Atmosphere/atmosphere/issues/1229
			//		http://middlewaresnippets.blogspot.com.es/2011/05/class-loading-and-application-packaging.html
			//
		}
		else {
			outSubTypes = SubTypeOfScanner.findSubTypesAtClassPathUrls(superType,
																	   pckgUrls,
																	   scannerFilter,
																	   otherClassLoader);
		}
		
		// When deploying at a WLS, the classes are inside a jar file whose URL is zip:/dominio_wls/servers/server1/tmp/_WL_user/myEAR/n5ymxm/APP-INF/lib/{appCode}Classes.jar!/{appCode}/client/internal  
		// (see org.reflections.vfs.Vfs.DefaultUrlTypes) 
		// ... so the scanned URLs loses the package part {appCode}Classes.jar!/{appCode}/client/internal and ALL jar classes are scanned
		if (CollectionUtils.hasData(outSubTypes)) {
			outSubTypes = FluentIterable.from(outSubTypes)
										.filter(new Predicate<Class<? extends T>>() {
														@Override
														public boolean apply(final Class<? extends T> subType) {
															boolean inPackage = false;
															for (JavaPackage pckg : pckgs) {
																if (subType.getPackage().getName().startsWith(pckg.asString())) {
																	inPackage = true;
																	break;
																}
															}
															return inPackage;
														}
												})
										.toSet();
		}
		return outSubTypes;
	}
	/**
	 * Finds subTypes of a given type scanning at given packages
	 * @param superType
	 * @param classPathUrls
	 * @pram scannerFilter
	 * @param otherClassLoader
	 * @return
	 */
	public static <T> Set<Class<? extends T>> findSubTypesAtClassPathUrls(final Class<T> superType,
													  		 			  final Collection<URL> classPathUrls,
													  		 			  final Predicate<JavaPackage> scannerFilter,
													  		 			  final ClassLoader otherClassLoader) {
		log.info("...finding subtypes of {} at classpath urls {} (BEWARE that every type between the type to be found and the supertype MUST be accesible in the classpath url list)",
				 superType,classPathUrls);
		
		// ensure the scanner filter
		Predicate<String> theScannerFilter = scannerFilter != null ? ScannerFilter.createScannerFilter(scannerFilter)
																   : ScannerFilter.DEFAULT_TYPE_FILTER;
		
		// The usual case (at least in tomcat) is that the package resources URLs can be found 
		// and org.Reflections can be used
		Reflections typeScanner = new Reflections(new ConfigurationBuilder()		// Reflections library NEEDS to have both the interface containing package and the implementation containing package
															.setUrls(classPathUrls)	// see https://code.google.com/p/reflections/issues/detail?id=53
															.filterInputsBy(theScannerFilter)
															.setScanners(new SubTypesScanner(true)));	// true = exclude object class
		Set<Class<? extends T>> outSubTypes = typeScanner.getSubTypesOf(superType);
		
		return outSubTypes;
	}	
	private static Collection<URL> _urlsForPackages(final Collection<JavaPackage> pckgNames,
											  		final ClassLoader otherClassLoader) {
		if (CollectionUtils.isNullOrEmpty(pckgNames)) throw new IllegalArgumentException();
		List<URL> outUrls = Lists.newLinkedList();
		for (JavaPackage pckgName : pckgNames) {
			outUrls.addAll(_urlsForPackage(pckgName,
										   otherClassLoader));
		}
		return Sets.newLinkedHashSet(outUrls);
	}
	private static Collection<URL> _urlsForPackage(final JavaPackage pckg,
                                                   final ClassLoader otherClassLoader) {
		ClassLoader[] classLoaders = _scanClassLoaders(otherClassLoader);
         
		// org.reflections.ClasspathHelper seems to return ONLY the jar or path containing the given package
		// ... so the package MUST be added back to the url to minimize scan time and unneeded class loading
        Collection<URL> outUrls = ClasspathHelper.forPackage(pckg.asString(),
                                                             classLoaders);
        if (CollectionUtils.hasData(outUrls)) {
        	outUrls = FluentIterable.from(outUrls)
                                    .transform(new Function<URL,URL>() {
														@Override
														public URL apply(final URL url) {
															try {
																URL fullUrl = new URL(url.toString() + _resourceName(pckg));
																log.trace("URL to be scanned: {}",fullUrl);
														        return fullUrl;
														    } catch(Throwable th) {
														    	th.printStackTrace(System.out);
														    }
														    return url;
														}
                                               })
                                    .toList();
         }
         return outUrls;
	}
	private static ClassLoader[] _scanClassLoaders(final ClassLoader otherClassLoader) {
		ClassLoader[] outClassLoaders =	otherClassLoader != null ? ClasspathHelper.classLoaders(ClasspathHelper.staticClassLoader(),
											 						 							ClasspathHelper.contextClassLoader(),
											 						 							otherClassLoader)
																 : ClasspathHelper.classLoaders(ClasspathHelper.staticClassLoader(),
											 						 							ClasspathHelper.contextClassLoader());
		return outClassLoaders;
	}	
    private static String _resourceName(final JavaPackage name) {
        if (name == null) return null;
        String resourceName = name.asString().replace(".","/")
        						  			 .replace("\\", "/");
        if (resourceName.startsWith("/")) resourceName = resourceName.substring(1);
        return resourceName;
    }
}
