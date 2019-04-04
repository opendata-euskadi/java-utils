package r01f.model.security.login.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.UserCode;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="loginUserCodeRequest")
public class LoginRequestWithUserCodeBase
		extends LoginRequestBase {

	private static final long serialVersionUID = -856701679733262495L;

	@MarshallField(as="userCode")
	@Getter @Setter private UserCode  _userCode;
}



