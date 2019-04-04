package r01f.services.users;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserData;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginData;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginEntry;
import r01f.api.interfaces.user.LoginNotifierServicesAPIForUser;
import r01f.api.interfaces.user.LoginServicesAPIForUser;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.security.business.SecurityBusinessOperationExecResult;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.login.recovery.PasswordRecoveryRequest;
import r01f.model.security.login.recovery.PasswordRecoveryResponse;
import r01f.model.security.login.request.LoginRequest;
import r01f.model.security.login.reset.PasswordResetRequestBase;
import r01f.model.security.login.reset.PasswordResetResponseBase;
import r01f.model.security.login.response.LoginResponse;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginEntryModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CorePersistenceServicesBase;

@Singleton
@Accessors(prefix="_")
public abstract class CoreLoginServicesImplForUser<OID_USER_CONFIG extends UserDataModelOID,
												   OID_USER_LOGIN extends  UserLoginDataModelOID,
												   OID_USER_LOGIN_ENTRY extends  UserLoginEntryModelOID,
												   USER_CONFIG extends PersistableModelForUserData<OID_USER_CONFIG,USER_CONFIG>,
												   USER_LOGIN extends PersistableModelForUserLoginData<OID_USER_LOGIN,USER_LOGIN>,
												   USER_LOGIN_ENTRY extends PersistableModelForUserLoginEntry<OID_USER_LOGIN_ENTRY,USER_LOGIN_ENTRY>,
												   L extends LoginRequest,
												   R extends LoginResponse,
												   PRQ extends PasswordRecoveryRequest,
												   PRP extends PasswordRecoveryResponse,
												   PRSQ extends PasswordResetRequestBase,
												   PRSP extends PasswordResetResponseBase>
			  extends CorePersistenceServicesBase
		   implements LoginServicesAPIForUser<L,R,PRQ,PRP,PRSQ,PRSP>,
					  UsersServiceImpl {

/////////////////////////////////////////////////////////////////////////////
// members
/////////////////////////////////////////////////////////////////////////////

	protected CRUDServicesAPIForUserData<OID_USER_CONFIG,USER_CONFIG>  _crudServicesAPIForUserData;
	protected CRUDServicesAPIForUserLoginData<OID_USER_LOGIN,USER_LOGIN>  _crudServicesAPIForUserLogin;
	protected CRUDServicesAPIForUserLoginEntry<OID_USER_LOGIN_ENTRY,USER_LOGIN_ENTRY>  _crudServicesAPIForUserLoginEntry;

	protected LoginNotifierServicesAPIForUser<OID_USER_CONFIG,USER_CONFIG> _notifier;

/////////////////////////////////////////////////////////////////////////////
// constructor
/////////////////////////////////////////////////////////////////////////////

	public CoreLoginServicesImplForUser (final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										 final Marshaller modelObjectsMarshaller,
										 final EventBus eventBus,
										 final Provider<EntityManager> entityManagerProvider,
										 final CRUDServicesAPIForUserData<OID_USER_CONFIG,USER_CONFIG>  crudServicesAPIForUserConfig,
										 final CRUDServicesAPIForUserLoginData<OID_USER_LOGIN,USER_LOGIN>  crudServicesAPIForUserLogin,
										 final CRUDServicesAPIForUserLoginEntry<OID_USER_LOGIN_ENTRY,USER_LOGIN_ENTRY>  crudServicesAPIForUserLoginEntry,
										 final LoginNotifierServicesAPIForUser<OID_USER_CONFIG,USER_CONFIG> notifier)
										  {
		super( coreCfg,modelObjectsMarshaller, eventBus, entityManagerProvider);
		_crudServicesAPIForUserData = crudServicesAPIForUserConfig;
		_crudServicesAPIForUserLogin = crudServicesAPIForUserLogin;
		_crudServicesAPIForUserLoginEntry = crudServicesAPIForUserLoginEntry;
		_notifier = notifier;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//METHOD TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////

	@Transactional
	@Override @SuppressWarnings("unchecked")
	public SecurityBusinessOperationExecResult<R> login(final SecurityContext securityContext,
														final L authorizationRequestObject) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(LoginServicesAPIForUser.class)
							.login(securityContext, authorizationRequestObject);
	}

	@Transactional
	@Override @SuppressWarnings("unchecked")
	public SecurityBusinessOperationExecResult<PRP> passwordRecoveryRequest(final SecurityContext securityContext,
																			final PRQ request) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(LoginServicesAPIForUser.class)
							.passwordRecoveryRequest(securityContext, request);
	}

	@Transactional
	@Override @SuppressWarnings("unchecked")
	public SecurityBusinessOperationExecResult<PRSP> passwordResetRequest(final SecurityContext securityContext,
																		  final PRSQ request) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(LoginServicesAPIForUser.class)
							.passwordResetRequest(securityContext, request);
	}

}
