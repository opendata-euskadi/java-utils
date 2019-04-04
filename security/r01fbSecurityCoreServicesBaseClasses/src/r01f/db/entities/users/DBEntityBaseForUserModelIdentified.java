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
import r01f.persistence.db.DBEntityHasModelObjectDescriptor;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;

@MappedSuperclass
@Multitenant(MultitenantType.SINGLE_TABLE)
@Accessors(prefix="_")
@NoArgsConstructor
public abstract class DBEntityBaseForUserModelIdentified
		      extends DBEntityBaseForUserModel
		   implements DBEntityForModelObject<DBPrimaryKeyForModelObject>,
		  			  DBEntityHasModelObjectDescriptor {

	private static final long serialVersionUID = -187718742261139982L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Column(name="ID",length=4000,nullable=false) @Basic
	@Getter @Setter protected String _id;
}
