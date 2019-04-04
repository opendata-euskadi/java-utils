package r01f.jwt.verifier;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(prefix="_")
public class JWTVerificationResultAsNotValidToken
  implements  JWTVerificationResult,Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Setter @Getter Collection<JWTVerificationResultInvalidCause> _cause;

///////////////////////////////////////////////////////////
//	METHOS TO IMPLEMENT
///////////////////////////////////////////////////////////
	@Override
	public boolean isValidJWT(){
		return false;
	}
	@Override
	public boolean isNotValidJWT() {
		return !isValidJWT();
	}
	@Override
	public CharSequence debugInfo() {
		StringBuffer sb = new StringBuffer();
		for (JWTVerificationResultInvalidCause c:_cause){
			sb.append(c.name()); };
		return sb.toString();
	}
	@Override @SuppressWarnings("unchecked")
	public <R extends JWTVerificationResult> R as(final Class<R> toCast) {
		return (R)this;
	}

}
