package r01f.services.users;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;

import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthProfileConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.security.auth.profile.PersistableModelForUserAuthProfileConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
import r01f.objectstreamer.Marshaller;
import r01f.services.persistence.CoreCRUDServicesForModelObjectBase;

@Singleton
@Accessors(prefix="_")
public abstract class CoreCRUDServicesImplForUserAuthProfileConfig<O extends UserAuthProfileModelOID,
																   ID extends UserAuthProfileID,
																   A extends PersistableModelForUserAuthProfileConfig<O,ID,A>>
			  extends CoreCRUDServicesForModelObjectBase<O,A>
		   implements CRUDServicesAPIForUserAuthProfileConfig<O,ID,A>,
			 		  UsersServiceImpl {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public CoreCRUDServicesImplForUserAuthProfileConfig(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
														final Marshaller modelObjectsMarshaller,
														final EventBus eventBus,
														final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
	}

}

