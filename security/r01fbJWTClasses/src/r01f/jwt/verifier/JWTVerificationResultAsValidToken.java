package r01f.jwt.verifier;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(prefix="_")
public class JWTVerificationResultAsValidToken
  implements  JWTVerificationResult {
///////////////////////////////////////////////////////////
// 	METHOS TO IMPLEMENT
///////////////////////////////////////////////////////////
	@Override
	public boolean isValidJWT(){
		return true;
	}
	@Override
	public boolean isNotValidJWT() {
		return !isValidJWT();
	}
	@Override @SuppressWarnings("unchecked")
	public <R extends JWTVerificationResult> R as(final Class<R> toCast) {
		return (R)this;
	}
}
