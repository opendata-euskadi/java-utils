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
public abstract class DBEntityForUserLoginEntry
			  extends DBEntityBaseForUserModel {

	private static final long serialVersionUID = -7685141208539038818L;

	@Column(name="USER_CODE",nullable=false,unique=false,length=OID.OID_LENGTH) @Basic
	@Getter @Setter protected String _userCode;

	@Column(name="ENTRY_TYPE",nullable=true,length=OID.OID_LENGTH) @Basic
	@Getter @Setter protected String _entryType;

	@Column(name="TOKEN",length=4000,nullable=true) @Basic
	@Getter @Setter protected String _token;

}
