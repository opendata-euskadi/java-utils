package r01f.db.transfer.users;

import r01f.db.entities.users.DBEntityForUserAuthProfileConfig;
import r01f.model.ModelObject;
import r01f.model.security.auth.profile.PersistableModelForUserAuthProfileConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.TransfersModelObjectStateToDBEntity;
import r01f.securitycontext.SecurityContext;

/**
 * Transfers a {@link ModelObject}'s state to a given {@link DBEntity}
 */
public abstract class UserAuthProfileConfigTransferDelegate<O extends UserAuthProfileModelOID,
															ID extends UserAuthProfileID,
															U extends PersistableModelForUserAuthProfileConfig<O,ID,U>,
															DB extends DBEntityForUserAuthProfileConfig>
			  extends UserModelTransferBase<O,U,DB>
		   implements TransfersModelObjectStateToDBEntity<U,DB> {

///////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
//////////////////////////////////////////////////////////////////////////////////////////

	public UserAuthProfileConfigTransferDelegate(final Marshaller modelObjectsMarshaller, final Class<U> classType) {
		super(modelObjectsMarshaller, classType);
	}

	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final U modelObj,
												 final DB dbEntity){
		dbEntity.setId(modelObj.getId().asString());
	}

}