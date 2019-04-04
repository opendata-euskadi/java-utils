package r01f.model.security.login;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.Password;
import r01f.model.security.PersistableSecurityModelObjectBase;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="loginData")
public abstract class UserLoginDataBase<O extends UserLoginDataModelOID,
										SELF_TYPE extends PersistableSecurityModelObjectBase<O,SELF_TYPE>>
			  extends PersistableSecurityModelObjectBase<O,SELF_TYPE>
		   implements PersistableModelForUserLoginData<O,SELF_TYPE> {

	private static final long serialVersionUID = 6847872821140655644L;

/////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////

	@MarshallField(as="password")
	@Getter @Setter private Password _password;

	@MarshallField(as="expiratingPwdAt",dateFormat=@MarshallDateFormat(use=DateFormat.CUSTOM,format="yyyy-MM-dd HH:mm:ss"),
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _expiratingPwdAt;

	@MarshallField(as="mandatoryInPasswordRecoveryRequest")
	@Getter @Setter private PasswordRecoveryNotificationType _mandatoryInPasswordRecoveryRequest;

}


