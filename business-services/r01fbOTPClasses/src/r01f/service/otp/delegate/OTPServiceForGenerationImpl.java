package r01f.service.otp.delegate;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import r01f.cache.DistributedCacheService;
import r01f.guids.CommonOIDs.Password;
import r01f.model.otp.OTPData;
import r01f.model.otp.OTPType;
import r01f.model.otp.oids.OTPOIDs.OTPOID;
import r01f.model.otp.oids.OTPOIDs.OTPRequestOID;
import r01f.model.otp.operations.OTPErrorType;
import r01f.model.otp.operations.OTPOperationExecResult;
import r01f.model.otp.operations.OTPOperationExecResultBuilder;
import r01f.model.otp.operations.OTPRequestedOperation;
import r01f.model.otp.request.OTPRequest;
import r01f.model.otp.request.OTPResponse;
import r01f.service.otp.OTPServiceForGeneration;

@Slf4j
public class OTPServiceForGenerationImpl
			 implements OTPServiceForGeneration {
//////////////////////////////////////////////////////////////////////////////////////////////////////
// MEMBERS
/////////////////////////////////////////////////////////////////////////////////////////////////////
	protected DistributedCacheService  _cacheService;
//////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public  OTPServiceForGenerationImpl(final DistributedCacheService cacheService){
		_cacheService = cacheService;
	}
	public OTPOperationExecResult<OTPResponse> generate( final OTPRequest otpRequest){

		if (otpRequest.getAppCode() == null) {
			return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.GENERATE.name())
											 	.because("Check the data of otp request", OTPErrorType.BAD_REQUEST_DATA);
		}

		log.debug("............Generate a otp...");
		 //0ª  Generate otp request OID if not Provided.
		if (otpRequest.getOid() == null) {
			otpRequest.setOid( OTPRequestOID.supplyOid());
		}
	    //////////// 1º Generate OTP Data
		OTPData data = new OTPData();
		OTPOID otpOid = OTPOID.supplyOid();
		data.setOid(otpOid);
		data.setAppCode(otpRequest.getAppCode());
		data.setAppComponent(otpRequest.getAppComponent());
		data.setOtpRetries(otpRequest.getOtpMaxRetries());

	    // Generate the OTP VALUE
        String otpValue = _createOTPValue(otpRequest.getOtpType(), otpRequest.getOtpLength());
        data.setValue(Password.forId(otpValue));

		//////////// 2º Store at cache
		_cacheService.getOrCreateCacheFor(OTPData.class)
					 .put(data.getOid(),
                          data,
                          otpRequest.getOtpLifeSeconds(),TimeUnit.SECONDS);
	    //////// 3º Generate Response
		OTPResponse response = new OTPResponse();
		response.setOtpOID(otpOid);
		response.setOtpRequestOID(otpRequest.getOid());
		return OTPOperationExecResultBuilder.executed(OTPRequestedOperation.GENERATE.name())
						.returning(response);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Private Methods
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String _createOTPValue(final OTPType otpType, final long length) {
		StringBuffer sb = new StringBuffer();
		SecureRandom r = new SecureRandom();
		for (int i = 0; i < length; i++) {
			int index = r.nextInt(otpType.getPattern().length());
			char c = otpType.getPattern().charAt( index );
			sb.append( c );
		}
		return sb.toString();
	}
}
