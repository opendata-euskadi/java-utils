package r01f.model.otp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCodeAndLabel;
import r01f.enums.EnumWithCodeAndLabelWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum OTPType
 implements EnumWithCodeAndLabel<Integer,OTPType> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	DIGITS_ONLY(0,"0123456789"),
	LETTERS_ONLY(1, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
	LETTERS_ONLY_MAY(2,"ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
	LETTERS_ONLY_MIN(3,"abcdefghijklmnopqrstuvwxyz"),
	ALPHANUMERIC(4,"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
	ALPHANUMERIC_MAY(5,"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
	ALPHANUMERIC_MIN(6,"0123456789abcdefghijklmnopqrstuvwxyz");

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Integer _code;
	@Getter private final String _pattern;
	@Getter private final Class<Integer> _codeType = Integer.class;
/////////////////////////////////////////////////////////////////////////////////////////
//  WRAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	private static final EnumWithCodeAndLabelWrapper<Integer,OTPType> WRAPPER = EnumWithCodeAndLabelWrapper.wrapEnumWithCodeAndLabel(OTPType.class);

	 OTPType(final int code, final String pattern) {
		_code = new Integer(code);
		_pattern = pattern;
	}


	@Override
	public boolean isIn(final OTPType... els) {
		return WRAPPER.isIn(this,els);
	}
	public boolean isNOTIn(final OTPType... els) {
		boolean isNOT = true;
		for (OTPType el : els) {
			if (this == el) {
				isNOT = false;
				break;
			}
		}
		return isNOT;
	}
	@Override
	public boolean is(final OTPType el) {
		return WRAPPER.is(this,el);
	}
	public static final boolean canBe(final String code) {
		return WRAPPER.canBe(code);
	}
	public static final OTPType fromString(final Integer code) {
		return WRAPPER.fromCode(code);
	}


	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return _pattern;
	}


	@Override
	public boolean canBeFrom(String label) {
		return false;
	}
}
