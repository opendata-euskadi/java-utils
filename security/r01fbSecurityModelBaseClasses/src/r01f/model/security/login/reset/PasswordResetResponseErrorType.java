package r01f.model.security.login.reset;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum PasswordResetResponseErrorType
 implements EnumWithCode<Integer,PasswordResetResponseErrorType>{

	INVALID_USER_CODE				(100 + 1),		// client id not found
	UNREGISTERED_RESET_REQUEST		(100 + 2),
	TOKEN_WITH_INVALID_SIGNATURE	(100 + 3),
	TOKEN_WITH_INVALID_AUDIENCE		(100 + 4),
	TOKEN_EXPIRED					(100 + 5),
	TOKEN_WITH_OTHER_ERROR			(100 + 6);

	@Getter private final Integer _code;			// this do not mandatory have to be an integer
	@Getter private final Class<Integer> _codeType = Integer.class;

	// Wrapper that encapsulates the EnumWithCode behavior
	private static EnumWithCodeWrapper<Integer,PasswordResetResponseErrorType> DELEGATE = EnumWithCodeWrapper.wrapEnumWithCode(PasswordResetResponseErrorType.class);

	@Override
	public boolean isIn(PasswordResetResponseErrorType... other) {
		return DELEGATE.isIn(this,other);
	}
	@Override
	public boolean is(final PasswordResetResponseErrorType el) {
		return DELEGATE.is(this,el);
	}
}
