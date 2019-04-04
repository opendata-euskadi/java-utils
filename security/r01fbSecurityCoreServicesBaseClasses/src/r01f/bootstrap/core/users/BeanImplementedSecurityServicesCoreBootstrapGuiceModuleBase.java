package r01f.bootstrap.core.users;

import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;

import lombok.EqualsAndHashCode;
import r01f.bootstrap.BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.bootstrap.services.core.DBPersistenceGuiceModule;
import r01f.inject.HasMoreBindings;
import r01f.inject.Matchers;

@EqualsAndHashCode(callSuper=true)				// This is important for guice modules
public abstract class BeanImplementedSecurityServicesCoreBootstrapGuiceModuleBase
			  extends BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase
		   implements ServicesBootstrapGuiceModuleBindsSecurityEventListeners,
					  HasMoreBindings {

/////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public BeanImplementedSecurityServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																	   final DBPersistenceGuiceModule dbGuiceModule) {
		super(coreBootstrapCfg, dbGuiceModule);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder){
		super.configure(binder);
		// Bind event listener to register EvenBus Subscribers after INJECTION
		//( Important : EventBusSubscriberTypeListener must be changed to PROTECTED at BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase )
		EventBusSubscriberTypeListener typeListener = new EventBusSubscriberTypeListener(binder.getProvider(EventBus.class));	// inject a Provider to get dependencies injected!!!
		Class<?>[] classesToBind = _getListenersToBind();
		if (classesToBind != null) {
			binder.bindListener(Matchers.subclassesOf(classesToBind),
					typeListener);
			bindSecurityEventListeners(binder);
		}
	}

	protected abstract Class<?>[] _getListenersToBind();

}
