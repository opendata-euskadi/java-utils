package r01f.db.transfer.users;

import lombok.extern.slf4j.Slf4j;
import r01f.db.entities.users.DBEntityForUserLoginEntry;
import r01f.model.ModelObject;
import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginEntryModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.TransfersModelObjectStateToDBEntity;
import r01f.securitycontext.SecurityContext;

/**
 * Transfers a {@link ModelObject}'s state to a given {@link DBEntity}
 */
@Slf4j
public abstract class UserLoginEntryTransferDelegate<O extends UserLoginEntryModelOID,
													 U extends PersistableModelForUserLoginEntry<O,U>,
													 DB extends DBEntityForUserLoginEntry>
			  extends UserModelTransferBase<O,U,DB>
		   implements TransfersModelObjectStateToDBEntity<U,DB> {

///////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
//////////////////////////////////////////////////////////////////////////////////////////

	public UserLoginEntryTransferDelegate(final Marshaller modelObjectsMarshaller, final Class<U> classType) {
		super(modelObjectsMarshaller, classType);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final U modelObj,
												 final DB dbEntity){

		log.debug(" > User Code {}",modelObj.getUserCode());
		dbEntity.setUserCode(modelObj.getUserCode().asString());
		dbEntity.setEntryType(modelObj.getLoginEntryType().toString());
		if (modelObj.getToken()!=null) {
			dbEntity.setToken(modelObj.getToken());
		}
	}

}