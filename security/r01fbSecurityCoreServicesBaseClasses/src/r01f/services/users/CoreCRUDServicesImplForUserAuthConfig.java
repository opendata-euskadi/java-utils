package r01f.services.users;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;

import lombok.experimental.Accessors;
import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.security.auth.PersistableModelForUserAuthConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.services.persistence.CoreCRUDServicesForModelObjectBase;

@Singleton
@Accessors(prefix="_")
public abstract class CoreCRUDServicesImplForUserAuthConfig<O extends UserAuthConfigModelOID,
															A extends PersistableModelForUserAuthConfig<O,A>>
			  extends CoreCRUDServicesForModelObjectBase<O,A>
		   implements CRUDServicesAPIForUserAuthConfig<O,A>,
		   			  UsersServiceImpl {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public CoreCRUDServicesImplForUserAuthConfig(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												 final Marshaller modelObjectsMarshaller,
												 final EventBus eventBus,
												 final Provider<EntityManager> entityManagerProvider) {
		super( coreCfg,modelObjectsMarshaller, eventBus, entityManagerProvider);
	}

}

