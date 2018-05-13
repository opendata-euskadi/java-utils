package r01f.model.otp.dispatch;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.AppCode;
import r01f.model.otp.oids.OTPOIDs.OTPOID;
import r01f.patterns.IsBuilder;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class OTPDispatchRequestBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static OTPOIDStep createForApp(final AppCode appCode) {
		OTPDispatchRequest otpRequest = new OTPDispatchRequest();
		otpRequest.setAppCode(appCode);
		return new OTPDispatchRequestBuilder() { /* nothing */ }
					.new OTPOIDStep(otpRequest);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class OTPOIDStep {
		private final OTPDispatchRequest  _otpRequest;

		public PresentationStep dispatch(final OTPOID otpOID) {
			_otpRequest.setOtpOid(otpOID);
			return new PresentationStep(_otpRequest);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PresentationStep {
		private final OTPDispatchRequest  _otpRequest;
		public PresentationStepViaMail usingMail() {
			_otpRequest.setPresentationData(new OTPPresentationDataMail());
			return new PresentationStepViaMail(_otpRequest);
		}
		public PresentationStepViaSMS usingSMS() {
			_otpRequest.setPresentationData(new OTPPresentationDataSms());
			return new PresentationStepViaSMS(_otpRequest);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PresentationStepViaMail {
		private final OTPDispatchRequest  _otpRequest;
		public PresentationStepViaMailSubject to(final EMail to) {
			_otpRequest.getPresentationData().asMail().setMailAddress(to);
			return new PresentationStepViaMailSubject(_otpRequest);
		}
		public PresentationStepViaMail withOTPMimeMessage(final OTPMimeMessage otpMimeMessage) {
			_otpRequest.getPresentationData().asMail().setOtpMimeMessage(otpMimeMessage);
			return  this;
		}
		public OTPDispatchRequest build() {
			return _otpRequest;
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PresentationStepViaMailSubject {
		private final OTPDispatchRequest  _otpRequest;
		public PresentationStepViaMailSubject withSubject(final String subject) {
			_otpRequest.getPresentationData().asMail().setMailSubject(subject);
			return  this;
		}
		public OTPDispatchRequest build() {
			return _otpRequest;
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class PresentationStepViaSMS {
		private final OTPDispatchRequest  _otpRequest;
		public PresentationStepViaSMS to(final Phone smsNumber) {
			_otpRequest.getPresentationData().asSMS().setSmsNumber(smsNumber);
			return this;
		}
		public OTPDispatchRequest build() {
			return _otpRequest;
		}
	}
}
