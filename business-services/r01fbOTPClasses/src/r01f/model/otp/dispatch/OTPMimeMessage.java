package r01f.model.otp.dispatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.mail.javamail.MimeMessageHelper;

import com.google.common.base.Function;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;


@MarshallType(as="otpMimeMessage")
@Accessors(prefix="_")
@NoArgsConstructor
public class OTPMimeMessage implements Serializable {

	private static final long serialVersionUID = 7245147531805456171L;

	public static final String OTP_CODE_KEY = "$otpCodeForTemplate$";
	public static final String SUBTYPE_RELATED = "related";

	//////////////////////////////////////////////////////////////////////////
	// Members
	///////////////////////////////////////////////////////////////////////
	@Setter @Getter String _to;
	@Setter @Getter String _from;
	@Setter @Getter String _subject;

	@Setter @Getter String _subType;

	@Setter @Getter OTPMimeBodyPart _mainBodyPart;
	@Setter @Getter String _alternativeBodyPart = null;
	
	@Setter @Getter List<OTPMimeBodyPart> _mimeBodyPartList = new ArrayList<OTPMimeBodyPart>();

	//////////////////////////////////////////////////////////////////////////
	//Public Methods
	///////////////////////////////////////////////////////////////////////
	public void addMimeBodyPart(final OTPMimeBodyPart mimeBodyPart){
		_mimeBodyPartList.add(mimeBodyPart);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// Inner Classes
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	@Accessors(prefix="_")
	@NoArgsConstructor
	public static class OTPMimeBodyPart {

		public static final String DISPOSITION_INLINE = "inline";
		public static final String DISPOSITION_ATTACHMENT = "attachment";

		public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
		public static final String CONTENT_TYPE_IMAGE_PNG = "image/png";

		@Setter @Getter String _diposition;
		@Setter @Getter String _content;
		@Setter @Getter String _contentType;
		@Setter @Getter String _contentID;
		@Setter @Getter byte[] _dataHandlerBytes;

		/**
		 * Converts to a standard mime body part
		 * @return
		 */
		public MimeBodyPart toMimeBodyPart(){
			Function<OTPMimeBodyPart, MimeBodyPart> transform = new Function<OTPMimeBodyPart, MimeBodyPart>(){
				@Override
				public MimeBodyPart apply(final OTPMimeBodyPart input){
					try {
						MimeBodyPart bodyPart = new MimeBodyPart();
						if(CONTENT_TYPE_TEXT_HTML.equals(input.getContentType())){
							//IS MAIN TEXT BODY PART
							bodyPart.setDisposition(input.getDiposition());
							bodyPart.setContent(input.getContent(), input.getContentType());
						}else if(CONTENT_TYPE_IMAGE_PNG.equals(input.getContentType())){
							ByteArrayDataSource rawData= new ByteArrayDataSource(input.getDataHandlerBytes(), input.getContentType());
							DataHandler data= new DataHandler(rawData);
							bodyPart.setDataHandler(data);
							bodyPart.setContentID("<"+input.getContentID()+">");
							bodyPart.setDisposition(input.getDiposition());
						}
						return bodyPart;
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			return transform.apply(this);
		}
	}
	
	/**
	 * Converts to a standard mime message
	 * @return
	 */
	public MimeMessage toMimmeMessage(final String otpValue){
		if(Strings.isNOTNullOrEmpty(otpValue)){
			if(_mainBodyPart != null && Strings.isNOTNullOrEmpty(_mainBodyPart._content)){
				_mainBodyPart._content = _mainBodyPart._content.replace(OTP_CODE_KEY, otpValue);
			}
			if (_alternativeBodyPart != null && Strings.isNOTNullOrEmpty(_alternativeBodyPart)){
				_alternativeBodyPart = _alternativeBodyPart.replace(OTP_CODE_KEY, otpValue);
			}
		}
		Function<OTPMimeMessage, MimeMessage> transform = new Function<OTPMimeMessage, MimeMessage>(){
			@Override
			public MimeMessage apply(final OTPMimeMessage input) {
				try{
					Properties props = new Properties();
					Session session = Session.getInstance(props);
					
					MimeMessage mimeMessage = new MimeMessage(session);
					
					MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
					
					if (_alternativeBodyPart != null && Strings.isNOTNullOrEmpty(_alternativeBodyPart)){
						messageHelper.setText(_alternativeBodyPart, true);
					}
					
					messageHelper.getMimeMultipart().setSubType(input.getSubType());
					
					//SEND DATA
					messageHelper.setFrom(input.getFrom());
					messageHelper.setTo(input.getTo());
					messageHelper.setSubject(input.getSubject());
					
					//BODY PARTS
					messageHelper.getMimeMultipart().addBodyPart(input.getMainBodyPart().toMimeBodyPart());
					
					for(OTPMimeBodyPart otpMimeBodyPart : input.getMimeBodyPartList()){
						messageHelper.getMimeMultipart().addBodyPart(otpMimeBodyPart.toMimeBodyPart());
					}
					
					return messageHelper.getMimeMessage();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		return transform.apply(this);
	}
	
//	/**
//	 * Replace OTP Key with Value
//	 * @return
//	 */
//	public void replaceOtpValue(final String otpValue){
//		if(Strings.isNOTNullOrEmpty(otpValue)){
//			if(_mainBodyPart != null && Strings.isNOTNullOrEmpty(_mainBodyPart._content)){
//				_mainBodyPart._content = _mainBodyPart._content.replace(OTP_CODE_KEY, otpValue);
//			}
//			if (_alternativeBodyPart != null && Strings.isNOTNullOrEmpty(_alternativeBodyPart)){
//				_mainBodyPart.
//				_alternativeBodyPart = _alternativeBodyPart.replace(OTP_CODE_KEY, otpValue);
//			}
//		}
//	}
}