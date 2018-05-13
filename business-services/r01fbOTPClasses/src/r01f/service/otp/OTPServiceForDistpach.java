package r01f.service.otp;


import r01f.model.otp.dispatch.OTPDispatchRequest;
import r01f.model.otp.dispatch.OTPDispatchResponse;
import r01f.model.otp.operations.OTPOperationExecResult;


public interface OTPServiceForDistpach {

	public OTPOperationExecResult<OTPDispatchResponse> dispatch(final OTPDispatchRequest  otpRequest);

}
