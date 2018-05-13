package r01f.model.otp.operations;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.util.types.Strings;

/**
 * Builder type for {@link OTPOperationExecResult}-implementing types:
 * <ul>
 * 		<li>A successful operation execution result: {@link OTPOperationExecOK}</li>
 * 		<li>An error on a FIND operation execution:  {@link OTPExecError}</li>
 * </ul>
 * If the operation execution was successful:
 * <pre class='brush:java'>
 * 		OTPOperationExecOK<MyReturnedObjType> opOK = OTPOperationExecResultBuilder.executed("an operation")
 * 																				     .returning(myReturnedObjTypeInstance);
 * </pre>
 * If an error is raised while executing the OTP operation:
 * <pre class='brush:java'>
 * 		OTPOperationExecError<MyReturnedObjType> opError = OTPOperationExecResultBuilder.notExecuted("an operation")
 *			  																							.because(error);
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class OTPOperationExecResultBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static OTPOperationExecResultBuilderReturnedObjStep executed(final String requestedOpName) {
		return new OTPOperationExecResultBuilder() {/* nothing */}
		           .new OTPOperationExecResultBuilderReturnedObjStep(requestedOpName);
	}
	public static OTPOperationExecResultBuilderErrorStep notExecuted(final String requestedOpName) {
		return  new OTPOperationExecResultBuilder() {/* nothing */}
		          .new OTPOperationExecResultBuilderErrorStep( requestedOpName);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  Operation
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class OTPOperationExecResultBuilderReturnedObjStep {

		protected final String _requestedOpName;

		public <T> OTPOperationExecOK<T> returning(final T instance) {
			OTPOperationExecOK<T> outOpOK = new OTPOperationExecOK<T>();
			outOpOK.setRequestedOperationName(_requestedOpName);
			outOpOK.setOperationExecResult(instance);
			return outOpOK;
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ERROR
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class OTPOperationExecResultBuilderErrorStep {
		protected final String _requestedOpName;


		public <T> OTPExecError<T> because(final Throwable th) {
			OTPExecError<T> outError = new OTPExecError<T>(th);
			outError.setRequestedOperationName(_requestedOpName);
			return outError;
		}
		public <T> OTPExecError<T> because(final String error,
										 final OTPErrorType errType) {
			OTPExecError<T> outError = new OTPExecError<T>(error,
																							 			 errType);
			outError.setRequestedOperationName(_requestedOpName);
			return outError;
		}
		public <T> OTPExecError<T> becauseClientBadRequest(final String msg,final Object... vars) {
			OTPExecError<T> outError = new OTPExecError<T>(Strings.customized(msg,vars),			// the error message
											     		 					   		   	     			 OTPErrorType.BAD_REQUEST_DATA);// is a client error
			outError.setRequestedOperationName(_requestedOpName);
			return outError;
		}

	}
}
