package r01f.db.find.users;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.user.FindServicesAPIForUserData;
import r01f.db.entities.users.DBEntityForUserData;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBFindForModelObjectBase;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.FindServicesForModelObject;
import r01f.util.types.Strings;

/**
 * Persistence layer User finder.
 */
@Slf4j
public class DBFindForUserData<O extends UserDataModelOID,
							   U extends PersistableModelForUserData<O,U>,
							   DB extends DBEntityForUserData>
	 extends DBFindForModelObjectBase<O, U,
	 								  DBPrimaryKeyForModelObject,
	 								  DB>
  implements FindServicesForModelObject<O,U>,
  			 FindServicesAPIForUserData<O,U> {

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public DBFindForUserData(final Class<U> modelObjectType,
							 final Class<DB> dbEntityType,
							 final DBModuleConfig dbCfg,
							 final EntityManager entityManager,
							 final Marshaller marshaller) {
		super(modelObjectType, dbEntityType, dbCfg, entityManager, marshaller);
	}

//////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
//////////////////////////////////////////////////////////////////////////////////

	@Override
	public FindResult<U> findAllUsers(final SecurityContext SecurityContext) {
		log.debug("> DBFindForUser.loading all users ");
		TypedQuery<DB> query = this.getEntityManager()
										.createQuery(Strings.customized("SELECT entity " +
																		"FROM {} entity ",
																		_DBEntityType.getSimpleName()),
																		_DBEntityType);
		Collection<DB> entities = query.getResultList();
		FindResult<U> outEntities = FindResultBuilder.using(SecurityContext)
													 .on(_modelObjectType)
													 .foundDBEntities(entities)
													 .transformedToModelObjectsUsing(this);
		return outEntities;
	}

}
