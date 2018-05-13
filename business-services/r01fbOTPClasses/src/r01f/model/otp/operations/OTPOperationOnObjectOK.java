package r01f.model.otp.operations;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
abstract class OTPOperationOnObjectOK<T>
	   extends OTPOperationExecOK<T>
    implements OTPOperationOnObjectResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE DATA
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The type of the entity subject of the requested operation 
	 */
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Class<T> _objectType;
	/**
	 * The requested operation
	 */
	@MarshallField(as="requestedOperation",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected OTPRequestedOperation _requestedOperation;
	/**
	 * The performed operation
	 * Sometimes the requested operation is NOT the same as the requested operation since
	 * for example, the client requests a create operation BUT an update operation is really 
	 * performed because the record already exists at the persistence store
	 */
	@MarshallField(as="performedOperation",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected OTPPerformedOperation _performedOperation;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public OTPOperationOnObjectOK() {
		/* nothing */
	}
	@SuppressWarnings("unchecked")
	OTPOperationOnObjectOK(final Class<?> entityType,
					  			      final OTPRequestedOperation reqOp,final OTPPerformedOperation performedOp) {
		_objectType = (Class<T>)entityType;
		_requestedOperation = reqOp;
		_performedOperation = performedOp;
		_requestedOperationName = reqOp.name();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getRequestedOperationName() {
		return _requestedOperation != null ? _requestedOperation.name() 
										   : "unknown persistence operation";
	}
}
