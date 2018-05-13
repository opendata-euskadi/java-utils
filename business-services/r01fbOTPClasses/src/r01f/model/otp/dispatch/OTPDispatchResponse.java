package r01f.model.otp.dispatch;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="otpDispatchResponse")
@Accessors(prefix="_")
public class OTPDispatchResponse implements Serializable {

	private static final long serialVersionUID = -4805574268683253492L;
	@Setter @Getter public boolean _dispatchOk = false;

}