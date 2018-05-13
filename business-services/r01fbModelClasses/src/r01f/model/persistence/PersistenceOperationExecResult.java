package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
public abstract class PersistenceOperationExecResult<T> 
    	   implements PersistenceOperationResult,
    	   			  Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="requestedOperationName",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected String _requestedOperationName;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public PersistenceOperationExecResult() {
		/* nothing */
	} 
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets the operation execution returned object
	 * @return the persistence operation returned object or throw a {@link PersistenceException} if the 
	 *  	   operation execution was not successful
	 * @throws PersistenceException
	 */
	public T getOrThrow() throws PersistenceException {
		if (this.hasFailed()) this.asOperationExecError()		
								  .throwAsPersistenceException();
		return this.asOperationExecOK()
				   .getOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean hasFailed() {
		return this instanceof PersistenceOperationError;
	}

	@Override
	public boolean hasSucceeded() {
		return this instanceof PersistenceOperationOK;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public abstract PersistenceOperationExecError<T> asOperationExecError();
	public abstract PersistenceOperationExecOK<T> asOperationExecOK();
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <R extends PersistenceOperationResult> R as(final Class<R> type) {
		return (R)this;
	}
}
