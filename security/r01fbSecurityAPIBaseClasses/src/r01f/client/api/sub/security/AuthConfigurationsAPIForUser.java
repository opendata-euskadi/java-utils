package r01f.client.api.sub.security;

import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthConfig;
import r01f.client.api.sub.delegates.security.ClientAPIDelegateForUserAuthConfigCRUDServices;
import r01f.model.security.auth.PersistableModelForUserAuthConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.interfaces.ServiceInterface;

/**
 * Client implementation of the User Auth Config
 */
@Singleton
@Accessors(prefix="_")
public abstract class AuthConfigurationsAPIForUser<O extends UserAuthConfigModelOID,
												   A extends PersistableModelForUserAuthConfig<O,A>>
	 extends ClientSubAPIBase {		// a sub API uses the proxy to access the services layer

/////////////////////////////////////////////////////////////////////////////////////////
//DELEGATE APIS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private ClientAPIDelegateForUserAuthConfigCRUDServices<O,A> _forCRUD;
/////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AuthConfigurationsAPIForUser(final Provider<SecurityContext>  securityContextProvider,
										final Marshaller modelObjectsMarshaller,
										final Map<Class,ServiceInterface> _usersServiceInterfaceTypesToImplOrProxy,
										final Class<? extends CRUDServicesAPIForUserAuthConfig > crudServicesAPIForUserAuthConfigClass) {

		super(securityContextProvider,
			   modelObjectsMarshaller,
			   _usersServiceInterfaceTypesToImplOrProxy);	// reference to other client apis

		if ( crudServicesAPIForUserAuthConfigClass != null){
			_forCRUD = new ClientAPIDelegateForUserAuthConfigCRUDServices<O,A>(securityContextProvider,
																   		       modelObjectsMarshaller,
																   		       this.getServiceInterfaceCoreImplOrProxy(crudServicesAPIForUserAuthConfigClass));
		}


	}
}
