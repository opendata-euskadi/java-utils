package r01f.jwt.verifier;

import r01f.jwt.JWT;

/**
 * JW verifies interface
 */
public interface JWTVerifier {

	JWTVerificationResult verify(final JWT jwt);
}
