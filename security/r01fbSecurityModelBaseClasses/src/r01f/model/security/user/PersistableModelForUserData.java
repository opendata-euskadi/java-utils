package r01f.model.security.user;

import r01f.model.security.PersistableSecurityModelObject;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.types.contact.PersonalDataWithContactInfo;

/**
 * Model object
 */
public interface PersistableModelForUserData<O extends UserDataModelOID,
											 SELF_TYPE extends PersistableSecurityModelObject<O>>
		 extends ModelForUserData<O>,
				 PersistableSecurityModelObject<O> {

	public PersonalDataWithContactInfo getContactData();

}
