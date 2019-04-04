package r01f.api.interfaces.user;

import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@ExposedServiceInterface
public interface CRUDServicesAPIForUserData<O extends UserDataModelOID,
											U extends PersistableModelForUserData<O,U>>
		extends CRUDServicesForModelObject<O,U>,
				SecurityServiceInterface {

	/**
	 * Loads an entity by it's UserCode
	 * @param UserCode
	 * @return
	 */
	public CRUDResult<U> loadByUserCode(final SecurityContext securityContext,
										final UserCode id);

	/**
	 * Loads the owner with the given email (should exist a single owner with the given mail)
	 * @param email
	 * @return
	 */
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
								final EMail email);

	/**
	 * Loads the owner with the given email (should exist a single owner with the given mail)
	 * @param email
	 * @return
	 */
	public CRUDResult<U> loadOrNull(final SecurityContext securityContext,
									final EMail email);

	/**
	 * Loads the owner with the given phone (should exist a single owner with the given phone)
	 * @param phone
	 * @return
	 */
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
								final Phone phone);

	/**
	 * Loads the owner with the given email (should exist a single owner with the given mail)
	 * @param phone
	 * @return
	 */
	public CRUDResult<U> loadOrNull(final SecurityContext securityContext,
									final Phone phone);

}

