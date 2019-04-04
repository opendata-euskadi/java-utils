package r01f.db.crud.users;

import javax.persistence.EntityManager;

import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthConfig;
import r01f.db.entities.users.DBEntityForUserAuthConfig;
import r01f.model.security.auth.PersistableModelForUserAuthConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBCRUDForModelObjectBase;
import r01f.persistence.db.TransfersModelObjectStateToDBEntity;
import r01f.persistence.db.TransformsDBEntityIntoModelObject;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.securitycontext.SecurityContext;

/**
 * Persistence layer
 */
public abstract class DBCRUDForUserAuthConfig<O extends UserAuthConfigModelOID,
											  A extends PersistableModelForUserAuthConfig<O,A>,
									 		  DB extends DBEntityForUserAuthConfig>
			  extends DBCRUDForModelObjectBase<O,A,
			  								   DBPrimaryKeyForModelObject,
			  								   DB>
		   implements CRUDServicesAPIForUserAuthConfig<O,A> {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public DBCRUDForUserAuthConfig(final Class<A> modelObjectType,
								   final  Class<DB> dbEntityType,
								   final TransformsDBEntityIntoModelObject<DB, A> dbEntityIntoModelObjectTransformer,
								   final DBModuleConfig dbCfg,
								   final EntityManager entityManager,
								   final Marshaller marshaller) {
		super(modelObjectType, dbEntityType, dbEntityIntoModelObjectTransformer, dbCfg, entityManager, marshaller);
	}

	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final A modelObj,
												 final DB dbEntity) {
		@SuppressWarnings("unchecked")
		TransfersModelObjectStateToDBEntity<A,DB>  _transformer  =
			( TransfersModelObjectStateToDBEntity<A, DB> ) (Object) _dbEntityIntoModelObjectTransformer;
		_transformer.setDBEntityFieldsFromModelObject(securityContext, modelObj, dbEntity);
	}

}
