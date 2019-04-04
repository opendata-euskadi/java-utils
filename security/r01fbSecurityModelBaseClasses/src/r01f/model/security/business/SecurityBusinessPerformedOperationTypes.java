package r01f.model.security.business;

/**
 * A performed persistence-related operation
 * Note that the performed operation is NOT always the same as the requested one
 * (ie: an update could be requested by the client BUT the record didn't exist so a creation is performed)
 */
public enum SecurityBusinessPerformedOperationTypes
 implements SecurityBusinessPerformedOperation {

	REGISTER_DONE,
	LOGIN_DONE,
	LOGIN_NOT_DONE,
	PASSWORD_RECOVERY_NOTIFIED,
	PASSWORD_RECOVERY_NOT_NOTIFIED,
	PASSWORD_RESET_DONE,
	PASSWORD_RESET_NOT_DONE,
	OTHER;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getName() {
		return this.name();
	}
}