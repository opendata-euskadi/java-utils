package r01f.client.api.sub.security;

import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginOperation;
import r01f.client.api.sub.delegates.security.ClientAPIDelegateForUserLoginOperationCRUDServices;
import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginOperationModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.interfaces.ServiceInterface;

/**
 * Client implementation of the User Login Config
 */
@Singleton
@Accessors(prefix="_")
public abstract class LoginOperationAPIForUser<O extends UserLoginOperationModelOID,
											   L extends PersistableModelForUserLoginEntry<O,L>>
			  extends ClientSubAPIBase {		// a sub API uses the proxy to access the services layer

/////////////////////////////////////////////////////////////////////////////////////////
//DELEGATE APIS
/////////////////////////////////////////////////////////////////////////////////////////

	@Getter private ClientAPIDelegateForUserLoginOperationCRUDServices<O,L> _forCRUD;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LoginOperationAPIForUser(final Provider<SecurityContext>  securityContextProvider,
									final Marshaller modelObjectsMarshaller,
									final Map<Class,ServiceInterface> _usersServiceInterfaceTypesToImplOrProxy,
									final Class<? extends CRUDServicesAPIForUserLoginOperation> cRUDServicesAPIForUserLoginOperationClass)  {

		super(securityContextProvider,
			   modelObjectsMarshaller,
			   _usersServiceInterfaceTypesToImplOrProxy);	// reference to other client apis

		_forCRUD = new ClientAPIDelegateForUserLoginOperationCRUDServices<O,L>(securityContextProvider,
																			   modelObjectsMarshaller,
																			   this.getServiceInterfaceCoreImplOrProxy(cRUDServicesAPIForUserLoginOperationClass));
	}

}
