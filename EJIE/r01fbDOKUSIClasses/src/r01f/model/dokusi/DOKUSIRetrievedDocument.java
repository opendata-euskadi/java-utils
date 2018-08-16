package r01f.model.dokusi;

import java.io.InputStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.types.Path;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class DOKUSIRetrievedDocument {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final Path _pifPath;
	@Getter private final int _size;
	@Getter private final InputStream _stream;
}
