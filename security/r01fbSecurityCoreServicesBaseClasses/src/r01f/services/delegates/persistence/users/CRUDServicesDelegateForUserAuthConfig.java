package r01f.services.delegates.persistence.users;

import com.google.common.eventbus.EventBus;

import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.security.auth.PersistableModelForUserAuthConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBCRUDForModelObject;
import r01f.services.delegates.persistence.CRUDServicesForModelObjectDelegateBase;

/**********************************************************************************
 * Delegate implementation
 **********************************************************************************/
public abstract class CRUDServicesDelegateForUserAuthConfig<O extends UserAuthConfigModelOID,
															A extends PersistableModelForUserAuthConfig<O,A>>
			  extends CRUDServicesForModelObjectDelegateBase<O,A>
		   implements CRUDServicesAPIForUserAuthConfig<O,A> {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR   & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////

	public CRUDServicesDelegateForUserAuthConfig(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												 final Class<A> modelObjectType,
												 final DBCRUDForModelObject<O,A> dbCrud,
												 final Marshaller marshaller,
												 final EventBus eventBus){
			super(coreCfg,
				  modelObjectType,
				  dbCrud,
				  eventBus);
	}

}
