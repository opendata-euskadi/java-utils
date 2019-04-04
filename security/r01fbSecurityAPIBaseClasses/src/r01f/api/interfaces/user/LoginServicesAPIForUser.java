package r01f.api.interfaces.user;

import r01f.model.operations.OperationExecResult;
import r01f.model.security.login.request.LoginRequest;
import r01f.model.security.login.response.LoginResponse;
import r01f.securitycontext.SecurityContext;

public interface LoginServicesAPIForUser<L extends LoginRequest,
										 R extends LoginResponse >
		 extends SecurityServiceInterface {

	public OperationExecResult<R> login(final SecurityContext securityContext,
										final L loginRequest);

}
