package r01f.bootstrap.services.legacy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.services.interfaces.ServiceInterface;

/**
 * Annotation that tells the system that a type is a service interface (see {@link ServiceInterface})
 */
@Deprecated
@Target({ ElementType.TYPE }) 
@Retention(RetentionPolicy.RUNTIME)
@interface ServiceInterfaceFor {
	/**
	 * The appCode
	 */
	String appCode();
	/**
	 * The app module
	 */
	String module();
}
