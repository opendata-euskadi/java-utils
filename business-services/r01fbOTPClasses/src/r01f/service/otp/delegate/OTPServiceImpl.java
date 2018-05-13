package r01f.service.otp.delegate;

import com.google.inject.Inject;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.model.otp.dispatch.OTPDispatchRequest;
import r01f.model.otp.dispatch.OTPDispatchResponse;
import r01f.model.otp.operations.OTPOperationExecResult;
import r01f.model.otp.request.OTPRequest;
import r01f.model.otp.request.OTPResponse;
import r01f.model.otp.validation.OTPValidationRequest;
import r01f.model.otp.validation.OTPValidationResponse;
import r01f.service.otp.OTPService;
import r01f.service.otp.OTPServiceForDistpach;
import r01f.service.otp.OTPServiceForGeneration;
import r01f.service.otp.OTPServiceForValidation;


@Accessors(prefix="_")
@NoArgsConstructor
public  class OTPServiceImpl
        implements OTPService {

//////////////////////////////////////////////////////////////////////////////////////
// INJECTED MEMBERS
/////////////////////////////////////////////////////////////////////////////////////
	@Inject OTPServiceForGeneration _serviceForGeneration;
	@Inject OTPServiceForValidation _serviceForValidation;
	@Inject OTPServiceForDistpach  _serviceForDistpach;

//////////////////////////////////////////////////////////////////////////////////////
//METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////
	public OTPOperationExecResult<OTPResponse> generate( final OTPRequest  otpRequest){
		return _serviceForGeneration.generate(otpRequest);

	}

	public OTPOperationExecResult<OTPDispatchResponse> dispatch(final OTPDispatchRequest  otpRequest){
		return _serviceForDistpach.dispatch( otpRequest);

	}

	public OTPOperationExecResult<OTPValidationResponse> validate( final OTPValidationRequest  otpRequest){
		return _serviceForValidation.validate(otpRequest);

	}

}
