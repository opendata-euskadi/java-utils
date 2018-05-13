package r01f.model.otp.dispatch;

import r01f.model.otp.OTPTypeOfDispatch;
import r01f.types.contact.Phone;

public class OTPPresentationDataSms extends OTPPresentationData {

	private static final long serialVersionUID = 9089528607072006335L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String SMS_NUMBER = "smsNumber";
	private static final String SMS_TEXT = "smsText";
	private static final String SMS_ACK = "smsAck";
	private static final String SMS_EXPIRE_TIME = "smsExpireTime";
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public OTPPresentationDataSms() {
		_presentationDataType = OTPTypeOfDispatch.SMS;
	}
	public OTPPresentationDataSms(final OTPPresentationData presentationData) {
		this();
		if (presentationData.isFillAditionalData(SMS_NUMBER)) {
			this.setSmsNumber(Phone.of(presentationData.getAditionalDataAsString(SMS_NUMBER)));
		}
		if (presentationData.isFillAditionalData(SMS_TEXT)) {
			this.setSmsText(presentationData.getAditionalDataAsString(SMS_TEXT));
		}
	}
	public OTPPresentationDataSms(final Phone number) {
		this();
		this.setSmsNumber(number);
	}
	public OTPPresentationDataSms(final Phone number,final String text) {
		this(number);
		this.setSmsText(text);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public String getSmsNumber() {
		return this.getAditionalDataAsString(SMS_NUMBER);
	}
	public void setSmsNumber(final Phone phone) {
		this.setAditionalDataFromString(SMS_NUMBER,phone.asString());
	}
	public boolean isFillSmsNumber() {
		return this.isFillAditionalData(SMS_NUMBER);
	}

	public String getSmsText() {
		return this.getAditionalDataAsString(SMS_TEXT);
	}
	public void setSmsText(String subject) {
		this.setAditionalDataFromString(SMS_TEXT, subject);
	}
	public boolean isFillSmsText() {
		return this.isFillAditionalData(SMS_TEXT);
	}

	public String getSmsAck() {
		return this.getAditionalDataAsString(SMS_ACK);
	}
	public void setSmsAck(String ack) {
		this.setAditionalDataFromString(SMS_ACK, ack);
	}
	public boolean isFillSmsAck() {
		return this.isFillAditionalData(SMS_ACK);
	}

	public String getSmsExpireTime() {
		return this.getAditionalDataAsString(SMS_EXPIRE_TIME);
	}
	public void setSmsExpireTime(String expireTime) {
		this.setAditionalDataFromString(SMS_EXPIRE_TIME, expireTime);
	}
	public boolean isFillSmsExpireTime() {
		return this.isFillAditionalData(SMS_EXPIRE_TIME);
	}
}