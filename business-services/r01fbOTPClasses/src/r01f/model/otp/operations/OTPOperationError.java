package r01f.model.otp.operations;

/**
 * An interface for a  operation that could not be completed successfully
 */
public interface OTPOperationError
		 extends OTPOperationResult {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the underlying error
	 */
	public Throwable getError();
	/**
	 * returns the underlying erro as a concrete {@link Throwable} type
	 * @param errorType
	 * @return
	 */
	public <E extends Throwable> E getErrorAs(final Class<E> errorType);
	/**
	 * @return a brief resume of the error
	 */
	public String getErrorMessage();
	/**
	 * @return some detailed message about the error
	 */
	public String getErrorDebug();
	/**
	 * @return the error type on a pre-defined typology basis
	 */
	public OTPErrorType getErrorType();
	/**
	 * @return An application-specific extended code that provides additional information
	 * 		   to what _errorType gives
	 */
	public int getExtendedErrorCode();
/////////////////////////////////////////////////////////////////////////////////////////
//  REASON
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if it was a server error
	 */
	public boolean wasBecauseAServerError();
	/**
	 * @return true if it was a client error
	 */
	public boolean wasBecauseAClientError();

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Throws this object as an exception
	 * @throws OTPException
	 */
	public void throwAsPersistenceException() throws OTPException;
	/**
	 * Gets a {@link OTPException} from this object
	 * @return
	 */
	public OTPException getPersistenceException();
}
