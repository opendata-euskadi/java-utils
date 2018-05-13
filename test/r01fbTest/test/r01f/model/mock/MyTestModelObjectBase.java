package r01f.model.mock;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.PersistableModelObjectBase;
import r01f.model.mock.MyOIDs.MyTestOID;

@Accessors(prefix="_")
public abstract class MyTestModelObjectBase 
     		  extends PersistableModelObjectBase<MyTestOID,MyTestModelObjectBase> {

	private static final long serialVersionUID = -2412005175445501481L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private MyTestDependentModelObject _sub;
	

}
