package r01f.model.otp.oids;

import java.util.UUID;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;

public abstract class OTPOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	BASE
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface IsOTPOID
	                extends OIDTyped<String> {
		// just a marker interface

	}
	/**
	 * Base for every otp oid objects
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class OTPOIDBase
	              		 extends OIDBaseMutable<String>
			   		  implements IsOTPOID {
		private static final long serialVersionUID = 7778880230670929915L;

		public OTPOIDBase(final String id) {
			super(id);
		}
	}
	/**
	 * Base for every  OTP oid objects
	 */
	@Immutable
	@NoArgsConstructor
	public static abstract class OTPModelObjectOIDBase
	                     extends OIDBaseMutable<String> // normally this should extend OIDBaseImmutable BUT it MUST have a default no-args constructor to be serializable
			          implements IsOTPOID  {
		private static final long serialVersionUID = 8996483673956096810L;
		public OTPModelObjectOIDBase(final String id) {
			super(id);
		}
		/**
		 * Generates an oid
		 * @return
		 */
		public static String supplyOidAsString() {
		    UUID uuid = UUID.randomUUID();
	        return uuid.toString();
		}

		public String convertToDatabaseColumn(final OID attribute) {
			return attribute.asString();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@NoArgsConstructor
	public static class OTPOID
    			extends OTPModelObjectOIDBase
    	     implements IsOTPOID  {
		private static final long serialVersionUID = -5379507184826775226L;

		public OTPOID(String oidAsString) {
			super(oidAsString);
		}
		public static OTPOID supplyOid() {
	        return new OTPOID(supplyOidAsString());
		}
		public static OTPOID forId(final String otpOid) {
			return new OTPOID(otpOid);
		}
		public static OTPOID valueOf(final String otpOid) {
			return new OTPOID(otpOid);
		}
	}
	public static class OTPRequestOID
		extends OTPModelObjectOIDBase implements OID  {

		private static final long serialVersionUID = 8605402146718882592L;

		public OTPRequestOID(final String oidAsString) {
			super(oidAsString);
		}
		public static OTPRequestOID supplyOid() {
	        return new OTPRequestOID(supplyOidAsString());
		}
		public static OTPRequestOID forId(final String otpOid) {
			return new OTPRequestOID(otpOid);
		}
		public static OTPRequestOID valueOf(final String otpOid) {
			return new OTPRequestOID(otpOid);
		}
	}
}
