package r01f.db.search.users;

import java.util.Collection;

import javax.inject.Singleton;

import r01f.api.interfaces.user.SearchServicesAPIForUser;
import r01f.model.search.SearchResults;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.model.security.filters.SearchFilterForSecurityObjectBase;
import r01f.model.security.filters.SearchResultItemForSecurity;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.persistence.search.Searcher;
import r01f.securitycontext.SecurityContext;

@SuppressWarnings("rawtypes")
@Singleton
public class DBSearcherForUserData<O extends UserDataModelOID,
								   U extends PersistableModelForUserData<O,U>>
  implements Searcher<SearchFilterForSecurityObjectBase,SearchResultItemForSecurity>,
			 SearchServicesAPIForUser {

	@Override
	public int countRecords(SecurityContext securityContext, SearchFilterForSecurityObjectBase filter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SearchResults<SearchFilterForSecurityObjectBase, SearchResultItemForSecurity> filterRecords(
			SecurityContext securityContext, SearchFilterForSecurityObjectBase filter,
			Collection<SearchResultsOrdering> ordering, int firstRowNum, int numberOfRows) {
		throw new UnsupportedOperationException();
	}

}

