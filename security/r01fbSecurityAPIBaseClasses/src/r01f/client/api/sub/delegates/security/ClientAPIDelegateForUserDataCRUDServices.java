package r01f.client.api.sub.delegates.security;

import javax.inject.Provider;

import r01f.api.interfaces.user.CRUDServicesAPIForUserData;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceException;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIDelegateForModelObjectCRUDServices;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

public class ClientAPIDelegateForUserDataCRUDServices<O extends UserDataModelOID,
													  U extends PersistableModelForUserData<O,U>>
	 extends ClientAPIDelegateForModelObjectCRUDServices<O,U> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public ClientAPIDelegateForUserDataCRUDServices(final Provider<SecurityContext>  securityContextProvider,
												final Marshaller marshaller,
												final CRUDServicesAPIForUserData<O,U> crudServicesProxy) {
		super(securityContextProvider,
			  marshaller,
			  crudServicesProxy);
	}

	/**
	 * Loads an User by ID
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public U loadByUserCode(final UserCode id) {
		CRUDResult<U> opResult = this.getServiceProxyAs(CRUDServicesAPIForUserData.class)
									 .loadByUserCode(this.getSecurityContext(),
													 id);
		U outEntity = opResult.getOrThrow();
		return outEntity;
	}

	/**
	 * Loads the owner with the given email (should exist a single owner with the given mail)
	 * @param email
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public U loadBy(final EMail email) {
		CRUDResult<U> opResult = this.getServiceProxyAs(CRUDServicesAPIForUserData.class)
				 .loadBy(this.getSecurityContext(),
						 email);
		U outEntity = opResult.getOrThrow();
		return outEntity;
	}

	/**
	 * Loads the owner with the given email (should exist a single owner with the given mail)
	 * @param email
	 * @return
	 */
	public U loadOrNull(final EMail email) {
		U outOwner = null;
		try {
			outOwner = this.loadBy(email);
		} catch (PersistenceException pEx) {
			if (pEx.isEntityNotFound()) {
				outOwner = null;
			} else {
				throw pEx;
			}
		}
		return outOwner;
	}

	/**
	 * Loads the owner with the given phone (should exist a single owner with the given phone)
	 * @param phone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public U loadBy(final Phone phone) {
		CRUDResult<U> opResult = this.getServiceProxyAs(CRUDServicesAPIForUserData.class)
				 .loadBy(this.getSecurityContext(),
						 phone);
		U outEntity = opResult.getOrThrow();
		return outEntity;
	}

	/**
	 * Loads the owner with the given email (should exist a single owner with the given mail)
	 * @param phone
	 * @return
	 */
	public U loadOrNull(final Phone phone) {
		U outOwner = null;
		try {
			outOwner = this.loadBy(phone);
		} catch (PersistenceException pEx) {
			if (pEx.isEntityNotFound()) {
				outOwner = null;
			} else {
				throw pEx;
			}
		}
		return outOwner;
	}

}
