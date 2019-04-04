package r01f.client.api.sub.delegates.security;

import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.experimental.Accessors;
import r01f.api.interfaces.user.SearchServicesAPIForUser;
import r01f.model.security.filters.SearchFilterForSecurityObjectBase;
import r01f.model.security.filters.SearchResultItemForSecurity;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIDelegateForModelObjectSearchServices;

/**
 * Client implementation of search api
 */
@SuppressWarnings("rawtypes")
@Singleton
@Accessors(prefix="_")
public abstract class ClientAPIDelegateForUserSearchServices
	   extends ClientAPIDelegateForModelObjectSearchServices<SearchFilterForSecurityObjectBase,
	   														 SearchResultItemForSecurity> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ClientAPIDelegateForUserSearchServices(final Provider<SecurityContext> securityContextProvider,
			                                      final Marshaller marshaller,
												  final SearchServicesAPIForUser entitySearchServicesProxy) {
		super(securityContextProvider,
			  marshaller,
			  null,
			  SearchFilterForSecurityObjectBase.class,
			  SearchResultItemForSecurity.class);
	}
}
