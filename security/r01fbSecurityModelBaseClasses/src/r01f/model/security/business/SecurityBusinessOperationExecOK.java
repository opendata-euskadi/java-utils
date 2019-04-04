package r01f.model.security.business;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="businessOperationResult",typeId="ok")
@Accessors(prefix="_")
public class SecurityBusinessOperationExecOK<T>
	 extends SecurityBusinessOperationExecResult<T>
  implements SecurityBusinessOperationOK {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The performed operation
	 * Sometimes the requested operation is NOT the same as the requested operation since
	 * for example, the client requests a create operation BUT an update operation is really
	 * performed because the record already exists at the persistence store
	 */
	@MarshallField(as="performedOperation",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected SecurityBusinessPerformedOperation _performedOperation;
	/**
	 * The result
	 */
	@MarshallField(as="operationExecResult",
				   whenXml=@MarshallFieldAsXml(collectionElementName="resultItem"))		// only when the result is a Collection (ie: find ops)
	@Getter @Setter protected T _operationExecResult;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityBusinessOperationExecOK(final SecurityBusinessRequestedOperation reqOp, final SecurityBusinessPerformedOperation perfOp) {
		super(reqOp);
		_performedOperation = perfOp;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public T getOrThrow() throws SecurityBusinessOperationException {
		return _operationExecResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public SecurityBusinessOperationExecError<T> asOperationExecError() {
		throw new ClassCastException();
	}
	@Override
	public SecurityBusinessOperationExecOK<T> asOperationExecOK() {
		return this;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getDetailedMessage() {
		// info about the returned object
		String resultInfo = null;
		if (_operationExecResult != null) {
			if (CollectionUtils.isCollection(_operationExecResult.getClass())) {
				resultInfo = Strings.customized("Collection of {} objects",
												CollectionUtils.safeSize((Collection<?>)_operationExecResult));
			} else {
				resultInfo = Strings.customized("an object of type {}",
												_operationExecResult.getClass());
			}
		} else {
			resultInfo = "null";
		}
		// the debug info
		return Strings.customized("The execution of '{}' operation was SUCCESSFUL returning {}",
						  		  _requestedOperation.getName(),
						  		  resultInfo);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return this.getDetailedMessage();
	}
}
