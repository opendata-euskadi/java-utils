package r01f.services.delegates.persistence.users;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.google.common.eventbus.EventBus;

import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.user.LoginNotifierServicesAPIForUser;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.security.business.SecurityBusinessOperationExecError;
import r01f.model.security.business.SecurityBusinessOperationExecOK;
import r01f.model.security.business.SecurityBusinessOperationExecResult;
import r01f.model.security.business.SecurityBusinessPerformedOperationTypes;
import r01f.model.security.business.SecurityBusinessRequestedOperationTypes;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.ServicesDelegateBase;

@Slf4j
public class LoginNotifierServicesDelegateForUser<OID_USER_DATA extends UserDataModelOID,
												  USER_DATA extends PersistableModelForUserData<OID_USER_DATA,USER_DATA>>
			  extends ServicesDelegateBase
		   implements LoginNotifierServicesAPIForUser<OID_USER_DATA,USER_DATA> {

/////////////////////////////////////////////////////////////////////////////
//members
/////////////////////////////////////////////////////////////////////////////

	protected JavaMailSender _mailSender;

/////////////////////////////////////////////////////////////////////////////////////////
////CONSTRUCTOR   & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////

	public LoginNotifierServicesDelegateForUser(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
						final Marshaller marshaller,
						final EventBus eventBus,
						final JavaMailSender mailSender) {
		super(coreCfg,
		eventBus);
		_mailSender = mailSender;
	}

	@Override
	public SecurityBusinessOperationExecResult<Void> notifyPasswordRecoveryRequest(final SecurityContext securityContext,
																				   final USER_DATA user,
																				   final String jwt) {
		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setTo(user.getContactData().getContactInfo().getDefaultMailAddressOrAny().asString());
		simpleMessage.setSubject("PASSWORD RESET REQUEST");
		simpleMessage.setText(jwt);
		log.debug("1. >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Send WITH SIMPLE MESSAGE");
		try {
			_mailSender.send(simpleMessage);
		} catch (MailException me) {
			SecurityBusinessOperationExecError<Void> outOpError = new SecurityBusinessOperationExecError<Void>(SecurityBusinessRequestedOperationTypes.NOTIFY_PASSWORD_RECOVERY);
			outOpError.setErrorMessage(me.getLocalizedMessage());
			return outOpError;
		}
		SecurityBusinessOperationExecOK<Void> outOpOK = new SecurityBusinessOperationExecOK<Void>(SecurityBusinessRequestedOperationTypes.NOTIFY_PASSWORD_RECOVERY,
																								  SecurityBusinessPerformedOperationTypes.PASSWORD_RECOVERY_NOTIFIED);
		return outOpOK;
	}
}
