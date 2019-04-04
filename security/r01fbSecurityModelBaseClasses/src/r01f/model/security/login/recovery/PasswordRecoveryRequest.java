package r01f.model.security.login.recovery;

import r01f.guids.CommonOIDs.UserCode;
import r01f.model.ModelObject;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

public interface PasswordRecoveryRequest
		 extends ModelObject {

	public UserCode getUserCode();

	public EMail getEmail();

	public Phone getPhone();

}
