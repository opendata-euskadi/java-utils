package r01f.services.users;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.springframework.mail.javamail.JavaMailSender;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import lombok.experimental.Accessors;
import r01f.api.interfaces.user.LoginNotifierServicesAPIForUser;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.security.business.SecurityBusinessOperationExecResult;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CorePersistenceServicesBase;

@Singleton
@Accessors(prefix="_")
public abstract class CoreLoginNotifierServicesImplForUser<OID_USER_CONFIG extends UserDataModelOID,
														   USER_CONFIG extends PersistableModelForUserData<OID_USER_CONFIG,USER_CONFIG>>
			  extends CorePersistenceServicesBase
		   implements LoginNotifierServicesAPIForUser<OID_USER_CONFIG,USER_CONFIG>,
					  UsersServiceImpl {

/////////////////////////////////////////////////////////////////////////////
// members
/////////////////////////////////////////////////////////////////////////////

	protected JavaMailSender _mailSender;

/////////////////////////////////////////////////////////////////////////////
// constructor
/////////////////////////////////////////////////////////////////////////////

	public CoreLoginNotifierServicesImplForUser (final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										 final Marshaller modelObjectsMarshaller,
										 final EventBus eventBus,
										 final Provider<EntityManager> entityManagerProvider,
										 final JavaMailSender mailSender)
										  {
		super( coreCfg,modelObjectsMarshaller, eventBus, entityManagerProvider);
		_mailSender = mailSender;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//METHOD TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////

	@Transactional
	@Override @SuppressWarnings("unchecked")
	public SecurityBusinessOperationExecResult<Void> notifyPasswordRecoveryRequest(final SecurityContext securityContext,
																				   final USER_CONFIG user,
																				   final String jwt) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(LoginNotifierServicesAPIForUser.class)
							.notifyPasswordRecoveryRequest(securityContext, user, jwt);
	}

}
