package r01f.model.otp.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.otp.oids.OTPOIDs.OTPOID;
import r01f.model.otp.oids.OTPOIDs.OTPRequestOID;
import r01f.objectstreamer.annotations.MarshallType;


@MarshallType(as="otpResponse")
@Accessors(prefix="_")
@NoArgsConstructor
public class OTPResponse implements Serializable {
	private static final long serialVersionUID = 5498390384328599485L;
////////////////////////////////////////////////////////////////////////////
// MEMBERS
////////////////////////////////////////////////////////////////////////////
	@Setter @Getter OTPOID _otpOID;
	@Setter @Getter OTPRequestOID _otpRequestOID;

}