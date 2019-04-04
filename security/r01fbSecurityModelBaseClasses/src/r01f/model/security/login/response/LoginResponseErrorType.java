package r01f.model.security.login.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum LoginResponseErrorType
 implements EnumWithCode<Integer,LoginResponseErrorType>{

	INVALID_USER_CODE	(100 + 1),		// client id not found
	PASSWORD_ERROR		(100 + 2),
	PASSWORD_EXPIRED	(100 + 3);

	@Getter private final Integer _code;			// this do not mandatory have to be an integer
	@Getter private final Class<Integer> _codeType = Integer.class;

	// Wrapper that encapsulates the EnumWithCode behavior
	private static EnumWithCodeWrapper<Integer,LoginResponseErrorType> DELEGATE = EnumWithCodeWrapper.wrapEnumWithCode(LoginResponseErrorType.class);

	@Override
	public boolean isIn(LoginResponseErrorType... other) {
		return DELEGATE.isIn(this,other);
	}
	@Override
	public boolean is(final LoginResponseErrorType el) {
		return DELEGATE.is(this,el);
	}
}
