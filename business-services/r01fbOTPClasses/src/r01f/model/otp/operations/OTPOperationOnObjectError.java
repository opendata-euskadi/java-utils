package r01f.model.otp.operations;

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
abstract class OTPOperationOnObjectError<T>
       extends OTPExecError<T>
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
	 * Some data about the requested operation target entity such as it's oid
	 */
	@MarshallField(as="requestedOperationTarget")
	@Getter @Setter protected Map<String,String> _requestedOperationTargetEntityIdInfo;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public OTPOperationOnObjectError() {
		// nothing
	}
	@SuppressWarnings("unchecked")
	public OTPOperationOnObjectError(final Class<?> entityType) {
		_objectType = (Class<T>)entityType;
	}
	OTPOperationOnObjectError(final Class<?> entityType,
		 				 			     final OTPRequestedOperation requestedOp,
		 				 			     final Throwable th) {
		this(entityType);
		_requestedOperation = requestedOp;
		_requestedOperationName = requestedOp.name();
		_error = th;
		if (th != null) {
			_errorMessage = th.getMessage();
			_errorDebug = Throwables.getStackTraceAsString(th);
			if (th instanceof OTPException) {
				OTPException persistEx = (OTPException)th;
				_errorType = persistEx.getPersistenceErrorType();
			} else {
				_errorType = OTPErrorType.SERVER_ERROR;		// a server error by default

			}
		}
	}
	OTPOperationOnObjectError(final Class<?> entityType,
						 			     final OTPRequestedOperation requestedOp,
						 			     final OTPErrorType errCode) {
		this(entityType,
			 requestedOp,
			 (Throwable)null);		// no exception
		_errorDebug = null;
		_errorType = errCode;
	}
	OTPOperationOnObjectError(final Class<?> entityType,
						 			     final OTPRequestedOperation requestedOp,
						 			      final String errMsg,final OTPErrorType errCode) {
		this(entityType,
			 requestedOp,
			 (Throwable)null);		// no exception
		_errorMessage = errMsg;
		_errorDebug = null;
		_errorType = errCode;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public OTPPerformedOperation getPerformedOperation() {
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
/////////////////////////////////////////////////////////////////////////////////////////
//  REASON
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if it was a client bad request due to the requested entity was NOT found
	 */
	public boolean wasBecausOTPNOTFoundOrExpired() {
		return _errorType == OTPErrorType.OTP_VALIDATION_ERROR_OTP_DOES_NOT_EXISTS_OR_EXPIRED;
	}
	/**
	 * @return true if it was a client bad request due to the requested entity already exists and a create operation was issued
	 */
	public boolean wasBecauseNumberOfAttempsToValidateExceed() {
		return _errorType == OTPErrorType.OTP_VALIDATION_ERROR_OTP_NUMBER_OF_ATTEMPTS_EXCEEDED;
	}
	/**
	 * @return true if it was because the entity's persisted status is NOT valid
	 */
	public boolean wasBecauseClientBadRequesr() {
		return _errorType == OTPErrorType.BAD_REQUEST_DATA;
	}
}
