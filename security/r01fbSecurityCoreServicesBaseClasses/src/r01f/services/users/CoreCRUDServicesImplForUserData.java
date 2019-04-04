package r01f.services.users;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserData;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CoreCRUDServicesForModelObjectBase;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@Singleton
@Accessors(prefix="_")
public abstract class CoreCRUDServicesImplForUserData<O extends UserDataModelOID,
													  U extends PersistableModelForUserData<O,U>>
			  extends CoreCRUDServicesForModelObjectBase<O,U>
		   implements CRUDServicesAPIForUserData<O,U>,
					  UsersServiceImpl{

	public CoreCRUDServicesImplForUserData (final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											final Marshaller modelObjectsMarshaller,
											final EventBus eventBus,
											final Provider<EntityManager> entityManagerProvider) {
		super( coreCfg,modelObjectsMarshaller, eventBus, entityManagerProvider);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//EXTENSION  METHODS
/////////////////////////////////////////////////////////////////////////////////////////

	@Transactional
	@Override @SuppressWarnings("unchecked")
	public CRUDResult<U> loadByUserCode(final SecurityContext securityContext,
										final UserCode userCode) {

		CRUDResult<U> outResult = this.forSecurityContext(securityContext)
									  .createDelegateAs(CRUDServicesAPIForUserData.class)
									  .loadByUserCode(securityContext,userCode);
		return outResult;
	}

	@Transactional
	@Override @SuppressWarnings("unchecked")
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
								final EMail email) {
		CRUDResult<U> outResult = this.forSecurityContext(securityContext)
									  .createDelegateAs(CRUDServicesAPIForUserData.class)
									  .loadBy(securityContext,email);
		return outResult;
	}

	@Transactional
	@Override
	public CRUDResult<U> loadOrNull(final SecurityContext securityContext,
									final EMail email) {
		CRUDResult<U> outResult = this.loadBy(securityContext, email);
		if (outResult.hasFailed() && outResult.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
			return null;
		}
		return outResult;
	}

	@Transactional
	@Override @SuppressWarnings("unchecked")
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
								final Phone phone) {
		CRUDResult<U> outResult = this.forSecurityContext(securityContext)
									  .createDelegateAs(CRUDServicesAPIForUserData.class)
									  .loadBy(securityContext,phone);
		return outResult;
	}

	@Transactional
	@Override
	public CRUDResult<U> loadOrNull(final SecurityContext securityContext,
									final Phone phone) {
		CRUDResult<U> outResult = this.loadBy(securityContext, phone);
		if (outResult.hasFailed() && outResult.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
			return null;
		}
		return outResult;
	}
}
