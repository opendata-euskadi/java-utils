package r01f.client.api.sub.delegates.security;


import javax.inject.Provider;

import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthProfileConfig;
import r01f.model.security.auth.profile.PersistableModelForUserAuthProfileConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIDelegateForModelObjectCRUDServices;

public  class ClientAPIDelegateForUserAuthProfileConfigCRUDServices<O extends UserAuthProfileModelOID,
																	ID extends UserAuthProfileID,
																	A extends PersistableModelForUserAuthProfileConfig<O,ID,A>>
	  extends ClientAPIDelegateForModelObjectCRUDServices<O,A> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForUserAuthProfileConfigCRUDServices(final Provider<SecurityContext>  securityContextProvider,
																 final Marshaller marshaller,
																 final CRUDServicesAPIForUserAuthProfileConfig<O,ID,A> crudServicesProxy) {
		super(securityContextProvider,
			  marshaller,
			  crudServicesProxy);
	}
}
