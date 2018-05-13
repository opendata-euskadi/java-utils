package r01f.cache.mock;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.objectstreamer.annotations.MarshallType;


@ConvertToDirtyStateTrackable
@MarshallType(as="mockObject")
@Accessors(prefix="_")
public class MockCachedObject
  implements ModelObject,
  			 HasOID<MockOID> {
	private static final long serialVersionUID = -3835573945049370549L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Setter @Getter MockOID _oid;
	@Setter @Getter String _someDescription;
	@Setter @Getter long _counter = 0;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void unsafeSetOid(final OID oid) {
		this.setOid((MockOID)oid);
	}
	public void incCounter() {
		_counter++;
	}
}
