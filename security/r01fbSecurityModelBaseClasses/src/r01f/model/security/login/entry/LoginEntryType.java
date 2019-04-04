package r01f.model.security.login.entry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum LoginEntryType
 implements EnumWithCode<Integer,LoginEntryType>{

	LOGIN_OK						(100 + 1),
	LOGIN_ERROR						(100 + 2),
	PASSWORD_RECOVERY_REQUEST_OK	(100 + 3),
	PASSWORD_RECOVERY_REQUEST_ERROR	(100 + 4),
	PASSWORD_CHANGED_OK				(100 + 5),
	PASSWORD_CHANGED_ERROR			(100 + 6);

	@Getter private final Integer _code;			// this do not mandatory have to be an integer
	@Getter private final Class<Integer> _codeType = Integer.class;

	// Wrapper that encapsulates the EnumWithCode behavior
	private static EnumWithCodeWrapper<Integer,LoginEntryType> DELEGATE = EnumWithCodeWrapper.wrapEnumWithCode(LoginEntryType.class);

	@Override
	public boolean isIn(LoginEntryType... other) {
		return DELEGATE.isIn(this,other);
	}
	@Override
	public boolean is(final LoginEntryType el) {
		return DELEGATE.is(this,el);
	}
}
