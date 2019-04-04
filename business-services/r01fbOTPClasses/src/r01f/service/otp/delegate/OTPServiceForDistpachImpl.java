package r01f.service.otp.delegate;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import r01f.cache.DistributedCacheService;
import r01f.guids.CommonOIDs.AppCode;
import r01f.model.latinia.LatiniaRequestMessage;
import r01f.model.otp.OTPData;
import r01f.model.otp.OTPTypeOfDispatch;
import r01f.model.otp.dispatch.OTPDispatchRequest;
import r01f.model.otp.dispatch.OTPDispatchResponse;
import r01f.model.otp.dispatch.OTPPresentationDataMail;
import r01f.model.otp.dispatch.OTPPresentationDataSms;
import r01f.model.otp.operations.OTPErrorType;
import r01f.model.otp.operations.OTPOperationExecResult;
import r01f.model.otp.operations.OTPOperationExecResultBuilder;
import r01f.model.otp.operations.OTPRequestedOperation;
import r01f.service.otp.OTPServiceForDistpach;
import r01f.services.latinia.LatiniaService;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@Slf4j
public class OTPServiceForDistpachImpl
  implements OTPServiceForDistpach {
////////////////////////////////////////////////////////////////////////////////////////////////////
// MEMBERS
///////////////////////////////////////////////////////////////////////////////////////////////////
	protected DistributedCacheService  _cacheService;
	protected JavaMailSender _mailSender;
	protected LatiniaService _latiniaService;
////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public  OTPServiceForDistpachImpl(final DistributedCacheService cacheService, final JavaMailSender mailSender, final LatiniaService latiniaService ) {
		_cacheService = cacheService;
		_mailSender = mailSender;
		_latiniaService = latiniaService;
	}
	@Override
	public OTPOperationExecResult<OTPDispatchResponse> dispatch(final OTPDispatchRequest dispatchRequest) {
		OTPData otpData = _cacheService.getOrCreateCacheFor(OTPData.class)
									   .get(dispatchRequest.getOtpOid());
		if (otpData == null) {
			return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.DISPATCH.name())
											 	.because("OTP is expired or does not exists!", OTPErrorType.OTP_DISPATCHING_ERROR_OTP_DOES_NOT_EXISTS_OR_EXPIRED);
		}
		//// Dispatch OTP via mail
		if ( dispatchRequest.getPresentationData().getPresentationDataType().equals(OTPTypeOfDispatch.MAIL)) {
			
			try {
				OTPPresentationDataMail  otpPresentationDataMail =  (OTPPresentationDataMail) dispatchRequest.getPresentationData();
				
				if (otpPresentationDataMail.getOtpMimeMessage() != null) {
					log.debug(">>>>>>>> Dispatch MIMEMESSAGE");
					MimeMessage msg = otpPresentationDataMail.getOtpMimeMessage().toMimmeMessage(otpData.getValue().asString());
					log.debug("1. >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Send WITH MIMEMESSAGE");
					_mailSender.send(msg);
				}else{
					log.debug(">>>>>>>> Dispatch SIMPLE MESSAGE");
					SimpleMailMessage simpleMessage = new SimpleMailMessage();
					simpleMessage.setTo(otpPresentationDataMail.getMailAddress());
					simpleMessage.setSubject(otpPresentationDataMail.getMailSubject());
					simpleMessage.setText(otpData.getValue().asString());
					log.debug("1. >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Send WITH SIMPLE MESSAGE");
					_mailSender.send(simpleMessage);
				}
				
				//Store mail in cache, but before if exists another not expired otp with this mail will be removed
				log.debug("2. >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Check");
				OTPData previousOTPForSameMail  = _getOTPDataFromCacheByMail(dispatchRequest.getAppCode(),
																			 EMail.create(otpPresentationDataMail.asMail().getMailAddress()));
				if (previousOTPForSameMail != null) {
					log.warn("Some previous non expired otp exists for this mail, this will be removed !");
					System.out.println("Some previous non expired otp exists for this mail, this will be removed !");
					_cacheService.getOrCreateCacheFor(OTPData.class)
								 .remove(previousOTPForSameMail.getOid());
				} else {
					log.debug(" No found with mail " + otpPresentationDataMail.asMail().getMailAddress());
				}
				log.debug("3. >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Cache with mail");
				otpData.setEmail(EMail.create(otpPresentationDataMail.getMailAddress()));
				_cacheService.getOrCreateCacheFor(OTPData.class)
							 .getAndReplace(otpData.getOid(), otpData);
				
				//Compose Response
				OTPDispatchResponse response = new OTPDispatchResponse();
				response.setDispatchOk(true);
				log.debug("4. >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  Return otp dispatch result");
				return OTPOperationExecResultBuilder.executed(OTPRequestedOperation.DISPATCH.name())
												 			.returning(response);
			} catch (Exception ex ) {
				ex.printStackTrace();
				return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.DISPATCH.name())
											 			.because(ex.getLocalizedMessage(), OTPErrorType.SERVER_ERROR);
			} catch (Throwable ex ) {
				ex.printStackTrace();
				return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.DISPATCH.name())
											 			.because(ex.getLocalizedMessage(), OTPErrorType.SERVER_ERROR);
			}
			
		//// Dispatch OTP via SMS
		} else 	if (dispatchRequest.getPresentationData().getPresentationDataType().equals(OTPTypeOfDispatch.SMS)) {
			try {
				
				OTPPresentationDataSms  otpPresentationDataSMS =  (OTPPresentationDataSms) dispatchRequest.getPresentationData();
				LatiniaRequestMessage msg = new LatiniaRequestMessage();
				msg.setReceiverNumbers(otpPresentationDataSMS.getSmsNumber());
				msg.setMessageContent(otpData.getValue().asString());
				log.debug(">>>>>>>>>>>>>>>>>>>> Dispatch OTP via SMS to phone number {} and value {}", otpPresentationDataSMS.getSmsNumber(),
						                                                                              otpData.getValue());
				_latiniaService.sendNotification(msg);
				 log.debug("Send SMS OK via Latinia");
				//Store phone in cache, but before if exists another not expired otp with this mail will be removed
				 OTPData previousOTPForSamePhone  = _getOTPDataFromCacheByPhoneNumber(dispatchRequest.getAppCode(),
						 															  Phone.create(otpPresentationDataSMS.getSmsNumber()));
				if (previousOTPForSamePhone != null) {
					log.warn("Some previous non expired otp exists for this phone, this will be removed!");
					_cacheService.getOrCreateCacheFor(OTPData.class)
								  .remove(previousOTPForSamePhone.getOid());
				}
				
				otpData.setPhone(Phone.create(otpPresentationDataSMS.getSmsNumber()));
				_cacheService.getOrCreateCacheFor(OTPData.class)
							 .getAndReplace(otpData.getOid(), otpData);
				//Compose response
				OTPDispatchResponse response = new OTPDispatchResponse();
				response.setDispatchOk(true);
				return OTPOperationExecResultBuilder.executed(OTPRequestedOperation.DISPATCH.name())
													.returning(response);
			} catch (Exception ex ) {
				ex.printStackTrace();
				return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.DISPATCH.name())
											 		.because(ex.getLocalizedMessage(),OTPErrorType.SERVER_ERROR);
			} catch (Throwable ex ) {
				ex.printStackTrace();
				return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.DISPATCH.name())
											 		.because(ex.getLocalizedMessage(), OTPErrorType.SERVER_ERROR);
			}
			
		}else {
			return OTPOperationExecResultBuilder.notExecuted(OTPRequestedOperation.DISPATCH.name())
												.because("Unknown method of otp dipataching..!", OTPErrorType.UNKNOWN_METHOD_OF_OTP_DISPATCHING);
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////
//PRIVATE METHODS.
/////////////////////////////////////////////////////////////////////////////////////////////////
   public OTPData _getOTPDataFromCacheByPhoneNumber(final AppCode appCode, final Phone phone) {
	   if (_cacheService.getOrCreateCacheFor(OTPData.class).isNullOrEmpty()) {
    	  return null;
      } else {
		  return FluentIterable.from(_cacheService.getOrCreateCacheFor(OTPData.class)
				  								  .getAll().values())
			                                .filter(new Predicate<OTPData>() {
											        @Override
											        public boolean apply(final OTPData input) {
											            return  input.getAppCode().equals(appCode)
											            		&& input.getPhone() != null
											            		&& input.getPhone().asString().equals(phone.asString());
											        }
											        })
			                           .first().orNull();
      }
   }

    public OTPData _getOTPDataFromCacheByMail(final AppCode appCode, final EMail email) {
      if (_cacheService.getOrCreateCacheFor(OTPData.class).isNullOrEmpty()) {
    	  return null;
      } else {
		  return FluentIterable.from(_cacheService.getOrCreateCacheFor(OTPData.class)
				  								  .getAll().values())
			                                .filter(new Predicate<OTPData>() {
											        @Override
											        public boolean apply(final OTPData input) {
											            return  input.getAppCode().equals(appCode)
											            		&& input.getEmail() != null
											            		&& input.getEmail().asString().equalsIgnoreCase(email.asString());
											        }
											        })
			                           .first().orNull();
      }
   }

}
