package r01f.model.security.filters;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.search.SearchResultItem;
import r01f.model.security.PersistableSecurityModelObject;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataOID;
import r01f.model.security.oids.SecurityIDS.UserID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
/**
 * A search result item for an User
 */

@MarshallType(as="searchResultItemForUser")
@Accessors(prefix="_")
@NoArgsConstructor
public class SearchResultItemForSecurity
  implements SearchResultItem {

	private static final long serialVersionUID = -1121801690861378765L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="type",
		   	   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter Class<? extends PersistableSecurityModelObject<?>> _modelObjectType;

	@MarshallField(as="userOID",
		   	   whenXml=@MarshallFieldAsXml(attr=true))
	@Setter @Getter UserDataOID _userOID;

	@MarshallField(as="userID",
		   	   whenXml=@MarshallFieldAsXml(attr=true))
	@Setter @Getter UserID _userID;

	@MarshallField(as="name",escape=true)
	@Setter @Getter  String _name;

	@MarshallField(as="surname",escape=true)
	@Setter @Getter  String _surname;

	@MarshallField(as="surname2",escape=true)
	@Setter @Getter  String _surname2;

	@MarshallField(as="creationDate",dateFormat=@MarshallDateFormat(use=DateFormat.CUSTOM,
																    format="yyyy-MM-dd HH:mm:ss"),
		       whenXml=@MarshallFieldAsXml(attr=true))
	@Setter @Getter Date  _creationDate;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
}
