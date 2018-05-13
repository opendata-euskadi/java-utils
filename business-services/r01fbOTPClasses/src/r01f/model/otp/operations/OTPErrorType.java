package r01f.model.otp.operations;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableSubType;
import r01f.exceptions.EnrichedThrowableSubTypeWrapper;
import r01f.exceptions.ExceptionSeverity;
import r01f.exceptions.Throwables;

/**
 * otp Operations error codes
 */
@Accessors(prefix="_")
public enum OTPErrorType
 implements EnrichedThrowableSubType<OTPErrorType> {

	// Bad Request Data for otp functions
	BAD_REQUEST_DATA(100+1),

	// OTP Generation Error.
	OTP_GENERATION_ERROR(100 +2),
	
	// OTP Validation
	OTP_VALIDATION_ERROR_OTP_NOT_VALID(200 +1),
	OTP_VALIDATION_ERROR_OTP_DOES_NOT_EXISTS_OR_EXPIRED(200 +2),
	OTP_VALIDATION_ERROR_OTP_NUMBER_OF_ATTEMPTS_EXCEEDED(200 +3),
	
	// OTP Dispataching
	UNKNOWN_METHOD_OF_OTP_DISPATCHING (300+1),
	OTP_DISPATCHING_ERROR_OTP_DOES_NOT_EXISTS_OR_EXPIRED(300 +2),
	
	SERVER_ERROR(500+1),
	UNKNOWN(500+2);



	public static final transient int OTP = 100;	// change enums if this changes

	@Getter private final int _group = OTP;
	@Getter private final int _code;

	private OTPErrorType(final int code) {
		_code = code;
	}

	private static EnrichedThrowableSubTypeWrapper<OTPErrorType> WRAPPER = EnrichedThrowableSubTypeWrapper.create(OTPErrorType.class);

	public static OTPErrorType from(final int errorCode) {
		return WRAPPER.from(OTP,errorCode);
	}
	public static OTPErrorType from(final int groupCode,final int errorCode) {
		if (groupCode != OTP) throw new IllegalArgumentException(Throwables.message("The group code for a {} MUST be {}",
																							OTPException.class,OTP));
		return WRAPPER.from(OTP,errorCode);
	}
	public static OTPErrorType fromName(final String name) {
		return WRAPPER.fromName(name);
	}
	@Override
	public boolean is(final int group,final int code) {
		return WRAPPER.is(this,
						  group,code);
	}
	public boolean is(final int code) {
		return this.is(OTP,code);
	}
	@Override
	public boolean isIn(final OTPErrorType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final OTPErrorType el) {
		return WRAPPER.is(this,el);
	}
	public boolean isServerError() {
		return this == SERVER_ERROR;
	}
	public boolean isClientError() {
		return !this.isServerError();
	}
	@Override
	public ExceptionSeverity getSeverity() {
		ExceptionSeverity outSeverity = null;
		switch(this) {
		case BAD_REQUEST_DATA:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case OTP_GENERATION_ERROR:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case SERVER_ERROR:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		case UNKNOWN:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		default:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		}
		return outSeverity;
	}
}