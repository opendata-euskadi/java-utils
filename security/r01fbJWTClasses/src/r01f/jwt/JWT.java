package r01f.jwt;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.CanBeRepresentedAsString;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//JWT TOKEN .

/* The IETF Standard : http://tools.ietf.org/html/rfc7519
 *
 *  JSON Web Token is just a String token :
 *
 *    f.e eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
 *
 * Use this class to wrap it. See alsep : https://jwt.io/
 *
 * ---> To Build a valid JWT:
 * 				              JWT myJWT = JWTBuilder.createJWToken()
														 .forIssuer(AppCode.forId("z99"))
														 .toAudience("p12.audience")
														 .withExpirationSeconds(1)
														 .noId()
														 .usingCustomClaims(otherFieldsAsClaims)
														 .withKey(keyBytes) <==  PKI or HMAC Key.
													.build();
 * ----> To Verify a valid JWT .
		  					JWTVerifier verifier = JWTVerifierBuilder.createVerifier()
		                                                             .usingKey(keyBytes)
		                                                             .checkingExpiration()
		                                                             .forAudience("theAudienceForWhoThisTokenWasIssued")
		                                                             .buid();
		                     verifier.verify(myJWT);
 *
 *
 */
@Immutable
@NoArgsConstructor
@Accessors(prefix="_")
public class JWT
  implements CanBeRepresentedAsString,
    		 Serializable {
	private static final long serialVersionUID = -3806666517358846110L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="value",
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _value;
/////////////////////////////////////////////////////////////////////////////////////////
// 	SOME CREATE METHODS METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public JWT(final String value) {
		_value = value;
	}
	public static JWT forValue(final String value) {
		return new JWT(value);
	}
	@Override
	public String asString(){
		return _value;
	}
	@Override
	public String  toString(){
		return asString();
	}
}
