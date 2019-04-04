package r01f.client.api.sub.delegates.security;

import javax.inject.Provider;

import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginData;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIDelegateForModelObjectCRUDServices;

public class ClientAPIDelegateForUserLoginDataCRUDServices<O extends UserLoginDataModelOID,
														   L extends PersistableModelForUserLoginData<O,L>>
	 extends ClientAPIDelegateForModelObjectCRUDServices<O,L>{

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForUserLoginDataCRUDServices(final Provider<SecurityContext>  securityContextProvider,
														 final Marshaller marshaller,
														 final CRUDServicesAPIForUserLoginData<O,L> crudServicesProxy) {
		super(securityContextProvider,
			  marshaller,
			  crudServicesProxy);
	}
	/**
	 * Loads an User by user code
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public L loadByUserCode(final SecurityContext securityContext,
							final UserCode oid) {
		CRUDResult<L> opResult = this.getServiceProxyAs(CRUDServicesAPIForUserLoginData.class)
									 .loadByUserCode(this.getSecurityContext(),
													 oid);
		L outEntity = opResult.getOrThrow();
		return outEntity;
	}
}
