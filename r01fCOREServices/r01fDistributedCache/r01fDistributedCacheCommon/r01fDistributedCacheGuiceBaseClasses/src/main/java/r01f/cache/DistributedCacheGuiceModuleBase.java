package r01f.cache;

import javax.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import r01f.service.ServiceHandler;
import r01f.util.types.Strings;

/**
 * This guice module is to be used when using the {@link DistributedCacheService} in a
 * standalone way (ie testing) something like:
 *
 * <pre class='brush:java'>
	     Injector GUICE_INJECTOR = Guice.createInjector(new XMLPropertiesGuiceModule(),
				                                        new DistributedCacheBootstrapModule(arg1,arg2));

		ServicesLifeCycleUtil.startServices(GUICE_INJECTOR); // Hazelcast doesnt need anything special to start, but YES to stop, so its important to bind a handler.
		DistributedCacheService cacheService = GUICE_INJECTOR.getInstance(DistributedCacheService.class);
		MockObject theMockObject = new MockObject();
		cacheService.getCacheForModelObject(MockObject.class)
						.put(theMockObject.getOid(), theMockObject);
		MockObject mockObjectFromCache =  cacheService.getCacheForModelObject(MockObject.class)
				                                        .get(oid);
		ServicesLifeCycleUtil.stopServices(GUICE_INJECTOR);
 * </pre>
 *
 * It's VERY important to bind the XMLPropertiesGuiceModule: *
 * <pre class='brush:java'>
 * 		binder.install(new XMLPropertiesGuiceModule());
 * </pre>
 */

public abstract class DistributedCacheGuiceModuleBase
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected final DistributedCacheConfig _config;
//////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
//////////////////////////////////////////////////////////////////////////////////////////
	static String R01_PREFIX = "R01.CACHE.";

/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DistributedCacheGuiceModuleBase(final DistributedCacheConfig cfg) {
		_config = cfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Provides @Singleton // beware the service is a singleton
	public abstract DistributedCacheService provideDistributedCacheService();
	
	
	@Override
	public void configure(final Binder binder) {
		// Service handler used to control (start/stop) the Persistence Service (see ServletContextListenerBase)
		// do NO forget!!  Hazelcast doesnt need anything special to start, but YES to stop, so its important to bind a handler.
		binder.bind(ServiceHandler.class)
        	  .annotatedWith(Names.named(Strings.customized("{}.{}",
        			  										_config.getAppCode(),_config.getAppComponent())))
        	  .to(DistributedCacheService.class)
        	  .in(Singleton.class);
		/*log.debug("... binded {} to {} with name {}",
				  ServiceHandler.class.getSimpleName(),DistributedCacheServiceHazelcastImpl.class,
				  _config.getAppCode(),_config.getAppComponent());*/
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////


}
