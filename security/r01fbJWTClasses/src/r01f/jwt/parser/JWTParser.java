package r01f.jwt.parser;

import r01f.jwt.JWT;
import r01f.jwt.JWTDecoded;

public interface JWTParser {

	JWTDecoded parse(final JWT jwt);
}
