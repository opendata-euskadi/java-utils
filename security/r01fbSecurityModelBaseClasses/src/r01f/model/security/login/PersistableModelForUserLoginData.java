package r01f.model.security.login;

import java.util.Date;

import r01f.guids.CommonOIDs.Password;
import r01f.model.security.PersistableSecurityModelObject;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;

/**
 * Model object
 */
public interface PersistableModelForUserLoginData<O extends UserLoginDataModelOID,
												  SELF_TYPE extends PersistableSecurityModelObject<O>>
		 extends ModelForUserLoginData<O>,
		 		 PersistableSecurityModelObject<O>{

	public Date getExpiratingPwdAt();

	public Password getPassword();

	public void setPassword(final Password password);

	public PasswordRecoveryNotificationType getMandatoryInPasswordRecoveryRequest();

}
