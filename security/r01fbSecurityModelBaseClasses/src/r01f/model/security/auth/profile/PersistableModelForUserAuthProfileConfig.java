package r01f.model.security.auth.profile;

import r01f.model.security.PersistableSecurityModelIdentifiedObject;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;

public interface PersistableModelForUserAuthProfileConfig<O extends UserAuthProfileModelOID,
														  ID extends UserAuthProfileID,
														  SELF_TYPE extends PersistableSecurityModelIdentifiedObject<O,ID,SELF_TYPE>>

		 extends PersistableSecurityModelIdentifiedObject<O,ID,SELF_TYPE> {

}
