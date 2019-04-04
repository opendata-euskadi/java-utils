package r01f.jwt.verifier;

public interface JWTVerificationResult {

	boolean isValidJWT();
	boolean isNotValidJWT();

	<R extends JWTVerificationResult> R as (final Class<R> toCast);
}
