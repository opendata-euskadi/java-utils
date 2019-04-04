package r01f.db.entities.users;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

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
public abstract class DBEntityForUserData
			  extends DBEntityBaseForUserModel {

	private static final long serialVersionUID = -6628025715362798888L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////

	@Column(name="USER_CODE",nullable=false,unique=true,length=OID.OID_LENGTH) @Basic
	@Getter @Setter protected String _userCode;

	@Column(name="EMAIL",length=OID.OID_LENGTH) @Basic
	@Getter @Setter protected String _email;

	@Column(name="PHONE",length=OID.OID_LENGTH) @Basic
	@Getter @Setter protected String _phone;

}
