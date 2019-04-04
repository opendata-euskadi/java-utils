package r01f.model.security.auth.profile;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.security.PersistableSecurityModelIdentifiedObjectBase;
import r01f.model.security.auth.profile.functions.ModelForUserAuthFunction;
import r01f.model.security.auth.profile.targets.ModelForUserAuthTarget;
import r01f.model.security.oids.SecurityCommonOIDs.UserAuthProfileModelOID;
import r01f.model.security.oids.SecurityIDS.UserAuthProfileID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@Accessors(prefix="_")
@MarshallType(as="authConfigProfile")
public abstract class UserAuthProfileConfigBase<O extends UserAuthProfileModelOID,
												ID extends UserAuthProfileID,
												AT extends ModelForUserAuthTarget,
												AF extends ModelForUserAuthFunction,
												SELF_TYPE extends PersistableSecurityModelIdentifiedObjectBase<O,ID,SELF_TYPE>>
			 extends PersistableSecurityModelIdentifiedObjectBase<O,ID,SELF_TYPE>
		  implements PersistableModelForUserAuthProfileConfig<O,ID,SELF_TYPE> {

	private static final long serialVersionUID = 1598296084397174561L;

////////////////////////////////////////////////////////////////////////////////////////
//MEMBERS : TARGETS AND FUNCTIONS
/////////////////////////////////////////////////////////////////////////////////////////

	@MarshallField( as="authTarget")
	@Setter @Getter private AT _target;

	@MarshallField( as="allowedFunctions",
			whenXml=@MarshallFieldAsXml(collectionElementName="function"))
	@Setter @Getter private Collection<AF> _functions;

}
