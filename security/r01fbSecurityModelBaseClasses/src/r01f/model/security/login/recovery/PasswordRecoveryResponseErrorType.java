package r01f.model.security.login.recovery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum PasswordRecoveryResponseErrorType
 implements EnumWithCode<Integer,PasswordRecoveryResponseErrorType>{

	INVALID_USER_CODE			(100 + 1),		// client id not found
	INVALID_NOTIFICATION_DATA	(100 + 2),
	INVALID_NOTIFICATION_TYPE	(100 + 3),
	NOTIFICATION_ERROR			(100 + 4);

	@Getter private final Integer _code;			// this do not mandatory have to be an integer
	@Getter private final Class<Integer> _codeType = Integer.class;

	// Wrapper that encapsulates the EnumWithCode behavior
	private static EnumWithCodeWrapper<Integer,PasswordRecoveryResponseErrorType> DELEGATE = EnumWithCodeWrapper.wrapEnumWithCode(PasswordRecoveryResponseErrorType.class);

	@Override
	public boolean isIn(PasswordRecoveryResponseErrorType... other) {
		return DELEGATE.isIn(this,other);
	}
	@Override
	public boolean is(final PasswordRecoveryResponseErrorType el) {
		return DELEGATE.is(this,el);
	}
}
