package r01f.persistence.db;

import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.services.interfaces.FindServicesForDependentModelObject;
import r01f.services.interfaces.FindServicesForModelObject;

/**
 * Convenience interface to mark DBFind implementation of {@link FindServicesForModelObject}
 * @param <O>
 * @param <M>
 */
public interface DBFindForDependentModelObject<O extends OID,M extends PersistableModelObject<O>,
											   P extends PersistableModelObject<?>> 
	     extends FindServicesForDependentModelObject<O,M,P> {
	// nothing
}
