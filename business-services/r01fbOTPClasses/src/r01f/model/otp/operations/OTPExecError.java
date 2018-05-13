package r01f.model.otp.operations;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowable;
import r01f.exceptions.Throwables;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.util.types.Strings;
import r01f.util.types.Strings.StringCustomizerVarsProvider;

@Accessors(prefix="_")
public class OTPExecError<T>
	 extends OTPOperationExecResult<T>
  implements OTPOperationError {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * If it's a bad client request this member stores an error number
	 * that should be used at the client side to present the user with
	 * some useful information about the action to be taken
	 */
	@MarshallField(as="errorCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected OTPErrorType _errorType;
	/**
	 * An application-specific extended code that provides additional information
	 * to what _errorType gives
	 */
	@MarshallField(as="extendedErrorCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected int _extendedErrorCode;
	/**
	 * Some message about the overall operation, usually used when there's an error
	 */
	@MarshallField(as="message",escape=true)
	@Getter @Setter protected String _errorMessage;
	/**
	 * Contains details about the error, usually a java stack trace
	 */
	@MarshallField(as="errorDebug",escape=true)
	@Getter @Setter protected String _errorDebug;

/////////////////////////////////////////////////////////////////////////////////////////
//  NOT-SERIALIZABLE STATUS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Contains the error in the case that there is a general error that prevents
	 * the find operation to be executed
	 */
	@MarshallIgnoredField
	@Getter @Setter(AccessLevel.MODULE) protected transient Throwable _error;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public OTPExecError() {
		// nothing
	}
	OTPExecError(final Throwable th) {
		_error = th;
		_errorMessage = th.getMessage();
		_errorDebug = Throwables.getStackTraceAsString(th);
		if (th instanceof EnrichedThrowable) {
			EnrichedThrowable enrichedTh = (EnrichedThrowable)th;
			_extendedErrorCode = enrichedTh.getExtendedCode();
		}
		if (th instanceof OTPException) {
			OTPException persistEx = (OTPException)th;
			_errorType = persistEx.getPersistenceErrorType();
		} else {
			_errorType = OTPErrorType.SERVER_ERROR;		// a server error by default

		}
	}
	OTPExecError(final String errMsg,
				 final OTPErrorType errorCode) {
		_errorMessage = errMsg;
		_errorDebug = null;
		_errorType = errorCode;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <E extends Throwable> E getErrorAs(final Class<E> errorType) {
		return (E)_error;
	}
	@Override
	public void throwAsPersistenceException() throws OTPException {
		throw this.getPersistenceException();
	}
	@Override
	public OTPException getPersistenceException() {
		String errorMsg = Strings.isNOTNullOrEmpty(_errorMessage) ? _errorMessage
																  : this.getDetailedMessage();
		OTPErrorType errorType = _errorType != null ? _errorType : OTPErrorType.UNKNOWN;
		OTPRequestedOperation reqOp = OTPRequestedOperation.canBe(_requestedOperationName)
																? OTPRequestedOperation.valueOf(_requestedOperationName)
																: OTPRequestedOperation.OTHER;
		return new OTPException(reqOp,_requestedOperationName,
											  errorMsg,
										      errorType,_extendedErrorCode);
		}
/////////////////////////////////////////////////////////////////////////////////////////
//  REASON
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean wasBecauseAServerError() {
		return !this.wasBecauseAClientError();
	}
	@Override
	public boolean wasBecauseAClientError() {
		if (_errorType == null) throw new IllegalStateException(Throwables.message("The {} object does NOT have error info!",
																				   OTPOperationError.class));
		return _errorType.isClientError();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public OTPExecError<T> asOperationExecError() {
		return this;
	}
	@Override
	public OTPOperationExecOK<T> asOperationExecOK() {
		throw new ClassCastException();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getDetailedMessage() {
		String outMsg = Strings.customizedWith("{} operation could NOT be performed because of a {} error{}: {}",
						       				   new StringCustomizerVarsProvider() {
														@Override
														public Object[] provideVars() {
															OTPExecError<T> err = OTPExecError.this;
															Object[] outVars = new Object[4];
															outVars[0] = err.getRequestedOperationName();
															outVars[1] = err.wasBecauseAClientError() ? "CLIENT"
																									  : "SERVER";
															if (err.getErrorType() != null) {
																outVars[2] = " (code=" + err.getErrorType() + ")";

															} else {
																outVars[2] = "";
															}
															outVars[3] = Strings.isNOTNullOrEmpty(err.getErrorMessage()) ? err.getErrorMessage()
																					 						     		 : err.getErrorType() != null ? err.getErrorType().toString() : "";
															return outVars;
														}
						       				  });
		return outMsg;
	}
	@Override
	public CharSequence debugInfo() {
		StringBuilder outDbgInfo = new StringBuilder();
		outDbgInfo.append(this.getDetailedMessage());
		if (_error != null) {
			outDbgInfo.append("\n")
					  .append(Throwables.getStackTraceAsString(_error));
		}
		return outDbgInfo.toString();
	}
}
