package r01f.api.interfaces.user;

import r01f.model.persistence.FindResult;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.services.interfaces.FindServicesForModelObject;

@ExposedServiceInterface
public interface FindServicesAPIForUserData<O extends UserDataModelOID,
											U extends PersistableModelForUserData<O,U>>
		 extends FindServicesForModelObject<O,U>,
				 SecurityServiceInterface {
	/**
	 * Returns all users
	 * @param userContext
	 * @return
	 */
	 public FindResult<U> findAllUsers(final SecurityContext securityContext);

}