package r01f.bootstrap;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.ServicesBootstrapUtil;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.bootstrap.services.config.core.ServicesCoreGuiceBootstrapConfigWhenBeanExposed;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.bootstrap.services.core.BeanImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.DBPersistenceGuiceModule;
import r01f.bootstrap.services.core.SearchEnginePersistenceGuiceModule;
import r01f.concurrent.ExecutorServiceManager;
import r01f.events.PersistenceOperationEventListeners.PersistenceOperationErrorEventListener;
import r01f.events.PersistenceOperationEventListeners.PersistenceOperationOKEventListener;
import r01f.events.crud.CRUDOperationErrorEventListener;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.inject.Matchers;
import r01f.persistence.jobs.AsyncEventBusProvider;
import r01f.persistence.jobs.ExecutorServiceManagerProvider;
import r01f.persistence.jobs.SyncEventBusProvider;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.types.ExecutionMode;
import r01f.util.types.Strings;

/**
 * Mappings internal to services core implementation
 * IMPORTANT!!!!
 * =============
 * If this type is refactored and move to another package, it's VERY IMPORTANT to 
 * change the ServicesCoreBootstrap _findCoreGuiceModuleOrNull() method!!!!!
 */
@Slf4j
@EqualsAndHashCode(callSuper=true)				// This is important for guice modules
public abstract class BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase
              extends BeanImplementedServicesCoreBootstrapGuiceModuleBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																		  final Module... otherModules) {
		super(coreBootstrapCfg,
			  otherModules != null ? Arrays.asList(otherModules) : null);		
	}
	public BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																		  final DBPersistenceGuiceModule dbGuiceModule) {
		super(coreBootstrapCfg,
			  dbGuiceModule,
			  null);
	}
	public BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase(final ServicesCoreGuiceBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																		  final DBPersistenceGuiceModule dbGuiceModule,
														   				  final Module... otherModules) {
		super(coreBootstrapCfg,
			  dbGuiceModule,
			  otherModules != null ? Arrays.asList(otherModules) : null);
	}
	public BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																		  final DBPersistenceGuiceModule dbGuiceModule,
														   				  final Collection<Module> otherModules) {
		super(coreBootstrapCfg,
			  dbGuiceModule,
			  otherModules);
	}
	public BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																		  final DBPersistenceGuiceModule dbGuiceModule,
														   				  final SearchEnginePersistenceGuiceModule searchGuiceModule,
														   				  final Collection<Module> otherModules) {
		super(coreBootstrapCfg,
			  dbGuiceModule,
			  searchGuiceModule,
			  otherModules);
	}
	public BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																		  final DBPersistenceGuiceModule dbGuiceModule,
														   				  final SearchEnginePersistenceGuiceModule searchGuiceModule,
														   				  final Module... otherModules) {
		super(coreBootstrapCfg,
			  dbGuiceModule,
			  searchGuiceModule,
			  otherModules != null ? Arrays.asList(otherModules) : null);
	}
	public BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																		  final SearchEnginePersistenceGuiceModule searchGuiceModule,
														   				  final Module... otherModules) {
		super(coreBootstrapCfg,
			  searchGuiceModule,
			  otherModules != null ? Arrays.asList(otherModules) : null);
	}
	public BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg,
																		  final SearchEnginePersistenceGuiceModule searchGuiceModule,
														   				  final Collection<Module> otherModules) {
		super(coreBootstrapCfg,
			  searchGuiceModule,
			  otherModules);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Avoid multiple bindings 
	 */
	private boolean CRUD_OPERATION_ERROR_LISTENER_BINDED = false;
	
//	private boolean DBPERSISTENCE_BINDINGS_SET = false;
//	private boolean SEARCH_BINDINGS_SET = false;
	
	@Override 
	public void configure(final Binder binder) {
		super.configure(binder);	// this is where all sub-modules are installed!!
		
		final Binder theBinder = binder;
		
		// [0]: Bind the core config
		binder.bind(ServicesCoreBootstrapConfigWhenBeanExposed.class)
			  .toInstance((ServicesCoreBootstrapConfigWhenBeanExposed)_coreBootstrapCfg);
		
		// [1]: Bind XMLProperties for persistence and search
		ServicesBootstrapUtil.bindXMLPropertiesForAppComponent(_coreBootstrapCfg.getCoreAppCode(),AppComponent.compose(_coreBootstrapCfg.getCoreModule(),
												   																	   CoreModule.DBPERSISTENCE),
															   CoreModule.DBPERSISTENCE,	// the binding name
												   			   theBinder);
		ServicesBootstrapUtil.bindXMLPropertiesForAppComponent(_coreBootstrapCfg.getCoreAppCode(),AppComponent.compose(_coreBootstrapCfg.getCoreModule(),
												   																	   CoreModule.SEARCHPERSISTENCE),
															   CoreModule.SEARCHPERSISTENCE,	// the binding name
												   			   theBinder);
		
		
		// [2]: Bind event listeners 
		// ==================================================
		// Event Bus & Background jobs
		if (this instanceof ServicesBootstrapGuiceModuleBindsCRUDEventListeners) {
			ServicesCoreModuleEventsConfig eventHandlingCfg = _coreBootstrapCfg.getEventHandling();
			if (eventHandlingCfg == null) {
				log.error("The core bootrap module {} BINDS events BUT there's NO event handling config... usgin defaults",
						  this.getClass());
				eventHandlingCfg = new ServicesCoreModuleEventsConfig(ExecutionMode.SYNC);
			}
			log.warn("EVENT HANDLING: {}",eventHandlingCfg.debugInfo());
			
			// The EventBus needs an ExecutorService (a thread pool) to manage events in the background
			ExecutorServiceManagerProvider execServiceManagerProvider = null;
			if (eventHandlingCfg.getExecutionMode() == ExecutionMode.ASYNC) {
				execServiceManagerProvider = new ExecutorServiceManagerProvider(eventHandlingCfg.getNumberOfBackgroundThreads());
				theBinder.bind(ExecutorServiceManager.class)
						 .toProvider(execServiceManagerProvider)
						 .in(Singleton.class);
				// Expose the ServiceHandler to stop the exec manager threads
				String bindingName = Strings.customized("{}.{}.backgroundTasksExecService",
														_coreBootstrapCfg.getCoreAppCode(),_coreBootstrapCfg.getCoreModule());
				// do NO forget!!
				ServicesBootstrapUtil.bindServiceHandler(theBinder,
														 ExecutorServiceManager.class,bindingName);
				
				// create the event bus provider
				theBinder.bind(EventBus.class)
						 .toProvider(AsyncEventBusProvider.class)
						 .in(Singleton.class);
			} else {
				theBinder.bind(EventBus.class)
						 .toProvider(SyncEventBusProvider.class)
						 .in(Singleton.class);
			}
			
			
			// Automatic registering of event listeners to the event bus avoiding the
			// manual registering of every listener; this simply listen for guice's binding events;
			// when an event listener gets binded, it's is automatically registered at the event bus
			// 		Listen to injection of CRUDOperationOKEventListener & CRUDOperationNOKEventListener subtypes (indexers are CRUD events listeners)
			// 		(when indexers are being injected)
			EventBusSubscriberTypeListener typeListener = new EventBusSubscriberTypeListener(theBinder.getProvider(EventBus.class));	// inject a Provider to get dependencies injected!!!
			theBinder.bindListener(Matchers.subclassesOf(PersistenceOperationOKEventListener.class,
													     PersistenceOperationErrorEventListener.class),
							       typeListener);	// registers the event listeners at the EventBus
			
			// These fires the creation of event listeners and thus them being registered at the event bus
			// by means of the EventBusSubscriberTypeListener bindListener (see below)
//			if (!CRUD_OPERATION_ERROR_LISTENER_BINDED) {
				theBinder.bind(CRUDOperationErrorEventListener.class)
					  	 .toInstance(new CRUDOperationErrorEventListener());				// CRUDOperationNOKEvent for EVERY model object
//				CRUD_OPERATION_ERROR_LISTENER_BINDED = true;
//			}
			
			// Bind every listener
			((ServicesBootstrapGuiceModuleBindsCRUDEventListeners)this).bindCRUDEventListeners(theBinder);
		}
	}
	/**
	 * Guice {@link TypeListener} that gets called when a {@link PersistenceOperationOKEventListener} subtype (the indexer is a CRUD events listener)
	 * is injected (or created) (this is called ONCE per type)
	 * AFTER the {@link PersistenceOperationOKEventListener} subtype is injected (or created), it MUST be registered at the {@link EventBus} 
	 */
	@RequiredArgsConstructor
	private class EventBusSubscriberTypeListener
	   implements TypeListener {
		
		// The EventBus cannot be injected because it cannot be created inside a module
		// however an EventBus provider can be injected and in turn it's injected with 
		// it's dependencies
		// see r01f.persistence.jobs.EventBusProvider
		private final Provider<EventBus> _eventBusProvider;
		
		@Override
		public <I> void hear(final TypeLiteral<I> type,
							 final TypeEncounter<I> encounter) {
			encounter.register(// AFTER the type is injected it MUST be registered at the EventBus
							   new InjectionListener<I>() {
										@Override
										public void afterInjection(final I injecteeEventListener) {
											log.warn("\tRegistering {} event listener at event bus {}",
													 injecteeEventListener.getClass(),
													 _eventBusProvider.get());
											_eventBusProvider.get()
													 		 .register(injecteeEventListener);	// register the indexer (the indexer is an event listener)
										}
							   });
		}
	}
}
