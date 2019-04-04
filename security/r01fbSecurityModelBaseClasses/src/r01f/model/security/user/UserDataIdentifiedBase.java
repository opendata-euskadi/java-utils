package r01f.model.security.user;

import lombok.experimental.Accessors;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.model.security.PersistableSecurityModelIdentifiedObject;
import r01f.model.security.PersistableSecurityModelIdentifiedObjectBase;
import r01f.model.security.metadata.MetaDataForSecurityModelBase;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.model.security.oids.SecurityIDS.UserModelID;

@ModelObjectData(MetaDataForSecurityModelBase.class)
@Accessors(prefix="_")
public abstract class UserDataIdentifiedBase<O extends UserDataModelOID,
											 ID extends UserModelID,
											 SELF_TYPE extends PersistableSecurityModelIdentifiedObjectBase<O,ID,SELF_TYPE>>
			  extends UserDataBase<O,SELF_TYPE> // PersistableSecurityModelIdentifiedObjectBase
  		   implements PersistableSecurityModelIdentifiedObject<O,ID,SELF_TYPE> {

	private static final long serialVersionUID = 8628625324366860597L;
}