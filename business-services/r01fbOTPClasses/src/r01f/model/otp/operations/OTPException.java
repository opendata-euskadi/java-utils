package r01f.model.otp.operations;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedRuntimeException;
import r01f.util.types.Strings;

/**
 * An error raised when performing any persistence-related operation
 */
@Accessors(prefix="_")
public class OTPException
	 extends EnrichedRuntimeException {

	private static final long serialVersionUID = -1161648233290893856L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The client's requested operation
	 */
	@Getter private final OTPRequestedOperation _requestedOperation;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected OTPException(final OTPRequestedOperation requestedOp,
						 		      	 final Throwable th) {
		super(OTPErrorType.class,
			  Strings.customized("OTP error when executing a {} operation: {}",requestedOp,th.getMessage()),
			  th,
			  OTPErrorType.SERVER_ERROR);
		_requestedOperation = requestedOp;
	}

	protected OTPException(final OTPRequestedOperation requestedOp,String requestedOpName,
						 		         final String msg,
						 		         final OTPErrorType errorType,final int extendedCode) {
		super(OTPErrorType.class,
		      Strings.customized("OTP error when executing a {} ({}): {}",
						   		 requestedOp,requestedOpName,
						   		 msg),
		      errorType,extendedCode);

		_requestedOperation = requestedOp;
	}
	protected OTPException(final OTPRequestedOperation requestedOp,
									     final String msg,
									     final OTPErrorType errorType) {
		this(requestedOp,null,
			 msg,
			 errorType,-1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public OTPErrorType getPersistenceErrorType() {
		return OTPErrorType.from(_code);
	}
	public boolean isServerError() {
		return this.is(OTPErrorType.SERVER_ERROR);
	}
	public boolean isClientError() {
		return !this.isServerError();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static OTPException serverError(final OTPRequestedOperation requestedOp) {
		return new OTPException(requestedOp,
										   	  null,
										   	  OTPErrorType.SERVER_ERROR);
	}
	public static OTPException serverError(final OTPRequestedOperation requestedOp,
														 final String msg,final Object... vars) {
		return new OTPException(requestedOp,
										   	  Strings.customized(msg,vars),
										   	  OTPErrorType.SERVER_ERROR);
	}
	public static OTPException serverError(final OTPRequestedOperation requestedOp,
													     final Throwable th) {
		return new OTPException(requestedOp,
											  th);
	}
	public static OTPException badClientRequest(final OTPRequestedOperation requestedOp) {
		return new OTPException(requestedOp,
										   	  OTPErrorType.BAD_REQUEST_DATA.name(),		// no message
										   	  OTPErrorType.BAD_REQUEST_DATA);
	}
	public static OTPException badClientRequest(final OTPRequestedOperation requestedOp,
												 		      final String msg,final Object... vars) {
		return new OTPException(requestedOp,
										   	  Strings.customized(msg,vars),
										   	  OTPErrorType.BAD_REQUEST_DATA);
	}

}
