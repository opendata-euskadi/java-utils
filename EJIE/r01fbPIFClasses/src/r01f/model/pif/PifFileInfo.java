package r01f.model.pif;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.file.FileNameAndExtension;
import r01f.mime.MimeType;
import r01f.types.Path;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class PifFileInfo 
  implements PifObject {

	private static final long serialVersionUID = 1760197891958522392L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final MimeType _contentType;
	@Getter private final FileNameAndExtension _fileName; 
	@Getter private final Path _filePath;  
	@Getter private final long _size;

}
