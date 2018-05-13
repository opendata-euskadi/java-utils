package r01f.filestore.api.local;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class LocalFileStoreFilerAPI
	 extends LocalFileStoreAPIBase
  implements FileStoreFilerAPI {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LocalFileStoreFilerAPI() throws IOException {
		_check = new FileStoreChecksDelegate(new LocalFileStoreAPI(this),
											 this);
	}
	LocalFileStoreFilerAPI(final LocalFileStoreAPI fileStoreApi) throws IOException {
		_check = new FileStoreChecksDelegate(fileStoreApi,
											 this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CHECK
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean existsFolder(final Path path) throws IOException {
		File f = new File(path.asAbsoluteString());
		return f.exists() && f.isDirectory();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY / MOVE / RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean copyFolder(final Path srcPath,final Path dstPath,
    						  final FileFilter fileFilter,
    						  final boolean overwrite) throws IOException {

		// check
		_check.checkBeforeCopyFolder(srcPath,dstPath,
									 overwrite);

		// copy folder
		File src = new File(srcPath.asAbsoluteString());
		File dst = new File(dstPath.asAbsoluteString());
		FileUtils.copyDirectory(src,dst,
								new java.io.FileFilter() {
										@Override
										public boolean accept(final File file) {
											return fileFilter.accept(Path.from(file));
										}
								},
								false);		// preserve file dates
		return true;
    }
	@Override
    public boolean moveFolder(final Path srcPath,final Path dstPath,
    						  final boolean overwrite) throws IOException {
		// check
		_check.checkBeforeMoveFolder(srcPath,dstPath,
									 overwrite);

		// move folder
		File src = new File(srcPath.asAbsoluteString());
		File dst = new File(dstPath.asAbsoluteString());

		Files.move(src,dst);
		return true;

    }
	@Override
	public boolean renameFolder(final Path existingPath,final FileNameAndExtension newName) throws IOException {
		Path dstPath = Path.from(existingPath.getPathElementsExceptLast())
						   .joinedWith(newName);
		return this.moveFolder(existingPath,dstPath,
							   false);		// DO NOT overwrite
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean createFolder(final Path path) throws IOException {
		if (this.existsFolder(path)) return true;	// already created

		// check
		_check.checkBeforeCreateFolder(path);

		// create the folder
		File f = new File(path.asAbsoluteString());
		Files.createParentDirs(f);

		return f.mkdir();
    }
	@Override
    public boolean deleteFolder(final Path path) throws IOException {
		if (!this.existsFolder(path)) return true;	// does NOT exists

		// check
		_check.checkBeforeRemoveFolder(path);

		// delete
		File file = new File(path.asAbsoluteString());
		if (!file.exists()) {
			log.warn("Could NOT delete folder at {} since it does NOT exists",path.asAbsoluteString());
			return false;
		}
		if (!file.isDirectory()) {
			log.warn("Could NOT delete file at {} since it's NOT a folder",path.asAbsoluteString());
			return false;
		}
		return FileUtils.deleteQuietly(file);		// maybe? FileUtils.deleteDirectory(folder)
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public FileProperties[] listFolderContents(final Path folderPath,
    										   final FileFilter fileFilter) throws IOException {

		if (fileFilter != null) throw new IllegalArgumentException("Not supported filter");

		// check
		_check.checkBeforeListFolderContents(folderPath);

		// list
		File folder = new File(folderPath.asAbsoluteString());
		String[] contents = folder.list();

		FileProperties[] out = null;
		if (CollectionUtils.hasData(contents)) {
			out = new FileProperties[contents.length];
			int i=0;
			for (String fn : contents) {
				File f = new File(fn);
				out[i] = LocalFileProperties.from(f);
				i++;
			}
		} else {
			out = new FileProperties[] {};
		}
		return out;
    }

/////////////////////////////////////////////////////////////////////////////////////////
//	PROPERTIES see FileStoreFileAPI
/////////////////////////////////////////////////////////////////////////////////////////
//	@Override
//	public FileProperties getFolderProperties(final Path folderPath) throws IOException {
//		if (folderPath == null) throw new IllegalArgumentException("the FileID MUST NOT be null and it have to be a Path");
//
//		File dir = new File(folderPath.asAbsoluteString());
//		if (!dir.exists()) throw new IOException("Folder at " + folderPath + " DOES NOT exists!");
//		
//
//		FileProperties outProps = LocalFileProperties.from(dir);
//        if (!outProps.isFolder()) throw new IllegalArgumentException(folderPath + " is NOT a folder path; it's a " + 
//        															 (outProps.isFile() ? "file"
//        																	 		   : outProps.isSymLink() ? "symlink" : "unknown"));
//        return outProps;
//	}
}
