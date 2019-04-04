package r01f.model.security.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.patterns.IsBuilder;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.PersonalData;
import r01f.types.contact.PersonalDataWithContactInfo;

@NoArgsConstructor
public abstract class UserDataBuilderBase<OID extends UserDataModelOID,
										  U extends UserDataBase<OID,U>>
		   implements IsBuilder {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	public class BuilderStart {
		private final U _user;

		public UserBuilderPersonalDataStep forUserCode(final UserCode userCode){
			_user.setUserCode(userCode);
			PersonalDataWithContactInfo contactData = new PersonalDataWithContactInfo();
			PersonalData  personalData = new PersonalData();
			ContactInfo contactInfo = new ContactInfo();
			contactData.setContactInfo(contactInfo);
			contactData.setPersonalData(personalData);
			_user.setContactData(contactData);
			return new UserBuilderPersonalDataStep(_user);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
// PERSONAL DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserBuilderPersonalDataStep {
		private final U _userConfig;

		public UserBuilderBuildStep withPersonalData(final PersonalData personalData) {
			_userConfig.getContactData().setPersonalData(personalData);
			return new UserBuilderBuildStep(_userConfig);
		}
		public UserBuilderBuildStep withoutPersonalData() {
			_userConfig.getContactData().setPersonalData(null);
			return new UserBuilderBuildStep(_userConfig);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////
// CONTACT DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class UserBuilderBuildStep {
		private final U _userConfig;

		public UserBuilderBuildStep withContactInfo(final ContactInfo contactInfo) {
			_userConfig.getContactData().setContactInfo(contactInfo);
			return this;
		}
		public UserBuilderBuildStep withoutContactInfo() {
			_userConfig.getContactData().setContactInfo(null);
			return this;
		}
		public U build() {
			return _userConfig;
		}
	}
}
