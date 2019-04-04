package r01f.model.security.login.recovery;

public interface PasswordRecoveryResponseWithToken
		 extends PasswordRecoveryResponse {

	public void setToken(final String token);

}
