package r01f.model.security.login.recovery;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.UserCode;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="passwordRecoveryResponse")
public abstract class PasswordRecoveryResponseBase
		   implements PasswordRecoveryResponseWithToken {

	@MarshallField(as="userCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Setter @Getter private UserCode _userCode;

	@MarshallField(as="requestAt",dateFormat=@MarshallDateFormat(use=DateFormat.CUSTOM,format="yyyy-MM-dd HH:mm:ss"),
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _requestAt;

	@MarshallField(as="notificationDone",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Boolean _notificationDone;

	@MarshallField(as="errorType",
			   escape=true)
	@Getter @Setter private PasswordRecoveryResponseErrorType _errorType;

	/**
	 * Token must be always null in client response
	 */
	@MarshallField(as="token")
	@Setter @Getter private String _token;

}
