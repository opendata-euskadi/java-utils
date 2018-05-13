package r01f.filestore.api.teamsite;

import java.util.regex.Matcher;

import com.interwoven.cssdk.common.CSClient;
import com.interwoven.cssdk.filesys.CSWorkarea;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.file.FileID;
import r01f.filestore.api.FileStoreChecksDelegate;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteArea;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteServer;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteStore;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteWorkArea;
import r01f.filestore.api.teamsite.TeamSiteStorageObjectsPaths.TeamSiteWorkAreaPath;
import r01f.filestore.api.teamsite.TeamSiteStorageObjectsPaths.TeamSiteWorkAreaRelativePath;
import r01f.types.Path;
import r01f.util.types.Strings;

abstract class TeamSiteFileStoreAPIBase {
///////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
	protected final TeamSiteCSSDKClientWrapper _cssdkClientWrapper;
	protected FileStoreChecksDelegate _check;

///////////////////////////////////////////////////////////////////////////////////////////
// 	FILESYSTEM STATIC INIT
///////////////////////////////////////////////////////////////////////////////////////////
	public TeamSiteFileStoreAPIBase(final TeamSiteCSSDKClientWrapper cssdkClientWrapper) {
		_cssdkClientWrapper = cssdkClientWrapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	static class TeamSiteWorkAreaAndWorkAreaRelativePath {
		@Getter private final CSWorkarea _workArea;
		@Getter private final TeamSiteWorkAreaRelativePath _workAreaRelativePath;
	}

	protected TeamSiteWorkAreaAndWorkAreaRelativePath _workAreaAndWorkAreaRelativePathFor(final FileID fileId) {
    	Path path = new Path(_fileIdToPath(fileId).asAbsoluteString());
    	return _workAreaAndWorkAreaRelativePathFor(path);
	}
	protected TeamSiteWorkAreaAndWorkAreaRelativePath _workAreaAndWorkAreaRelativePathFor(final Path path) {
    	Matcher m = TeamSiteFileStoreUtils.WA_PATTERN
    									  .matcher(path.asAbsoluteString());
    	if (m.find()) {
    		TeamSiteServer tsServer = TeamSiteServer.IWMNT;
    		TeamSiteStore tsStore = TeamSiteStore.forId(m.group(1));
    		TeamSiteArea tsArea = TeamSiteArea.forId(m.group(2));
    		TeamSiteWorkArea tsWorkarea = TeamSiteWorkArea.forId(m.group(3));
	        TeamSiteWorkAreaRelativePath waRelPath = TeamSiteWorkAreaRelativePath.create(Path.from(m.group(4))); //path.remainingPathFrom(waPath));
	        
    		TeamSiteWorkAreaPath waPath = TeamSiteWorkAreaPath.create(tsServer,tsStore,tsArea,tsWorkarea);
	        
	    	CSClient cssdkClient = _cssdkClientWrapper.getOrCreateCSSDKClient();	// this will create the cssdk client if needed
	        CSWorkarea csWorkArea = TeamSiteFileStoreFindUtils.findWorkArea(cssdkClient,
	        														 		waPath);
	        return new TeamSiteWorkAreaAndWorkAreaRelativePath(csWorkArea,
	        												   waRelPath);
    	}
        throw new IllegalArgumentException("The path " + path.asAbsoluteString() + " is NOT a valid WORKAREA path");
	}
	private Path _fileIdToPath(final FileID fileId) {
		if (fileId == null) throw new IllegalArgumentException("fileId MUST NOT be null!");
		if (!(fileId instanceof Path)) throw new IllegalArgumentException(Strings.customized("The {} instance MUST be a {} instance",
																							 FileID.class,r01f.types.Path.class));
		return (r01f.types.Path)fileId;
	}
}

