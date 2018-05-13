package r01f.model.otp.request;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.guids.OID;
import r01f.model.otp.OTPModelObject;
import r01f.model.otp.OTPType;
import r01f.model.otp.oids.OTPOIDs.OTPRequestOID;
import r01f.objectstreamer.annotations.MarshallType;


@MarshallType(as="otpRequest")
@Accessors(prefix="_")
public class OTPRequest
       implements OTPModelObject<OTPRequestOID> {
	private static final long serialVersionUID = 6848253460622291571L;
///////////////////////////////////////////////////////////////////////////////////////
// MEMBERS
///////////////////////////////////////////////////////////////////////////////////////
	@Setter @Getter OTPRequestOID _oid;
	@Setter @Getter AppCode       _appCode; // Identificador de la aplicación solicitante del OTP
	@Setter @Getter AppComponent  _appComponent;  // Servicio de la aplicación solictante
	@Setter @Getter OTPType _otpType;
	@Setter @Getter long _otpLength = 5;  //Longitud del OTP.
	@Setter @Getter long _otpLifeSeconds = -1; //  Tiempo de vida, en segundos, del OTP. -1 ilimitado.
	@Setter @Getter long _otpMaxRetries = -1;

///////////////////////////////////////////////////////////////////////////////////////
//METHODS
///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void unsafeSetOid(OID oid) {
		setOid((OTPRequestOID) oid);
	}

}