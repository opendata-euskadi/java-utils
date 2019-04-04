package r01f.db.entities.users;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;

@MappedSuperclass
@Multitenant(MultitenantType.SINGLE_TABLE)
@NoArgsConstructor
@Accessors(prefix="_")
public abstract class DBEntityForUserLoginData
		extends DBEntityBaseForUserModel {

	private static final long serialVersionUID = -6628025715362798888L;

	@Column(name="USER_CODE",nullable=false,unique=true,length=OID.OID_LENGTH) @Basic
	@Getter @Setter protected String _userCode;

	@Column(name="EXPIRATES_AT",
			insertable=true,updatable=true) @Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter protected Calendar _expiratesAt;		// http://www.developerscrappad.com/228/java/java-ee/ejb3-jpa-dealing-with-date-time-and-timestamp/


}
