package r01f.jwt.parser;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import r01f.jwt.JWT;
import r01f.jwt.JWTDecoded;
import r01f.jwt.JWTDecoded.CustomClaim;
import r01f.jwt.JWTDecoded.Header;
import r01f.jwt.JWTDecoded.Payload;
import r01f.jwt.JWTDecoded.Signature;
import r01f.util.types.collections.CollectionUtils;


public class JWTParserImpl
	implements JWTParser {

	@Override
	public JWTDecoded parse(final JWT jwt) {
		JWTDecoded jwtDecoded = new JWTDecoded();
		Payload payload = new Payload();
		Header header = new Header();
		Signature signature = new Signature();
		try {
			final SignedJWT sjwt = SignedJWT.parse(jwt.asString());

			//... the payload
			payload.setAudience(sjwt.getJWTClaimsSet().getAudience());
			payload.setExpirationTime(sjwt.getJWTClaimsSet().getExpirationTime());
			payload.setIssuedAt(sjwt.getJWTClaimsSet().getIssueTime());
			payload.setIssuer(sjwt.getJWTClaimsSet().getIssuer());
			payload.setJti(sjwt.getJWTClaimsSet().getJWTID());
			payload.setNotBefore(sjwt.getJWTClaimsSet().getNotBeforeTime());
			payload.setSubject(sjwt.getJWTClaimsSet().getSubject());

			//... the customclaims
			Collection<String>  customClaimKeySet = _getCustomClaimKeySet(sjwt.getJWTClaimsSet());


			if (CollectionUtils.hasData(customClaimKeySet)){
				Collection<CustomClaim> ccc= FluentIterable.from(customClaimKeySet)
				              .transform(new Function<String,CustomClaim>(){
										@Override
										public CustomClaim apply(final String key) {
												CustomClaim cc = new CustomClaim();
												cc.setName(key);
												try {
													cc.setValue((String) sjwt.getJWTClaimsSet().getClaim(key));
												} catch (ParseException e) {
													e.printStackTrace();
													throw new JWTParserException(e.getMessage());
												}
												return cc;
											}}).toList();
				payload.setCustomClaims(ccc);


			}

			//.....the header
			if (sjwt.getHeader().getAlgorithm().getName() != null){
				header.setAlgorithm(sjwt.getHeader().getAlgorithm().getName());
			}
			if (sjwt.getHeader().getType() != null) {
				header.setType(sjwt.getHeader().getType().toString());
			}
			//.....the signature
			signature.setValue(sjwt.getSignature().decodeToString());
			//...set fields

			jwtDecoded.setHeader(header);
			jwtDecoded.setPayload(payload);
			jwtDecoded.setSignature(signature);
		} catch (final ParseException e) {
			e.printStackTrace();
			throw new JWTParserException(e.getMessage());
		}
		return jwtDecoded;
	}

///////////////////////////////////////////////////////////////////////////////////////////////
//PRIVATE METHODS
//////////////////////////////////////////////////////////////////////////////////////////////
	 private  List<String> _getCustomClaimKeySet(final JWTClaimsSet claimSet){
		 return FluentIterable.from(claimSet.getClaims().keySet()).filter(new Predicate<String>(){
			@Override
			public boolean apply(final String key) {
				return  !JWTClaimsSet.getRegisteredNames().contains(key);
			}}).toList();
	 }
///////////////////////////////////////////////////////////////////////////////////////////////
// INNER CLASES.
//////////////////////////////////////////////////////////////////////////////////////////////
		class JWTParserException extends RuntimeException {
			private static final long serialVersionUID = 1664993677307211822L;

			public JWTParserException(final String msg) {
				super(msg);
			}
		}


}
