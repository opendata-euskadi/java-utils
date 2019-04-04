package r01f.model.security.login.reset;

import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.IsBuilder;


@NoArgsConstructor
public abstract class PasswordResetResponseBuilderBase<R extends PasswordResetResponseBase>
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class BuilderStart {
		private final R _passwordResetRequest;

		public UserCodeStep requestAt(final Date date){
			_passwordResetRequest.setRequestAt(date);
			return new UserCodeStep(_passwordResetRequest);
		}
		public NotificationDoneStep forUserCode(final UserCode userCode) {
			_passwordResetRequest.setRequestAt(new Date());
			_passwordResetRequest.setUserCode(userCode);
			return new NotificationDoneStep(_passwordResetRequest);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserCodeStep {
		private final R  _passwordResetRequest;

		public UserBuilderBuildStep forUserCode(final UserCode userCode) {
			_passwordResetRequest.setUserCode(userCode);
			return new UserBuilderBuildStep(_passwordResetRequest);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class NotificationDoneStep {
		private final R  _passwordResetRequest;

		public UserBuilderBuildStep done() {
			_passwordResetRequest.setPasswordUpdateDone(true);
			return new UserBuilderBuildStep(_passwordResetRequest);
		}
		public UserBuilderBuildStep withError(final PasswordResetResponseErrorType errorType) {
			_passwordResetRequest.setPasswordUpdateDone(false);
			_passwordResetRequest.setErrorType(errorType);
			return new UserBuilderBuildStep(_passwordResetRequest);
		}

	}


//////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserBuilderBuildStep {
		private final R _passwordResetRequest;

		public R build() {
			return _passwordResetRequest;
		}
	}
}
