package r01f.model.security.login.entry;

import r01f.model.security.PersistableSecurityModelObject;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginEntryModelOID;

/**
 * Model object
 */
public interface PersistableModelForUserLoginEntry<O extends UserLoginEntryModelOID,
													   SELF_TYPE extends PersistableSecurityModelObject<O>>
		 extends ModelForUserLoginEntry<O>,
		 		 PersistableSecurityModelObject<O>{

	public LoginEntryType getLoginEntryType();

	public String getToken();

}
