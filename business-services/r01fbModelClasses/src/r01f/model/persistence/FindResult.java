package r01f.model.persistence;

import java.util.Collection;

import r01f.debug.Debuggable;

public interface FindResult<T> 
   		 extends PersistenceOperationResult,
   		  		 Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the found entities or throws a {@link PersistenceException} 
	 * if the find operation resulted on an error
	 * @return
	 */
	public Collection<T> getOrThrow() throws PersistenceException;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link FindOK}
	 */
	public FindOK<T> asFindOK();
	/**
	 * @return a {@link FindError}
	 */
	public FindError<T> asFindError();
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
}