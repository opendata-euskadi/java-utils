package r01f.service.otp;

import r01f.model.otp.operations.OTPOperationExecResult;
import r01f.model.otp.validation.OTPValidationRequest;
import r01f.model.otp.validation.OTPValidationResponse;


public interface OTPServiceForValidation {


	public OTPOperationExecResult<OTPValidationResponse> validate( final OTPValidationRequest  otpRequest);

}
