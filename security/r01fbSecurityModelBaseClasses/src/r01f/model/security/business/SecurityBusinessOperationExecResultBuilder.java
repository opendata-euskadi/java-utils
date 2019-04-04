package r01f.model.security.business;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.securitycontext.SecurityContext;

/**
 * Builder type for {@link OperationExecResult}-implementing types:
 * <ul>
 * 		<li>A successful operation execution result: {@link OperationExecOK}</li>
 * 		<li>An error on a FIND operation execution: {@link OperationExecError}</li>
 * </ul>
 * If the operation execution was successful:
 * <pre class='brush:java'>
 * 		BusinessOperationExecOK<MyReturnedObjType> opOK = BusinessOperationExecResultBuilder.using(securityContext)
 * 																	   			   				  .executed("an operation")
 * 																								  .returning(myReturnedObjTypeInstance);
 * </pre>
 * If an error is raised while executing the Business operation:
 * <pre class='brush:java'>
 * 		BusinessOperationExecError<MyReturnedObjType> opError = BusinessOperationExecResultBuilder.using(securityContext)
 *			  																			 		    	.notExecuted("an operation")
 *			  																								.because(error);
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class SecurityBusinessOperationExecResultBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static BusinessOperationExecResultBuilderResultStep using(final SecurityContext securityContext) {
		return new SecurityBusinessOperationExecResultBuilder() {/* nothing */ }
						.new BusinessOperationExecResultBuilderResultStep(securityContext);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BusinessOperationExecResultBuilderResultStep {
		private final SecurityContext _securityContext;

		public BusinessOperationExecResultBuilderReturnedObjStep executed(final SecurityBusinessRequestedOperation requestedOp,
																				final SecurityBusinessPerformedOperation performedOp) {
			return new BusinessOperationExecResultBuilderReturnedObjStep(_securityContext,
																				requestedOp,
																			    performedOp);
		}
		public BusinessOperationExecResultBuilderErrorStep notExecuted(final SecurityBusinessRequestedOperation requestedOp) {
			return new BusinessOperationExecResultBuilderErrorStep(_securityContext,
																		  requestedOp);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Operation
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BusinessOperationExecResultBuilderReturnedObjStep {
		protected final SecurityContext _securityContext;
		protected final SecurityBusinessRequestedOperation _requestedOp;
		protected final SecurityBusinessPerformedOperation _performedOp;

		public <T> SecurityBusinessOperationExecOK<T> returning(final T instance) {
			SecurityBusinessOperationExecOK<T> outOpOK = new SecurityBusinessOperationExecOK<T>(_requestedOp, _performedOp);
			outOpOK.setOperationExecResult(instance);
			return outOpOK;
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ERROR
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class BusinessOperationExecResultBuilderErrorStep {
		protected final SecurityContext _securityContext;
		protected final SecurityBusinessRequestedOperation _requestedOp;

		public <T> SecurityBusinessOperationExecError<T> because(final String error,
												 		 final SecurityBusinessOperationErrorType errType) {
			SecurityBusinessOperationExecError<T> outError = new SecurityBusinessOperationExecError<T>(_requestedOp);
			outError.setErrorType(errType);
			return outError;
		}

	}
}
