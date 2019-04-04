package r01f.model.security.login.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.security.oids.SecurityIDS.UserID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="loginUserCodeRequest")
public  class LoginRequestWithUserIDBase<ID extends UserID>
		extends LoginRequestBase {

	private static final long serialVersionUID = -9166268756550457251L;

	@MarshallField(as="id")
	@Getter @Setter private ID  _userId;
}



