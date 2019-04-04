package r01f.model.security.business;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
abstract class SecuirtyBusinessOperationOnObjectOK<T>
	   extends SecurityBusinessOperationExecOK<T>
    implements SecurityBusinessOperationOnObjectResult<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE DATA
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The type of the entity subject of the requested operation
	 */
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Class<T> _objectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public SecuirtyBusinessOperationOnObjectOK(final SecurityBusinessRequestedOperation reqOp,final SecurityBusinessPerformedOperation perfOp) {
		super(reqOp,perfOp);
	}
	@SuppressWarnings("unchecked")
	SecuirtyBusinessOperationOnObjectOK(final SecurityBusinessRequestedOperation reqOp,final SecurityBusinessPerformedOperation perfOp,
								   final Class<?> entityType) {
		this(reqOp,perfOp);
		_objectType = (Class<T>)entityType;
		_requestedOperation = reqOp;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getRequestedOperationName() {
		return _requestedOperation != null ? _requestedOperation.getName()
										   : "unknown persistence operation";
	}
}
