package r01f.jwt;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.joda.time.Instant;

import com.google.common.collect.Lists;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.jwt.JWTDecoded.CustomClaim;
import r01f.jwt.JWTDecoded.Payload;
import r01f.patterns.IsBuilder;
import r01f.util.types.collections.CollectionUtils;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//JWT TOKEN BUILDER.

/* The IETF Standard : http://tools.ietf.org/html/rfc7519
	See: https://developer.atlassian.com/static/connect/docs/latest/concepts/understanding-jwt.html
	See http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-hmac

	In this use are specially relevant this claims:

	- ISSUER:  : The APP CLient Code
	- AUDIENCE : in this case the service invoked
	- EXPIRATION  :   When jwt expirates

	Use  web https://jwt.io/  to parse public claims of a JWT string.

	To use this BUILDER:

		 = JWTBuilder.createJWToken()
									.forIssuer(AppCode.forId("z99"))
									 .toAudience("p12.audience")
									 .withExpirationSeconds(1)
									 .noId()
									 .usingCustomClaims(otherFieldsAsClaims)
									 .withKey(keyBytes) <==  PKI or HMAC Key.
								.build();


*/
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class JWTBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static JWTBuilderIssuerStep createJWToken() {
		JWTInitWrapperData initData = new JWTInitWrapperData();
		initData.getPayloadData().setIssuedAt(new Date(System.currentTimeMillis()));
		return new JWTBuilder() { /* nothing */ }
					.new JWTBuilderIssuerStep(initData);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTBuilderIssuerStep {
		private final JWTInitWrapperData _initData;

		public JWTBuilderAudienceStep forIssuer(final AppCode appCode) {
			_initData.getPayloadData().setIssuer(appCode.asString());
			return new JWTBuilderAudienceStep(_initData);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTBuilderAudienceStep {
		private final JWTInitWrapperData _initData;

		public JWTBuilderExpirationStep toAudience(final String...  audience) {
			_initData.getPayloadData().setAudience(Lists.<String>newArrayList(audience));
			return new JWTBuilderExpirationStep(_initData);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTBuilderExpirationStep {
		private final JWTInitWrapperData _initData;

		public JWTBuilderIdStep withExpirationDays(final long expirationDays) {
			_initData.getPayloadData().setExpirationTime(new Instant( Calendar.getInstance().getTimeInMillis() + 1000L * 60L * 60L * 24L * expirationDays).toDate());
			return new JWTBuilderIdStep(_initData);
		}

		public JWTBuilderIdStep withExpirationSeconds(final long expirationSeconds) {
			_initData.getPayloadData().setExpirationTime(new Instant( Calendar.getInstance().getTimeInMillis() + 1000L * expirationSeconds).toDate());
			return new JWTBuilderIdStep(_initData);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTBuilderIdStep {
		private final JWTInitWrapperData _initData;

		public JWTBuilderCustomClaimsStep withJWTId(final String jwtId) {
			_initData.getPayloadData().setJti(jwtId);
			return new JWTBuilderCustomClaimsStep(_initData);
		}
		public JWTBuilderCustomClaimsStep noId() {
			return new JWTBuilderCustomClaimsStep(_initData);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTBuilderCustomClaimsStep {
		private final JWTInitWrapperData _initData;

		public JWTBuilderKeyStep withoutCustomClaims() {
			return new JWTBuilderKeyStep(_initData);
		}
		public JWTBuilderCustomClaimsStep usingCustomClaim(final String name,final String customClaim) {
			if (CollectionUtils.isNullOrEmpty(_initData.getPayloadData().getCustomClaims())){
				_initData.getPayloadData().setCustomClaim(name, customClaim);
			} else {
				  Collection<CustomClaim> claims =  Lists.newArrayList(_initData.getPayloadData().getCustomClaims());
				  CustomClaim cc = new CustomClaim();
				  cc.setName(name);
				  cc.setValue(customClaim);
				  claims.add(cc);
				  _initData.getPayloadData().setCustomClaims(claims);
			}
			return this;
		}
		public JWTBuilderCustomClaimsStep usingCustomClaims(final Map<String,String> customClaimsAsStringValueMap) {
			if (CollectionUtils.isNullOrEmpty(customClaimsAsStringValueMap)){
				return this;
			}
			if (CollectionUtils.isNullOrEmpty(_initData.getPayloadData().getCustomClaims())){
				for (Map.Entry<String, String> entry : customClaimsAsStringValueMap.entrySet()){
					_initData.getPayloadData().setCustomClaim((String)entry.getKey(),(String) entry.getValue());
				}
			} else {
			  Collection<CustomClaim> claims =  Lists.newArrayList(_initData.getPayloadData().getCustomClaims());
			  for (Map.Entry<String, String> entry : customClaimsAsStringValueMap.entrySet()){
				  String claimsetName = entry.getKey();
				  String claimsetValue = entry.getValue();
				  CustomClaim cc = new CustomClaim();
				  cc.setName(claimsetName);
				  cc.setValue(claimsetValue);
				  claims.add(cc);
			   }
			  _initData.getPayloadData().setCustomClaims(claims);

			}
			return this;
		}
		public JWTBuilderBuildStep withKey(final byte[] key) {
			return new JWTBuilderKeyStep(_initData)
									.withKey(key);
		}
		public JWTBuilderBuildStep withPrivateKey(final RSAPrivateKey privateKey) {
			return new JWTBuilderKeyStep(_initData)
					        .withPrivateKey(privateKey);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTBuilderKeyStep {
		private final JWTInitWrapperData _initData;

		public JWTBuilderBuildStep  withKey(final byte[] key) {
			_initData.setKey(key);
			return new JWTBuilderBuildStep(_initData);
		}

		public JWTBuilderBuildStep withPrivateKey(final RSAPrivateKey privateKey) {
			_initData.setPrivateKey(privateKey);
			return new JWTBuilderBuildStep(_initData);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTBuilderBuildStep {
		private final JWTInitWrapperData _initData;

		public JWT build() {
			SignedJWT signedJWT = null;
        	JWSSigner signer = null;
			try {
			    JWTClaimsSet claimsSet = null;
			    if (CollectionUtils.hasData(_initData.getPayloadData().getCustomClaims())){


		        	 claimsSet = new JWTClaimsSet.Builder(_getCustomClaims(_initData.getPayloadData().getCustomClaims()))
							        			     .subject(_initData.getPayloadData().getSubject())
							        			     .audience(_initData.getPayloadData().getAudience())
							        			     .expirationTime(_initData.getPayloadData().getExpirationTime())
							        			     .issuer(_initData.getPayloadData().getIssuer())
							        			     .issueTime(_initData.getPayloadData().getIssuedAt())
							        			     .jwtID(_initData.getPayloadData().getJti())
		        			                     .build();
			    } else {

				   	 claimsSet = new JWTClaimsSet.Builder()
							        			     .subject(_initData.getPayloadData().getSubject())
							        			     .audience(_initData.getPayloadData().getAudience())
							        			     .expirationTime(_initData.getPayloadData().getExpirationTime())
							        			     .issuer(_initData.getPayloadData().getIssuer())
							        			     .issueTime(_initData.getPayloadData().getIssuedAt())
							        			     .jwtID(_initData.getPayloadData().getJti())
						        			   .build();

			    }
	        	///////////////// HMAC BASED
	        	if (_initData.getKey() != null) {
	        		signedJWT =  new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
	        		signer =  new MACSigner(_initData.getKey());
	        	//////////// PRIVATE/PUBLIC KEY BASED
	        	} else if (_initData.getPrivateKey() != null  ) {
	        		signedJWT =  new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
	        		signer = new RSASSASigner( _initData.getPrivateKey());

	        	}
				signedJWT.sign(signer);
			    String serialized = signedJWT.serialize();
			    return JWT.forValue(serialized);
			} catch (final JOSEException e) {
				e.printStackTrace();
				throw new JWTBuilderImplException(e.getLocalizedMessage());
			}
		}
		private JWTClaimsSet _getCustomClaims(final Collection<CustomClaim> customClaims) {
			JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
			for (CustomClaim cc : customClaims) {
		     	builder = builder.claim(cc.getName(), cc.getValue());
			}
			return builder.build();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	INNER WRAPPER HELPER CLASSES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * JWTInitWrapperData
	 */
	@Accessors(prefix="_")
	@NoArgsConstructor
	public static class JWTInitWrapperData
	         implements Serializable {
		private static final long serialVersionUID = 5787425585729960483L;

		@Getter @Setter  private Payload _payloadData = new Payload();
		@Getter @Setter  private byte[]  _key ;
		@Getter @Setter  private PrivateKey _privateKey;
	}
	public static class JWTBuilderImplException
			    extends RuntimeException {
		private static final long serialVersionUID = -8313498571229772866L;
		public JWTBuilderImplException(final String msg) {
			super(msg);
		}
	}
}
