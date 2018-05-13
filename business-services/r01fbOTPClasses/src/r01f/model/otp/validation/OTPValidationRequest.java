package r01f.model.otp.validation;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.Password;
import r01f.model.otp.oids.OTPOIDs.OTPOID;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;



@MarshallType(as="otpValidationRequest")
@Accessors(prefix="_")
public class OTPValidationRequest implements Serializable {

	private static final long serialVersionUID = 1206292544725125704L;
///////////////////////////////////////////////////////////////////////////////////////
// MEMBERS
///////////////////////////////////////////////////////////////////////////////////////
	@Setter @Getter OTPOID _oid;
	
	@Setter @Getter AppCode       _appCode;

    // If OTPOID is not available _phone o email could be used as identifier of OTP.
	@Setter @Getter Phone  _phone;
	
	@Setter @Getter EMail  _email;

	@Setter @Getter Password _otpToValidate;
}