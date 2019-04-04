package r01f.services.delegates.persistence.users;

import java.util.Date;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.eventbus.EventBus;

import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.user.CRUDServicesAPIForUserData;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginData;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginEntry;
import r01f.api.interfaces.user.LoginNotifierServicesAPIForUser;
import r01f.api.interfaces.user.LoginServicesAPIForUser;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.UserCode;
import r01f.jwt.JWT;
import r01f.jwt.JWTBuilder;
import r01f.jwt.JWTDecoded;
import r01f.jwt.parser.JWTParser;
import r01f.jwt.parser.JWTParserBuilder;
import r01f.jwt.verifier.JWTVerificationResult;
import r01f.jwt.verifier.JWTVerificationResultAsNotValidToken;
import r01f.jwt.verifier.JWTVerificationResultInvalidCause;
import r01f.jwt.verifier.JWTVerifier;
import r01f.jwt.verifier.JWTVerifierBuilder;
import r01f.model.persistence.CRUDError;
import r01f.model.persistence.CRUDResult;
import r01f.model.security.business.SecurityBusinessOperationErrorType;
import r01f.model.security.business.SecurityBusinessOperationExecResult;
import r01f.model.security.business.SecurityBusinessOperationExecResultBuilder;
import r01f.model.security.business.SecurityBusinessPerformedOperationTypes;
import r01f.model.security.business.SecurityBusinessRequestedOperationTypes;
import r01f.model.security.login.PasswordRecoveryNotificationType;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.login.recovery.PasswordRecoveryRequest;
import r01f.model.security.login.recovery.PasswordRecoveryResponseErrorType;
import r01f.model.security.login.recovery.PasswordRecoveryResponseWithToken;
import r01f.model.security.login.request.LoginRequest;
import r01f.model.security.login.request.LoginRequestWithUserCodeBase;
import r01f.model.security.login.reset.PasswordResetRequestBase;
import r01f.model.security.login.reset.PasswordResetResponseBase;
import r01f.model.security.login.reset.PasswordResetResponseErrorType;
import r01f.model.security.login.response.LoginResponse;
import r01f.model.security.login.response.LoginResponseErrorType;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginEntryModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.ServicesDelegateBase;
import r01f.services.users.utils.PasswordAuthentication;

/**********************************************************************************
 * Delegate implementation
 **********************************************************************************/
