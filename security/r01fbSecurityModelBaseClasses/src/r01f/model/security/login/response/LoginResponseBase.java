package r01f.model.security.login.response;

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
@MarshallType(as="loginResponse")
public abstract class LoginResponseBase
		   implements LoginResponse {

	@MarshallField(as="userCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Setter @Getter private UserCode _userCode;

	@MarshallField(as="requestAt",dateFormat=@MarshallDateFormat(use=DateFormat.CUSTOM,format="yyyy-MM-dd HH:mm:ss"),
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _requestAt;

	// Login OK attributes

	@MarshallField(as="loggedOn",dateFormat=@MarshallDateFormat(use=DateFormat.CUSTOM,format="yyyy-MM-dd HH:mm:ss"),
			   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _loggedOn;

	@MarshallField(as="name",
				   escape=true)
	@Getter @Setter private String _name;

	@MarshallField(as="firstSurname",
				   escape=true)
	@Getter @Setter private String _surname1;

	@MarshallField(as="secondSurname",
				   escape=true)
	@Getter @Setter private String _surname2;

	// Login Error attributes

	@MarshallField(as="errorType",
				   escape=true)
	@Getter @Setter private LoginResponseErrorType _errorType;

	@Override
	public boolean hasFailed() {
		if (_errorType == null) {
			return false;
		} else {
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends LoginResponse> R as(final Class<R> classRespon) {
		return (R)this;
	}

	@Override
	public LoginResponseError asLoginError() {
		if (hasFailed()) {
			return (LoginResponseError)this;
		} else {
			throw new ClassCastException();
		}
	}

	@Override
	public LoginResponseOK asLoginOK() {
		if (hasFailed()) {
			throw new ClassCastException();
		} else {
			return (LoginResponseOK)this;
		}
	}

}
