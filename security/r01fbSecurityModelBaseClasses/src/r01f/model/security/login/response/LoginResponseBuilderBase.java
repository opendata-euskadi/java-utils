package r01f.model.security.login.response;

import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.IsBuilder;

@NoArgsConstructor
public abstract class LoginResponseBuilderBase<LOK extends LoginResponseOK,
											   LER extends LoginResponseError>
		   implements IsBuilder {

/////////////////////////////////////////////////////////////////////////////////////////
// METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////

	protected abstract LOK _createAsLoggedOK(final UserCode userCode,
											 final Date requestAt);

	protected abstract LER _createAsLoggedError(final UserCode userCode,
												final Date requestAt);

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@NoArgsConstructor
	public class BuilderStart {
		public RequestedAtStep forUser(final UserCode usrCode){
			return new RequestedAtStep(usrCode);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class RequestedAtStep {
		private final UserCode _userCode;

		public LoggedResultStep requestedAt(final Date requestAt){
			return new LoggedResultStep(_userCode, requestAt);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class LoggedResultStep {
		private final UserCode _userCode;
		private final Date _requestAt;

		public LoggedOkNamedStep loggedOn(final Date logonData){
			LOK _loginResponse = _createAsLoggedOK(_userCode, _requestAt);
			((LoginResponseBase)_loginResponse).setLoggedOn(logonData);
			return new LoggedOkNamedStep(_loginResponse);
		}

		public UserBuilderBuildStep withErrorType(final LoginResponseErrorType errorType){
			LER _loginResponse = _createAsLoggedError(_userCode, _requestAt);
			((LoginResponseBase)_loginResponse).setErrorType(errorType);
			return new UserBuilderBuildStep((LoginResponseBase) _loginResponse);
		}
	}


/////////////////////////////////////////////////////////////////////////////////
// LOGGED OK
/////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class LoggedOkNamedStep {
		private final LoginResponseOK _loginResponse;

		public LoggedOkFirstSurnameStep named(final String name){
			((LoginResponseBase)_loginResponse).setName(name);
			return new LoggedOkFirstSurnameStep(_loginResponse);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class LoggedOkFirstSurnameStep {
		private final LoginResponseOK _loginResponse;

		public LoggedOkSecondSurnameStep firstSurname(final String firstName ){
			((LoginResponseBase)_loginResponse).setSurname1(firstName);
			return new LoggedOkSecondSurnameStep(_loginResponse);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class LoggedOkSecondSurnameStep {
		private final LoginResponseOK _loginResponse;

		public UserBuilderBuildStep secondSurname(final String errorType ){
			((LoginResponseBase)_loginResponse).setSurname2(errorType);
			return new UserBuilderBuildStep((LoginResponseBase) _loginResponse);
		}
	}

/////////////////////////////////////////////////////////////////////////////////
// LOGGED ERROR
/////////////////////////////////////////////////////////////////////////////////

// if any fields on error

/////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserBuilderBuildStep {
		private final LoginResponseBase _loginResponse;

		public LoginResponseBase build() {
			return _loginResponse;
		}
	}

}
