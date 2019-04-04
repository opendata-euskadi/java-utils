package r01f.model.security.login.request;

import r01f.guids.CommonOIDs.Password;
import r01f.model.ModelObject;

public interface LoginRequest
		 extends ModelObject {

	 public Password getPassword();
}
