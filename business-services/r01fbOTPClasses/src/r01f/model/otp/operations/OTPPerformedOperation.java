package r01f.model.otp.operations;

/**
 * A performed persistence-related operation
 * Note that the performed operation is NOT always the same as the requested one
 * (ie: an update could be requested by the client BUT the record didn't exist so a creation is performed)
 */
public enum OTPPerformedOperation {
	INITIALIZED,
	GOT_STATUS,
	OTHER;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////

}