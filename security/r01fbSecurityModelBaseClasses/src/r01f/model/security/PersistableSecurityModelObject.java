package r01f.model.security;

import r01f.guids.CommonOIDs.UserCode;
import r01f.model.IndexableModelObject;
import r01f.model.PersistableModelObject;
import r01f.model.security.oids.SecurityCommonOIDs.UserModelOID;

/**
 * Interface for every P12 Model Object
 * @param <O>
 * @param <ID>
 */
public interface PersistableSecurityModelObject<O extends UserModelOID>
		 extends PersistableModelObject<O>,			// is persistable
				 IndexableModelObject {	// is indexable

	public UserCode getUserCode();

}
