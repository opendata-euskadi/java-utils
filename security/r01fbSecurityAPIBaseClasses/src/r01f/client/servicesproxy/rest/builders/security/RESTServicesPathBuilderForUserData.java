package r01f.client.servicesproxy.rest.builders.security;

import r01f.client.servicesproxy.rest.builders.security.RESTResourcePathBuilderBase.RESTServiceResourceUrlPathBuilderForPersistenceBase;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.types.contact.PersonID;
import r01f.types.url.UrlPath;
import r01f.util.types.Paths;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

public  class RESTServicesPathBuilderForUserData<O extends UserDataModelOID,
												 U extends PersistableModelForUserData<O,U>>
	  extends RESTServiceResourceUrlPathBuilderForPersistenceBase<O> {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public RESTServicesPathBuilderForUserData(final XMLPropertiesForAppComponent clientProps) {
		super(new RESTServiceEndPointUrl(clientProps,
										 "users"),
										 UrlPath.from("users"));
	}

	public UrlPath pathOfEntityByUserCode(final UserCode userCode) {
		return Paths.forUrlPaths().join(this.pathOfAllEntities(),
										"byUserCode",
										userCode);
	}

	public UrlPath pathOfEntityByNif(final PersonID nif) {
		return Paths.forUrlPaths().join(this.pathOfAllEntities(),
										"byNif",
										nif);
	}

}