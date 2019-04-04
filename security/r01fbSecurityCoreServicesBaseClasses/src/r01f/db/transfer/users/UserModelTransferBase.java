package r01f.db.transfer.users;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import r01f.db.entities.users.DBEntityBaseForUserModel;
import r01f.model.HasTrackingInfo;
import r01f.model.facets.HasEntityVersion;
import r01f.model.security.PersistableSecurityModelObject;
import r01f.model.security.oids.SecurityCommonOIDs.UserModelOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBEntityHasModelObjectDescriptor;
import r01f.persistence.db.TransfersModelObjectStateToDBEntity;
import r01f.persistence.db.TransformsDBEntityIntoModelObject;
import r01f.securitycontext.SecurityContext;

@Slf4j
public abstract class UserModelTransferBase<O extends UserModelOID,
											U extends  PersistableSecurityModelObject<O>,
											DB extends DBEntityBaseForUserModel>
		   implements TransfersModelObjectStateToDBEntity<U,DB> ,
		   			  TransformsDBEntityIntoModelObject<DB, U>{

/////////////////////////////////////////////////////////////////////////////////////////
//MEMBERS
/////////////////////////////////////////////////////////////////////////////////////////

	@Setter @Getter Marshaller _modelObjectsMarshaller;
	@Setter @Getter Class<U> _classType;

/////////////////////////////////////////////////////////////////////////////////////////
//CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////

	public UserModelTransferBase (final Marshaller modelObjectsMarshaller,Class<U> classType) {
		_modelObjectsMarshaller = modelObjectsMarshaller;
		_classType = classType;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final U modelObj,
												 final DB dbEntity){
		if (modelObj.getOid() != null ){
			dbEntity.setOid(modelObj.getOid().asString());
		}
	}

	@Override
	public U dbEntityToModelObject(final SecurityContext securityContext, final DB dbEntity) {
		log.debug( "UserModelTransferBase.dbEntityToModelObject");
		U outObj = null;
		// use the descriptor to build the model object
		if (dbEntity instanceof DBEntityHasModelObjectDescriptor) {
			DBEntityHasModelObjectDescriptor hasDescriptor = (DBEntityHasModelObjectDescriptor)dbEntity;

			outObj = _modelObjectsMarshaller.forReading().fromXml(hasDescriptor.getDescriptor(),
																	_classType);
		} else {
			log.warn("The db entity of type {} does NOT implements {} so the db entity MUST be manually translated bo model object",
			dbEntity.getClass().getSimpleName(),DBEntityHasModelObjectDescriptor.class.getSimpleName());
		}
		// copy some info from the dbEntity
		if (outObj != null) {
			if (dbEntity instanceof HasTrackingInfo) {
				outObj.setTrackingInfo(((HasTrackingInfo)dbEntity).getTrackingInfo());
			}
			if (dbEntity instanceof HasEntityVersion) {
				outObj.setEntityVersion(((HasEntityVersion)dbEntity).getEntityVersion());
			}
		}
		return outObj;
	}

}

