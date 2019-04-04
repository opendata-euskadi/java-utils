package r01f.model.security.auth.profile.targets.attributes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.security.oids.SecurityIDS.UserAuthTargetAttributeID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
@NoArgsConstructor
public abstract class UserAuthTargetAttributeBase<ID extends UserAuthTargetAttributeID>
		   implements ModelForUserAuthTargetAttribute {

	private static final long serialVersionUID = -1109899297001700892L;

////////////////////////////////////////////////////////////////////////////
//MEMBERS
///////////////////////////////////////////////////////////////////////////
	@MarshallField( as="id", whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ID _id;

	// TODO Tipar?
	@MarshallField(as="value",escape=true)
	@Getter @Setter private String _value;



}
