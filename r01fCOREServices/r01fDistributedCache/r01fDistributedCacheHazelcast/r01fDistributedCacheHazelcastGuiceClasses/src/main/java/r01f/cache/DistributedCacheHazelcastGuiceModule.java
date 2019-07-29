package r01f.cache;

import javax.inject.Singleton;

import com.google.inject.Provides;

import r01f.cache.hazelcast.DistributedCacheHazelcastConfig;
import r01f.cache.hazelcast.DistributedCacheServiceHazelcastImpl;

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
public class DistributedCacheHazelcastGuiceModule
     extends DistributedCacheGuiceModuleBase {	
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	@Provides @Singleton // beware the service is a singleton
	public DistributedCacheService provideDistributedCacheService() {
		DistributedCacheService outService = new DistributedCacheServiceHazelcastImpl(_config.as(DistributedCacheHazelcastConfig.class));
		return outService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DistributedCacheHazelcastGuiceModule(final DistributedCacheConfig cfg) {
		super(cfg);
	}
}
