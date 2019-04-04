package r01f.model.security.business;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
public abstract class SecurityBusinessOperationExecResult<T>
    	   implements SecurityBusinessOperationResult,
    	   			  Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The requested operation
	 */
	@MarshallField(as="requestedOperation",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected SecurityBusinessRequestedOperation _requestedOperation;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////

	public SecurityBusinessOperationExecResult(final SecurityBusinessRequestedOperation reqOp) {
		_requestedOperation = reqOp;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the operation execution returned object
	 * @return the persistence operation returned object or throw a {@link SecurityBusinessOperationException} if the
	 *  	   operation execution was not successful
	 * @throws SecurityBusinessOperationException
	 */
	public T getOrThrow() throws SecurityBusinessOperationException {
		if (this.hasFailed()) this.asOperationExecError()
								  .throwAsBusinessException();
		return this.asOperationExecOK()
				   .getOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getRequestedOperationName() {
		return _requestedOperation != null ? _requestedOperation.getName()
										   : "unknown persistence operation";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasFailed() {
		return this instanceof SecurityBusinessOperationError;
	}

	@Override
	public boolean hasSucceeded() {
		return this instanceof SecurityBusinessOperationOK;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract SecurityBusinessOperationExecError<T> asOperationExecError();
	public abstract SecurityBusinessOperationExecOK<T> asOperationExecOK();
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <R extends SecurityBusinessOperationResult> R as(final Class<R> type) {
		return (R)this;
	}
}
