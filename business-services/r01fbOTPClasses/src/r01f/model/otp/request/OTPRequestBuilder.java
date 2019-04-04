package r01f.model.otp.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.model.otp.OTPType;
import r01f.model.otp.oids.OTPOIDs.OTPRequestOID;
import r01f.patterns.IsBuilder;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class OTPRequestBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static OTPTypeStep createForApp(final AppCode appCode) {
		OTPRequest otpRequest = new OTPRequest();
		otpRequest.setAppCode(appCode);
		return new OTPRequestBuilder() { /* nothing */ }
					.new OTPTypeStep(otpRequest);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class OTPTypeStep {
		private final OTPRequest  _otpRequest;

		public TimeOfLifeStep ofType(final OTPType otpType) {
			_otpRequest.setOtpType(otpType);
			return new TimeOfLifeStep(_otpRequest);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class TimeOfLifeStep {
		private final OTPRequest  _otpRequest;

		public MaxNumOfRetriesStep withSecondOfLife(final long secondOfLife) {
			_otpRequest.setOtpLifeSeconds(secondOfLife);
			return new MaxNumOfRetriesStep(_otpRequest);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class MaxNumOfRetriesStep {
		private final OTPRequest  _otpRequest;

		public LengthStep andMaxNumOfRetries(final int numMaxOfRetries) {
			_otpRequest.setOtpMaxRetries(numMaxOfRetries);
			return new LengthStep(_otpRequest);
		}

	}

	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class LengthStep {
		private final OTPRequest  _otpRequest;
		public LengthStep withLength(final long length) {
			_otpRequest.setOtpLength(length);
			return this;
		}
		public OTPRequest build() {
			if (_otpRequest.getOid() == null) _otpRequest.setOid(OTPRequestOID.supplyOid());
			return _otpRequest;
		}
	}

}
