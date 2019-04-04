package r01f.model.security.auth.profile.targets;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.security.auth.profile.targets.attributes.UserAuthTargetAttributeBase;
import r01f.model.security.oids.SecurityIDS.UserAuthTargetAttributeID;
import r01f.model.security.oids.SecurityIDS.UserAuthTargetID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="authTarget")
public abstract class UserAuthTargetBase<ID extends UserAuthTargetID,
										 IDA extends UserAuthTargetAttributeID,
										 A extends UserAuthTargetAttributeBase<IDA>>
		   implements ModelForUserAuthTarget {

	private static final long serialVersionUID = -6697689709808766885L;

////////////////////////////////////////////////////////////////////////////
//MEMBERS
///////////////////////////////////////////////////////////////////////////
	@MarshallField( as="id",
					whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private ID _id;

	@MarshallField( as="attributes",
					whenXml=@MarshallFieldAsXml(collectionElementName="attr"))
	@Setter @Getter private Collection<A> _attributes;


}
