package r01f.cache.hazelcast;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Maps;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cache.DistributedCache;
import r01f.cache.DistributedCacheService;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;

@Slf4j
@Accessors(prefix="_")
public class DistributedCacheServiceHazelcastImpl
  implements DistributedCacheService {

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter Map<Class<? extends ModelObject>,DistributedCache<? extends OID,? extends ModelObject>> _caches = Maps.newLinkedHashMap();
	private HazelcastInstance _hazelCastInstance;
	@Getter @Setter DistributedCacheHazelcastConfig _cfg;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public  DistributedCacheServiceHazelcastImpl(final DistributedCacheHazelcastConfig cfg) {
		_cfg = cfg;
	}

////////////////////////////////////////////////////////////////////////////////
//  DistributedCacheService
/////////////////////////////////////////////////////////////////////////////////
	@Override	@SuppressWarnings("unchecked")
	public <O extends OID,M extends ModelObject & HasOID<O>> DistributedCache<O,M> getOrCreateCacheFor(final Class<M> modelObjType) {
		DistributedCache<? extends OID,? extends ModelObject> outCache = null;
		_checkCacheStatus();
		if (_caches.get(modelObjType) != null ) {
			outCache = _caches.get(modelObjType);
		} else {
			outCache = _createTypedCacheFor(modelObjType);
			_caches.put(modelObjType,outCache);
		}
		return (DistributedCache<O,M>)outCache;
	}
	private <O extends OID,M extends ModelObject & HasOID<O>> DistributedCache<O,M> _createTypedCacheFor(final Class<M> modelObjType) {
		DistributedCache<O,M> outCache = new DistributedHazelcastCacheForModelObject<O,M>(modelObjType,
				_getOrCreateHazelcastInstance());
		return  outCache;
	}
	@Override
	public <M extends ModelObject> boolean existsCacheFor(final Class<M> modelObjType) {
		return _caches.get(modelObjType) != null;
	}

	private HazelcastInstance _getOrCreateHazelcastInstance() {
		if (_hazelCastInstance==null) {
			synchronized(this) {
				if(_hazelCastInstance == null) {
					_hazelCastInstance = HazelcastManager.getOrCreateeHazelcastInstance(_cfg);
				}
			}
		}
		return _hazelCastInstance;
	}
	private void _checkCacheStatus() {
		if (_hazelCastInstance==null || !_hazelCastInstance.getLifecycleService().isRunning()) {
			_hazelCastInstance = null;
			_clearAll();
			_getOrCreateHazelcastInstance();
		}
	}
	private void _clearAll() {
		try {
			for (Iterator<DistributedCache<? extends OID,? extends ModelObject>> cacheIt = _caches.values().iterator(); cacheIt.hasNext(); ) {
				DistributedCache<? extends OID,? extends ModelObject> cache = cacheIt.next();
				cache.clear();
			}
			_caches.clear();
		} catch (Throwable t) {
			//
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	ServiceHandler
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void start() {
		//Do Nothing
//		_hazelCastInstance = HazelcastManager.getOrCreateeHazelcastInstance(_cfg);
//		HazelcastManager.getOrCreateeHazelcastInstance(_cfg);
		_getOrCreateHazelcastInstance();
	}
	@Override
	public void stop() {
		log.warn("######################################################################################");
		log.warn("Stopping Hazelcast");
		log.warn("######################################################################################");
		if (_hazelCastInstance!=null) {
			_clearAll();
			synchronized(this) {
				if(_hazelCastInstance != null) {
					_hazelCastInstance.shutdown();
					_hazelCastInstance = null;
				}
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUGGABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();

		Set<HazelcastInstance> hazelcastInstances = Hazelcast.getAllHazelcastInstances();
		sb.append("Hazlecast Instances (/n");
		for (HazelcastInstance hzI : hazelcastInstances) {
			sb.append(hzI.getCluster()+ " > "+hzI.getName()+" ("+hzI.hashCode()+")"+"/n");
		}
		sb.append(")/n");
		sb.append("Cache Status (").append(_caches.size()).append(" caches created)\n");
		for (Iterator<DistributedCache<? extends OID,? extends ModelObject>> cacheIt = _caches.values().iterator(); cacheIt.hasNext(); ) {
			DistributedCache<? extends OID,? extends ModelObject> cache = cacheIt.next();
			sb.append(cache.debugInfo());
			if (cacheIt.hasNext()) sb.append("\n_____________________________________________\n");
		}
		return sb;
	}
}
