package r01f.model.security.login;

import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.patterns.IsBuilder;

@NoArgsConstructor
public abstract class UserLoginDataBuilderBase<O extends UserLoginDataModelOID,
											   L extends UserLoginDataBase<O,L>>
		   implements IsBuilder {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class BuilderStart {
		private final L  _loginConfig;

		public PasswordStep forUser(final UserCode userCode){
			_loginConfig.setUserCode(userCode);
			return new PasswordStep(_loginConfig);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class PasswordStep {
		private final L  _loginConfig;

		public ExpirationStep withPassword(final Password password){
			_loginConfig.setPassword(password);
			return new ExpirationStep(_loginConfig);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class ExpirationStep {
		private final L  _loginConfig;

		public MandatoryDataStep expiratesAt(final Date expiration){
			_loginConfig.setExpiratingPwdAt(expiration);
			return new MandatoryDataStep(_loginConfig);
		}
		public MandatoryDataStep withoutExpiration(){
			return new MandatoryDataStep(_loginConfig);
		}
	}

	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public class MandatoryDataStep {
		private final L  _loginConfig;

		public UserBuilderBuildStep withMandatoryDataInPasswordRecoveryRequest(final PasswordRecoveryNotificationType notificationData){
			_loginConfig.setMandatoryInPasswordRecoveryRequest(notificationData);
			return new UserBuilderBuildStep(_loginConfig);
		}
		public UserBuilderBuildStep withoutMandatoryDataInPasswordRecoveryRequest(){
			_loginConfig.setMandatoryInPasswordRecoveryRequest(PasswordRecoveryNotificationType.NONE);
			return new UserBuilderBuildStep(_loginConfig);
		}
	}

//////////////////////////////////////////////////////////////////////////////////////
//
//////////////////////////////////////////////////////////////////////////////////////

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserBuilderBuildStep {
		private final L _userConfig;

		public L build() {
			return _userConfig;
		}
	}

}
