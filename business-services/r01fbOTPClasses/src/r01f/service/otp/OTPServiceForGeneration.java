package r01f.service.otp;

import r01f.model.otp.operations.OTPOperationExecResult;
import r01f.model.otp.request.OTPRequest;
import r01f.model.otp.request.OTPResponse;


public interface OTPServiceForGeneration {

	public OTPOperationExecResult<OTPResponse> generate( final OTPRequest  otpRequest);

}
