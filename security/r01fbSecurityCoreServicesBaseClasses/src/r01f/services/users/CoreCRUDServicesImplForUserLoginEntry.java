package r01f.services.users;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;

import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginEntry;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginEntryModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CoreCRUDServicesForModelObjectBase;

@Singleton
@Accessors(prefix="_")
public abstract class CoreCRUDServicesImplForUserLoginEntry<O extends UserLoginEntryModelOID,
															L extends PersistableModelForUserLoginEntry<O,L>>
			  extends CoreCRUDServicesForModelObjectBase<O,L>
		   implements CRUDServicesAPIForUserLoginEntry<O,L>,
					  UsersServiceImpl {

	public CoreCRUDServicesImplForUserLoginEntry(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
													 final Marshaller modelObjectsMarshaller,
													 final EventBus eventBus,
													 final Provider<EntityManager> entityManagerProvider) {
		super( coreCfg,modelObjectsMarshaller, eventBus, entityManagerProvider);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// EXTENSION METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override @SuppressWarnings("unchecked")
	public CRUDResult<L> loadForPasswordReset(final SecurityContext securityContext,
											  final UserCode userCode,
											  final String token) {
		return this.forSecurityContext(securityContext)
				.createDelegateAs(CRUDServicesAPIForUserLoginEntry.class)
					.loadForPasswordReset(securityContext,userCode, token);
	}

	@Override @SuppressWarnings("unchecked")
	public CRUDResult<L> updateAfterPasswordReset(final SecurityContext securityContext,
												   final UserCode userCode,
												   final String token) {
		return this.forSecurityContext(securityContext)
				.createDelegateAs(CRUDServicesAPIForUserLoginEntry.class)
					.updateAfterPasswordReset(securityContext,userCode, token);
	}

}

