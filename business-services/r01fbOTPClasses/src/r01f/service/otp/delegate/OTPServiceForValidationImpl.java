package r01f.service.otp.delegate;


import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import r01f.cache.DistributedCacheService;
import r01f.guids.CommonOIDs.AppCode;
import r01f.model.otp.OTPData;
import r01f.model.otp.operations.OTPErrorType;
import r01f.model.otp.operations.OTPOperationExecResult;
import r01f.model.otp.operations.OTPOperationExecResultBuilder;
import r01f.model.otp.operations.OTPRequestedOperation;
import r01f.model.otp.validation.OTPValidationRequest;
import r01f.model.otp.validation.OTPValidationResponse;
import r01f.service.otp.OTPServiceForValidation;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@Slf4j
public class OTPServiceForValidationImpl
			 implements OTPServiceForValidation {
////////////////////////////////////////////////////////////////////////////////////////////////////
// MEMBERS
///////////////////////////////////////////////////////////////////////////////////////////////////
	protected DistributedCacheService  _cacheService;
////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
//////////////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public  OTPServiceForValidationImpl(final DistributedCacheService cacheService) {
		_cacheService = cacheService;
	}
	@Override
	public OTPOperationExecResult<OTPValidationResponse> validate(final OTPValidationRequest otpValidationRequest) {

		OTPData otpData = null;
	    if (otpValidationRequest.getOid() != null ) {
			otpData =_cacheService.getOrCreateCacheFor(OTPData.class)
								  .get(otpValidationRequest.getOid());
	    } else  if (otpValidationRequest.getPhone() != null) {
	    	otpData = _getOTPDataFromCacheByPhoneNumber(otpValidationRequest.getAppCode(), otpValidationRequest.getPhone());
	    } else  if (otpValidationRequest.getEmail() != null) {
	    	otpData = _getOTPDataFromCacheByMail(otpValidationRequest.getAppCode(), otpValidationRequest.getEmail());
	    } else {
	    	log.debug("Bad Request Data Missing Parameters for ValidationRequest");
	    	return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.VALIDATION.name())
									 	        .because("Bad Request Data Missing Parameters for ValidationRequest, this otp  does not exist o has expired!", OTPErrorType.BAD_REQUEST_DATA);

	    }
		if (otpData == null) {
			return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.VALIDATION.name())
											 	.because("OTP is expired or does not exists!", OTPErrorType.OTP_VALIDATION_ERROR_OTP_DOES_NOT_EXISTS_OR_EXPIRED);
	    } else {
		   if ( otpData.getOtpRetries() == 0) {
			    log.debug("OTP number of retries expired");
			   	return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.VALIDATION.name())
										 	        .because("Number of attempts exceeded !", OTPErrorType.OTP_VALIDATION_ERROR_OTP_NUMBER_OF_ATTEMPTS_EXCEEDED);

		   } else {
				if (otpData.getValue().equals(otpValidationRequest.getOtpToValidate())) {
					OTPValidationResponse response = new OTPValidationResponse();
					response.setValidOtp(true);
					response.setOtpValidationRetriesToExpire(otpData.getOtpRetries());
					//Remove from cache after suceed otp validation,
					 _cacheService.getOrCreateCacheFor(OTPData.class)
					 			  .remove(otpValidationRequest.getOid());
					return OTPOperationExecResultBuilder.executed(OTPRequestedOperation.VALIDATION.name())
															.returning(response);
				} else {
					long numberOfRetries = otpData.decrementNumberOfRetries();
					_cacheService.getOrCreateCacheFor(OTPData.class)
								 .replace(otpData.getOid(),
										  otpData);
					OTPValidationResponse response = new OTPValidationResponse();
					response.setValidOtp(false);
					response.setOtpValidationRetriesToExpire(numberOfRetries);
					return OTPOperationExecResultBuilder.executed(OTPRequestedOperation.VALIDATION.name())
														.returning(response);
				}
		   }
	   }
	}
//////////////////////////////////////////////////////////////////////////////////////////////////
// PRIVATE METHODS.
/////////////////////////////////////////////////////////////////////////////////////////////////
   public OTPData _getOTPDataFromCacheByPhoneNumber(final AppCode appCode, final Phone phone) {
	  return FluentIterable.from(_cacheService.getOrCreateCacheFor(OTPData.class)
			  								  .getAll().values())
		                                .filter(new Predicate<OTPData>() {
										        @Override
										        public boolean apply(final OTPData input) {
										            return  input.getAppCode() != null
										            		     && input.getAppCode().equals(appCode)
										            		&& input.getPhone()  != null
										            	     	&& input.getPhone().asString().equals(phone.asString());
										        }
										        })
		                           .first().orNull();
   }

    public OTPData _getOTPDataFromCacheByMail(final AppCode appCode, final EMail email) {
	  return FluentIterable.from(_cacheService.getOrCreateCacheFor(OTPData.class)
			  								  .getAll().values())
		                                .filter(new Predicate<OTPData>() {
										        @Override
										        public boolean apply(final OTPData input) {
										            return  input.getAppCode() != null
										            			&& input.getAppCode().equals(appCode)
										            		&& input.getEmail() != null
										            			&& input.getEmail().asString().equalsIgnoreCase(email.asString());
										        }
										        })
		                           .first().orNull();
   }

}
