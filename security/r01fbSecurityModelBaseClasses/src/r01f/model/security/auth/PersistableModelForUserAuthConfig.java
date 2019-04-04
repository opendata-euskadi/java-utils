package r01f.model.security.auth;

import r01f.guids.OIDTyped;
import r01f.model.security.PersistableSecurityModelObject;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;

public interface PersistableModelForUserAuthConfig<O extends  UserAuthConfigModelOID,
												   SELF_TYPE extends PersistableSecurityModelObject<O>>

		  extends PersistableSecurityModelObject<O> {

	public  OIDTyped<String> getUserOID();

}
