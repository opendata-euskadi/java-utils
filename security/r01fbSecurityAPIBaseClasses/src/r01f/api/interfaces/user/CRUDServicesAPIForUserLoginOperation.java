package r01f.api.interfaces.user;

import r01f.model.security.login.entry.PersistableModelForUserLoginEntry;
import r01f.model.security.oids.SecurityCommonOIDs.UserLoginOperationModelOID;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface CRUDServicesAPIForUserLoginOperation<O extends UserLoginOperationModelOID,
													  L extends PersistableModelForUserLoginEntry<O,L>>
		 extends CRUDServicesForModelObject<O,L>,
		 		 SecurityServiceInterface {


}

