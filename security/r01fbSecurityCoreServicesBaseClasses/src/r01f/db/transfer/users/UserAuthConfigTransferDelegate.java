package r01f.db.transfer.users;

import r01f.db.entities.users.DBEntityForUserAuthConfig;
import r01f.model.ModelObject;
import r01f.model.security.auth.PersistableModelForUserAuthConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.TransfersModelObjectStateToDBEntity;
import r01f.securitycontext.SecurityContext;

/**
 * Transfers a {@link ModelObject}'s state to a given {@link DBEntity}
 */
public abstract class UserAuthConfigTransferDelegate<O extends UserAuthConfigModelOID,
													 U extends PersistableModelForUserAuthConfig<O,U>,
													 DB extends DBEntityForUserAuthConfig>
			  extends UserModelTransferBase<O,U,DB>
		   implements TransfersModelObjectStateToDBEntity<U,DB> {

///////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
//////////////////////////////////////////////////////////////////////////////////////////

	public UserAuthConfigTransferDelegate(final Marshaller modelObjectsMarshaller, final Class<U> classType) {
		super(modelObjectsMarshaller, classType);
	}

	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final U modelObj,
												 final DB dbEntity){
		if (modelObj.getUserOID() != null ) {
			dbEntity.setUserOid(modelObj.getUserOID().toString());
		}
	}

}