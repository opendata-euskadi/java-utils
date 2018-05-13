package r01f.model.otp;

import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;

public interface OTPModelObject<O extends OID>
	     extends HasOID<O>,
	     		 ModelObject {
	// just a marker interface
}