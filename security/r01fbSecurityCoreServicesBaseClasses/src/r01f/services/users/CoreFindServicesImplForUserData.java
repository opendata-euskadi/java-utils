package r01f.services.users;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import lombok.experimental.Accessors;
import r01f.api.interfaces.user.FindServicesAPIForUserData;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.FindResult;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CoreFindServicesForModelObjectBase;

/**
 * Implements the {@link R01MServiceRequest} find-related services which in turn are
 * delegated to {@link P12GNFindServicesImplBaseForEntity}
 */
@Singleton
@Accessors(prefix="_")
public abstract class CoreFindServicesImplForUserData<O extends UserDataModelOID,
													  U extends PersistableModelForUserData<O,U>>
			  extends CoreFindServicesForModelObjectBase<O,U>
		   implements FindServicesAPIForUserData<O,U>,
			 		  UsersServiceImpl {

	@Inject
	public CoreFindServicesImplForUserData(	final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											final Marshaller modelObjectsMarshaller,
											final EventBus eventBus,
											final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg, modelObjectsMarshaller, eventBus, entityManagerProvider);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////

	@Override @SuppressWarnings("unchecked")
	public FindResult<U> findAllUsers(final SecurityContext securityContext) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(FindServicesAPIForUserData.class)
							.findAllUsers(securityContext);
	}

}
