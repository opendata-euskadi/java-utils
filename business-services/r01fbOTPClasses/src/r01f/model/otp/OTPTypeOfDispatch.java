package r01f.model.otp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum OTPTypeOfDispatch
 implements EnumWithCode<Integer,OTPTypeOfDispatch> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	MAIL(0),
	SMS(1);

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Integer _code;
	@Getter private final Class<Integer> _codeType = Integer.class;
/////////////////////////////////////////////////////////////////////////////////////////
//  WRAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	private static final EnumWithCodeWrapper<Integer,OTPTypeOfDispatch> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(OTPTypeOfDispatch.class);


	@Override
	public boolean isIn(final OTPTypeOfDispatch... els) {
		return WRAPPER.isIn(this,els);
	}
	public boolean isNOTIn(final OTPTypeOfDispatch... els) {
		boolean isNOT = true;
		for (OTPTypeOfDispatch el : els) {
			if (this == el) {
				isNOT = false;
				break;
			}
		}
		return isNOT;
	}
	@Override
	public boolean is(final OTPTypeOfDispatch el) {
		return WRAPPER.is(this,el);
	}
	public static final boolean canBe(final String code) {
		return WRAPPER.canBe(code);
	}
	public static final OTPTypeOfDispatch fromString(final Integer code) {
		return WRAPPER.fromCode(code);
	}
}
