package r01f.model.mock;

import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.PersistableModelObjectBase;
import r01f.model.metadata.MetaDataForMyTestModelObject;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.model.mock.MyOIDs.MyTestOID;
import r01f.types.Path;
import r01f.types.url.Url;

@ModelObjectData(MetaDataForMyTestModelObject.class)
@Accessors(prefix="_")
public class MyTestModelObject 
     extends PersistableModelObjectBase<MyTestOID,MyTestModelObject>
  implements MyTestInterface {

	private static final long serialVersionUID = -2412005175445501481L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private String _name;
	@Getter @Setter private Language _lang;
	@Getter @Setter private MyTestEnum _enum;
	@Getter @Setter private Url _url;
	@Getter @Setter private Path _path;
	@Getter @Setter private LanguageTexts _description;
	@Getter @Setter private Collection<String> _col;
	@Getter @Setter private Map<Integer,String> _map;
	@Getter @Setter private MyTestDependentModelObject _sub;
}
