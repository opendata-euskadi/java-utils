package r01f.cache;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Maps;
import com.hazelcast.core.HazelcastInstance;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;

@Accessors(prefix="_")
public class DistributedCacheServiceHazelcastImpl 
  implements DistributedCacheService {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS 
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter Map<Class<? extends ModelObject>,DistributedCache<? extends OID,? extends ModelObject>> _caches = Maps.newLinkedHashMap();
	@Getter @Setter HazelcastInstance _hazelCastInstance;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR 
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public  DistributedCacheServiceHazelcastImpl(final HazelcastInstance hzInstance) {
		_hazelCastInstance = hzInstance;
	}
////////////////////////////////////////////////////////////////////////////////
//  DistributedCacheService
/////////////////////////////////////////////////////////////////////////////////
	@Override	@SuppressWarnings("unchecked")
	public <O extends OID,M extends ModelObject & HasOID<O>> DistributedCache<O,M> getOrCreateCacheFor(final Class<M> modelObjType) {
		DistributedCache<? extends OID,? extends ModelObject> outCache = null;
		if (_caches.get(modelObjType) != null ) {
			outCache = _caches.get(modelObjType);
		} else {
			outCache = _createTypedCacheFor(modelObjType);
			_caches.put(modelObjType,
						(DistributedCache<O,M>)outCache);
		}
		return (DistributedCache<O,M>)outCache;
	}
	private <O extends OID,M extends ModelObject & HasOID<O>> DistributedCache<O,M> _createTypedCacheFor(final Class<M> modelObjType) {
		DistributedCache<O,M> outCache = new DistributedHazelcastCacheForModelObject<O,M>(modelObjType,
																				   		  _hazelCastInstance); 
//		Class[] constructorArgsTypes = { Class.class,HazelcastInstance.class };
//		Object[] constructorArgs = {modelObjType,_hazelCastInstance};
//		DistributedCache<O,M> outCache = ReflectionUtils.createInstanceOf(DistributedHazelcastCacheForModelObject.class,
//				                                                          constructorArgsTypes,constructorArgs);
		return  outCache;
	}
	@Override
	public <M extends ModelObject> boolean existsCacheFor(final Class<M> modelObjType) {
		return _caches.get(modelObjType) != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ServiceHandler 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void start() {
		//Do Nothing
	}
	@Override
	public void stop() {
		_hazelCastInstance.shutdown();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUGGABLE 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cache Status (").append(_caches.size()).append(" caches created)\n");
		for (Iterator<DistributedCache<? extends OID,? extends ModelObject>> cacheIt = _caches.values().iterator(); cacheIt.hasNext(); ) {
			DistributedCache<? extends OID,? extends ModelObject> cache = cacheIt.next();
			sb.append(cache.debugInfo());
			if (cacheIt.hasNext()) sb.append("\n_____________________________________________\n");
		}
		return sb;
	}
}
