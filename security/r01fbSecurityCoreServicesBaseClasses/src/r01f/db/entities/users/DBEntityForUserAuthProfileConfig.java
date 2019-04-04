package r01f.db.entities.users;

import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@MappedSuperclass
@Multitenant(MultitenantType.SINGLE_TABLE)
@NoArgsConstructor
@Accessors(prefix="_")
public abstract class DBEntityForUserAuthProfileConfig
			  extends DBEntityBaseForUserModelIdentified {

	private static final long serialVersionUID = -6628025715362798888L;

}
