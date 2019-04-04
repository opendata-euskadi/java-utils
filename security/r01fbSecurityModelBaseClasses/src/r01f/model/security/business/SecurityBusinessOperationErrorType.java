package r01f.model.security.business;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.exceptions.EnrichedThrowableSubType;
import r01f.exceptions.EnrichedThrowableSubTypeWrapper;
import r01f.exceptions.ExceptionSeverity;
import r01f.exceptions.Throwables;

/**
 * Persistence error codes
 */
@Accessors(prefix="_")
public enum SecurityBusinessOperationErrorType
 implements EnrichedThrowableSubType<SecurityBusinessOperationErrorType> {

	SERVER_ERROR						(100 + 1),		// a server error (ie pool exhausted, no connection, etc)
	CLIENT_CANNOT_CONNECT_SERVER		(100 + 2),		// the client cannot reach the server
	ILLEGAL_STATUS						(100 + 3),		// the entity to be persisted is in an illegal status in the db so the persistence operation cannot continue
	BAD_REQUEST_DATA					(100 + 4),		// the request data is not enougth to execute the persistence operation
	UNKNOWN								(100);

	public static final transient int BUSINESS = 100;	// change enums if this changes

	@Getter private final int _group = BUSINESS;
	@Getter private final int _code;

	private SecurityBusinessOperationErrorType(final int code) {
		_code = code;
	}

	private static EnrichedThrowableSubTypeWrapper<SecurityBusinessOperationErrorType> WRAPPER = EnrichedThrowableSubTypeWrapper.create(SecurityBusinessOperationErrorType.class);

	public static SecurityBusinessOperationErrorType from(final int errorCode) {
		return WRAPPER.from(BUSINESS,errorCode);
	}
	public static SecurityBusinessOperationErrorType from(final int groupCode,final int errorCode) {
		if (groupCode != BUSINESS) throw new IllegalArgumentException(Throwables.message("The group code for a {} MUST be {}",
																							SecurityBusinessOperationException.class,BUSINESS));
		return WRAPPER.from(BUSINESS,errorCode);
	}
	public static SecurityBusinessOperationErrorType fromName(final String name) {
		return WRAPPER.fromName(name);
	}
	@Override
	public boolean is(final int group,final int code) {
		return WRAPPER.is(this,
						  group,code);
	}
	public boolean is(final int code) {
		return this.is(BUSINESS,code);
	}
	@Override
	public boolean isIn(final SecurityBusinessOperationErrorType... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final SecurityBusinessOperationErrorType el) {
		return WRAPPER.is(this,el);
	}
	public boolean isServerError() {
		return this == SERVER_ERROR;
	}
	public boolean isClientError() {
		return !this.isServerError();
	}
	@Override
	public ExceptionSeverity getSeverity() {
		ExceptionSeverity outSeverity = null;
		switch(this) {
		case BAD_REQUEST_DATA:
			outSeverity = ExceptionSeverity.RECOVERABLE;
			break;
		case SERVER_ERROR:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		case CLIENT_CANNOT_CONNECT_SERVER:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		case UNKNOWN:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		default:
			outSeverity = ExceptionSeverity.FATAL;
			break;
		}
		return outSeverity;
	}
}