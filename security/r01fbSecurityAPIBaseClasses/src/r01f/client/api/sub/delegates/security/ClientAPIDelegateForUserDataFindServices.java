package r01f.client.api.sub.delegates.security;

import java.util.Collection;

import javax.inject.Provider;

import r01f.api.interfaces.user.FindServicesAPIForUserData;
import r01f.model.persistence.FindResult;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIDelegateForModelObjectFindServices;

public class ClientAPIDelegateForUserDataFindServices<O extends UserDataModelOID,
													  U extends PersistableModelForUserData<O,U>>
	 extends ClientAPIDelegateForModelObjectFindServices<O,U> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public ClientAPIDelegateForUserDataFindServices(final Provider<SecurityContext>  securityContextProvider,
												final Marshaller marshaller,
												final FindServicesAPIForUserData<O,U> findServicesProxy) {
		super(securityContextProvider,
			  marshaller,
			  findServicesProxy);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public Collection<U> findAllUsers() {
		FindResult<U>  findResult = this.getServiceProxyAs(FindServicesAPIForUserData.class)
											.findAllUsers(this.getSecurityContext());
		return findResult.getOrThrow();
	}

}
