package r01f.model.security.login.response;

import java.util.Date;

import r01f.guids.CommonOIDs.UserCode;
import r01f.model.ModelObject;

public interface LoginResponse
		 extends ModelObject {

	public <R extends LoginResponse> R as(final Class<R> type);

	public boolean hasFailed();

	public UserCode getUserCode();

	public Date getRequestAt();

	/**
	 * @param <R>
	 * @return a {@link LoginOK} instance
	 */
	public LoginResponseOK asLoginOK();

	/**
	 * @return a {@link LoginError} instance
	 */
	public LoginResponseError asLoginError();

}
