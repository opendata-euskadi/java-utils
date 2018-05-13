package r01f.service.otp;

import r01f.model.otp.dispatch.OTPDispatchRequest;
import r01f.model.otp.dispatch.OTPDispatchResponse;
import r01f.model.otp.operations.OTPOperationExecResult;
import r01f.model.otp.request.OTPRequest;
import r01f.model.otp.request.OTPResponse;
import r01f.model.otp.validation.OTPValidationRequest;
import r01f.model.otp.validation.OTPValidationResponse;


public interface OTPService {

	public OTPOperationExecResult<OTPResponse> generate(final OTPRequest  otpRequest);

	public OTPOperationExecResult<OTPDispatchResponse> dispatch(final OTPDispatchRequest  otpDispatchRequest);

	public OTPOperationExecResult<OTPValidationResponse> validate(final OTPValidationRequest  otpValidationRequest);

}
