package r01f.model.security.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.security.PersistableSecurityModelObjectBase;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="authConfig")
public abstract class UserAuthConfigBase<O extends  UserAuthConfigModelOID,
										 SELF_TYPE extends PersistableSecurityModelObjectBase<O,SELF_TYPE>>
		extends PersistableSecurityModelObjectBase<O,SELF_TYPE>
	 implements PersistableModelForUserAuthConfig<O,SELF_TYPE> {

	private static final long serialVersionUID = 6847872821140655644L;

	@MarshallField(as="userCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private UserCode  _userCode;

}
