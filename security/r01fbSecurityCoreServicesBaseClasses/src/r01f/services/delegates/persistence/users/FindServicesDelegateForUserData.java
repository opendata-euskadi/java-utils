package r01f.services.delegates.persistence.users;

import com.google.common.eventbus.EventBus;

import r01f.api.interfaces.user.FindServicesAPIForUserData;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.FindResult;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBFindForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.FindServicesForModelObjectDelegateBase;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
public abstract class FindServicesDelegateForUserData<O extends UserDataModelOID,
 													  U extends PersistableModelForUserData<O,U>>
			  extends FindServicesForModelObjectDelegateBase<O,U>
		   implements FindServicesAPIForUserData<O,U> {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR   & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////

	public FindServicesDelegateForUserData(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
									   final Class<U> modelObjectType,
									   final DBFindForModelObject<O,U> dbFind,
									   final Marshaller marshaller,
									   final EventBus eventBus){
		super(coreCfg,
			  modelObjectType,
			  dbFind,
			  eventBus);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////

	@Override @SuppressWarnings("unchecked")
	public FindResult<U> findAllUsers(final SecurityContext userContext) {
		return this.getServiceImplAs(FindServicesAPIForUserData.class)
					.findAllUsers(userContext);
	}

}
