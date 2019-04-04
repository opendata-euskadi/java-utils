package r01f.client.api.security;

import java.util.Map;

import javax.inject.Provider;

import lombok.experimental.Accessors;
import r01f.client.api.sub.security.AuthConfigurationsAPIForUser;
import r01f.client.api.sub.security.AuthProfileConfigurationsAPIForUser;
import r01f.client.api.sub.security.DataAPIForUser;
import r01f.client.api.sub.security.LoginDataAPIForUser;
import r01f.client.api.sub.security.LoginOperationAPIForUser;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.security.auth.PersistableModelForUserAuthConfig;
import r01f.model.security.auth.profile.PersistableModelForUserAuthProfileConfig;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginOperationModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientAPIImplBase;
import r01f.services.interfaces.ServiceInterface;

@Accessors(prefix="_")
public abstract class UsersAPI
			  extends ClientAPIImplBase {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("rawtypes")
	public UsersAPI(						final  Provider<SecurityContext> securityContextProvider,
					@ModelObjectsMarshaller final  Marshaller modelObjectsMarshaller,
											final  Map<Class,ServiceInterface> _usersServiceInterfaceTypesToImplOrProxy) {	// comes from injection
		// Services proxy
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  _usersServiceInterfaceTypesToImplOrProxy);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//SUB-APIs TO IMPLEMENTS
/////////////////////////////////////////////////////////////////////////////////////////

	public abstract <O extends UserDataModelOID,
					 U extends PersistableModelForUserData<O,U>> DataAPIForUser<O,U> usersDataAPI();

	public abstract <O extends UserLoginDataModelOID,
					 L extends PersistableModelForUserLoginData<O,L> > LoginDataAPIForUser<O,L> loginDataAPI();

	public abstract <O extends UserLoginOperationModelOID,
	 				 L extends PersistableModelForUserLoginEntry<O,L> > LoginOperationAPIForUser<O,L> loginOperationAPI();

	public abstract <O extends UserAuthConfigModelOID,
					 A extends PersistableModelForUserAuthConfig<O,A>> AuthConfigurationsAPIForUser<O,A> authConfigAPI();

	public abstract <O extends UserAuthProfileModelOID,
					 ID extends UserAuthProfileID,
					 A extends PersistableModelForUserAuthProfileConfig<O,ID,A>> AuthProfileConfigurationsAPIForUser<O,ID,A> authProfileConfigAPI();

}
