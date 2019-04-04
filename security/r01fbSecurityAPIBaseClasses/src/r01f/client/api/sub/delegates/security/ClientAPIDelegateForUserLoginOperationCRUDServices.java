package r01f.client.api.sub.delegates.security;

import javax.inject.Provider;

import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginOperation;
import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginOperationModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIDelegateForModelObjectCRUDServices;

public class ClientAPIDelegateForUserLoginOperationCRUDServices<O extends UserLoginOperationModelOID,
																L extends PersistableModelForUserLoginEntry<O,L>>
	 extends ClientAPIDelegateForModelObjectCRUDServices<O,L>{

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public ClientAPIDelegateForUserLoginOperationCRUDServices(final Provider<SecurityContext>  securityContextProvider,
															  final Marshaller marshaller,
															  final CRUDServicesAPIForUserLoginOperation<O,L> crudServicesProxy) {
		super(securityContextProvider,
			  marshaller,
			  crudServicesProxy);
	}

}
