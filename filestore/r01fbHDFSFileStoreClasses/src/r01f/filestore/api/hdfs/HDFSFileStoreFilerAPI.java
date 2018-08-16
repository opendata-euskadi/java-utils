package r01f.filestore.api.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class HDFSFileStoreFilerAPI
	 extends HDFSFileStoreAPIBase
  implements FileStoreFilerAPI {
///////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Common checkings
	 */
	protected final FileStoreChecksDelegate _check;
	
	protected final HDFSFileStoreAPI _api;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HDFSFileStoreFilerAPI(final Configuration conf) throws IOException {
		super(conf);
		_api = new HDFSFileStoreAPI(_fs,this); // reuse the filesystem
		_check = new FileStoreChecksDelegate(_api, this);
		
	}
	HDFSFileStoreFilerAPI(final FileSystem fs,
						  final HDFSFileStoreAPI fileApi) throws IOException {
		super(fs);
		_api = fileApi;
		_check = new FileStoreChecksDelegate(fileApi,
					  					  	 this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean existsFolder(r01f.types.Path path) throws IOException {
		// exists?
    	Path theHDFSFilePath = _pathToHDFSPath(path);
		return _fs.exists(theHDFSFilePath) && _fs.isDirectory(theHDFSFilePath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY / MOVE / RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean copyFolder(final r01f.types.Path srcPath,final r01f.types.Path dstPath,
    						  final FileFilter fileFilter,
    						  final boolean overwrite) throws IOException {
    	log.trace("Copying from {} to {}",srcPath,dstPath);    	

		// check
		_check.checkBeforeCopyFolder(srcPath,dstPath,
									 overwrite);
		
		boolean copyFolderStateOK = true;
		
		if (fileFilter != null) {
			
			// copy folder applying filter (recursive)
			FileProperties[] filteredFiles = this.listFolderContents(srcPath, fileFilter, false);
			for (int i=0; i<filteredFiles.length; i++) {
				FileProperties currentFileOrFolder = filteredFiles[i];

				r01f.types.Path effectiveDstPath = dstPath.joinedWith(currentFileOrFolder.getPath()
																						 .remainingPathFrom(srcPath));
				if (currentFileOrFolder.isFile()) {
					boolean copyFileStateOK = _api.copyFile(filteredFiles[i].getPath(),effectiveDstPath, 
															overwrite);
					if (!copyFileStateOK) copyFolderStateOK = false;
				} else {
					return this.copyFolder(currentFileOrFolder.getPath(),effectiveDstPath, 
										   fileFilter,
										   overwrite);
				}
			}
			
		} else {
			// copy folder without applying filter
	    	// If needed: The method FileUtils.stat2Path convert an array of FileStatus to an array of Path
	        copyFolderStateOK = FileUtil.copy(_fs,_pathToHDFSPath(srcPath),
	    									  _fs,_pathToHDFSPath(dstPath),
	    									  false, 		// delete source
	    									  overwrite,	// overwrite
	    									  _conf);
		}
		
        return copyFolderStateOK;
    }
	@Override
    public boolean moveFolder(final r01f.types.Path srcPath,final r01f.types.Path dstPath,
    						  final boolean overwrite) throws IOException {
    	log.trace("Moving folder from {} to {}",srcPath,dstPath);

		// check
		_check.checkBeforeMoveFolder(srcPath,dstPath,
									 overwrite);

		// copy
    	// If needed: The method FileUtils.stat2Path convert an array of FileStatus to an array of Path
        boolean copyFileStateOK = FileUtil.copy(_fs,_pathToHDFSPath(srcPath),
        										_fs,_pathToHDFSPath(dstPath),
        										true, 		// delete source
        										overwrite,	// overwrite
        										_conf);
        return copyFileStateOK;
    }
	@Override
	public boolean renameFolder(final r01f.types.Path existingPath,final FileNameAndExtension newName) throws IOException {
    	log.trace("Renaming folder from {} to {}",existingPath,newName);


		r01f.types.Path dstPath = r01f.types.Path.from(existingPath.withoutLastPathElement())
						  						 .joinedWith(newName);

		// check
		_check.checkBeforeMoveFolder(existingPath,dstPath,
									 false);		// DO NOT overwrite

		// rename
		return _fs.rename(_pathToHDFSPath(existingPath),
				   		  _pathToHDFSPath(dstPath));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean createFolder(final r01f.types.Path path) throws IOException {
		log.trace("Creating a folder at {}",path);

		if (this.existsFolder(path)) return true;	// already created

		// check
		_check.checkBeforeCreateFolder(path);
		return _fs.mkdirs(_pathToHDFSPath(path));
    }
	@Override
    public boolean deleteFolder(final r01f.types.Path path) throws IOException {
		log.trace("Deleting a folder at {}",path);

		if (!this.existsFolder(path)) return true;	// does NOT exists

		// check
		_check.checkBeforeRemoveFolder(path);

		// delete
		boolean opState = _fs.delete(_pathToHDFSPath(path),
									 true);		// recursive
		return opState;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public FileProperties[] listFolderContents(final r01f.types.Path folderPath,
    										   final FileFilter fileFilter,
    										   final boolean recursive) throws IOException {
    	log.debug("Listing folder {} contents",folderPath);

		// check
		_check.checkBeforeListFolderContents(folderPath);
		
		// filter
		PathFilter newFilter = null;
		if (fileFilter != null) {
			newFilter = new PathFilter() {

				@Override
				public boolean accept(Path path) {				
					return fileFilter.accept(r01f.types.Path.from(Path.getPathWithoutSchemeAndAuthority(path)));				
				}
				
			};
		}
		
		// list
        FileStatus[] statusFiles;
        if (recursive) {
        	HDFSFileListing listing = new HDFSFileListing();
			statusFiles = listing.getFileListing(_fs,
												 _pathToHDFSPath(folderPath), 
												 newFilter); // if filter is null, don't use it
        } else {
        	if (newFilter != null) {
        		statusFiles = _fs.listStatus(_pathToHDFSPath(folderPath),
        									 newFilter);
        	} else {
        		statusFiles = _fs.listStatus(_pathToHDFSPath(folderPath));
        	}
        }
        
        // transform to FileProperties
        FileProperties[] outProps = null;
        if (CollectionUtils.hasData(statusFiles)) {
        	outProps = FluentIterable.from(statusFiles)
        							 .transform(new Function<FileStatus,FileProperties>() {
														@Override
														public FileProperties apply(final FileStatus fs) {
															FileProperties p = null;
															try {
																p = HDFSFileProperties.from(fs);
															} catch(IOException ioEx) {
																log.error("Error creating a {} from {}: {}",
																		  HDFSFileProperties.class,fs.getPath(),ioEx.getMessage(),
																		  ioEx);
															}
															return p;
														}
        							 			})
        							 .filter(Predicates.notNull())
        							 .toArray(FileProperties.class);
        } else {
        	outProps = new FileProperties[] { /* nothing */ };
        }
        return outProps;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	PROPERTIES (see FileStoreFileAPI)
/////////////////////////////////////////////////////////////////////////////////////////	
//	@Override
//	public FileProperties getFolderProperties(r01f.types.Path folderPath) throws IOException {
//		log.trace("Folder {} properties {}",
//				  folderPath);
//
//		if (!this.existsFolder(folderPath)) throw new IOException("Folder at " + folderPath + " DOES NOT exists!");
//		
//		FileStatus fs = _fs.getFileStatus(_pathToHDFSPath(folderPath));        
//        FileProperties outProps = HDFSFileProperties.from(fs);
//        if (!outProps.isFolder()) throw new IllegalArgumentException(folderPath + " is NOT a folder path; it's a " + 
//        															 (outProps.isFile() ? "file"
//        																	 		   : outProps.isSymLink() ? "symlink" : "unknown"));
//
//        return outProps;
//	}
}
