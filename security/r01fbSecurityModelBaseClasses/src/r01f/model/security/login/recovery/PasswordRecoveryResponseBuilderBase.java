package r01f.model.security.login.recovery;

import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.IsBuilder;


@NoArgsConstructor
public abstract class PasswordRecoveryResponseBuilderBase<R extends PasswordRecoveryResponseBase>
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class BuilderStart {
		private final R _passwordRecoveryRequest;

		public UserCodeStep requestAt(final Date date){
			_passwordRecoveryRequest.setRequestAt(date);
			return new UserCodeStep(_passwordRecoveryRequest);
		}
		public NotificationDoneStep forUserCode(final UserCode userCode) {
			_passwordRecoveryRequest.setRequestAt(new Date());
			_passwordRecoveryRequest.setUserCode(userCode);
			return new NotificationDoneStep(_passwordRecoveryRequest);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserCodeStep {
		private final R  _passwordRecoveryRequest;

		public UserBuilderBuildStep forUserCode(final UserCode userCode) {
			_passwordRecoveryRequest.setUserCode(userCode);
			return new UserBuilderBuildStep(_passwordRecoveryRequest);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class NotificationDoneStep {
		private final R  _passwordRecoveryRequest;

		public TokenStep done() {
			_passwordRecoveryRequest.setNotificationDone(true);
			return new TokenStep(_passwordRecoveryRequest);
		}
		public UserBuilderBuildStep withError(final PasswordRecoveryResponseErrorType errorType) {
			_passwordRecoveryRequest.setNotificationDone(false);
			_passwordRecoveryRequest.setErrorType(errorType);
			return new UserBuilderBuildStep(_passwordRecoveryRequest);
		}

	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class TokenStep {
		private final R  _passwordRecoveryRequest;

		public UserBuilderBuildStep withToken(final String token) {
			_passwordRecoveryRequest.setToken(token);
			return new UserBuilderBuildStep(_passwordRecoveryRequest);
		}

		public R build() {
			return _passwordRecoveryRequest;
		}
	}

//////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserBuilderBuildStep {
		private final R _passwordRecoveryRequest;

		public R build() {
			return _passwordRecoveryRequest;
		}
	}
}
