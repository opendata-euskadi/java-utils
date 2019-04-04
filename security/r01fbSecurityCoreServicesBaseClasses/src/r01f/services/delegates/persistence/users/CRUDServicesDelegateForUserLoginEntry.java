package r01f.services.delegates.persistence.users;

import com.google.common.eventbus.EventBus;

import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginEntry;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginEntryModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBCRUDForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.CRUDServicesForModelObjectDelegateBase;

/**********************************************************************************
 * Delegate implementation
 **********************************************************************************/
public abstract class CRUDServicesDelegateForUserLoginEntry<O extends UserLoginEntryModelOID,
															L extends PersistableModelForUserLoginEntry<O,L>>
			  extends CRUDServicesForModelObjectDelegateBase<O,L>
		   implements CRUDServicesAPIForUserLoginEntry<O,L> {

/////////////////////////////////////////////////////////////////////////////////////////
// //CONSTRUCTOR   & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////

	public CRUDServicesDelegateForUserLoginEntry(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
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
// EXTENDION METHODS
///////////////////////////////////////////////////////////////////////////////////////


	@Override @SuppressWarnings("unchecked")
	public CRUDResult<L> loadForPasswordReset(final SecurityContext securityContext,
											  final UserCode userCode,
											  final String token) {
		if (userCode == null || token == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The entity userCode and token MUST not be null")
									.build();
		}
		CRUDResult<L> outResult = this.getServiceImplAs(CRUDServicesAPIForUserLoginEntry.class)
									  .loadForPasswordReset(securityContext,
															userCode, token);
		return outResult;
	}

	@Override @SuppressWarnings("unchecked")
	public CRUDResult<L> updateAfterPasswordReset(final SecurityContext securityContext,
												   final UserCode userCode,
												   final String token) {
		if (userCode == null || token == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The entity userCode and token MUST not be null")
									.build();
		}
		CRUDResult<L> outResult = this.getServiceImplAs(CRUDServicesAPIForUserLoginEntry.class)
									  .updateAfterPasswordReset(securityContext,
																 userCode, token);
		return outResult;
	}

}
