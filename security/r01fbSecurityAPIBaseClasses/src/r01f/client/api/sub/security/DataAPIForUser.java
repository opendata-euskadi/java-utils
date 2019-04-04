package r01f.client.api.sub.security;

import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserData;
import r01f.api.interfaces.user.FindServicesAPIForUserData;
import r01f.client.api.sub.delegates.security.ClientAPIDelegateForUserDataCRUDServices;
import r01f.client.api.sub.delegates.security.ClientAPIDelegateForUserDataFindServices;
import r01f.client.api.sub.delegates.security.ClientAPIDelegateForUserSearchServices;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.interfaces.ServiceInterface;

/**
 * Client implementation of the Users
 */
@Singleton
@Accessors(prefix="_")
public abstract class DataAPIForUser<O extends UserDataModelOID,
									 U extends PersistableModelForUserData<O,U>>
			  extends ClientSubAPIBase {		// a sub API uses the proxy to access the services layer

/////////////////////////////////////////////////////////////////////////////////////////
//DELEGATE APIS
/////////////////////////////////////////////////////////////////////////////////////////

	@Getter private ClientAPIDelegateForUserDataCRUDServices<O,U> _forCRUD;
	@Getter private ClientAPIDelegateForUserDataFindServices<O,U>  _forFind;
	@Getter private ClientAPIDelegateForUserSearchServices _forSearch;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DataAPIForUser(final Provider<SecurityContext>  securityContextProvider,
									final Marshaller modelObjectsMarshaller,
									final Map<Class,ServiceInterface> usersServiceInterfaceTypesToImplOrProxy,
									final Class<? extends CRUDServicesAPIForUserData> forCRUDClass,
									final Class<? extends FindServicesAPIForUserData> forFindClass,
									final ClientAPIDelegateForUserSearchServices forSearch) {

		super(securityContextProvider,
			  modelObjectsMarshaller,
			  usersServiceInterfaceTypesToImplOrProxy);	// reference to other client apis

		if (forCRUDClass != null){
			_forCRUD = new ClientAPIDelegateForUserDataCRUDServices(securityContextProvider,
																modelObjectsMarshaller,
																this.getServiceInterfaceCoreImplOrProxy(forCRUDClass));
		}
		if (forFindClass != null){
			_forFind = new ClientAPIDelegateForUserDataFindServices(securityContextProvider,
														    modelObjectsMarshaller,
															this.getServiceInterfaceCoreImplOrProxy(forFindClass));
		}

	}



}
