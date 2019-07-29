package r01f.cache.redis;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cache.DistributedCache;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.patterns.Memoized;

/**
 * DistributedRedisCacheForModelObject base class for Model objects
 *   see :  https://github.com/redisson/redisson
 *          https://github.com/redisson/redisson/wiki/7.-distributed-collections#71-map
 *          https://www.baeldung.com/redis-redisson
 */
@Slf4j
@Accessors(prefix="_")
public class DistributedRedisCacheForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
  implements  DistributedCache<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
     @Getter @Setter RedissonClient  _redisInstance;
     @Getter @Setter Class<M> _modelObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
     public DistributedRedisCacheForModelObject(final  Class<M> modelObjectType,
    		 									final  RedissonClient redisInstance) {
    	 _redisInstance = redisInstance;
    	 _modelObjectType = modelObjectType;
     }
/////////////////////////////////////////////////////////////////////////////////////
// 
////////////////////////////////////////////////////////////////////////////////////
    private final  Memoized<RMapCache<O,M>> _internalMappedCache = new Memoized<RMapCache<O,M>>() {																		
																		@Override
																		protected RMapCache<O, M> supply() {																			
																			RMapCache<O,M> imap =  _redisInstance.getMapCache(_modelObjectType.getName());																	    
																	    	return imap;
																		}
    														  };
    public RMapCache<O,M> getInternalMappedCache() {
    	return _internalMappedCache.get();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public M get(final O key) {
		if (log.isDebugEnabled()) {
			log.debug("{}",this.debugInfo());
		}
		M e = this.getInternalMappedCache().get(key);
		return e;
	}
	@Override
	public Map<O, M> getAll(final Set<? extends O> keys) {
		return 	this.getInternalMappedCache().getAll(_keysAsSetOfOIDs(keys));
	}

	@Override
	public Map<O, M> getAll() {		
		return this.getInternalMappedCache().getAll(this.getKeySet());
	}
	@Override
	public Set<O> getKeySet() {
		return this.getInternalMappedCache().keySet();
	}
	@Override
	public boolean containsKey(final O key) {
		return this.getInternalMappedCache().containsKey(key);
	}
	@Override
	public <I extends OID> M getByIdField(final I modelObjectId) {
	
		throw new UnsupportedOperationException("No implemented yet");
		/*Collection<M>  results = this.getInternalMappedCache().values(Predicate.equal("id",
																	  modelObjectId ) );
		
		return CollectionUtils.pickOneAndOnlyElementOrNull(results);*/
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PUT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void put(final O key,final M value) {
		this.getInternalMappedCache().put(key, value);
	}
	@Override
	public void put(final O key,final M value,
					final long ttl,final TimeUnit timeunit ) {
		this.getInternalMappedCache().put(key,value,
										  ttl,timeunit);
	}
	@Override
	public M getAndPut(final O key,final M value) {
		return this.getInternalMappedCache().putIfAbsent(key,value);
	}
	@Override
	public void putAll(Map<? extends O,? extends M> map) {
		this.getInternalMappedCache().putAll(map);
	}
	@Override
	public boolean putIfAbsent(final O key,final M value) {
	  try {
		 this.getInternalMappedCache().putIfAbsent(key,value);
	  } catch (NullPointerException nulex) {
		 return false;
      } catch (Exception nulex) {
		 return false;
	  }
	  return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REPLACE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean replace(final O key,final M oldValue,final M newValue) {
		 return this.getInternalMappedCache().replace(key,oldValue,newValue);
	}
	@Override
	public boolean replace(final O key,final M value) {
		try {
			this.getInternalMappedCache().replace(key,value);
		} catch (NullPointerException nulex ) {//- if the specified key is null.)
			return false;
		}
		return true;
	}
	@Override
	public M getAndReplace(final O key, final M value) {
		return this.getInternalMappedCache().replace(key,value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean remove(final O key) {
		try {
			this.getInternalMappedCache().remove(key);
		} catch (NullPointerException nulex) {//- if the specified key is null.)
			return false;
		}
		return true;
	}
	@Override
	public boolean remove(final O key,final M oldValue) {
		throw new UnsupportedOperationException(">>>>>>>> Not Implemented");
	}
	@Override
	public M getAndRemove(final O key) {
	  return this.getInternalMappedCache().remove(key);
	}
	@Override
	public void removeAll(Set<? extends O> keys) {
		for (O id : keys ) {
			this.remove(id);
		}
	}
	@Override
	public void removeAll() {
		this.removeAll(this.getKeySet());
	}
	@Override
	public void clear() {
		this.getInternalMappedCache().clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isNullOrEmpty() {
		 return this.getInternalMappedCache() == null
			 && this.getInternalMappedCache().size() < 1;
	}
	@Override
	public boolean hasElements() {
		return !this.isNullOrEmpty();
	}
    @Override
	public long size() {
		return this.getInternalMappedCache().size();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		//LocalMapStats lms = this.getInternalMappedCache().

		/*StringBuilder sb = new StringBuilder();
		sb.append(">>>>>>>>>>>  DEBUG : CACHE OF TYPE :: ").append(this.getInternalMappedCache().getName()).append("\n");
		sb.append("----------------------------------");
		sb.append(">> SIZE : ").append(lms.getOwnedEntryCount()).append(" (").append(lms.getOwnedEntryMemoryCost()).append(" bytes) ").append("\n");
		sb.append(">> HEAP COST : ").append(lms.getHeapCost()).append(" bytes ").append("\n");
		sb.append(">> OTHER INFO : ").append(lms).append("\n");
		sb.append(">> ELEMENTS : ").append(this.getKeySet().size()).append("\n");
		sb.append("----------------------------------");
		return sb;*/ 
		throw new UnsupportedOperationException("No implemented yet");
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// PRIVATE METHODS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private Set<O> _keysAsSetOfOIDs(final Set<? extends O> keys) {
		Set<O> setID = FluentIterable.from(keys)
                               .transform(new Function<O, O>() {
													@Override
													public O apply(final O key) {
														return key;
													}

                               			  })
                               .toSet();
		return setID;
	}
}

