package r01f.model.otp.dispatch;



import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.otp.OTPTypeOfDispatch;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="otpPresentationData")
@Accessors(prefix="_")
@NoArgsConstructor
public abstract class OTPPresentationData implements Serializable {

	private static final long serialVersionUID = 6435663354837778005L;

//////////////////////////////////////////////////////////////////////////
// Members
///////////////////////////////////////////////////////////////////////
	@Setter @Getter OTPTypeOfDispatch _presentationDataType;
	@Setter @Getter Map<String, AditionalData> _aditionalData = new HashMap<String, AditionalData>();
	@Setter @Getter OTPMimeMessage  _otpMimeMessage;

//////////////////////////////////////////////////////////////////////////
//Public Methods
///////////////////////////////////////////////////////////////////////
	protected void setAditionalDataFromString(final String id,final String value) {
		AditionalData datoAdicional = new AditionalData();
		datoAdicional.setName(id);
		datoAdicional.setValue(value);
		_aditionalData.put(id,datoAdicional);
	}
	protected String getAditionalDataAsString(final String id) {
		if (_aditionalData.containsKey(id)) {
			return ((AditionalData)_aditionalData.get(id)).getValue();
		}
		return null;
	}
	protected boolean isFillAditionalData(final String id) {
		if (_aditionalData.containsKey(id) && !"".equals(((AditionalData)_aditionalData.get(id)).getValue())) {
			return true;
		}
		return false;
	}
	public OTPPresentationDataMail asMail() {
		return(OTPPresentationDataMail)this;
	}

	public OTPPresentationDataSms asSMS() {
		return(OTPPresentationDataSms)this;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////
// Inner Classes
/////////////////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@NoArgsConstructor
	public static class AditionalData {
		@Setter @Getter String _name;
		@Setter @Getter String _value;
	}

}