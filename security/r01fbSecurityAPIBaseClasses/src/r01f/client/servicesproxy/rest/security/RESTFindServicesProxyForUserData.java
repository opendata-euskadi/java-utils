package r01f.client.servicesproxy.rest.security;

import javax.inject.Singleton;

import r01f.api.interfaces.user.FindServicesAPIForUserData;
import r01f.client.servicesproxy.rest.builders.security.RESTResourcePathBuilderBase.RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import r01f.client.servicesproxy.rest.builders.security.RESTResourcePathBuilderBase.RESTServiceResourceUrlPathBuilderForPersistenceBase;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindResult;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.RESTServicesForDBFindProxyBase;
import r01f.types.url.Url;

@Singleton
public class RESTFindServicesProxyForUserData<O extends UserDataModelOID,
											  U extends PersistableModelForUserData<O,U>>
	 extends RESTServicesForDBFindProxyBase<O,U>
  implements FindServicesAPIForUserData<O,U>,
			 RESTServiceProxyForUsers {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public <P extends RESTServiceResourceUrlPathBuilderForPersistenceBase<O>> RESTFindServicesProxyForUserData(Marshaller marshaller, Class<U> modelObjectType,
			P servicesRESTResourceUrlPathBuilder) {
		super(marshaller, modelObjectType, servicesRESTResourceUrlPathBuilder);

	}

	@Override
	public FindOIDsResult<O> findAll(final SecurityContext scx) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForEntityPersistenceBase.class)
								  .pathOfEntityListByOids());
		return _findDelegate.doFindOids(scx, restResourceUrl);
	}

	@Override
	public FindResult<U> findAllUsers(final SecurityContext scx) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForEntityPersistenceBase.class)
								  .pathOfEntityList());
		return _findDelegate.doFindEntities(scx, restResourceUrl);
	}

}
