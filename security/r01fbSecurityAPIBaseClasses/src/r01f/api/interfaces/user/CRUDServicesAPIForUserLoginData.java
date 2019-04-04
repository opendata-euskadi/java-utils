package r01f.api.interfaces.user;

import r01f.guids.CommonOIDs.UserCode;
import r01f.model.PersistableModelObject;
import r01f.model.facets.Versionable;
import r01f.model.persistence.CRUDResult;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface CRUDServicesAPIForUserLoginData<O extends UserLoginDataModelOID,
												 L extends PersistableModelForUserLoginData<O,L>>
		 extends CRUDServicesForModelObject<O,L>,
		 		 SecurityServiceInterface {

	/**
	 * Returns a entity from its User Code
	 * If the entity is a {@link Versionable} {@link PersistableModelObject}, this method returns the
	 * currently active version
	 * @param securityContext the user auth data & context info
	 * @param oid the entity identifier
	 * @return a {@link CRUDResult} that encapsulates the entity if it was loaded successfully
	 */
	public CRUDResult<L> loadByUserCode(final SecurityContext securityContext,
										final UserCode userCode);

}

