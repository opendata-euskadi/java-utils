package r01f.model.otp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.OID;
import r01f.model.otp.oids.OTPOIDs.OTPOID;
import r01f.model.otp.oids.OTPOIDs.OTPRequestOID;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@MarshallType(as="otpData")
@Accessors(prefix="_")
@NoArgsConstructor
public class OTPData
  implements OTPModelObject<OTPOID> {

	private static final long serialVersionUID = -680786326519889653L;
///////////////////////////////////////////////////////////////////////////////////////
// BASIC FIELDS
///////////////////////////////////////////////////////////////////////////////////////
	@Setter @Getter OTPOID _oid;	        // The OID of the generated OTP
	@Setter @Getter OTPRequestOID _otpRequestOid;   // The OID of the requesting otp request)
	@Setter @Getter AppCode       _appCode;  //  The requesting app code
	@Setter @Getter AppComponent  _appComponent;     //  Requesting  APPOde If needed
	@Setter @Getter Password _value;     //  The OTP Generated Value;
	@Setter @Getter long _otpRetries = 0;

	// If OTPOID is not available _phone o email could be used as identifier of OTP.
	@Setter @Getter Phone  _phone;
	@Setter @Getter EMail  _email;


	@Override
	public void unsafeSetOid(final OID oid) {
		setOid((OTPOID) oid);
	}
///////////////////////////////////////////////////////////////////////////////////////
// PUBLIC METHOS
///////////////////////////////////////////////////////////////////////////////////////
	public long decrementNumberOfRetries() {
		return --_otpRetries;
	}

}