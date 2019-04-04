package r01f.client.api.sub.security;

import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginData;
import r01f.client.api.sub.delegates.security.ClientAPIDelegateForUserLoginDataCRUDServices;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.interfaces.ServiceInterface;

/**
 * Client implementation of the User Login Config
 */
@Singleton
@Accessors(prefix="_")
public abstract class LoginDataAPIForUser<O extends UserLoginDataModelOID,
										  L extends PersistableModelForUserLoginData<O,L>>
			  extends ClientSubAPIBase {		// a sub API uses the proxy to access the services layer

/////////////////////////////////////////////////////////////////////////////////////////
//DELEGATE APIS
/////////////////////////////////////////////////////////////////////////////////////////

	@Getter private ClientAPIDelegateForUserLoginDataCRUDServices<O,L> _forCRUD;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LoginDataAPIForUser(final Provider<SecurityContext>  securityContextProvider,
										 final Marshaller modelObjectsMarshaller,
										 final Map<Class,ServiceInterface> _usersServiceInterfaceTypesToImplOrProxy,
										 final Class<? extends CRUDServicesAPIForUserLoginData> cRUDServicesAPIForUserLoginConfigClass)  {

		super(securityContextProvider,
			   modelObjectsMarshaller,
			   _usersServiceInterfaceTypesToImplOrProxy);	// reference to other client apis

		_forCRUD = new ClientAPIDelegateForUserLoginDataCRUDServices<O,L>(securityContextProvider,
															   			     modelObjectsMarshaller,
															   			     this.getServiceInterfaceCoreImplOrProxy(cRUDServicesAPIForUserLoginConfigClass));
	}

}
