package r01f.db.crud.users;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;
import r01f.api.interfaces.user.CRUDServicesAPIForUserLoginData;
import r01f.db.entities.users.DBEntityForUserLoginData;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
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
public class DBCRUDForUserLoginData<O extends UserLoginDataModelOID,
									L extends PersistableModelForUserLoginData<O,L>,
									DB extends DBEntityForUserLoginData>
	 extends DBCRUDForModelObjectBase<O, L, DBPrimaryKeyForModelObject, DB>
  implements CRUDServicesAPIForUserLoginData<O,L> {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public DBCRUDForUserLoginData(final Class<L> modelObjectType,
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
	public CRUDResult<L> loadByUserCode(final SecurityContext securityContext,
										final UserCode userCode) {
		log.warn("DBCRUDForUser.loadById {} " ,
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
		CRUDResult<L> outEntities =
		CRUDResultBuilder.using(securityContext)
						 .on(_modelObjectType)
						 .loaded()
						 .dbEntity(dbEntity)
						 .transformedToModelObjectUsing(this);
		return outEntities;
	}

	@Override
	public CRUDResult<L> updatePassword(final SecurityContext securityContext,
										final UserCode userCode,
										final Password password) {
		// NOTHING HERE, USING OTHER CRUD METHODS
		return null;
	}

}
