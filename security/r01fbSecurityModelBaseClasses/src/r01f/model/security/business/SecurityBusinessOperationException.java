package r01f.model.security.business;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedRuntimeException;
import r01f.util.types.Strings;

/**
 * An error raised when performing any persistence-related operation
 */
@Accessors(prefix="_")
public class SecurityBusinessOperationException
	 extends EnrichedRuntimeException {

	private static final long serialVersionUID = -1161648233290893856L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The client's requested operation
	 */
	@Getter private final SecurityBusinessRequestedOperation _requestedOperation;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected SecurityBusinessOperationException(final SecurityBusinessRequestedOperation requestedOp,
								final Throwable th) {
		super(SecurityBusinessOperationErrorType.class,
			  Strings.customized("Persistence error when executing a {} operation: {}",
					  			 requestedOp,th.getMessage()),
			  th,
			  SecurityBusinessOperationErrorType.SERVER_ERROR);
		_requestedOperation = requestedOp;
	}

	protected SecurityBusinessOperationException(final SecurityBusinessRequestedOperation requestedOp,
								final String requestedOpName,
								final String msg,
								final SecurityBusinessOperationErrorType errorType,final int extendedCode) {
		super(SecurityBusinessOperationErrorType.class,
		      Strings.customized("Persistence error when executing a {} ({}: {})",
		    		  			 requestedOp,requestedOpName != null ? requestedOpName
		    		  					 							 : requestedOp != null ? requestedOp.getName() : "",
		    		  			 msg),
		      errorType,extendedCode);
		_requestedOperation = requestedOp;
	}
	protected SecurityBusinessOperationException(final SecurityBusinessRequestedOperation requestedOp,
								final String msg,
								final SecurityBusinessOperationErrorType errorType) {
		this(requestedOp,null,
			 msg,
			 errorType,-1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityBusinessOperationErrorType getPersistenceErrorType() {
		return SecurityBusinessOperationErrorType.from(_code);
	}
	public boolean isServerError() {
		return this.is(SecurityBusinessOperationErrorType.SERVER_ERROR);
	}
	public boolean isClientError() {
		return !this.isServerError();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static SecurityBusinessOperationException serverError(final SecurityBusinessRequestedOperation requestedOp) {
		return new SecurityBusinessOperationException(requestedOp,
										null,		// no message
										SecurityBusinessOperationErrorType.SERVER_ERROR);
	}
	public static SecurityBusinessOperationException serverError(final SecurityBusinessRequestedOperation requestedOp,
												   final String msg,final Object... vars) {
		return new SecurityBusinessOperationException(requestedOp,
										Strings.customized(msg,vars),
										SecurityBusinessOperationErrorType.SERVER_ERROR);
	}
	public static SecurityBusinessOperationException serverError(final SecurityBusinessRequestedOperation requestedOp,
												   final Throwable th) {
		return new SecurityBusinessOperationException(requestedOp,
										th);
	}
	public static SecurityBusinessOperationException badClientRequest(final SecurityBusinessRequestedOperation requestedOp) {
		return new SecurityBusinessOperationException(requestedOp,
										SecurityBusinessOperationErrorType.BAD_REQUEST_DATA.name(),		// no message
										SecurityBusinessOperationErrorType.BAD_REQUEST_DATA);
	}
	public static SecurityBusinessOperationException badClientRequest(final SecurityBusinessRequestedOperation requestedOp,
												 		final String msg,final Object... vars) {
		return new SecurityBusinessOperationException(requestedOp,
										Strings.customized(msg,vars),
										SecurityBusinessOperationErrorType.BAD_REQUEST_DATA);
	}
}
