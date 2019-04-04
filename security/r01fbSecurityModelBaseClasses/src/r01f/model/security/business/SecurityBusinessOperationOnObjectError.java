package r01f.model.security.business;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.util.types.collections.CollectionUtils;


@Accessors(prefix="_")
abstract class SecurityBusinessOperationOnObjectError<T>
       extends SecurityBusinessOperationExecError<T>
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
	/**
	 * Some data about the requested operation target entity such as it's oid
	 */
	@MarshallField(as="requestedOperationTarget")
	@Getter @Setter protected Map<String,String> _requestedOperationTargetEntityIdInfo;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public SecurityBusinessOperationOnObjectError(final SecurityBusinessRequestedOperation reqOp) {
		super(reqOp);
	}
	@SuppressWarnings("unchecked")
	public SecurityBusinessOperationOnObjectError(final SecurityBusinessRequestedOperation reqOp,
											 final Class<?> entityType) {
		super(reqOp);
		_objectType = (Class<T>)entityType;
	}
	SecurityBusinessOperationOnObjectError(final SecurityBusinessRequestedOperation reqOp,
									  final Class<?> entityType,
		 				 			  final Throwable th) {
		this(reqOp,
			 entityType);
		_error = th;
		if (th != null) {
			_errorMessage = th.getMessage();
			_errorDebug = Throwables.getStackTraceAsString(th);
			if (th instanceof SecurityBusinessOperationException) {
				SecurityBusinessOperationException persistEx = (SecurityBusinessOperationException)th;
				_errorType = persistEx.getPersistenceErrorType();
			} else {
				_errorType = SecurityBusinessOperationErrorType.SERVER_ERROR;		// a server error by default

			}
		}
	}
	SecurityBusinessOperationOnObjectError(final SecurityBusinessRequestedOperation reqOp,
									  final Class<?> entityType,
						 			  final SecurityBusinessOperationErrorType errCode) {
		this(reqOp,
			 entityType,
			 (Throwable)null);		// no exception
		_errorDebug = null;
		_errorType = errCode;
	}
	SecurityBusinessOperationOnObjectError(final SecurityBusinessRequestedOperation reqOp,
									  final Class<?> entityType,
						 			  final String errMsg,final SecurityBusinessOperationErrorType errCode) {
		this(reqOp,
			 entityType,
			 (Throwable)null);		// no exception
		_errorMessage = errMsg;
		_errorDebug = null;
		_errorType = errCode;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public SecurityBusinessPerformedOperation getPerformedOperation() {
		return null;	// no performed operation
	}
	protected void addTargetEntityIdInfo(final String field,final String value) {
		if (_requestedOperationTargetEntityIdInfo == null) _requestedOperationTargetEntityIdInfo = Maps.newHashMap();
		_requestedOperationTargetEntityIdInfo.put(field,value);
	}
	/**
	 * @return any info about the target entity such as it's oid
	 */
	protected String getTargetEntityIdInfo() {
		String outIdInfo = null;
		if (CollectionUtils.hasData(_requestedOperationTargetEntityIdInfo)) {
			StringBuilder sb = new StringBuilder();
			for (Iterator<Map.Entry<String,String>> meIt =_requestedOperationTargetEntityIdInfo.entrySet().iterator(); meIt.hasNext(); ) {
				Map.Entry<String,String> me = meIt.next();
				sb.append(me.getKey())
				  .append("=")
				  .append(me.getValue());
				if (meIt.hasNext()) sb.append(", ");
			}
			outIdInfo = sb.toString();
		}
		return outIdInfo != null ? outIdInfo : "unknown";
	}

}
