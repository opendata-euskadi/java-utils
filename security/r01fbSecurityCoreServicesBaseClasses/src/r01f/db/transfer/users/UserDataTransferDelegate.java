package r01f.db.transfer.users;

import r01f.db.entities.users.DBEntityForUserData;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.user.PersistableModelForUserData;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.TransfersModelObjectStateToDBEntity;
import r01f.securitycontext.SecurityContext;

public abstract class UserDataTransferDelegate<O extends UserDataModelOID,
											   U extends PersistableModelForUserData<O,U>,
											   DB extends DBEntityForUserData>
			  extends UserModelTransferBase<O,U,DB>
		   implements TransfersModelObjectStateToDBEntity<U,DB> {

///////////////////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR
//////////////////////////////////////////////////////////////////////////////////////////

	public UserDataTransferDelegate(final Marshaller modelObjectsMarshaller, final Class<U> classType) {
		super(modelObjectsMarshaller, classType);
	}

///////////////////////////////////////////////////////////////////////////////////////////
// METHODS
//////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final U modelObj,
												 final DB dbEntity){
		dbEntity.setUserCode(modelObj.getUserCode().asString());
		if (modelObj.getContactData() != null
				&& modelObj.getContactData().getContactInfo() != null) {
			if (modelObj.getContactData().getContactInfo().hasMailAddress()) {
				dbEntity.setEmail(modelObj.getContactData().getContactInfo().getDefaultMailAddressOrAny().asString());
			}
			if (modelObj.getContactData().getContactInfo().hasPhones()) {
				dbEntity.setPhone(modelObj.getContactData().getContactInfo().getDefaultPhoneOrAny().asString());
			}
		}
	}

}
