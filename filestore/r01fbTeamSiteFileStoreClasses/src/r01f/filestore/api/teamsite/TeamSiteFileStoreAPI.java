package r01f.filestore.api.teamsite;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import com.google.common.base.Preconditions;
import com.interwoven.cssdk.filesys.CSFile;
import com.interwoven.cssdk.filesys.CSSimpleFile;
import com.interwoven.cssdk.filesys.CSVPath;
import com.interwoven.cssdk.filesys.CSWorkarea;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileID;
import r01f.file.FileProperties;
import r01f.filestore.api.FileStoreAPI;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.filestore.api.teamsite.TeamSiteStorageObjectsPaths.TeamSiteWorkAreaRelativePath;
import r01f.types.Path;
import r01f.util.types.Strings;

/**
 * TeamSite {@link FileStoreAPI} api
 * Usage:
 * <pre class='brush:java'>
 * 		// 1) Create the auth data
 *		TeamSiteAuthData authData = new TeamSiteAuthData()
 *											.login(UserCode.forId("r01d"),
 *												   Password.forId("r01d"),
 *												   UserRole.forId("Editor"))
 *											.forContentServicesUrl("http://iwdes01.ejgvdns:80");
 *		// 2) Create a TS CSSDK client wrapper
 *		TeamSiteCSSDKClientWrapper cssdkClientWrapper = TeamSiteCSSDKClientWrapper.createCachingClient(authData);
 *
 *		// 3) Create the api
 *		FileStoreAPI api = new TeamSiteFileStoreAPI(cssdkClientWrapper);
 * </pre>
 */
