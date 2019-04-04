package r01f.db.transfer.users;

import java.util.Calendar;

import lombok.extern.slf4j.Slf4j;
import r01f.db.entities.users.DBEntityForUserLoginData;
import r01f.model.ModelObject;
import r01f.model.security.login.PersistableModelForUserLoginData;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginDataModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.TransfersModelObjectStateToDBEntity;
import r01f.securitycontext.SecurityContext;

/**
 * Transfers a {@link ModelObject}'s state to a given {@link DBEntity}
 */
@Slf4j
public abstract class UserLoginDataTransferDelegate<O extends UserLoginDataModelOID,
													U extends PersistableModelForUserLoginData<O,U>,
													DB extends DBEntityForUserLoginData>
			  extends UserModelTransferBase<O,U,DB>
		   implements TransfersModelObjectStateToDBEntity<U,DB> {

///////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTOR
//////////////////////////////////////////////////////////////////////////////////////////

	public UserLoginDataTransferDelegate(final Marshaller modelObjectsMarshaller, final Class<U> classType) {
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
		if (modelObj.getExpiratingPwdAt() != null ){
			Calendar cal = Calendar.getInstance();
			cal.setTime(modelObj.getExpiratingPwdAt());
			dbEntity.setExpiratesAt(cal);
		}

	}
}