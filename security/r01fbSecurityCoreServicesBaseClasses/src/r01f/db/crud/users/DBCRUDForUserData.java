package r01f.db.crud.users;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.user.CRUDServicesAPIForUserData;
import r01f.db.entities.users.DBEntityForUserData;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBCRUDForModelObjectBase;
import r01f.persistence.db.TransfersModelObjectStateToDBEntity;
import r01f.persistence.db.TransformsDBEntityIntoModelObject;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Persistence layer for User
 */
@Accessors(prefix="_")
@Slf4j
public abstract class DBCRUDForUserData<O extends UserDataModelOID,
										U extends PersistableModelForUserData<O,U>,
										DB extends DBEntityForUserData>
			  extends DBCRUDForModelObjectBase<O,U,
			  								   DBPrimaryKeyForModelObject,
			  								   DB>
		  implements CRUDServicesAPIForUserData<O,U> {;

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public DBCRUDForUserData(final Class<U> modelObjectType,
							 final  Class<DB> dbEntityType,
							 final TransformsDBEntityIntoModelObject<DB, U> dbEntityIntoModelObjectTransformer,
							 final DBModuleConfig dbCfg,
							 final EntityManager entityManager,
							 final Marshaller marshaller) {
		super(modelObjectType, dbEntityType,
			  dbEntityIntoModelObjectTransformer, dbCfg, entityManager, marshaller);
	}

	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final U modelObj,
												 final DB dbEntity) {
		@SuppressWarnings("unchecked")
		TransfersModelObjectStateToDBEntity<U,DB>  _transformer  =
			( TransfersModelObjectStateToDBEntity<U, DB> ) (Object) _dbEntityIntoModelObjectTransformer;
		_transformer.setDBEntityFieldsFromModelObject(securityContext, modelObj, dbEntity);
	}

/////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<U> loadByUserCode(final SecurityContext securityContext,
										final UserCode userCode) {
		log.warn("DBCRUDForUser.loadByUserCode {} " ,
				userCode);
		if (userCode == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The Id MUST not be null")
									.build();
		}
		// Do the query
		TypedQuery<DB> query = this.getEntityManager()
								   .createQuery(Strings.customized("SELECT entity " +
																   "FROM {} entity where entity._userCode = :userCode",
																   _DBEntityType.getSimpleName()),
																   _DBEntityType)
								   .setParameter("userCode", userCode.asString());

		DB dbEntity = (CollectionUtils.hasData(query.getResultList()))? query.getResultList().get(0) : null;
		if (dbEntity == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientRequestedEntityWasNOTFound()
									.build();
		}
		CRUDResult<U> outEntities =
				CRUDResultBuilder.using(securityContext)
								 .on(_modelObjectType)
								 .loaded()
								 .dbEntity(dbEntity)
								 .transformedToModelObjectUsing(this);
		return outEntities;
	}

	@Override
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
								final EMail email) {
		log.warn("DBCRUDForUser.loadBy (email) {} " ,
				 email);
		if (email == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The Id MUST not be null")
									.build();
		}
		// Do the query
		TypedQuery<DB> query = this.getEntityManager()
								   .createQuery(Strings.customized("SELECT entity " +
																   "FROM {} entity where entity._email = :email",
																   _DBEntityType.getSimpleName()),
																   _DBEntityType)
								   .setParameter("email", email.asString());

		DB dbEntity = (CollectionUtils.hasData(query.getResultList()))? query.getResultList().get(0) : null;
		if (dbEntity == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientRequestedEntityWasNOTFound()
									.build();
		}
		CRUDResult<U> outEntities =
				CRUDResultBuilder.using(securityContext)
								 .on(_modelObjectType)
								 .loaded()
								 .dbEntity(dbEntity)
								 .transformedToModelObjectUsing(this);
		return outEntities;
	}

	@Override
	public CRUDResult<U> loadOrNull(SecurityContext securityContext, EMail email) {
		CRUDResult<U> outEntity = this.loadBy(securityContext, email);
		if (outEntity.hasFailed() && outEntity.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
			return null;
		}
		return outEntity;
	}

	@Override
	public CRUDResult<U> loadBy(final SecurityContext securityContext,
								final Phone phone) {
		log.warn("DBCRUDForUser.loadBy (phone) {} " ,
				 phone);
		if (phone == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The Id MUST not be null")
									.build();
		}
		// Do the query
		TypedQuery<DB> query = this.getEntityManager()
								   .createQuery(Strings.customized("SELECT entity " +
																   "FROM {} entity where entity._phone = :phone",
																   _DBEntityType.getSimpleName()),
																   _DBEntityType)
								   .setParameter("phone", phone.asString());

		DB dbEntity = (CollectionUtils.hasData(query.getResultList()))? query.getResultList().get(0) : null;
		if (dbEntity == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientRequestedEntityWasNOTFound()
									.build();
		}
		CRUDResult<U> outEntities =
				CRUDResultBuilder.using(securityContext)
								 .on(_modelObjectType)
								 .loaded()
								 .dbEntity(dbEntity)
								 .transformedToModelObjectUsing(this);
		return outEntities;
	}

	@Override
	public CRUDResult<U> loadOrNull(SecurityContext securityContext, Phone phone) {
		CRUDResult<U> outEntity = this.loadBy(securityContext, phone);
		if (outEntity.hasFailed() && outEntity.asCRUDError().wasBecauseClientRequestedEntityWasNOTFound()) {
			return null;
		}
		return outEntity;
	}

}
