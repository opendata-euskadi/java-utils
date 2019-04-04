package r01f.client.servicesproxy.rest.security;

import javax.inject.Singleton;

import r01f.api.interfaces.user.CRUDServicesAPIForUserData;
import r01f.client.servicesproxy.rest.builders.security.RESTResourcePathBuilderBase.RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import r01f.client.servicesproxy.rest.builders.security.RESTResourcePathBuilderBase.RESTServiceResourceUrlPathBuilderForPersistenceBase;
import r01f.guids.CommonOIDs.UserCode;
import r01f.httpclient.HttpResponse;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.DelegateForRawREST;
import r01f.services.client.servicesproxy.rest.RESTServicesForDBCRUDProxyBase;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.types.url.Url;

@Singleton
public class RESTCRUDServicesProxyForUserData<O extends UserDataModelOID,
											  U extends PersistableModelForUserData<O,U>>
	 extends RESTServicesForDBCRUDProxyBase<O,U>
  implements CRUDServicesAPIForUserData<O,U>,
  			 RESTServiceProxyForUsers {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public <P extends RESTServiceResourceUrlPathBuilderForPersistenceBase<O>>
	RESTCRUDServicesProxyForUserData(final Marshaller marshaller, final Class<U> modelObjectType,
									 final P servicesRESTResourceUrlPathBuilder) {
		super(marshaller, modelObjectType, servicesRESTResourceUrlPathBuilder);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//METHODS
/////////////////////////////////////////////////////////////////////////////////////////

	@Override @SuppressWarnings("unchecked")
	public CRUDResult<U> loadByUserCode(final SecurityContext securityContext,
								  		final UserCode id) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForEntityPersistenceBase.class)
													 .pathOfEntityById(id));
		String ctxXml = _marshaller.forWriting().toXml(securityContext);
		HttpResponse httpResponse = DelegateForRawREST.GET(restResourceUrl,
										 				   ctxXml);		// map the response
		CRUDResult<U> outResponse = this.getResponseToCRUDResultMapperForModelObject()
										.mapHttpResponseForEntity(securityContext,
																  PersistenceRequestedOperation.LOAD,
																  restResourceUrl,
																  httpResponse)
										.identifiedOnErrorBy(id);
		// log & return
		_logResponse(restResourceUrl,outResponse);
		return outResponse;
	}

	@Override
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
	  							final EMail email) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForEntityPersistenceBase.class)
													 .pathOfEntityByEMail(email));
		String ctxXml = _marshaller.forWriting().toXml(securityContext);
		HttpResponse httpResponse = DelegateForRawREST.GET(restResourceUrl,
														   ctxXml);		// map the response
		CRUDResult<U> outResponse = this.getResponseToCRUDResultMapperForModelObject()
										.mapHttpResponseForEntity(securityContext,
																  PersistenceRequestedOperation.LOAD,
																  restResourceUrl,
																  httpResponse)
										.identifiedOnErrorBy(email.asString());
		// log & return
		_logResponse(restResourceUrl,outResponse);
		return outResponse;
	}

	@Override
	public CRUDResult<U> loadOrNull(final SecurityContext securityContext,
									  final EMail email) {
		CRUDResult<U> outResponse = this.loadBy(securityContext, email);
		// log & return
		if (outResponse.hasFailed() && outResponse.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
			return null;
		}
		return outResponse;
	}

	@Override
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
								final Phone phone) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForEntityPersistenceBase.class)
													 .pathOfEntityByPhone(phone));
		String ctxXml = _marshaller.forWriting().toXml(securityContext);
		HttpResponse httpResponse = DelegateForRawREST.GET(restResourceUrl,
														   ctxXml);		// map the response
		CRUDResult<U> outResponse = this.getResponseToCRUDResultMapperForModelObject()
										.mapHttpResponseForEntity(securityContext,
																  PersistenceRequestedOperation.LOAD,
																  restResourceUrl,
																  httpResponse)
										.identifiedOnErrorBy(phone.asString());
		// log & return
		_logResponse(restResourceUrl,outResponse);
		return outResponse;
	}

	@Override
	public CRUDResult<U> loadOrNull(final SecurityContext securityContext,
									  final Phone phone) {
		CRUDResult<U> outResponse = this.loadBy(securityContext, phone);
		// log & return
		if (outResponse.hasFailed() && outResponse.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
			return null;
		}
		return outResponse;
	}

}
