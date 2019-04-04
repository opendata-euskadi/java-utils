package r01f.model.security.login.reset;

import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.IsBuilder;

@NoArgsConstructor
public abstract class PasswordResetRequestBuilderBase<R extends PasswordResetRequestBase>
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
		public TokenStep forUserCode(final UserCode userCode) {
			_passwordResetRequest.setRequestAt(new Date());
			_passwordResetRequest.setUserCode(userCode);
			return new TokenStep(_passwordResetRequest);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserCodeStep {
		private final R  _passwordResetRequest;

		public TokenStep forUserCode(final UserCode userCode) {
			_passwordResetRequest.setUserCode(userCode);
			return new TokenStep(_passwordResetRequest);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class TokenStep {
		private final R  _passwordResetRequest;

		public PasswordStep usingToken(final String token) {
			_passwordResetRequest.setToken(token);
			return new PasswordStep(_passwordResetRequest);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PasswordStep {
		private final R  _passwordResetRequest;

		public UserBuilderBuildStep withPassword(final Password password) {
			_passwordResetRequest.setPassword(password);
			return new UserBuilderBuildStep(_passwordResetRequest);
		}
	}

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
