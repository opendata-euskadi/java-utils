package r01f.cache;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.inject.Guice;
import com.google.inject.Injector;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.ServicesBootstrapUtil;
import r01f.cache.mock.MockCachedObject;
import r01f.cache.mock.MockCachedObjectFactory;
import r01f.cache.mock.MockOID;
import r01f.concurrent.Threads;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.AppCode;
import r01f.types.TimeLapse;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.XMLPropertiesGuiceModule;


@Accessors(prefix="_")
@RequiredArgsConstructor
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class CacheServiceTest  {
/////////////////////////////////////////////////////////////////////////////////////////
// 	GUICE INJECTOR
/////////////////////////////////////////////////////////////////////////////////////////
	static Injector GUICE_INJECTOR = null;

	private static DistributedCacheService _getDistributedCacheService() {
		return GUICE_INJECTOR.getInstance(DistributedCacheService.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JUnit
/////////////////////////////////////////////////////////////////////////////////////////
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			XMLPropertiesForAppComponent xmlProps = XMLPropertiesBuilder.createForApp(AppCode.forId("z99"))
																	    .notUsingCache()
																	    .forComponent("hazelcast");
			DistributedCacheConfig cfg = DistributedCacheConfig.createFrom(xmlProps);
			GUICE_INJECTOR = Guice.createInjector(new XMLPropertiesGuiceModule(),
					                              new DistributedCacheGuiceModule(cfg));
			ServicesBootstrapUtil.startServices(GUICE_INJECTOR);
		} catch(Exception ex) {
			ex.printStackTrace(System.out);
			Throwables.throwUnchecked(ex);
		}
	}
	@AfterClass
	public static void tearDownAfterClass()  {
		// [99]-Tear things down
		try {
			ServicesBootstrapUtil.stopServices(GUICE_INJECTOR);
		} catch(Exception ex) {
			ex.printStackTrace(System.out);
			Throwables.throwUnchecked(ex);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Test @SuppressWarnings("static-method")
	public void testCacheForMockObjets() {
		DistributedCacheService cacheService = _getDistributedCacheService();
		
		// [0] - Create the mock object
		MockOID oid = new MockOID("oid1");
		MockCachedObject theMockObject = MockCachedObjectFactory.create(oid);	
		
		// [1] - Put
		cacheService.getOrCreateCacheFor(MockCachedObject.class)
					.put(theMockObject.getOid(), theMockObject);
		log.debug("{}",cacheService.debugInfo());
		
        // [2] - Get
		MockCachedObject mockObjectFromCache =  cacheService.getOrCreateCacheFor(MockCachedObject.class)
				                                        	.get(theMockObject.getOid());
		log.debug("Mock object='{}' with counter={}",
				  mockObjectFromCache.getSomeDescription(),mockObjectFromCache.getCounter());

		// [3] - Check	
		Assert.assertNotNull(mockObjectFromCache);
		Assert.assertTrue(mockObjectFromCache.getOid().equals(oid));
	}

	@Test @SuppressWarnings("static-method")
	public void testCacheForMockObjetPutAndReplace() throws InterruptedException {
		DistributedCacheService cacheService = _getDistributedCacheService();
		
		// [0] - Create the mock object
		MockOID oid = new MockOID("oid1");
		MockCachedObject theMockObject =  MockCachedObjectFactory.create(oid);
		
		// [1] - Put
		cacheService.getOrCreateCacheFor(MockCachedObject.class)
				    .put(theMockObject.getOid(),theMockObject,3,TimeUnit.SECONDS);
		log.debug("{}",cacheService.debugInfo());
		
        // [2] - Get
		MockCachedObject mockObjectFromCache =  cacheService.getOrCreateCacheFor(MockCachedObject.class)
				                                        	.get(theMockObject.getOid());
		log.debug("Mock object='{}' with counter={}",
				  mockObjectFromCache.getSomeDescription(),mockObjectFromCache.getCounter());
		
		Assert.assertNotNull(mockObjectFromCache);
		Assert.assertTrue(mockObjectFromCache.getOid().equals(oid));

		// [3] - Change & replace
		mockObjectFromCache.setSomeDescription("This is the Updated Description");
		mockObjectFromCache.incCounter();
		cacheService.getOrCreateCacheFor(MockCachedObject.class)
					.replace(mockObjectFromCache.getOid(),mockObjectFromCache);

		 // [4] - Get again
		MockCachedObject anotherObjectFromCache =  cacheService.getOrCreateCacheFor(MockCachedObject.class)
				                                               .get(theMockObject.getOid());
		log.debug("Mock object='{}' with counter={}",
				  anotherObjectFromCache.getSomeDescription(),anotherObjectFromCache.getCounter());
		
		// [5] - Check
		Assert.assertNotNull(anotherObjectFromCache);
		Assert.assertTrue(anotherObjectFromCache.getOid().equals(oid));
		Assert.assertTrue(anotherObjectFromCache.getCounter() > theMockObject.getCounter());
	}
	@Test @SuppressWarnings("static-method")
	public void testCacheForMockObjetsWithExpiration() throws InterruptedException {
		DistributedCacheService cacheService = _getDistributedCacheService();
		
		// [0] - Create two objects
		MockCachedObject aMockObjectWith3SecondsOfLife =  MockCachedObjectFactory.create(new MockOID("oid1"));
		MockCachedObject aMockObjectWith3MinutesOfLife =  MockCachedObjectFactory.create(new MockOID("oid2"));

		// [1] - Put aMockObjectWith3SecondsOfLife
		cacheService.getOrCreateCacheFor(MockCachedObject.class)
					.put(aMockObjectWith3SecondsOfLife.getOid(),aMockObjectWith3SecondsOfLife,
						 3,TimeUnit.SECONDS);
		cacheService.getOrCreateCacheFor(MockCachedObject.class)
					.put(aMockObjectWith3MinutesOfLife.getOid(),aMockObjectWith3MinutesOfLife,
						 3,TimeUnit.MINUTES);
		log.debug("{}",cacheService.debugInfo());
		
		// [2] - Wait 10 seconds
		log.debug(".......sleep 10 seconds");
		Threads.safeSleep(TimeLapse.createFor("10s"));
		
        // [3] - Get again
		MockCachedObject mockObjectWith3SecondsOfLifeFromCache =  cacheService.getOrCreateCacheFor(MockCachedObject.class)
																			  .get(aMockObjectWith3SecondsOfLife.getOid());

		MockCachedObject mockObjectWith3MinutessOfLifeFromCache =  cacheService.getOrCreateCacheFor(MockCachedObject.class)
																			   .get(aMockObjectWith3MinutesOfLife.getOid());
		if (mockObjectWith3SecondsOfLifeFromCache != null) {
			log.debug("Mock object='{}' with counter={}",
					  mockObjectWith3SecondsOfLifeFromCache.getSomeDescription(),mockObjectWith3SecondsOfLifeFromCache.getCounter());
		} else {
			log.debug("Mock Object with 3 seconds is expired or is null");
		}
		Assert.assertNull(mockObjectWith3SecondsOfLifeFromCache);

		if (mockObjectWith3MinutessOfLifeFromCache != null) {
			log.debug("Mock object='{}' with counter={}",
					  mockObjectWith3MinutessOfLifeFromCache.getSomeDescription(),mockObjectWith3MinutessOfLifeFromCache.getCounter());
		} else {
			log.debug("Mock Object with 3 minutes is expired or is null");
		}
		Assert.assertNotNull(mockObjectWith3MinutessOfLifeFromCache);
	}
}
