package r01f.model.otp.validation;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="otpValidationResponse")
@Accessors(prefix="_")
public class OTPValidationResponse
  implements Serializable {

	private static final long serialVersionUID = -8128342400403464333L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Setter @Getter public boolean _validOtp = false;
	@Setter @Getter public Long _otpValidationRetriesToExpire = null;

}