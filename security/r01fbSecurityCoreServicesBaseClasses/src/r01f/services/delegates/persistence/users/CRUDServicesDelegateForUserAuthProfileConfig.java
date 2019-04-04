package r01f.services.delegates.persistence.users;

import com.google.common.eventbus.EventBus;

import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthProfileConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.security.auth.profile.PersistableModelForUserAuthProfileConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBCRUDForModelObject;
import r01f.services.delegates.persistence.CRUDServicesForModelObjectDelegateBase;

/**********************************************************************************
 * Delegate implementation
 **********************************************************************************/
public abstract class CRUDServicesDelegateForUserAuthProfileConfig<O extends UserAuthProfileModelOID,
																   ID extends UserAuthProfileID,
																   A extends PersistableModelForUserAuthProfileConfig<O,ID,A>>
			  extends CRUDServicesForModelObjectDelegateBase<O,A>
		   implements CRUDServicesAPIForUserAuthProfileConfig<O,ID,A> {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR   & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////

	public CRUDServicesDelegateForUserAuthProfileConfig(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
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
