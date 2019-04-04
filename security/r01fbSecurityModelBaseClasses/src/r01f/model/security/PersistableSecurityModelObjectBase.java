package r01f.model.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.IndexableModelObject;
import r01f.model.PersistableModelObjectBase;
import r01f.model.security.oids.SecurityCommonOIDs.UserModelOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@NoArgsConstructor
@Accessors(prefix="_")
public abstract class PersistableSecurityModelObjectBase<O extends UserModelOID,
														 SELF_TYPE extends PersistableSecurityModelObjectBase<O,SELF_TYPE>>
			  extends PersistableModelObjectBase<O,SELF_TYPE>
		   implements PersistableSecurityModelObject<O>,			// is persistable
					  IndexableModelObject {

	private static final long serialVersionUID = -432678024812158368L;

/////////////////////////////////////////////////////////////////////////////////////////
// USER CODE FIELD
/////////////////////////////////////////////////////////////////////////////////////////

	@MarshallField(as="userCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected UserCode _userCode;

}
