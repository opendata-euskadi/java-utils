package r01f.model.security.login.request;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.Password;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="loginRequest")
public class LoginRequestBase
  implements LoginRequest {

	private static final long serialVersionUID = -127968625327879670L;

	@MarshallField(as="password")
	@Getter @Setter private Password _password;

	@MarshallField(as="requestAt",dateFormat=@MarshallDateFormat(use=DateFormat.CUSTOM,format="yyyy-MM-dd HH:mm:ss"),
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _requestLoginAt;




}
