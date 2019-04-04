package r01f.model.security.login.reset;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.ModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="passwordResetResponse")
public abstract class PasswordResetResponseBase
		   implements ModelObject {

	private static final long serialVersionUID = -6088053849496707570L;

	@MarshallField(as="userCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Setter @Getter private UserCode _userCode;

	@MarshallField(as="requestAt",dateFormat=@MarshallDateFormat(use=DateFormat.CUSTOM,format="yyyy-MM-dd HH:mm:ss"),
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _requestAt;

	@MarshallField(as="passwordUpdateDone",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Boolean _passwordUpdateDone;

	@MarshallField(as="errorType",
			   escape=true)
	@Getter @Setter private PasswordResetResponseErrorType _errorType;

}
