package r01f.client.servicesproxy.rest.security;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import r01f.api.interfaces.user.SearchServicesAPIForUser;
import r01f.client.servicesproxy.rest.builders.security.RESTServicesPathBuilderForUserData;
import r01f.client.servicesproxy.rest.builders.security.RESTResourcePathBuilderBase.RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import r01f.guids.PersistableObjectOID;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.search.SearchResults;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.model.security.filters.SearchFilterForSecurityObjectBase;
import r01f.model.security.filters.SearchResultItemForSecurity;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.DelegateForRawRESTSearch;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilder;
import r01f.services.client.servicesproxy.rest.RESTServicesForSearchProxyBase;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.types.url.Urls;
import r01f.util.types.Paths;
import r01f.xmlproperties.XMLPropertiesComponent;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Singleton
@SuppressWarnings("rawtypes")
public class RESTSearchServicesProxyForUser
	 extends RESTServicesForSearchProxyBase<SearchFilterForSecurityObjectBase,SearchResultItemForSecurity>
  implements SearchServicesAPIForUser,
  			 RESTServiceProxyForUsers {

/////////////////////////////////////////////////////////////////////////////////////////
//  DELEGATE
/////////////////////////////////////////////////////////////////////////////////////////
	private final DelegateForRawRESTSearch<SearchFilterForSecurityObjectBase,SearchResultItemForSecurity> _rawSearchDelegate;
/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	@Inject @SuppressWarnings("unchecked")
	public RESTSearchServicesProxyForUser(@XMLPropertiesComponent("users") final XMLPropertiesForAppComponent clientProps,
										  @ModelObjectsMarshaller final Marshaller marshaller) {
		super(marshaller,
			  new RESTServicesPathBuilderForUserData(clientProps)
		);
		_rawSearchDelegate = new DelegateForRawRESTSearch<SearchFilterForSecurityObjectBase,SearchResultItemForSecurity>(marshaller);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int countRecords(final SecurityContext SecurityContext,
			                final SearchFilterForSecurityObjectBase filter) {
		throw new UnsupportedOperationException("Not  implemented yet!");
	}

	@Override
	public <O extends PersistableObjectOID> Collection<O> filterRecordsOids(final SecurityContext SecurityContext,
			final SearchFilterForSecurityObjectBase filter) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public SearchResults<SearchFilterForSecurityObjectBase,
						 SearchResultItemForSecurity> filterRecords(final SecurityContext SecurityContext,
								 								final SearchFilterForSecurityObjectBase filter,
								 								final Collection<SearchResultsOrdering> ordering,
								 								final int firstRowNum,
								 								final int numberOfRows){
		RESTServiceResourceUrlPathBuilder pathBuilder = this.getServicesRESTResourceUrlPathBuilderAs(RESTServiceResourceUrlPathBuilderForEntityPersistenceBase.class);
		Url restResourceUrl =  Urls.join(pathBuilder.getHost(),
										Paths.forUrlPaths().join(pathBuilder.getEndPointBasePath(),
												                  UrlPath.from("index"),
												                  UrlPath.from("users")));

		return _rawSearchDelegate.doSEARCH(restResourceUrl,
											SecurityContext,
											filter,
											ordering,
											firstRowNum,
											numberOfRows);
	}


}
