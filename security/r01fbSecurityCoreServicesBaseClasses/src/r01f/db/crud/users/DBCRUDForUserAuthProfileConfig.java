package r01f.db.crud.users;

import javax.persistence.EntityManager;

import r01f.api.interfaces.user.CRUDServicesAPIForUserAuthProfileConfig;
import r01f.db.entities.users.DBEntityForUserAuthProfileConfig;
import r01f.model.security.auth.profile.PersistableModelForUserAuthProfileConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
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
public abstract class DBCRUDForUserAuthProfileConfig< O extends UserAuthProfileModelOID,
													 ID extends UserAuthProfileID,
													  A extends PersistableModelForUserAuthProfileConfig<O,ID,A>,
													 DB extends DBEntityForUserAuthProfileConfig>
			  extends DBCRUDForModelObjectBase<O,A,
			  								   DBPrimaryKeyForModelObject,
			  								   DB>
		   implements CRUDServicesAPIForUserAuthProfileConfig<O,ID,A> {

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////

	public DBCRUDForUserAuthProfileConfig(final Class<A> modelObjectType,
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
