package r01f.model.security.login.request;

import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.security.oids.SecurityIDS.UserID;
import r01f.patterns.IsBuilder;


@NoArgsConstructor
public abstract class LoginRequestBuilderBase<R extends LoginRequestBase>
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class BuilderStart {

		public TypeStep requestAt(final Date date){
			return new TypeStep(date);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class TypeStep {
		private final Date  _requestAt;

		@SuppressWarnings("unchecked")
		public LoggedOnPassWordStep forUser(final UserCode useCode){
			LoginRequestWithUserCodeBase request = new LoginRequestWithUserCodeBase();
			request.setRequestLoginAt(_requestAt);
			request.setUserCode(useCode);
			return new LoggedOnPassWordStep((R)request);
		}

		@SuppressWarnings("unchecked")
		public <ID extends UserID> LoggedOnPassWordStep forUser(final ID id){
			LoginRequestWithUserIDBase<ID> request = new LoginRequestWithUserIDBase<ID>();
			request.setRequestLoginAt(_requestAt);
			request.setUserId(id);
			return new LoggedOnPassWordStep((R)request);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class LoggedOnPassWordStep {
		private final R  _loginRequest;

		public UserBuilderBuildStep loginWith(final Password pwd) {
			_loginRequest.setPassword(pwd);
			return new UserBuilderBuildStep(_loginRequest);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserBuilderBuildStep {
		private final R  _loginResponse;

		public R build() {
			return _loginResponse;
		}
	}
}
