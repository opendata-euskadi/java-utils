package r01f.bootstrap.client.user;

import com.google.inject.Binder;

import lombok.EqualsAndHashCode;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.bootstrap.services.config.client.ServicesClientGuiceBootstrapConfig;
import r01f.inject.HasMoreBindings;

/**
 * Client-API bindings
 */
@EqualsAndHashCode(callSuper=true) // This is important for guice modules
public class UsersClientBootstrapGuiceModule
  	 extends ServicesClientAPIBootstrapGuiceModuleBase 	// this is a client guice bindings module
  implements HasMoreBindings {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public UsersClientBootstrapGuiceModule(final ServicesClientGuiceBootstrapConfig servicesClientBootstrapCfg) {
		super(servicesClientBootstrapCfg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GUICE MODULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configureMoreBindings(final Binder binder) {
		_bindModelObjectExtensionsModule(binder);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MODEL EXTENSIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param binder
	 * @return bindings for the model extensions
	 */
	private static void _bindModelObjectExtensionsModule(final Binder binder) {
		// nothing
	}
}
