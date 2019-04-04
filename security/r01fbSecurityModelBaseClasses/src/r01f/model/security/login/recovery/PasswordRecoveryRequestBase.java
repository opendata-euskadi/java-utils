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
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@Accessors(prefix="_")
@MarshallType(as="passwordRecoveryRequest")
public abstract class PasswordRecoveryRequestBase
		   implements PasswordRecoveryRequest {

	private static final long serialVersionUID = -2830273065723865959L;

	@MarshallField(as="userCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Setter @Getter private UserCode _userCode;

	@MarshallField(as="requestAt",dateFormat=@MarshallDateFormat(use=DateFormat.CUSTOM,format="yyyy-MM-dd HH:mm:ss"),
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _requestAt;

	@MarshallField(as="email",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private EMail _email;

	@MarshallField(as="phone",
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Phone _phone;

}
