package r01f.model.security.login.recovery;

import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.IsBuilder;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;


@NoArgsConstructor
public abstract class PasswordRecoveryRequestBuilderBase<R extends PasswordRecoveryRequestBase>
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
		public NotificationTypeStep forUserCode(final UserCode userCode) {
			_passwordRecoveryRequest.setRequestAt(new Date());
			_passwordRecoveryRequest.setUserCode(userCode);
			return new NotificationTypeStep(_passwordRecoveryRequest);
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
	public class NotificationTypeStep {
		private final R  _passwordRecoveryRequest;

		public UserBuilderBuildStep withEmail(final EMail email) {
			_passwordRecoveryRequest.setEmail(email);
			return new UserBuilderBuildStep(_passwordRecoveryRequest);
		}
		public UserBuilderBuildStep withPhone(final Phone phone) {
			_passwordRecoveryRequest.setPhone(phone);
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
