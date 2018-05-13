package r01f.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.query.Predicates;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.patterns.Memoized;
import r01f.util.types.collections.CollectionUtils;

/**
 * DistributedHazelcastCacheForModelObjectBase base class for Model objects
 */
@Slf4j
@Accessors(prefix="_")
public class DistributedHazelcastCacheForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
  implements  DistributedCache<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
     @Getter @Setter HazelcastInstance _hazelCastInstance;
     @Getter @Setter Class<M> _modelObjectType;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
     public DistributedHazelcastCacheForModelObject(final  Class<M> modelObjectType,
    		 									    final HazelcastInstance hazelCastInstance) {
    	 _hazelCastInstance = hazelCastInstance;
    	 _modelObjectType = modelObjectType;
     }
/////////////////////////////////////////////////////////////////////////////////////
// 	https://stackoverflow.com/questions/30486837/possible-to-query-by-key-instead-of-value-in-hazelcast-using-predicates
////////////////////////////////////////////////////////////////////////////////////
    private final  Memoized<IMap<O,M>> _internalMappedCache = new Memoized<IMap<O,M>>() {
																		@Override
																		protected IMap<O, M> supply() {
																	    	 IMap<O,M> imap =  _hazelCastInstance.getMap(_modelObjectType.getName());
																	    	 imap.addEntryListener(new EntryAddedListener<O,M>() {
																	    		 							@Override
																										    public void entryAdded(final EntryEvent<O,M> event) {
																										        // this will deserialize the new value and throw exception if format doesn't match
																										    	// http://stackoverflow.com/questions/38912877/how-to-prevent-hazelcast-mapstore-to-put-into-imap-old-versions-of-objects
																										        event.getValue();
																										    }
																	    	 						},
																	    			 				true);	// true if EntryEvent should contain the value
																	    	return imap;
																		}
    														  };
    public IMap<O,M> getInternalMappedCache() {
    	return _internalMappedCache.get();
    }
//	@SuppressWarnings("unused")
//	private String _serialize(final Object obj) {
//		try {
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			ObjectOutput out = new ObjectOutputStream(bos);
//			out.writeObject(obj);
//			byte b[] = bos.toByteArray();
//			out.close();
//			bos.close();
//			String result = Arrays.toString(b);
//			return result;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public M get(final O key) {
		if (log.isDebugEnabled()) log.debug("{}",this.debugInfo());
		M e = this.getInternalMappedCache().get(key);
		return e;
	}
	@Override
	public Map<O, M> getAll(final Set<? extends O> keys) {
		return 	this.getInternalMappedCache().getAll(_keysAsSetOfOIDs(keys));
	}

	@Override
	public Map<O, M> getAll() {
		try {
			Map<O, M> result = new HashMap<O, M>();
			for (Entry<O, M> entrada : this.getInternalMappedCache().entrySet()) {
				result.put(entrada.getKey(), entrada.getValue());
			}
			return result;
		} catch(HazelcastSerializationException hazex) {
			 _removeInvalidSerializedObjectFromMap();
		}
		// Try again
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
	public <I extends OID> M getByIdField(final I modelObjectId){
		Collection<M>  results = this.getInternalMappedCache().values(Predicates.equal("id",
																	  modelObjectId ) );
		
		return CollectionUtils.pickOneAndOnlyElementOrNull(results);
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
		LocalMapStats lms = this.getInternalMappedCache().getLocalMapStats();

		StringBuilder sb = new StringBuilder();
		sb.append(">>>>>>>>>>>  DEBUG : CACHE OF TYPE :: ").append(this.getInternalMappedCache().getName()).append("\n");
		sb.append("----------------------------------");
		sb.append(">> SIZE : ").append(lms.getOwnedEntryCount()).append(" (").append(lms.getOwnedEntryMemoryCost()).append(" bytes) ").append("\n");
		sb.append(">> HEAP COST : ").append(lms.getHeapCost()).append(" bytes ").append("\n");
		sb.append(">> OTHER INFO : ").append(lms).append("\n");
		sb.append(">> ELEMENTS : ").append(this.getKeySet().size()).append("\n");
		sb.append("----------------------------------");
		return sb;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// PRIVATE METHODS
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	private  void _removeInvalidSerializedObjectFromMap() {
		log.error("_______________________________________________________________________________________________");
		log.error("Warning! Detected some invalid class on HazelCast IMAP ({})",HazelcastSerializationException.class);
		log.error("_______________________________________________________________________________________________");

		Set<O> keys = this.getKeySet();
		 for (final O key : keys) {
			 log.debug(" Key : {}", key.asString());
			 try {
			      this.getInternalMappedCache().get(key);
			 } catch (com.hazelcast.nio.serialization.HazelcastSerializationException ex) {
				 log.error("Catch it!  Try to remove this {}", key.asString());
				 // Removes the mapping for a key from this map if it is present (optional operation).
                 // Unlike remove(Object), this operation does not return the removed value, which avoids the serialization  of the returned value.
				 this.getInternalMappedCache().delete(key);
			 }
		 }
	}




}

