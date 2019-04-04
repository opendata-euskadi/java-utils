package r01f.jwt.verifier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.jwt.JWT;
import r01f.util.types.collections.Lists;

@Slf4j
@AllArgsConstructor
public class JWTVerifierImpl
  implements JWTVerifier {
/////////////////////////////////////////////////////////////////
// 	FIELDS
////////////////////////////////////////////////////////////////
	private final JWSVerifier _verifier;
	private String _audienceToVerificate;
	private boolean _expirationVerificationEnabled;
/////////////////////////////////////////////////////////////
// METHODS TO IMPLEMENT
/////////////////////////////////////////////////////////////
	@Override
	public JWTVerificationResult verify(final JWT jwt){
		boolean jwtIsOk = true;
		Collection<JWTVerificationResultInvalidCause> invalidCauses =  Lists.newArrayList();
		SignedJWT signedJWT;
		///////////////////// Check sign validations and others ( expirations, audience, etc....
		try {
			signedJWT = SignedJWT.parse(jwt.asString());
			boolean jwtSignOK = signedJWT.verify(_verifier);
			if (jwtSignOK){
				log.warn("JWT {} signed ok ", jwt.asString());
			} else {
				jwtIsOk = false;
				invalidCauses.add(JWTVerificationResultInvalidCause.INVALID_SIGNATURE);
			}
			if ( _isJwtExpirationVerificationEnabled() ){
				Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
				if (expirationTime == null) {
					jwtIsOk = false;
					invalidCauses.add(JWTVerificationResultInvalidCause.EXPIRED);
				} else {
					Date now = new Date();
					if (now.equals(expirationTime) || now.after(expirationTime)) {
						SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
						String expVal = sdf.format(expirationTime);
						String nowVal = sdf.format(now);
						log.info("JWT expired at {}. Current time: {}", expVal, nowVal);
						jwtIsOk = false;
						invalidCauses.add(JWTVerificationResultInvalidCause.EXPIRED);
					}
				}
			}
			if (_isJwtAudienceVerificationEnabled()) {
				boolean jwtAudienceVerifiedOK = signedJWT.getJWTClaimsSet().getAudience()!=null
		                  && signedJWT.getJWTClaimsSet().getAudience().contains(_audienceToVerificate);
				if (! jwtAudienceVerifiedOK){
					jwtIsOk = false;
					invalidCauses.add(JWTVerificationResultInvalidCause.INVALID_AUDIENCE);
				}
			}
			//// BUILD RESULT
			if  (jwtIsOk){
				return new JWTVerificationResultAsValidToken();
			} else {
				return new JWTVerificationResultAsNotValidToken(invalidCauses);
			}

		} catch (final ParseException e) {
			invalidCauses.add(JWTVerificationResultInvalidCause.OTHER);
			return new JWTVerificationResultAsNotValidToken(invalidCauses);
		} catch (final JOSEException e) {
			invalidCauses.add(JWTVerificationResultInvalidCause.OTHER);
			return new JWTVerificationResultAsNotValidToken(invalidCauses);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 	PRIVATE METHODS
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean _isJwtExpirationVerificationEnabled() {
		return _expirationVerificationEnabled;
	}
	private boolean _isJwtAudienceVerificationEnabled() {
		return ( _audienceToVerificate !=  null);
	}
}
