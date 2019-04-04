package r01f.api.interfaces.user;

import r01f.model.security.filters.SearchFilterForSecurityObjectBase;
import r01f.model.security.filters.SearchResultItemForSecurity;
import r01f.services.interfaces.SearchServices;

@SuppressWarnings("rawtypes")
public interface SearchServicesAPIForUser
		 extends SearchServices<SearchFilterForSecurityObjectBase, SearchResultItemForSecurity>,
		 		 SecurityServiceInterface {

}