package r01f.model.security;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.model.security.oids.SecurityCommonOIDs.UserModelOID;
import r01f.model.security.oids.SecurityIDS.UserModelID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public abstract class PersistableSecurityModelIdentifiedObjectBase<O extends UserModelOID,
																   ID extends UserModelID,
																   SELF_TYPE extends PersistableSecurityModelIdentifiedObjectBase<O,ID,SELF_TYPE>>
			  extends PersistableSecurityModelObjectBase<O,SELF_TYPE>
		   implements PersistableSecurityModelIdentifiedObject<O,ID,SELF_TYPE> {

	private static final long serialVersionUID = -3535852608223022810L;

/////////////////////////////////////////////////////////////////////////////////////////
//  ID FIELD
/////////////////////////////////////////////////////////////////////////////////////////

	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected ID _id;

}
