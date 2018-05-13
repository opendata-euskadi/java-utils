package r01f.model.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="crudResult",typeId="CRUDError")
@Accessors(prefix="_")
public class CRUDError<T>
	 extends PersistenceOperationOnObjectError<T>
  implements CRUDResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * When the un-successful operation is a create or update operation, this
	 * field contains the client-sent data
	 */
	@MarshallField(as="targetEntity")
	@Getter @Setter private T _targetEntity;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public CRUDError() {
		// nothing
	}
	CRUDError(final Class<T> entityType,
	  		  final PersistenceRequestedOperation requestedOp,
	  		  final Throwable th) {
		super(entityType,
			  requestedOp,
			  th);	
	}
	CRUDError(final Class<T> entityType,
	  		  final PersistenceRequestedOperation requestedOp,
	  		  final PersistenceErrorType errCode) {
		super(entityType,
			  requestedOp,
			  errCode);
	}
	CRUDError(final Class<T> entityType,
	  		  final PersistenceRequestedOperation requestedOp,
	  		  final String errMsg,final PersistenceErrorType errCode) {
		super(entityType,
			  requestedOp,
			  errMsg,errCode);
	}
	public CRUDError(final Class<T> entityType,
					 final CRUDError<?> otherCRUDError) {
		super(entityType);
		this.setError(otherCRUDError.getError());
		this.setErrorDebug(otherCRUDError.getErrorDebug());
		this.setErrorMessage(otherCRUDError.getErrorMessage());
		this.setErrorType(otherCRUDError.getErrorType());
		this.setExtendedErrorCode(otherCRUDError.getExtendedErrorCode());
		this.setRequestedOperation(otherCRUDError.getRequestedOperation());
		this.setRequestedOperationName(otherCRUDError.getRequestedOperationName());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CAST
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDError<T> asCRUDError() {
		return this;
	}
	@Override
	public CRUDOK<T> asCRUDOK() {
		throw new ClassCastException();
	}
}
