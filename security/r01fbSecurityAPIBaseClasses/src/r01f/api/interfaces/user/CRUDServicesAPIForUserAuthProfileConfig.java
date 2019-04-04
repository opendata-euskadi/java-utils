package r01f.api.interfaces.user;

import r01f.model.security.auth.profile.PersistableModelForUserAuthProfileConfig;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
import r01f.services.interfaces.CRUDServicesForModelObject;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface CRUDServicesAPIForUserAuthProfileConfig< O extends UserAuthProfileModelOID,
														 ID extends UserAuthProfileID,
														  A extends PersistableModelForUserAuthProfileConfig<O,ID,A>>
		 extends CRUDServicesForModelObject<O,A>,
				 SecurityServiceInterface {

}