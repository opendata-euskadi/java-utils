package r01f.model.otp.dispatch;

import r01f.model.otp.OTPTypeOfDispatch;
import r01f.types.contact.EMail;

public class OTPPresentationDataMail extends OTPPresentationData {

	private static final long serialVersionUID = 7773402530304647369L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String MAIL_ADDRESS = "mailAddress";
	private static final String MAIL_SUBJECT = "mailSubject";
	private static final String MAIL_TEMPLATE = "mailTemplate";
	private static final String MAIL_TEXT = "mailText";
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public OTPPresentationDataMail() {
		_presentationDataType = OTPTypeOfDispatch.MAIL;
	}
	public OTPPresentationDataMail(final OTPPresentationData presentationData) {
		this();
		if (presentationData.isFillAditionalData(MAIL_ADDRESS)) {
			this.setMailAddress(EMail.of(presentationData.getAditionalDataAsString(MAIL_ADDRESS)));
		}
		if (presentationData.isFillAditionalData(MAIL_SUBJECT)) {
			this.setMailSubject(presentationData.getAditionalDataAsString(MAIL_SUBJECT));
		}
		if (presentationData.isFillAditionalData(MAIL_TEMPLATE)) {
			this.setMailTemplate(presentationData.getAditionalDataAsString(MAIL_TEMPLATE));
		}
		if (presentationData.isFillAditionalData(MAIL_TEXT)) {
			this.setMailText(presentationData.getAditionalDataAsString(MAIL_TEXT));
		}
	}
	public OTPPresentationDataMail(final EMail mail,
								   final String subject) {
		this();
		this.setMailAddress(mail);
		this.setMailSubject(subject);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public String getMailAddress() {
		return this.getAditionalDataAsString(MAIL_ADDRESS);
	}
	public void setMailAddress(final EMail mail) {
		this.setAditionalDataFromString(MAIL_ADDRESS,mail.asString());
	}
	public boolean isFillMailAddress() {
		return this.isFillAditionalData(MAIL_ADDRESS);
	}
	public String getMailSubject() {
		return this.getAditionalDataAsString(MAIL_SUBJECT);
	}
	public void setMailSubject(String subject) {
		this.setAditionalDataFromString(MAIL_SUBJECT,subject);
	}
	public boolean isFillMailSubject() {
		return this.isFillAditionalData(MAIL_SUBJECT);
	}
	public String getMailTemplate() {
		return this.getAditionalDataAsString(MAIL_TEMPLATE);
	}
	public void setMailTemplate(String template) {
		this.setAditionalDataFromString(MAIL_TEMPLATE, template);
	}
	public boolean isFillMailTemplate() {
		return this.isFillAditionalData(MAIL_TEMPLATE);
	}
	public String getMailText() {
		return this.getAditionalDataAsString(MAIL_TEXT);
	}
	public void setMailText(String text) {
		this.setAditionalDataFromString(MAIL_TEXT, text);
	}
	public boolean isFillMailText() {
		return this.isFillAditionalData(MAIL_TEXT);
	}
}