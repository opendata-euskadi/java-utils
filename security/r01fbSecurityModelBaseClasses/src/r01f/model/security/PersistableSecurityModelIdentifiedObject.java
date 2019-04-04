package r01f.model.security;

import r01f.model.security.oids.SecurityCommonOIDs.UserModelOID;
import r01f.model.security.oids.SecurityIDS.UserModelID;

/**
 * Interface for every  for P12PersistableObject with an ID and an OID
 * @param <O>
 * @param <ID>
 */
public interface PersistableSecurityModelIdentifiedObject<O extends UserModelOID,
														  ID extends UserModelID,
														  SELF_TYPE extends PersistableSecurityModelIdentifiedObject<O,ID,SELF_TYPE>>
		 extends PersistableSecurityModelObject<O> {

	public ID getId();

	public void setId(ID id);

}
