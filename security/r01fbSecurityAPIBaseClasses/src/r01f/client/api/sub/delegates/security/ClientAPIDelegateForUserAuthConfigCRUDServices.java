package r01f.client.api.sub.delegates.security;


import javax.inject.Provider;

import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthConfig;
import r01f.model.security.auth.PersistableModelForUserAuthConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIDelegateForModelObjectCRUDServices;

public  class ClientAPIDelegateForUserAuthConfigCRUDServices<O extends UserAuthConfigModelOID,
															 A extends PersistableModelForUserAuthConfig<O,A>>
	 extends ClientAPIDelegateForModelObjectCRUDServices<O,A> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForUserAuthConfigCRUDServices(final Provider<SecurityContext>  securityContextProvider,
														  final Marshaller marshaller,
														  final CRUDServicesAPIForUserAuthConfig<O,A> crudServicesProxy) {
		super(securityContextProvider,
			  marshaller,
			  crudServicesProxy);
	}
}
