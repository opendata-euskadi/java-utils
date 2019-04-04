package r01f.db.entities.users;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.persistence.db.DBEntityHasModelObjectDescriptor;
import r01f.persistence.db.entities.DBEntityBase;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;

@MappedSuperclass
@Multitenant(MultitenantType.SINGLE_TABLE)
@Accessors(prefix="_")
@NoArgsConstructor
public abstract class DBEntityBaseForUserModel
			  extends DBEntityBase
		   implements DBEntityForModelObject<DBPrimaryKeyForModelObject>,
					  DBEntityHasModelObjectDescriptor {

	private static final long serialVersionUID = -187718742261139982L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////

	@Id @Column(name="OID",length=OID.OID_LENGTH,nullable=false) @Basic
	@Getter @Setter protected String  _oid;

	@Column(name="DESCRIPTION",length=4000,nullable=true) @Basic
	@Getter @Setter protected String _description;

	@Column(name="DESCRIPTOR") @Lob @Basic(fetch=FetchType.EAGER)
	@Getter @Setter protected String _descriptor;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public DBPrimaryKeyForModelObject getDBEntityPrimaryKey() {
		return DBPrimaryKeyForModelObjectImpl.from(_oid);
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void _preCreate() {
		// nothing
	}
	@Override
	protected void _preUpdate() {
		// nothing
	}

}
