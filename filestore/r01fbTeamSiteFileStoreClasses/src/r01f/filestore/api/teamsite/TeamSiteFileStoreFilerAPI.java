package r01f.filestore.api.teamsite;

import java.io.IOException;

import javax.inject.Inject;

import com.google.common.base.Preconditions;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.filesys.CSDir;
import com.interwoven.cssdk.filesys.CSFile;
import com.interwoven.cssdk.filesys.CSHole;
import com.interwoven.cssdk.filesys.CSNode;
import com.interwoven.cssdk.filesys.CSWorkarea;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.filestore.api.FileFilter;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.filestore.api.FileStoreFilerAPI;
import r01f.filestore.api.teamsite.TeamSiteStorageObjectsPaths.TeamSiteWorkAreaRelativePath;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class TeamSiteFileStoreFilerAPI
	 extends TeamSiteFileStoreAPIBase
  implements FileStoreFilerAPI {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public TeamSiteFileStoreFilerAPI(final TeamSiteCSSDKClientWrapper cssdkClient) throws IOException {
		super(cssdkClient);
		_check = new FileStoreChecksDelegate(new TeamSiteFileStoreAPI(_cssdkClientWrapper,this),
											 this);
	}
	@Inject
	public TeamSiteFileStoreFilerAPI(final TeamSiteAuthData authData) throws IOException {
		this(TeamSiteCSSDKClientWrapper.createCachingClient(authData));
	}
	@Inject
	TeamSiteFileStoreFilerAPI(final TeamSiteCSSDKClientWrapper cssdkClientWrapper,
							  final TeamSiteFileStoreAPI fileStoreApi) throws IOException {
		super(cssdkClientWrapper);
		_check = new FileStoreChecksDelegate(fileStoreApi,
											 this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean existsFolder(final Path path) throws IOException {
		// [0] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(path);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

      	// [1] - Find the file
      	CSFile csFile = TeamSiteFileStoreFindUtils.findFolderOrFile(csWorkArea,
      															    waRelPath);
      	// [2] - Check
      	return csFile != null && (csFile instanceof CSDir);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean copyFolder(final Path srcPath,final Path dstPath,
    						  final FileFilter fileFilter,
    						  final boolean overwrite) throws IOException {

		if (fileFilter != null) throw new IllegalArgumentException("Not supported filter");

		// check
		_check.checkBeforeCopyFolder(srcPath,dstPath,
									 overwrite);

		// [0] - Get the workarea & workArea relative path
      	TeamSiteWorkAreaAndWorkAreaRelativePath srcWAandWARelPath = _workAreaAndWorkAreaRelativePathFor(srcPath);
      	TeamSiteWorkAreaAndWorkAreaRelativePath dstWAandWARelPath = _workAreaAndWorkAreaRelativePathFor(dstPath);

    	log.debug("Copying from {} to {}",
    			  srcPath,dstPath);

    	// [1] - Copy the file
    	TeamSiteFileStoreUtils.copyFilesOrFolders(srcWAandWARelPath.getWorkArea(),new TeamSiteWorkAreaRelativePath[] {srcWAandWARelPath.getWorkAreaRelativePath()},
    									 		  dstWAandWARelPath.getWorkArea(),new TeamSiteWorkAreaRelativePath[] {dstWAandWARelPath.getWorkAreaRelativePath()});

        return true;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  MOVE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean moveFolder(final Path srcPath,final Path dstPath,
    						  final boolean overwrite) throws IOException {
		// check
		_check.checkBeforeMoveFolder(srcPath,dstPath,
									 overwrite);

		// [0] - Get the workarea & workArea relative path
      	TeamSiteWorkAreaAndWorkAreaRelativePath srcWAandWARelPath = _workAreaAndWorkAreaRelativePathFor(srcPath);
      	TeamSiteWorkAreaAndWorkAreaRelativePath dstWAandWARelPath = _workAreaAndWorkAreaRelativePathFor(dstPath);

    	log.debug("Moving from {} to {}",
    			  srcPath,dstPath);

    	// [1] - move the file
    	TeamSiteFileStoreUtils.moveFilesOrFolders(srcWAandWARelPath.getWorkArea(),new TeamSiteWorkAreaRelativePath[] {srcWAandWARelPath.getWorkAreaRelativePath()},
    									 		  dstWAandWARelPath.getWorkArea(),new TeamSiteWorkAreaRelativePath[] {dstWAandWARelPath.getWorkAreaRelativePath()});

        return true;
    }
	@Override
    public boolean renameFolder(final Path existingPath,
    					  		final FileNameAndExtension newName) throws IOException {
        Preconditions.checkArgument(existingPath != null && newName != null,
        							"The original file path and new file name cannot be null");

		// [0] - Get the workarea & workArea relative path
      	TeamSiteWorkAreaAndWorkAreaRelativePath srcWAandWARelPath = _workAreaAndWorkAreaRelativePathFor(existingPath);

      	// [1] - rename
      	TeamSiteFileStoreUtils.rename(srcWAandWARelPath.getWorkArea(),srcWAandWARelPath.getWorkAreaRelativePath(),
      								  newName,
      								  true);
      	return true;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean createFolder(final Path path) throws IOException {
		if (this.existsFolder(path)) return true;	// already created

		// check
		_check.checkBeforeCreateFolder(path);

		// [0] - Get the workarea & workArea relative path
      	TeamSiteWorkAreaAndWorkAreaRelativePath waAndWaRelPath = _workAreaAndWorkAreaRelativePathFor(path);

      	// [1] - Create folder
      	TeamSiteFileStoreUtils.createFolder(waAndWaRelPath.getWorkArea(),waAndWaRelPath.getWorkAreaRelativePath(),
      								  		true);	// create parent folders
      	return true;
    }
	@Override
    public boolean deleteFolder(final r01f.types.Path path) throws IOException {
		if (!this.existsFolder(path)) return true;	// does NOT exists

		// check
		_check.checkBeforeRemoveFolder(path);

		// [0] - Get the workarea & workArea relative path
      	TeamSiteWorkAreaAndWorkAreaRelativePath waAndWaRelPath = _workAreaAndWorkAreaRelativePathFor(path);

      	// [1] - Create folder
      	TeamSiteFileStoreUtils.delete(waAndWaRelPath.getWorkArea(),waAndWaRelPath.getWorkAreaRelativePath(),
      								  true);	// create parent folders
      	return true;
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

		// [0] - Get the workarea & workArea relative path
      	TeamSiteWorkAreaAndWorkAreaRelativePath waAndWaRelPath = _workAreaAndWorkAreaRelativePathFor(folderPath);

      	// [1] - List folder contents
        FileProperties[] outProps = null;
    	CSNode[] childCSNodes = TeamSiteFileStoreFindUtils.listFolderContents(waAndWaRelPath.getWorkArea(),waAndWaRelPath.getWorkAreaRelativePath());

        if (CollectionUtils.hasData(childCSNodes)) {
        	outProps = new FileProperties[childCSNodes.length];
        	int i=0;
        	for (CSNode csNode : childCSNodes) {
        		try {
	        		if (csNode.getKind() == CSHole.KIND) {
	        			log.trace("\t... ignoring hole {}",csNode.getVPath());
	        			continue;
	        		}
	        		if (!(csNode instanceof CSFile)) {
	        			log.trace("\t... ignoring node {}; it's not a file/folder",csNode.getVPath());
	        			continue;
	        		}
        		} catch(CSException csEx) {
        			log.error("error guessing a TeamSite's node's kind: {}",csEx.getMessage(),csEx);
        			continue;
        		}
       			outProps[i] = TeamSiteFileProperties.from((CSFile)csNode);
        		i++;
        	}
        }
        return outProps;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	PROPERTIES see FileStoreFileAPI
/////////////////////////////////////////////////////////////////////////////////////////	
//	@Override
//	public FileProperties getFolderProperties(r01f.types.Path folderPath) throws IOException {
//		log.trace("Folder {} properties {}",
//				  folderPath);
//
//		if (!this.existsFolder(folderPath)) return null;	// does NOT exists
//		
//		// [1] - Get the workarea & workarea relative path
//    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(folderPath);
//        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
//      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();
//
//        // [2] - File Properties
//        CSFile csFile = TeamSiteFileStoreFindUtils.findFolderOrFile(csWorkArea,
//        														    waRelPath);
//        if (csFile == null) throw new IOException("Folder at " + folderPath + " DOES NOT exists!");
//        
//        FileProperties outProps = TeamSiteFileProperties.from(csFile);
//        if (!outProps.isFolder()) throw new IllegalArgumentException(folderPath + " is NOT a folder path; it's a " + 
//        															 (outProps.isFile() ? "file"
//        																	 		   : outProps.isSymLink() ? "symlink" : "unknown"));
//        return outProps;
//	}
}
