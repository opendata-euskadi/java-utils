package r01f.services.users;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginData;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.services.persistence.CoreCRUDServicesForModelObjectBase;

@Singleton
@Accessors(prefix="_")
public abstract class CoreCRUDServicesImplForUserLoginData<O extends UserLoginDataModelOID,
														   L extends PersistableModelForUserLoginData<O,L>>
			  extends CoreCRUDServicesForModelObjectBase<O,L>
		   implements CRUDServicesAPIForUserLoginData<O,L>,
		   			  UsersServiceImpl {

	public CoreCRUDServicesImplForUserLoginData(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												final Marshaller modelObjectsMarshaller,
												final EventBus eventBus,
												final Provider<EntityManager> entityManagerProvider) {
		super( coreCfg,modelObjectsMarshaller, eventBus, entityManagerProvider);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// FOR PASSWORD HASH STORE
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Transactional
	@Override @SuppressWarnings("unchecked")
	public CRUDResult<L> create(final SecurityContext securityContext,
								final L record) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(CRUDServicesForModelObject.class)
							.create(securityContext,
									record);
	}


///////////////////////////////////////////////////////////////////////////////////////////////////////////
// EXTENSION METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override @SuppressWarnings("unchecked")
	public CRUDResult<L> loadByUserCode(final SecurityContext securityContext,
										final UserCode userCode) {
		return this.forSecurityContext(securityContext)
				.createDelegateAs(CRUDServicesAPIForUserLoginData.class)
					.loadByUserCode(securityContext,userCode);
	}


	@Override @SuppressWarnings("unchecked")
	public CRUDResult<L> updatePassword(final SecurityContext securityContext,
										final UserCode userCode,
										final Password password) {
		return this.forSecurityContext(securityContext)
				.createDelegateAs(CRUDServicesAPIForUserLoginData.class)
					.updatePassword(securityContext,userCode,password);
	}

}

