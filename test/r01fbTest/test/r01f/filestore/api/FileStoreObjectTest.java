package r01f.filestore.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.types.Path;

@Accessors(prefix="_")
@AllArgsConstructor
public class FileStoreObjectTest {

	@Getter @Setter private Path _rootPath;
	@Getter @Setter private FileStoreAPI _fileStoreApi;
	@Getter @Setter private FileStoreFilerAPI _filerApi;

}
