package r01f.model.otp.dispatch;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.model.otp.oids.OTPOIDs.OTPOID;
import r01f.objectstreamer.annotations.MarshallType;


@MarshallType(as="otpDispatchRequest")
@Accessors(prefix="_")
public class OTPDispatchRequest implements Serializable {
	private static final long serialVersionUID = 4935499367686559959L;
///////////////////////////////////////////////////////////////////////////////////////
// MEMBERS
///////////////////////////////////////////////////////////////////////////////////////
	@Setter @Getter OTPOID _otpOid;
	@Setter @Getter AppCode       _appCode;
	@Setter @Getter AppComponent  _appComponent;
	@Setter @Getter OTPPresentationData _presentationData;

}