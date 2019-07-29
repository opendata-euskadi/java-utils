package r01f.cache;


import r01f.debug.Debuggable;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.service.ServiceHandler;

public interface DistributedCacheService
		 extends ServiceHandler,
		 		 Debuggable {
	/**
	 * Gets (or creates a new one if it already does NOT exists) a cache for a given model object type
	 * @param modelObjType
	 * @return
	 */
	public <O extends OID,M extends ModelObject & HasOID<O>> DistributedCache<O,M> getOrCreateCacheFor(final Class<M> modelObjType);
	/**
	 * Checks if there already exists a cache for the given model object type
	 * @param modelObjType
	 * @return
	 */
	public <M extends ModelObject> boolean existsCacheFor(final Class<M> modelObjType);
}
