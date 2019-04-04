package r01f.model.security.login.entry;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.model.security.PersistableSecurityModelObjectBase;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginEntryModelOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="loginEntry")
public abstract class UserLoginEntryBase<O extends UserLoginEntryModelOID,
										 SELF_TYPE extends PersistableSecurityModelObjectBase<O,SELF_TYPE>>
			  extends PersistableSecurityModelObjectBase<O,SELF_TYPE>
		   implements PersistableModelForUserLoginEntry<O,SELF_TYPE>,
		   			  HasSummaryFacet {

	private static final long serialVersionUID = 6847872821140655644L;

/////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////

	@MarshallField(as="entryType")
	@Getter @Setter private LoginEntryType _loginEntryType;

	@MarshallField(as="token")
	@Getter @Setter private String _token;
}


