package r01f.model.otp.operations;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="otpOperationExecOK")
@Accessors(prefix="_")
public class OTPOperationExecOK<T>
	 extends OTPOperationExecResult<T>
  implements OTPOperationOK {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="opExecResult")
	@Getter @Setter protected T _operationExecResult;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public T getOrThrow() throws OTPException {
		return _operationExecResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public OTPExecError<T> asOperationExecError() {
		throw new ClassCastException();
	}
	@Override
	public OTPOperationExecOK<T> asOperationExecOK() {
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
						  		  _requestedOperationName,
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
