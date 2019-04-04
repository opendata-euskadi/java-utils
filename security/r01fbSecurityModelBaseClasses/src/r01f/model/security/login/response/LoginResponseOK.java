package r01f.model.security.login.response;

import java.util.Date;

public interface LoginResponseOK
		 extends LoginResponse {

	public Date getLoggedOn();

	public String getName();

	public String getSurname1();

	public String getSurname2();

}
