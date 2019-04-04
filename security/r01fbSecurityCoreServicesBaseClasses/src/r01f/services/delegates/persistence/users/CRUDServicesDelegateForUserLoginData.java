package r01f.services.delegates.persistence.users;

import com.google.common.eventbus.EventBus;

import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginData;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBCRUDForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.CRUDServicesForModelObjectDelegateBase;
import r01f.services.users.utils.PasswordAuthentication;

/**********************************************************************************
 * Delegate implementation
 **********************************************************************************/
public abstract class CRUDServicesDelegateForUserLoginData<O extends UserLoginDataModelOID,
															L extends PersistableModelForUserLoginData<O,L>>
			  extends CRUDServicesForModelObjectDelegateBase<O,L>
		   implements CRUDServicesAPIForUserLoginData<O,L> {

/////////////////////////////////////////////////////////////////////////////////////////
// //CONSTRUCTOR   & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////

	public CRUDServicesDelegateForUserLoginData(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												 final Class<L> modelObjectType,
												 final DBCRUDForModelObject<O,L> dbCrud,
												 final Marshaller marshaller,
												 final EventBus eventBus) {
		super(coreCfg,
			  modelObjectType,
			  dbCrud,
			  eventBus);
	}

/////////////////////////////////////////////////////////////////////////////////////////
// OVERRIDE TO MANAGE PASSWORD HASH
///////////////////////////////////////////////////////////////////////////////////////

	@Override
	public CRUDResult<L> create(final SecurityContext securityContext,
								final L modelObj) {
		PasswordAuthentication pAuth = new PasswordAuthentication();
		// Password is hashed before stored
		modelObj.getPassword().setId(pAuth.hash(modelObj.getPassword().getId().toCharArray()));
		CRUDResult<L> createdObject = super.create(securityContext, modelObj);
		if (createdObject.hasSucceeded()) {
			createdObject.getOrThrow().setPassword(null);
		}
		return createdObject;
	}

	@Override
	public CRUDResult<L> update(final SecurityContext securityContext,
								final L modelObj) {
		// Password can't be updated with this function
		L entityToUpdate = super.load(securityContext, modelObj.getOid()).getOrThrow();
		modelObj.setPassword(entityToUpdate.getPassword());
		CRUDResult<L> updatedObject = super.update(securityContext, modelObj);
		if (updatedObject.hasSucceeded()) {
			updatedObject.getOrThrow().setPassword(null);
		}
		return updatedObject;
	}

///////////////////////////////////////////////////////////////////////////////////////
// EXTENDION METHODS
///////////////////////////////////////////////////////////////////////////////////////

	@Override
	public CRUDResult<L> loadByUserCode(final SecurityContext securityContext,
										final UserCode userCode) {
		if (userCode == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The entity id MUST not be null")
									.build();
		}
		@SuppressWarnings("unchecked")
		CRUDResult<L> outResult = this.getServiceImplAs(CRUDServicesAPIForUserLoginData.class)
												.loadByUserCode(securityContext,
														userCode);
		return outResult;
	}

	@Override
	public CRUDResult<L> updatePassword(final SecurityContext securityContext,
										final UserCode userCode,
										final Password password) {
		if (userCode == null || password == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The entity userCode and password MUST not be null")
									.build();
		}
		L entityToUpdate = this.loadByUserCode(securityContext, userCode).getOrThrow();
		PasswordAuthentication pAuth = new PasswordAuthentication();
		// Password is hashed before stored
		entityToUpdate.getPassword().setId(pAuth.hash(password.getId().toCharArray()));
		return super.update(securityContext, entityToUpdate);
	}

}
