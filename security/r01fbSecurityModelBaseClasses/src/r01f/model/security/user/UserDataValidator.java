package r01f.model.security.user;

import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

public class UserDataValidator<O extends UserDataModelOID,
							   U extends PersistableModelForUserData<O,U>>
  implements Validates<U> {

	@Override
	public ObjectValidationResult<U> validate(final U owner) {
		// validate the oid
		if (owner.getOid() == null) return ObjectValidationResultBuilder.on(owner)
													.isNotValidBecause("The oid cannot be null");
		if (owner.getUserCode() == null) return ObjectValidationResultBuilder.on(owner)
													.isNotValidBecause("The user code cannot be null");
		if (owner.getContactData()== null || owner.getContactData().getPersonalData() == null ||
				owner.getContactData().getPersonalData().getName() == null) return ObjectValidationResultBuilder.on(owner)
													.isNotValidBecause("The user data name cannot be null");
		// Phone and Mail validations may be null in some UserData so dont do any validation
		return ObjectValidationResultBuilder.on(owner)
										    .isValid();
	}

}