@Slf4j
public class TeamSiteFileStoreAPI
	 extends TeamSiteFileStoreAPIBase
  implements FileStoreAPI {

///////////////////////////////////////////////////////////////////////////////////////////
// 	FILESYSTEM STATIC INIT
///////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public TeamSiteFileStoreAPI(final TeamSiteAuthData authData) throws IOException {
		this(TeamSiteCSSDKClientWrapper.createCachingClient(authData));
	}
	public TeamSiteFileStoreAPI(final TeamSiteCSSDKClientWrapper cssdkClient) throws IOException {
		super(cssdkClient);
		_check = new FileStoreChecksDelegate(this,
											 new TeamSiteFileStoreFilerAPI(_cssdkClientWrapper,this));
	}
	TeamSiteFileStoreAPI(final TeamSiteCSSDKClientWrapper cssdkClientWrapper,
						 final TeamSiteFileStoreFilerAPI filerApi) throws IOException {
		super(cssdkClientWrapper);
		_check = new FileStoreChecksDelegate(this,
											 filerApi);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private Path _fileIdToPath(final FileID fileId) {
		if (fileId == null) throw new IllegalArgumentException("fileId MUST NOT be null!");
		if (!(fileId instanceof Path)) throw new IllegalArgumentException(Strings.customized("The {} instance MUST be a {} instance",
																							 FileID.class,r01f.types.Path.class));
		return (r01f.types.Path)fileId;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean existsFile(final FileID fileId) throws IOException {
		// [0] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(fileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

      	// [1] - Find the file
      	CSFile csFile = TeamSiteFileStoreFindUtils.findFolderOrFile(csWorkArea,
      															    waRelPath);
      	// [2] - Check
      	return csFile != null && (csFile instanceof CSSimpleFile);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY & RENAME
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean copyFile(final FileID srcFileId,final FileID dstFileId,
							final boolean overwrite) throws IOException {
		// check
		_check.checkFileId(srcFileId,dstFileId);
		_check.checkBeforeCopyFile(srcFileId,dstFileId,
								   overwrite);

		// [0] - Get the workarea & workArea relative path
      	TeamSiteWorkAreaAndWorkAreaRelativePath srcWAandWARelPath = _workAreaAndWorkAreaRelativePathFor(srcFileId);
      	TeamSiteWorkAreaAndWorkAreaRelativePath dstWAandWARelPath = _workAreaAndWorkAreaRelativePathFor(dstFileId);

    	log.debug("Copying file from {} to {}",
    			  srcFileId,dstFileId);

    	// [1] - Copy the file
    	TeamSiteFileStoreUtils.copyFilesOrFolders(srcWAandWARelPath.getWorkArea(),new TeamSiteWorkAreaRelativePath[] {srcWAandWARelPath.getWorkAreaRelativePath()},
    									 		  dstWAandWARelPath.getWorkArea(),new TeamSiteWorkAreaRelativePath[] {dstWAandWARelPath.getWorkAreaRelativePath()});

        return true;
	}
	@Override
	public boolean renameFile(final FileID srcFileId,final FileID dstFileId) throws IOException {
		// check
		_check.checkFileId(srcFileId,dstFileId);
		_check.checkBeforeMoveFile(srcFileId,dstFileId,
								   false);		// do NOT overwrite if the file exists!

		// [0] - Get the workarea & workArea relative path
      	TeamSiteWorkAreaAndWorkAreaRelativePath srcWAandWARelPath = _workAreaAndWorkAreaRelativePathFor(dstFileId);
      	TeamSiteWorkAreaAndWorkAreaRelativePath dstWAandWARelPath = _workAreaAndWorkAreaRelativePathFor(dstFileId);

      	// [1] - rename
    	TeamSiteFileStoreUtils.moveFilesOrFolders(srcWAandWARelPath.getWorkArea(),new TeamSiteWorkAreaRelativePath[] {srcWAandWARelPath.getWorkAreaRelativePath()},
    									 		  dstWAandWARelPath.getWorkArea(),new TeamSiteWorkAreaRelativePath[] {dstWAandWARelPath.getWorkAreaRelativePath()});
      	return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STREAM WRITE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public OutputStream getFileOutputStreamForWriting(final FileID dstFileId,
    												  final boolean overwrite) throws IOException {
    	return this.getFileOutputStreamForWriting(dstFileId,
    											  0,	// start at the beginning of the file
    											  overwrite);
    }
	@Override
	public OutputStream getFileOutputStreamForWriting(final FileID dstFileId,final long offset,
													  final boolean overwrite) throws IOException {
		log.trace("get outputstream for writing file {} (overwrite={})",
				  dstFileId,overwrite);
		// check
		_check.checkFileId(dstFileId);
		_check.checkBeforeWriteToFile(dstFileId,
									  overwrite);

		// [1] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(dstFileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

      	// [2] - Create the output stream
		return TeamSiteFileStoreUtils.getFileOutputStreamForWriting(csWorkArea,
																	waRelPath,
																	offset,			// write start position
																	true,			// create parent folders
																	overwrite);
	}
	@Override
    public void writeToFile(final InputStream srcFileIS,
    						final FileID dstFileId,
    					    final boolean overwrite) throws IOException {
		// [1] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(dstFileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

      	// [2] - Write
		log.debug("Writing to file {} at {} (overwrite={})",
				  waRelPath,csWorkArea.getVPath(),overwrite);

		// BEWARE!! The transfer to TeamSite is ALWAYS chunked (see TeamSiteFileStoreUtils)
		//			... the source stream is translated into chunked writing at TeamSiteFileStoreUtils
		//			... so it's useless chunk the stream here
		//
		//			[client] -------------- [appServer] ------------ [TeamSite]
		//                           ^                         ^
		//	The transfer between    _|                         |_ this API is here: the transfer is ALWAYS
		//  the client and appServer                           |  chunked (see TeamSiteFileStoreUtils)
		//  can also use stream or chunked                     |
		//  transfer (ie RMI)                        This API is used HERE
		//
		TeamSiteFileStoreUtils.writeToFile(srcFileIS,
										   csWorkArea,waRelPath,
										   0,				// starting at the beginning of the file
										   true,			// create parent folders
										   overwrite);		// overwrite
    }
	@Override
	public void writeChunkToFile(final byte[] data,
								 final FileID dstFileId,final long offset,
								 final boolean overwrite) throws IOException {
		// [1] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(dstFileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

      	// [2] - Write
		log.debug("Writing chunk to file {} at {} (overwrite={})",
				  waRelPath,csWorkArea.getVPath(),overwrite);

		// BEWARE!! The transfer to TeamSite is ALWAYS chunked (see TeamSiteFileStoreUtils)
		//			... the source stream is translated into chunked writing at TeamSiteFileStoreUtils
		//			... so it's useless chunk the stream here
		//
		//			[client] -------------- [appServer] ------------ [TeamSite]
		//                           ^                         ^
		//	The transfer between    _|                         |_ this API is here: the transfer is ALWAYS
		//  the client and appServer                           |  chunked (see TeamSiteFileStoreUtils)
		//  can also use stream or chunked                     |
		//  transfer (ie RMI)                        This API is used HERE
		//
		TeamSiteFileStoreUtils.writeToFile(new ByteArrayInputStream(data),
										   csWorkArea,waRelPath,
										   offset,			// starting at the given offset
										   true,			// create parent folders
										   overwrite);		// overwrite
	}
	@Override
	public OutputStream getFileOutputStreamForAppending(final FileID dstFileId) throws IOException {
		// check
		_check.checkFileId(dstFileId);
		_check.checkBeforeAppendToFile(dstFileId);

		// [1] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(dstFileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

      	// [2] - Create the output stream
		return TeamSiteFileStoreUtils.getFileOutputStreamForAppending(csWorkArea,
																	  waRelPath,
																	  true);			// create parent folders
	}
	@Override
	public void appendToFile(final InputStream srcFileIS,
							 final FileID dstFileId) throws IOException {
        Preconditions.checkArgument(srcFileIS != null,
        							"The local file input stream cannot be null");

		// [1] - Get the workarea $ workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(dstFileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

      	// [2] - Write
		log.debug("Appending to file {} at {} ",
				  waRelPath,csWorkArea.getVPath());

		// BEWARE!! The transfer to TeamSite is ALWAYS chunked (see TeamSiteFileStoreUtils)
		//			... the source stream is translated into chunked writing at TeamSiteFileStoreUtils
		//			... so it's useless chunk the stream here
		//
		//			[client] -------------- [appServer] ------------ [TeamStie]
		//                           ^                         ^
		//	The transfer between    _|                         |_ this API is here: the transfer is ALWAYS
		//  the client and appServer                           |  chunked (see TeamSiteFileStoreUtils)
		//  can also use stream or chunked                     |
		//  transfer (ie RMI)                        This API is used HERE
		//

        long srcReadOffset = 0; 						// source stream read offset
        int srcAvailableData = srcFileIS.available(); 	// total size

        // initial chunk size BEWARE! use the same buffer size as the TeamSite does
        int chunkSize = srcAvailableData < TeamSiteFileStoreUtils.RW_BLOCK_SIZE ? srcAvailableData
        												  				 		: TeamSiteFileStoreUtils.RW_BLOCK_SIZE;
        do {
            byte[] chunk = new byte[chunkSize];
            srcFileIS.read(chunk);
            TeamSiteFileStoreUtils.appendChunkToFile(chunk,
            										 csWorkArea,waRelPath,
            									     true);			// create parent folders
            srcReadOffset = srcReadOffset + chunkSize;

            // the new chunk size
            chunkSize = (srcReadOffset + chunkSize > srcAvailableData) ? srcAvailableData - (int)srcReadOffset
            										     			   : TeamSiteFileStoreUtils.RW_BLOCK_SIZE;
        } while (srcReadOffset < srcAvailableData);
	}
    @Override
    public void appendChunkToFile(final byte[] srcDataChunk,
    							  final FileID dstFileId) throws IOException {
        if (srcDataChunk == null || srcDataChunk.length == 0) {
            log.warn("The data to write in file is NULL!!!");
            return;
        }

		// [1] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(dstFileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

        // [2] - Append
    	log.debug("Append chunk to file {} at {}",
				  waRelPath,csWorkArea.getVPath());

        TeamSiteFileStoreUtils.appendChunkToFile(srcDataChunk,
        										 csWorkArea,waRelPath,
        									     true);			// create parent folders
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  READ
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public InputStream readFromFile(final FileID fileId) throws IOException {
		return this.readFromFile(fileId,
								 0); 		// starting at the beginning of the file
	}
    @Override
    public InputStream readFromFile(final FileID fileId,final long offset) throws IOException {
		// [1] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(fileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath fileWARelativePath = waAndRelPath.getWorkAreaRelativePath();

        // [2] - Read
		log.debug("Reading file {} at {}",
				  fileWARelativePath,csWorkArea.getVPath());

		InputStream outIS = TeamSiteFileStoreUtils.readFromFile(csWorkArea,
																fileWARelativePath,
																offset);
		return outIS;
    }
    @Override
    public byte[] readChunkFromFile(final FileID fileId,
			  		   				final long offset,final int len) throws IOException {
		// [1] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(fileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath fileWARelativePath = waAndRelPath.getWorkAreaRelativePath();

        // [2] - Read
		log.debug("Reading a chunk of the file {} at {}",
				  fileWARelativePath,csWorkArea.getVPath());

		byte[] outData = TeamSiteFileStoreUtils.readChunkFromFile(csWorkArea,fileWARelativePath,
															      offset,len);
		return outData;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean deleteFile(final FileID fileId) throws IOException {
		// [1] - Get theworkarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(fileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

        // [2] - Delete
		log.debug("Deleting file {} at {}",
				  waRelPath,csWorkArea.getVPath());

		TeamSiteFileStoreUtils.delete(csWorkArea,waRelPath,
									  false);		// do NOT consolidate
		return true;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public FileProperties getFileProperties(final FileID fileId) throws IOException {
		// [1] - Get the workarea & workarea relative path
    	TeamSiteWorkAreaAndWorkAreaRelativePath waAndRelPath = _workAreaAndWorkAreaRelativePathFor(fileId);
        CSWorkarea csWorkArea = waAndRelPath.getWorkArea();
      	TeamSiteWorkAreaRelativePath waRelPath = waAndRelPath.getWorkAreaRelativePath();

        // [2] - File Properties
        CSFile csFile = TeamSiteFileStoreFindUtils.findFolderOrFile(csWorkArea,
        														    waRelPath);
        FileProperties outProps = TeamSiteFileProperties.from(csFile);
        return outProps;
    }
    static Path fixCSFileVPath(final CSVPath csVPath) {
    	return Path.from("iwmnt").joinedWith(csVPath.getPathNoServer().toString());
    }
	@Override
	public void setFileModifiedDate(final FileID fileId, final long modifiedTimeInMillis) throws IOException {
		throw new UnsupportedOperationException();
	}
}

