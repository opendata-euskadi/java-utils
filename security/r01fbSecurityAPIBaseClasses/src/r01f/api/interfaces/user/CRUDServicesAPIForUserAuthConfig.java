package r01f.api.interfaces.user;

import r01f.model.security.auth.PersistableModelForUserAuthConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthConfigModelOID;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.services.interfaces.ExposedServiceInterface;


@ExposedServiceInterface
public interface CRUDServicesAPIForUserAuthConfig<O extends UserAuthConfigModelOID, A extends PersistableModelForUserAuthConfig<O,A>>
		extends CRUDServicesForModelObject<O,A>,
				SecurityServiceInterface {

}