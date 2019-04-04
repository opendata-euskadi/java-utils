package r01f.model.security.login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum PasswordRecoveryNotificationType
 implements EnumWithCode<Integer,PasswordRecoveryNotificationType>{

	NONE	(100 + 1),		// client id not found
	EMAIL	(100 + 2),
	PHONE	(100 + 3);

	@Getter private final Integer _code;			// this do not mandatory have to be an integer
	@Getter private final Class<Integer> _codeType = Integer.class;

	// Wrapper that encapsulates the EnumWithCode behavior
	private static EnumWithCodeWrapper<Integer,PasswordRecoveryNotificationType> DELEGATE = EnumWithCodeWrapper.wrapEnumWithCode(PasswordRecoveryNotificationType.class);

	@Override
	public boolean isIn(PasswordRecoveryNotificationType... other) {
		return DELEGATE.isIn(this,other);
	}
	@Override
	public boolean is(final PasswordRecoveryNotificationType el) {
		return DELEGATE.is(this,el);
	}
}
