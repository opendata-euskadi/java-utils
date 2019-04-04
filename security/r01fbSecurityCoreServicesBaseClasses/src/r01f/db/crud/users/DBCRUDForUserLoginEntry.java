package r01f.db.crud.users;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginEntry;
import r01f.db.entities.users.DBEntityForUserLoginEntry;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.security.login.entry.LoginEntryType;
import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginEntryModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBCRUDForModelObjectBase;
import r01f.persistence.db.TransfersModelObjectStateToDBEntity;
import r01f.persistence.db.TransformsDBEntityIntoModelObject;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Persistence layer
 */
@Slf4j
public class DBCRUDForUserLoginEntry<O extends UserLoginEntryModelOID,
									 L extends PersistableModelForUserLoginEntry<O,L>,
									 DB extends DBEntityForUserLoginEntry>
	 extends DBCRUDForModelObjectBase<O, L, DBPrimaryKeyForModelObject, DB>
  implements CRUDServicesAPIForUserLoginEntry<O,L> {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public DBCRUDForUserLoginEntry(final Class<L> modelObjectType,
								   final Class<DB> dbEntityType,
								   final TransformsDBEntityIntoModelObject<DB, L> dbEntityIntoModelObjectTransformer,
								   final DBModuleConfig dbCfg,
								   final EntityManager entityManager,
								   final Marshaller marshaller) {
		super(modelObjectType, dbEntityType, dbEntityIntoModelObjectTransformer, dbCfg, entityManager, marshaller);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//MODEL <TO> DB
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final L modelObj,
												 final DB dbEntity) {
		@SuppressWarnings("unchecked")
		TransfersModelObjectStateToDBEntity<L,DB>  _transformer  =
			( TransfersModelObjectStateToDBEntity<L, DB> ) (Object) _dbEntityIntoModelObjectTransformer;
		_transformer.setDBEntityFieldsFromModelObject(securityContext, modelObj, dbEntity);
	}

/////////////////////////////////////////////////////////////////////////////////////////
// EXTENDION METHODS
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public CRUDResult<L> loadForPasswordReset(final SecurityContext securityContext,
											  final UserCode userCode,
											  final String token) {
		log.warn("DBCRUDForUser.loadForPasswordReset {} " ,
				 userCode);
		if (userCode == null || token == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The UserCode and Token MUST not be null")
									.build();
		}
		// Do the query
		TypedQuery<DB> query = this.getEntityManager()
										.createQuery(Strings.customized("SELECT entity " +
																		"FROM {} entity WHERE entity._userCode = :userCode " +
																		"    and entity._entryType = :entryType and entity._token = :token",
																		_DBEntityType.getSimpleName()),
																		_DBEntityType)
									.setParameter("userCode", userCode.asString())
									.setParameter("entryType", LoginEntryType.PASSWORD_RECOVERY_REQUEST_OK.name())
									.setParameter("token", token);
		DB dbEntity = (CollectionUtils.hasData(query.getResultList()))? query.getResultList().get(0) : null;
		if (dbEntity == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientRequestedEntityWasNOTFound()
									.build();
		}
		CRUDResult<L> outEntities = CRUDResultBuilder.using(securityContext)
													 .on(_modelObjectType)
													 .loaded()
													 .dbEntity(dbEntity)
													 .transformedToModelObjectUsing(this);
		return outEntities;
	}

	@Override
	public CRUDResult<L> updateAfterPasswordReset(final SecurityContext securityContext,
												  final UserCode userCode,
												  final String token) {
		log.warn("DBCRUDForUser.loadForPasswordReset {} " ,
				 userCode);
		if (userCode == null || token == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientBadRequest("The UserCode and Token MUST not be null")
									.build();
		}
		// Do the query
		TypedQuery<DB> query = this.getEntityManager()
									.createQuery(Strings.customized("SELECT entity " +
																	"FROM {} entity WHERE entity._userCode = :userCode " +
																	"    and entity._entryType = :entryType and entity._token = :token",
																	_DBEntityType.getSimpleName()),
																	_DBEntityType)
									.setParameter("userCode", userCode.asString())
									.setParameter("entryType", LoginEntryType.PASSWORD_RECOVERY_REQUEST_OK.name())
									.setParameter("token", token);
		DB dbEntity = (CollectionUtils.hasData(query.getResultList()))? query.getResultList().get(0) : null;
		if (dbEntity == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientRequestedEntityWasNOTFound()
									.build();
		}
		TypedQuery<DB> queryUpdate = this.getEntityManager()
										 .createQuery(Strings.customized("UPDATE {} entity " +
																		"SET entity._token = :tokenNull " +
																		"where entity._userCode = :userCode " +
																		"    and entity._entryType = :entryType and entity._token = :token",
																		_DBEntityType.getSimpleName()),
																		_DBEntityType)
										.setParameter("tokenNull", null)
										.setParameter("userCode", userCode.asString())
										.setParameter("entryType", LoginEntryType.PASSWORD_RECOVERY_REQUEST_OK.name())
										.setParameter("token", token);
		int updatedRegistries = queryUpdate.executeUpdate();
		if (updatedRegistries == 0) {
			return CRUDResultBuilder.using(securityContext)
									.on(_modelObjectType)
									.notLoaded()
									.becauseClientRequestedEntityWasNOTFound()
									.build();
		}
		CRUDResult<L> outEntities = CRUDResultBuilder.using(securityContext)
													 .on(_modelObjectType)
													 .loaded()
													 .dbEntity(dbEntity)
													 .transformedToModelObjectUsing(this);
		return outEntities;
	}

}
