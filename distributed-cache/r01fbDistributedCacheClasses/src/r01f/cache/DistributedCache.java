package r01f.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import r01f.debug.Debuggable;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;

/**
 * Interface for Distributed Cached MODEL Types.
 * @param <O>
 * @param <M>
 */
public interface DistributedCache<O extends OID,M extends ModelObject & HasOID<O>>
		 extends Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
	public M get(final O key);

	public Map<O, M> getAll(final Set<? extends O> keys);

	public Map<O, M> getAll();

	public boolean containsKey(final O key);

	public Set<O> getKeySet();

	public <I extends OID> M getByIdField(final I key);
/////////////////////////////////////////////////////////////////////////////////////////
//	PUT
/////////////////////////////////////////////////////////////////////////////////////////
	public void put(final O key,final M value);

	public void put(final O key,M value,
					long ttl,final TimeUnit timeunit);

	void putAll(final Map<? extends O,? extends M> map);

	boolean putIfAbsent(final O key,final M value);

	public M getAndPut(final O key,final M value);
/////////////////////////////////////////////////////////////////////////////////////////
//	REPLACE
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean replace(final O key, final M value);

	public boolean replace(final O key,final M oldValue,final M newValue);

	public M getAndReplace(final O key,final  M value);
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean remove(final O key);

	public boolean remove(final O key,final  M oldValue);

	public M getAndRemove(final O key);

	public void removeAll(final Set<? extends O> keys);

	public void clear();

	public void removeAll();
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isNullOrEmpty();

	public boolean hasElements();

	public long size();
}
