package r01f.model.otp.operations;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;

/**
 * OTPRequestedOperation
 */
public enum OTPRequestedOperation
 implements EnumExtended<OTPRequestedOperation> {
	GENERATE,
	VALIDATION,
	DISPATCH,
	OTHER;
///////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static final EnumExtendedWrapper<OTPRequestedOperation> WRAPPER = EnumExtendedWrapper.wrapEnumExtended(OTPRequestedOperation.class);

	public static OTPRequestedOperation fromName(final String name) {
		return WRAPPER.fromName(name);
	}

	@Override
	public boolean isIn(final OTPRequestedOperation... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final OTPRequestedOperation el) {
		return WRAPPER.is(this,el);
	}
	public static boolean canBe(final String name) {
		return WRAPPER.canBe(name);
	}
}