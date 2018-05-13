package r01f.cache.mock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.guids.OIDBase;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="mockOid")
@Accessors(prefix="_")
@AllArgsConstructor
public class MockOID 
			extends OIDBase<String> {
	private static final long serialVersionUID = -337578273728620532L;
	protected @Getter String _id;
}