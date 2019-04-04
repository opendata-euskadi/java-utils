package r01f.jwt.verifier;

import java.io.Serializable;
import java.security.interfaces.RSAPublicKey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.patterns.IsBuilder;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//JWT TOKEN VERIFIER .

/*				JWTVerifier verifier = JWTVerifierBuilder.createVerifier()
	                                                         .usingKey("cortomaltese".getBytes())
	                                                         .checkingExpiration()
	                                                         .forAudience("theAudienceForWhoThisTokenWasIssued")
                                                         .buid();
		          verifier.verify(myJWT);
 **/
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class JWTVerifierBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static JWTVerifierBuilderKeyStep createVerifier() {
		JWTVerifierBuilderInitWrapperData initData = new JWTVerifierBuilderInitWrapperData();

		return new JWTVerifierBuilder() { /* nothing */ }
					.new JWTVerifierBuilderKeyStep(initData);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTVerifierBuilderKeyStep {
		private final JWTVerifierBuilderInitWrapperData _initData;

		public JWTVerifierBuilderExpirationStep  usingKey(final byte[] key) {
			_initData.setKey(key);
			return new JWTVerifierBuilderExpirationStep(_initData);
		}

		public JWTVerifierBuilderExpirationStep usingPublicKey(final RSAPublicKey pk) {
			_initData.setPublicKey(pk);
			return new JWTVerifierBuilderExpirationStep(_initData);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTVerifierBuilderExpirationStep {
		private final JWTVerifierBuilderInitWrapperData _initData;

		public JWTVerifierBuilderAudienceStep checkingExpiration() {
			_initData.setCheckExpiration(true);
			return new JWTVerifierBuilderAudienceStep(_initData);
		}

		public JWTVerifierBuilderAudienceStep ignoreExpiration() {
			_initData.setCheckExpiration(false);
			return new JWTVerifierBuilderAudienceStep(_initData);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTVerifierBuilderAudienceStep {
		private final JWTVerifierBuilderInitWrapperData _initData;

		public JWTVerifierBuilderBuildStep ignoreAudience() {
			return new JWTVerifierBuilderBuildStep(_initData);
		}
		public JWTVerifierBuilderBuildStep forAudience(final String audience ) {
			_initData.setAudienceToCheck(audience);
			return new JWTVerifierBuilderBuildStep(_initData);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class JWTVerifierBuilderBuildStep {
		private final JWTVerifierBuilderInitWrapperData _initData;

		public JWTVerifier build() {
			JWSVerifier verifierProvider = null;
			try {
				if (_initData.getKey() != null){
					verifierProvider = new MACVerifier(_initData.getKey());

				} else {
					verifierProvider = new RSASSAVerifier(_initData.getPublicKey());
				}
				return new JWTVerifierImpl(verifierProvider,_initData._audienceToCheck,_initData._checkExpiration );

			} catch (JOSEException e) {
				e.printStackTrace();
				throw new JWTVerifierBuilderImplException(e.getLocalizedMessage());

			}

		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
// INNER WRAPPER HELPER CLASSES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * JWTInitVerifierWrapperData
	 */
	@Accessors(prefix="_")
	public static class JWTVerifierBuilderInitWrapperData
	         implements Serializable {

		private static final long serialVersionUID = 5787425585729960483L;

		@Getter @Setter  private  byte[]  _key ;
		@Getter @Setter  private RSAPublicKey _publicKey;
		@Getter @Setter  boolean _checkExpiration;
		@Getter @Setter  String _audienceToCheck;

	}
	public static class JWTVerifierBuilderImplException
	            extends RuntimeException {
		private static final long serialVersionUID = -8313498571229772866L;
		public JWTVerifierBuilderImplException(final String msg) {
			super(msg);
		}
	}
}
