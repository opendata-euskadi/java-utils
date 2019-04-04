package r01f.model.security.filters;

import r01f.model.search.SearchFilterForModelObjectBase;


/**
 * A search filter for a {@link SearchFilterForSecurityObjectBase} like
 * {@link SearchFilterForSecurityObjectBase}, {@link X47BEntityModelObject} or {@link X47BAgent}
 *
 * <pre class='brush:java'>
 * // Find all locations or agents with a certain name belonging to an organization
 * X47BSearchFilterForEntity filter = X47BSearchFilterForEntity.create()
 * 		.belongingTo(X47BOrganizationOID.forId(&quot;myOrg&quot;)).withText(&quot;text&quot;)
 * 		.in(Language.ENGLISH);
 * </pre>
 */

public abstract class SearchFilterForSecurityObjectBase<SELF_TYPE extends SearchFilterForSecurityObjectBase<SELF_TYPE>>
	 extends SearchFilterForModelObjectBase<SELF_TYPE> {

	private static final long serialVersionUID = 3582407033514859849L;

//
}