package r01f.model.security.auth.profile.functions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.security.oids.SecurityIDS.UserAuthFunctionID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
@NoArgsConstructor
public abstract class UserAuthFunctionBase<ID extends UserAuthFunctionID>
		   implements ModelForUserAuthFunction {

	private static final long serialVersionUID = -4571113245352289549L;

////////////////////////////////////////////////////////////////////////////
//MEMBERS
///////////////////////////////////////////////////////////////////////////
	@MarshallField( as="id", whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ID _id;

	// TODO Tipar?
	@MarshallField(as="value",escape=true)
	@Getter @Setter private String _value;

}
