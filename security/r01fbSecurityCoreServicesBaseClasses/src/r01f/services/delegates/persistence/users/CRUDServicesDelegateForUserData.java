package r01f.services.delegates.persistence.users;

import com.google.common.eventbus.EventBus;

import r01f.api.interfaces.user.CRUDServicesAPIForUserData;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.model.security.user.UserDataValidator;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBCRUDForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.CRUDServicesForModelObjectDelegateBase;
import r01f.services.delegates.persistence.ValidatesModelObjectBeforeCreateOrUpdate;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

/**********************************************************************************
 * Delegate implementation
 **********************************************************************************/
public abstract class CRUDServicesDelegateForUserData<O extends UserDataModelOID,
													  U extends PersistableModelForUserData<O,U>>
			  extends CRUDServicesForModelObjectDelegateBase<O,U>
		   implements CRUDServicesAPIForUserData<O,U>,
		   			  ValidatesModelObjectBeforeCreateOrUpdate<U> {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR   & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////

	public CRUDServicesDelegateForUserData(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										   final Class<U> modelObjectType,
										   final DBCRUDForModelObject<O,U> dbCrud,
										   final Marshaller marshaller,
										   final EventBus eventBus){
		super(coreCfg,
			  modelObjectType,
			  dbCrud,
			  eventBus);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////

	@Override @SuppressWarnings("unchecked")
	public CRUDResult<U> loadByUserCode(final SecurityContext securityContext,
										final UserCode userCode) {
		if (userCode == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The entity id MUST not be null")
									.build();
		}
		CRUDResult<U> outResult = this.getServiceImplAs(CRUDServicesAPIForUserData.class)
									  .loadByUserCode(securityContext,
											  		  userCode);
		return outResult;
	}

	@Override @SuppressWarnings("unchecked")
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
								final EMail email) {
		if (email == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The email to load user MUST not be null")
									.build();
		}
		CRUDResult<U> outResult = this.getServiceImplAs(CRUDServicesAPIForUserData.class)
									  .loadBy(securityContext,
											  email);
		return outResult;
	}

	@Override
	public CRUDResult<U> loadOrNull(final SecurityContext securityContext,
									final EMail email) {
		CRUDResult<U> outResult = this.loadBy(securityContext, email);
		if (outResult.hasFailed() && outResult.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
			return null;
		}
		return outResult;
	}

	@Override @SuppressWarnings("unchecked")
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
								final Phone phone) {
		if (phone == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The phone to load user MUST not be null")
									.build();
		}
		CRUDResult<U> outResult = this.getServiceImplAs(CRUDServicesAPIForUserData.class)
									  .loadBy(securityContext,
											  phone);
		return outResult;
	}

	@Override
	public CRUDResult<U> loadOrNull(final SecurityContext securityContext,
									final Phone phone) {
		CRUDResult<U> outResult = this.loadBy(securityContext, phone);
		if (outResult.hasFailed() && outResult.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
			return null;
		}
		return outResult;
	}


/////////////////////////////////////////////////////////////////////////////////////////
//PARAMS VALIDATION ON CREATION / UPDATE
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ObjectValidationResult<U> validateModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
																		  final PersistenceRequestedOperation requestedOp,
																		  final U owner) {
		// [1]: Do base validations
		ObjectValidationResult<U> outValid = new UserDataValidator<O,U>().validate(owner);

		// [2]: Ensure there does NOT exist another owner with the same userCode
		if (outValid.isValid()
				&& requestedOp.is(PersistenceRequestedOperation.CREATE)) {
			CRUDResult<U> byUserCodeLoadResult = this.loadByUserCode(securityContext,
																	owner.getUserCode());
			if (byUserCodeLoadResult.hasSucceeded()) {
				return ObjectValidationResultBuilder.on(owner)
													.notValidwithErrorCode(100)
													.isNotValidBecause("There exists another api key owner with the same userCode={}",owner.getUserCode());
			}
			// Phone and Mail validations may be null in some UserData so dont do any validation
		}
		// [3]: Return
		return outValid;
	}

}
