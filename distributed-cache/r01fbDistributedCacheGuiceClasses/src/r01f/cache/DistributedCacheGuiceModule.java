package r01f.cache;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.hazelcast.core.HazelcastInstance;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DistributedCacheGuiceModule
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final DistributedCacheConfig _config;
//////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
//////////////////////////////////////////////////////////////////////////////////////////
	static String R01_PREFIX = "R01.HAZELCAST.";

/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DistributedCacheGuiceModule(final DistributedCacheConfig cfg) {
		_config = cfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// Service handler used to control (start/stop) the Persistence Service (see ServletContextListenerBase)
		// do NO forget!!  Hazelcast doesnt need anything special to start, but YES to stop, so its important to bind a handler.
		binder.bind(ServiceHandler.class)
        	  .annotatedWith(Names.named(Strings.customized("{}.{}",
        			  										_config.getAppCode(),_config.getAppComponent())))
        	  .to(DistributedCacheServiceControlHandler.class)
        	  .in(Singleton.class);
		log.debug("... binded {} to {} with name {}",
				  ServiceHandler.class.getSimpleName(),DistributedCacheServiceControlHandler.class,
				  _config.getAppCode(),_config.getAppComponent());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides a {@link DistributedCacheService} implementation
	 * @param props
	 * @return
	 */
	@Provides @Singleton // beware the service is a singleton
	DistributedCacheService _provideDistributedCacheService(final HazelcastInstance hzInstance) {
		DistributedCacheService outService = new DistributedCacheServiceHazelcastImpl(hzInstance);
		return outService;
	}
	@Provides @Singleton // beware the service is a singleton
	HazelcastInstance _provideHazelcastInstance() {
		return HazelcastManager.getOrCreateeHazelcastInstance(_config);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// SERVICE HANDLER control
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * see https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree
	 * The {@link ServiceHandler} interface is used to start & stop the JPA's PersistenceService
	 * at ServletContextListenerBase type
	 */
	static class DistributedCacheServiceControlHandler
	  implements ServiceHandler {

		private final DistributedCacheService  _service;

		@Inject
		public DistributedCacheServiceControlHandler(final DistributedCacheService service) {
			_service = service;
		}
		@Override
		public void start() {
			if (_service == null) throw new IllegalStateException("NO distributed cache service available!");
				_service.start();
			}
		@Override
		public void stop() {
		   if (_service == null) throw new IllegalStateException("NO distributed cache service available!");
			try {
				 log.debug("...Stopping Hazelcast instance......");
				_service.stop();
			} catch (Throwable th) {/* just in the case where Service were NOT started */ }
		}
	}
}
