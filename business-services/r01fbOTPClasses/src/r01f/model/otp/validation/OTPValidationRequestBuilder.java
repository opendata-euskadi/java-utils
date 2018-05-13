package r01f.model.otp.validation;




import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.Password;
import r01f.model.otp.oids.OTPOIDs.OTPOID;
import r01f.patterns.IsBuilder;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class OTPValidationRequestBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static OTPOIDStep createForApp(final AppCode appCode) {
		OTPValidationRequest otpRequest = new OTPValidationRequest();
		otpRequest.setAppCode(appCode);
		return new OTPValidationRequestBuilder() { /* nothing */ }
					.new OTPOIDStep(otpRequest);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class OTPOIDStep {
		private final OTPValidationRequest  _otpRequest;

		public ValueStep validate(final OTPOID otpOID) {
			_otpRequest.setOid(otpOID);
			return new ValueStep(_otpRequest);
		}
		public ValueStep validateFor(final Phone phoneNumber) {
			_otpRequest.setPhone(phoneNumber);
			return new ValueStep(_otpRequest);
		}
		public ValueStep validateFor(final EMail email) {
			_otpRequest.setEmail(email);
			return new ValueStep(_otpRequest);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ValueStep {
		private final OTPValidationRequest  _otpRequest;
		public ValueStep usingValue(final Password value) {
			_otpRequest.setOtpToValidate(value);
			return this;
		}
		public OTPValidationRequest build() {
			return _otpRequest;
		}
	}

}
