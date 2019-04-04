package r01f.jwt.verifier;



import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum JWTVerificationResultInvalidCause
  implements EnumWithCode<Integer,JWTVerificationResultInvalidCause>{

	INVALID_SIGNATURE(1),
	EXPIRED(2),
	INVALID_AUDIENCE(3),
	OTHER(100);

	@Getter private final Integer _code;			// this do not mandatory have to be an integer
	@Getter private final Class<Integer> _codeType = Integer.class;

	// Wrapper that encapsulates the EnumWithCode behavior
	private static EnumWithCodeWrapper<Integer,JWTVerificationResultInvalidCause> DELEGATE = EnumWithCodeWrapper.wrapEnumWithCode(JWTVerificationResultInvalidCause.class);

	@Override
	public boolean isIn(JWTVerificationResultInvalidCause... other) {
		return DELEGATE.isIn(this,other);
	}
	@Override
	public boolean is(final JWTVerificationResultInvalidCause el) {
		return DELEGATE.is(this,el);
	}
 }