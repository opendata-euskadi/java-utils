package r01f.model.security.business;

/**
 * PaymentGatewayRequestedOperation
 */
public enum SecurityBusinessRequestedOperationTypes
 implements SecurityBusinessRequestedOperation {

	REGISTER,
	LOGIN_REQUEST,
	PASSWORD_RECOVERY_REQUEST,
	NOTIFY_PASSWORD_RECOVERY,
	PASSWORD_RESET_REQUEST,
	OTHER;

	@Override
	public String getName() {
		return this.name();
	}
}