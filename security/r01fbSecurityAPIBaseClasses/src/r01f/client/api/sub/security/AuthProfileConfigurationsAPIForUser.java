package r01f.client.api.sub.security;

import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthProfileConfig;
import r01f.client.api.sub.delegates.security.ClientAPIDelegateForUserAuthProfileConfigCRUDServices;
import r01f.model.security.auth.profile.PersistableModelForUserAuthProfileConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.interfaces.ServiceInterface;

/**
 * Client implementation of the User Auth Config
 */
@Singleton
@Accessors(prefix="_")
public abstract class AuthProfileConfigurationsAPIForUser<O extends UserAuthProfileModelOID,
														  ID extends UserAuthProfileID,
														  A extends PersistableModelForUserAuthProfileConfig<O,ID,A>>
			  extends ClientSubAPIBase {		// a sub API uses the proxy to access the services layer

/////////////////////////////////////////////////////////////////////////////////////////
//DELEGATE APIS
/////////////////////////////////////////////////////////////////////////////////////////

	@Getter private ClientAPIDelegateForUserAuthProfileConfigCRUDServices<O,ID,A> _forCRUD;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AuthProfileConfigurationsAPIForUser(final Provider<SecurityContext>  securityContextProvider,
										final Marshaller modelObjectsMarshaller,
										final Map<Class,ServiceInterface> _usersServiceInterfaceTypesToImplOrProxy,
										final Class<? extends CRUDServicesAPIForUserAuthProfileConfig > crudServicesAPIForUserAuthProfileConfigClass) {

		super(securityContextProvider,
			   modelObjectsMarshaller,
			   _usersServiceInterfaceTypesToImplOrProxy);	// reference to other client apis

		if ( crudServicesAPIForUserAuthProfileConfigClass != null){
			_forCRUD = new ClientAPIDelegateForUserAuthProfileConfigCRUDServices<O,ID,A>(securityContextProvider,
																						 modelObjectsMarshaller,
																						 this.getServiceInterfaceCoreImplOrProxy(crudServicesAPIForUserAuthProfileConfigClass));
		}
	}

}
