package r01f.bootstrap.services.legacy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Annotation that tells the {@link ServicesCoreBootstrapModulesFinder} type in charge of bootstraping the core
 * services that a service guice module:
 * <ul>	
 * 		<li>Is for a certain application module (from the ones in r01m.client.properties.xml)</li>
 * 		<li>DEPENDS UPON or NEEDS another module</li>
 * </ul>
 * <pre class='brush:java'>
 * 		@ServicesCore(moduleId="myModule",				// The id on r01m.client.properties.xml				
 * 					  dependsOn={ServicesImpl.Bean})	// The REST module depends on (or needs) the BEAN module
 * 		public class MyServiceBootstrpingModule
 * 			 extends RESTImplementedServicesCoreGuiceModuleBase {
 * 			....
 * 		}
 * </pre>
 */
@Deprecated
@BindingAnnotation 
@Target({ ElementType.TYPE }) 
@Retention(RetentionPolicy.RUNTIME)
public @interface ServicesCore {
	/**
	 * The module id from r01m.client.properties.xml
	 */
	String moduleId();
//	/**
//	 * The module dependencies
//	 */
//	ServicesImpl[] dependsOn() ;//default ServicesImpl.NULL;
//	/**
//	 * Some times a bootstrap guice module for a coreAppCode/module depends upon a bootstrap module
//	 * of OTHER coreAppCode/module
//	 * (it's NOT necessary to set this value if the dependencies are at the same appCode/module)
//	 */
//	String fromOtherCoreAppCodeAndModule() default "";
}
