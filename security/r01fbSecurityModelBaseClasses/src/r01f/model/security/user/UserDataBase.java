package r01f.model.security.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.model.security.PersistableSecurityModelObjectBase;
import r01f.model.security.metadata.MetaDataForSecurityModelBase;
import r01f.model.security.oids.SecurityCommonOIDs.UserDataModelOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.contact.PersonalData;
import r01f.types.contact.PersonalDataWithContactInfo;

@ModelObjectData(MetaDataForSecurityModelBase.class)
@Accessors(prefix="_")
public abstract class UserDataBase<O extends UserDataModelOID,
								   SELF_TYPE extends PersistableSecurityModelObjectBase<O,SELF_TYPE>>
			  extends PersistableSecurityModelObjectBase<O,SELF_TYPE>
  		   implements PersistableModelForUserData<O,SELF_TYPE> {

	private static final long serialVersionUID = 8628625324366860597L;

	@MarshallField(as="active",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Setter @Getter private boolean _isActive = true;

	@MarshallField(as="contactData")
	@Getter @Setter  PersonalDataWithContactInfo _contactData;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	String _getStringToCompare(final PersonalData person) {
		String name = person.getName();
		String surname = _getSurnameAsString(person);
		if (_isNotNullOrEmpty(_getSurnameAsString(person)) && _isNotNullOrEmpty(this.getContactData().getPersonalData().getName())) {
			return surname +", "+name;
		}
		else if (_isNotNullOrEmpty(_getSurnameAsString(person))) {
			return surname;
		}
		else if (_isNotNullOrEmpty(this.getContactData().getPersonalData().getName())) {
			return name;
		}
//		else {
//			return person.getId().asString();
//		}
		return name;
	}

	String _getSurnameAsString(final PersonalData person) {
		String outSurname = null;
		if (_isNotNullOrEmpty(person.getSurname1()) && _isNotNullOrEmpty(person.getSurname2())) {
			outSurname = person.getSurname1()+" "+person.getSurname2();
		}
		else if (_isNotNullOrEmpty(person.getSurname1())) {
			outSurname = person.getSurname1();
		}
		else if (_isNotNullOrEmpty(person.getSurname2())) {
			outSurname = person.getSurname2();
		}
		return outSurname;
	}

	boolean _isNotNullOrEmpty(final String str){
		return (str == null? false:!"".equals(str.trim()));
	}

}