@Slf4j
public abstract class LoginServicesDelegateForUser<OID_USER_DATA extends UserDataModelOID,
												   OID_USER_LOGIN extends  UserLoginDataModelOID,
												   OID_USER_LOGIN_ENTRY extends  UserLoginEntryModelOID,
												   USER_DATA extends PersistableModelForUserData<OID_USER_DATA,USER_DATA>,
												   USER_LOGIN extends PersistableModelForUserLoginData<OID_USER_LOGIN,USER_LOGIN>,
												   USER_LOGIN_ENTRY extends PersistableModelForUserLoginEntry<OID_USER_LOGIN_ENTRY,USER_LOGIN_ENTRY>,
												   L extends LoginRequest,
												   R extends LoginResponse,
												   PRQ extends PasswordRecoveryRequest,
												   PRP extends PasswordRecoveryResponseWithToken,
												   PRSQ extends PasswordResetRequestBase,
												   PRSP extends PasswordResetResponseBase>
			  extends ServicesDelegateBase
		   implements LoginServicesAPIForUser<L,R,PRQ,PRP,PRSQ,PRSP> {

/////////////////////////////////////////////////////////////////////////////
//members
/////////////////////////////////////////////////////////////////////////////

	protected CRUDServicesAPIForUserData<OID_USER_DATA,USER_DATA>  _crudServicesAPIForUserData;
	protected CRUDServicesAPIForUserLoginData<OID_USER_LOGIN,USER_LOGIN>  _crudServicesAPIForUserLogin;
	protected CRUDServicesAPIForUserLoginEntry<OID_USER_LOGIN_ENTRY,USER_LOGIN_ENTRY>  _crudServicesAPIForUserLoginEntry;

	protected LoginNotifierServicesAPIForUser<OID_USER_DATA,USER_DATA> _notifier;

	protected AppCode _appCode;

/////////////////////////////////////////////////////////////////////////////////////////
// //CONSTRUCTOR   & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////

	public LoginServicesDelegateForUser(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										final Marshaller marshaller,
										final EventBus eventBus,
										final CRUDServicesAPIForUserData<OID_USER_DATA,USER_DATA> crudServicesAPIForUserConfig,
										final CRUDServicesAPIForUserLoginData<OID_USER_LOGIN,USER_LOGIN>  crudServicesAPIForUserLogin,
										final CRUDServicesAPIForUserLoginEntry<OID_USER_LOGIN_ENTRY,USER_LOGIN_ENTRY>  crudServicesAPIForUserLoginEntry,
										final LoginNotifierServicesAPIForUser<OID_USER_DATA,USER_DATA> notifier) {
		super(coreCfg,
			  eventBus);
		_crudServicesAPIForUserData = crudServicesAPIForUserConfig;
		_crudServicesAPIForUserLogin = crudServicesAPIForUserLogin;
		_crudServicesAPIForUserLoginEntry = crudServicesAPIForUserLoginEntry;
		_notifier = notifier;
		_appCode = coreCfg.getCoreAppCode();
	}

/////////////////////////////////////////////////////////////////////////////////////////
//// METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public SecurityBusinessOperationExecResult<R> login(final SecurityContext securityContext,
												final L loginRequest) {
		R loginResponse = null;
		try {
			USER_LOGIN loginDataForRequest = null;
			if ( loginRequest instanceof LoginRequestWithUserCodeBase ){
				LoginRequestWithUserCodeBase loginRequestBasedOnUserCode  = (LoginRequestWithUserCodeBase) loginRequest;
				CRUDResult<USER_LOGIN> loginDataCRUDResult =
						_crudServicesAPIForUserLogin.loadByUserCode(securityContext, loginRequestBasedOnUserCode.getUserCode());
				if (loginDataCRUDResult.hasFailed()){
					if (loginDataCRUDResult.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
						loginResponse = _buildLoginResponse(loginRequest, LoginResponseErrorType.INVALID_USER_CODE);
						return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																		 .executed(SecurityBusinessRequestedOperationTypes.LOGIN_REQUEST,
																				   SecurityBusinessPerformedOperationTypes.LOGIN_NOT_DONE)
																		 .returning(loginResponse);
					} else {
						return SecurityBusinessOperationExecResultBuilder.using(securityContext)
								 .notExecuted(SecurityBusinessRequestedOperationTypes.LOGIN_REQUEST)
								 	.because(loginDataCRUDResult.asCRUDError().getErrorMessage(),
								 			 _getOperationErrorResultFromCRUDResult(loginDataCRUDResult.asCRUDError()));
					}
				} else {
					loginDataForRequest = loginDataCRUDResult.getOrThrow();
				}
			} else {
				log.error("Must provide a method to access Login Data based on Login Request Interface, or just override this method");
				return SecurityBusinessOperationExecResultBuilder.using(securityContext)
						 .notExecuted(SecurityBusinessRequestedOperationTypes.LOGIN_REQUEST)
						 	.because("LoginRequest of type "+loginRequest.getClass().getName()+" is not valid",
						 			SecurityBusinessOperationErrorType.SERVER_ERROR);
			}

			PasswordAuthentication pAuth = new PasswordAuthentication();
			SecurityBusinessPerformedOperationTypes performedOperation = null;
			if (loginDataForRequest == null){
				log.warn("No login data for this request");
				loginResponse = _buildLoginResponse(loginRequest,LoginResponseErrorType.INVALID_USER_CODE);
				performedOperation = SecurityBusinessPerformedOperationTypes.LOGIN_NOT_DONE;
			} else if (loginDataForRequest.getExpiratingPwdAt() != null
					&& loginDataForRequest.getExpiratingPwdAt().before( new Date())) {
				log.warn("Password is expired");
				loginResponse =  _buildLoginResponse(loginRequest,LoginResponseErrorType.PASSWORD_EXPIRED);
				performedOperation = SecurityBusinessPerformedOperationTypes.LOGIN_NOT_DONE;
			} else if (! pAuth.authenticate(loginRequest.getPassword().getId().toCharArray(),
											loginDataForRequest.getPassword().getId())) {
				log.warn("Password does not  match");
				loginResponse =  _buildLoginResponse(loginRequest,LoginResponseErrorType.PASSWORD_ERROR);
				performedOperation = SecurityBusinessPerformedOperationTypes.LOGIN_NOT_DONE;
			} else {
				log.warn(">>> Let's build login response !");
				// Build login response based un some user data
				UserCode userCode = loginDataForRequest.getUserCode();
				USER_DATA user = _crudServicesAPIForUserData.loadByUserCode(securityContext,userCode).getOrThrow();
				loginResponse = _buildLoginResponse(loginRequest,user);
				performedOperation = SecurityBusinessPerformedOperationTypes.LOGIN_DONE;
				log.debug(">>> Built login response {}!", loginResponse);
			}
			return SecurityBusinessOperationExecResultBuilder.using(securityContext)
													 .executed(SecurityBusinessRequestedOperationTypes.LOGIN_REQUEST,
															   performedOperation)
													 .returning(loginResponse);
		} finally {
			sendLoginResponseEvent(loginResponse);
		}
	}


	@Override
	public SecurityBusinessOperationExecResult<PRP> passwordRecoveryRequest(final SecurityContext securityContext,
															final PRQ request) {
		PRP passwordRecoveryResponse = null;
		try {
			if ( request.getUserCode() != null ){
				CRUDResult<USER_LOGIN> loginDataCRUDResult =
											_crudServicesAPIForUserLogin.loadByUserCode(securityContext, request.getUserCode());
				if (loginDataCRUDResult.hasFailed()){
					if (loginDataCRUDResult.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
						passwordRecoveryResponse = _buildPasswordRecoveryResponse(request, PasswordRecoveryResponseErrorType.INVALID_USER_CODE);
						return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST,
																		   SecurityBusinessPerformedOperationTypes.PASSWORD_RECOVERY_NOT_NOTIFIED)
																 .returning(passwordRecoveryResponse);
					} else {
						return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																 .notExecuted(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST)
																 .because(loginDataCRUDResult.asCRUDError().getErrorMessage(),
																 		  _getOperationErrorResultFromCRUDResult(loginDataCRUDResult.asCRUDError()));
					}
				} else {
					USER_LOGIN userLoginData = loginDataCRUDResult.getOrThrow();
					CRUDResult<USER_DATA> userDataCRUDResult = _crudServicesAPIForUserData.loadByUserCode(securityContext,request.getUserCode());

					if (userDataCRUDResult.hasFailed()){
						if (userDataCRUDResult.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
							passwordRecoveryResponse = _buildPasswordRecoveryResponse(request, PasswordRecoveryResponseErrorType.INVALID_USER_CODE);
							return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																	 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST,
																			   SecurityBusinessPerformedOperationTypes.PASSWORD_RECOVERY_NOT_NOTIFIED)
																	 .returning(passwordRecoveryResponse);
						} else {
							return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																	 .notExecuted(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST)
																	 .because(loginDataCRUDResult.asCRUDError().getErrorMessage(),
																	 		  _getOperationErrorResultFromCRUDResult(loginDataCRUDResult.asCRUDError()));
						}
					} else {
						USER_DATA user = _crudServicesAPIForUserData.loadByUserCode(securityContext,request.getUserCode()).getOrThrow();

						if (userLoginData.getMandatoryInPasswordRecoveryRequest()!=null
								&& !PasswordRecoveryNotificationType.NONE.equals(userLoginData.getMandatoryInPasswordRecoveryRequest())) {
							if (PasswordRecoveryNotificationType.EMAIL.equals(userLoginData.getMandatoryInPasswordRecoveryRequest())) {
								if (request.getEmail()==null
										|| !request.getEmail().asString().equals(user.getContactData().getContactInfo().getDefaultMailAddressOrAny().asString())) {
									passwordRecoveryResponse = _buildPasswordRecoveryResponse(request, PasswordRecoveryResponseErrorType.INVALID_NOTIFICATION_DATA);
									return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																			 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST,
																					   SecurityBusinessPerformedOperationTypes.PASSWORD_RECOVERY_NOT_NOTIFIED)
																			 .returning(passwordRecoveryResponse);
								}
							} else if (PasswordRecoveryNotificationType.PHONE.equals(userLoginData.getMandatoryInPasswordRecoveryRequest())) {
								if (request.getPhone()==null
										|| !request.getPhone().asString().equals(user.getContactData().getContactInfo().getDefaultPhoneOrAny().asString())) {
									passwordRecoveryResponse = _buildPasswordRecoveryResponse(request, PasswordRecoveryResponseErrorType.INVALID_NOTIFICATION_DATA);
									return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																			 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST,
																					   SecurityBusinessPerformedOperationTypes.PASSWORD_RECOVERY_NOT_NOTIFIED)
																			 .returning(passwordRecoveryResponse);
								}
							} else {
								passwordRecoveryResponse = _buildPasswordRecoveryResponse(request, PasswordRecoveryResponseErrorType.INVALID_NOTIFICATION_TYPE);
								return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																		 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST,
																				   SecurityBusinessPerformedOperationTypes.PASSWORD_RECOVERY_NOT_NOTIFIED)
																		 .returning(passwordRecoveryResponse);
							}
						}

						JWT jwt = JWTBuilder.createJWToken()
											.forIssuer(_appCode)
											.toAudience(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST.getName())
											.withExpirationSeconds(1800L)
											.withJWTId(request.getUserCode().getId())
											.withKey(userLoginData.getPassword().asString().getBytes())
											.build();
						log.info(">>>> JWT : {}",jwt.asString());
						log.debug(">>>>>>>> Dispatch SIMPLE MESSAGE");
						_notifier.notifyPasswordRecoveryRequest(securityContext, user, jwt.toString());
						passwordRecoveryResponse = _buildPasswordRecoveryResponse(request);
						passwordRecoveryResponse.setToken(jwt.toString());
						return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST,
																		   SecurityBusinessPerformedOperationTypes.PASSWORD_RECOVERY_NOTIFIED)
																 .returning(_buildPasswordRecoveryResponse(request));
					}
				}

			} else {

				log.error("UserCode can't be null");
				return SecurityBusinessOperationExecResultBuilder.using(securityContext)
														 .notExecuted(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST)
														 .because("UserCode can't be null",
																 SecurityBusinessOperationErrorType.BAD_REQUEST_DATA);

			}
		} finally {
			sendPasswordRecoveryResponseEvent(passwordRecoveryResponse);
		}
	}

	@Override
	public SecurityBusinessOperationExecResult<PRSP> passwordResetRequest(final SecurityContext securityContext,
														  final PRSQ request) {
		PRSP passwordResetResponse = null;
		try {
			if ( request.getUserCode() != null ){
				CRUDResult<USER_LOGIN> loginDataCRUDResult =
											_crudServicesAPIForUserLogin.loadByUserCode(securityContext, request.getUserCode());
				if (loginDataCRUDResult.hasFailed()){
					if (loginDataCRUDResult.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
						passwordResetResponse = _buildPasswordResetResponse(request, PasswordResetResponseErrorType.INVALID_USER_CODE);
						return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RESET_REQUEST,
																		   SecurityBusinessPerformedOperationTypes.PASSWORD_RESET_NOT_DONE)
																 .returning(passwordResetResponse);
					} else {
						return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																 .notExecuted(SecurityBusinessRequestedOperationTypes.PASSWORD_RESET_REQUEST)
																 .because(loginDataCRUDResult.asCRUDError().getErrorMessage(),
																 		  _getOperationErrorResultFromCRUDResult(loginDataCRUDResult.asCRUDError()));
					}
				} else {
					USER_LOGIN userLoginData = loginDataCRUDResult.getOrThrow();

					// Obtener el LoginEntry
					CRUDResult<USER_LOGIN_ENTRY> paswordChangeRequestCRUDResult = _crudServicesAPIForUserLoginEntry.loadForPasswordReset(securityContext, request.getUserCode(), request.getToken());
					if (paswordChangeRequestCRUDResult.hasFailed()){
						if (paswordChangeRequestCRUDResult.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
							passwordResetResponse = _buildPasswordResetResponse(request, PasswordResetResponseErrorType.UNREGISTERED_RESET_REQUEST);
							return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																	 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RESET_REQUEST,
																			   SecurityBusinessPerformedOperationTypes.PASSWORD_RESET_NOT_DONE)
																	 .returning(passwordResetResponse);
						} else {
							return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																	 .notExecuted(SecurityBusinessRequestedOperationTypes.PASSWORD_RESET_REQUEST)
																	 .because(loginDataCRUDResult.asCRUDError().getErrorMessage(),
																	 		  _getOperationErrorResultFromCRUDResult(loginDataCRUDResult.asCRUDError()));
						}
					} else {
						JWT signedJWT = JWT.forValue(request.getToken());
						JWTParser parserJWT = JWTParserBuilder.createParser().build();
						JWTDecoded decodedJWT = parserJWT.parse(signedJWT);

						byte[] sharedSecret = userLoginData.getPassword().asString().getBytes();

						JWTVerifier verifier = JWTVerifierBuilder.createVerifier()
								 .usingKey(sharedSecret)
								 .checkingExpiration()
								 .forAudience(SecurityBusinessRequestedOperationTypes.PASSWORD_RECOVERY_REQUEST.getName())
								 .build();
						JWTVerificationResult isJwtVerified = verifier.verify(signedJWT);

						String jwtIssuer = decodedJWT.getPayload()!=null?decodedJWT.getPayload().getIssuer():null;

						if (isJwtVerified.isNotValidJWT() || !_appCode.getId().equals(jwtIssuer)) {
							JWTVerificationResultAsNotValidToken notValidJWT = isJwtVerified.as(JWTVerificationResultAsNotValidToken.class);
							String errors = FluentIterable.from(notValidJWT.getCause())
														  .transform(new Function<JWTVerificationResultInvalidCause,String>(){
																public String apply(JWTVerificationResultInvalidCause obj) {
																	return obj.name();
																}
															}).toString();
							if (JWTVerificationResultInvalidCause.INVALID_SIGNATURE.name().equals(errors)) {
								passwordResetResponse = _buildPasswordResetResponse(request, PasswordResetResponseErrorType.TOKEN_WITH_INVALID_SIGNATURE);
							} else if  (JWTVerificationResultInvalidCause.INVALID_AUDIENCE.name().equals(errors)) {
								passwordResetResponse = _buildPasswordResetResponse(request, PasswordResetResponseErrorType.TOKEN_WITH_INVALID_AUDIENCE);
							} else if  (JWTVerificationResultInvalidCause.EXPIRED.name().equals(errors)) {
								passwordResetResponse = _buildPasswordResetResponse(request, PasswordResetResponseErrorType.TOKEN_EXPIRED);
							} else {
								passwordResetResponse = _buildPasswordResetResponse(request, PasswordResetResponseErrorType.TOKEN_WITH_OTHER_ERROR);
							}
							return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																	 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RESET_REQUEST,
																			   SecurityBusinessPerformedOperationTypes.PASSWORD_RESET_NOT_DONE)
																	 .returning(passwordResetResponse);
						} else {
							CRUDResult<USER_LOGIN> paswordChangedCRUDResult = _crudServicesAPIForUserLogin.updatePassword(securityContext, request.getUserCode(), request.getPassword());

							if (paswordChangedCRUDResult.hasFailed()){
								return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																		 .notExecuted(SecurityBusinessRequestedOperationTypes.PASSWORD_RESET_REQUEST)
																		 .because(loginDataCRUDResult.asCRUDError().getErrorMessage(),
																				  _getOperationErrorResultFromCRUDResult(paswordChangedCRUDResult.asCRUDError()));
							}
							passwordResetResponse = _buildPasswordResetResponse(request);
							if (passwordResetResponse.getPasswordUpdateDone()) {
								_crudServicesAPIForUserLoginEntry.updateAfterPasswordReset(securityContext, request.getUserCode(), request.getToken());
							}
							return SecurityBusinessOperationExecResultBuilder.using(securityContext)
																	 .executed(SecurityBusinessRequestedOperationTypes.PASSWORD_RESET_REQUEST,
																			   SecurityBusinessPerformedOperationTypes.PASSWORD_RESET_DONE)
																	 .returning(passwordResetResponse);
						}
					}
				}

			} else {
				log.error("UserCode can't be null");
				return SecurityBusinessOperationExecResultBuilder.using(securityContext)
														 .notExecuted(SecurityBusinessRequestedOperationTypes.PASSWORD_RESET_REQUEST)
														 .because("UserCode can't be null",
																 SecurityBusinessOperationErrorType.BAD_REQUEST_DATA);
			}
		} finally {
			sendPasswordResetResponseEvent(passwordResetResponse);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
//// EVENTS METHODS, OVERRIDE IF NECESARY
/////////////////////////////////////////////////////////////////////////////////////////

	protected void sendLoginResponseEvent(final R loginResponse) {
		if (loginResponse != null) {
			_eventBus.post(loginResponse);
		}
	}

	protected void sendPasswordRecoveryResponseEvent(final PRP passwordRecoveryResponse) {
		if (passwordRecoveryResponse != null) {
			_eventBus.post(passwordRecoveryResponse);
		}
	}

	protected void sendPasswordResetResponseEvent(final PRSP passwordResetResponse) {
		if (passwordResetResponse != null) {
			_eventBus.post(passwordResetResponse);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
//// PROTECTED METHODS METHODS
/////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("rawtypes")
	protected SecurityBusinessOperationErrorType _getOperationErrorResultFromCRUDResult(final CRUDError crudError) {
		if (crudError.wasBecauseAClientError()) {
			return SecurityBusinessOperationErrorType.BAD_REQUEST_DATA;
		} else if (crudError.wasBecauseAnOptimisticLockingError()) {
			return SecurityBusinessOperationErrorType.SERVER_ERROR;
		} else if (crudError.wasBecauseAServerError()) {
			return SecurityBusinessOperationErrorType.SERVER_ERROR;
		} else if (crudError.wasBecauseClientCouldNotConnectToServer()) {
			return SecurityBusinessOperationErrorType.CLIENT_CANNOT_CONNECT_SERVER;
		} else if (crudError.wasBecauseClientRequestedEntityValidationErrors()) {
			return SecurityBusinessOperationErrorType.ILLEGAL_STATUS;
		} else if (crudError.wasBecauseClientRequestedEntityWasInAnIllegalStatus()) {
			return SecurityBusinessOperationErrorType.ILLEGAL_STATUS;
		}
		return SecurityBusinessOperationErrorType.UNKNOWN;
	}

	protected abstract R _buildLoginResponse(final L loginRequest , final USER_DATA user);

	protected abstract R _buildLoginResponse(final L loginRequest , final LoginResponseErrorType errorType);

	protected abstract PRP _buildPasswordRecoveryResponse(final PRQ request);

	protected abstract PRP _buildPasswordRecoveryResponse(final PRQ request , final PasswordRecoveryResponseErrorType errorType);

	protected abstract PRSP _buildPasswordResetResponse(final PRSQ request);

	protected abstract PRSP _buildPasswordResetResponse(final PRSQ request , final PasswordResetResponseErrorType errorType);

}