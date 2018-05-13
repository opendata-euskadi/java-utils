package r01f.model.mock;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.ModelObject;
import r01f.model.metadata.MetaDataForMyTestDependentModelObject;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

@ModelObjectData(MetaDataForMyTestDependentModelObject.class)
@Accessors(prefix="_")
public class MyTestDependentModelObject 
  implements ModelObject {

	private static final long serialVersionUID = 894764864071103542L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private Year _year;
	@Getter @Setter private MonthOfYear _monthOfYear;
}
