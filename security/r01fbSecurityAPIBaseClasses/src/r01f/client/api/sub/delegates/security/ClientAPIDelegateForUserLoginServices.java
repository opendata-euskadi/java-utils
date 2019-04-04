package r01f.client.api.sub.delegates.security;

import javax.inject.Provider;

import r01f.api.interfaces.user.LoginServicesAPIForUser;
import r01f.model.operations.OperationExecResult;
import r01f.model.security.login.request.LoginRequest;
import r01f.model.security.login.response.LoginResponse;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIServiceDelegateBase;

public class ClientAPIDelegateForUserLoginServices<L extends LoginRequest, R extends LoginResponse >
	 extends ClientAPIServiceDelegateBase<LoginServicesAPIForUser<L,R>> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public ClientAPIDelegateForUserLoginServices(final Provider<SecurityContext>  securityContextProvider,
												 final Marshaller marshaller,
												 final LoginServicesAPIForUser<L,R> loginServicesProxy) {
		super(securityContextProvider,
			  marshaller,
			  loginServicesProxy);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public R login(final  LoginRequest loginRequest) {
		OperationExecResult<R> opResult = this.getServiceProxyAs(LoginServicesAPIForUser.class)
											  .login(this.getSecurityContext(),
													 loginRequest);
		R loginResponse = opResult.getOrThrow();
		return loginResponse;
	}

}
