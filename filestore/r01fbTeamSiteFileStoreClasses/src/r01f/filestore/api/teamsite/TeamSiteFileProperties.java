package r01f.filestore.api.teamsite;

import java.io.IOException;

import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.filesys.CSDir;
import com.interwoven.cssdk.filesys.CSFile;
import com.interwoven.cssdk.filesys.CSSimpleFile;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.file.FilePermission;
import r01f.file.FileProperties;
import r01f.file.FilePropertiesBase;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.UserGroupCode;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Path;

@MarshallType(as="teamSiteFileProperties")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
@Slf4j
public class TeamSiteFileProperties 
	 extends FilePropertiesBase {
	
	private static final long serialVersionUID = -5407160921534936414L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileProperties from(final CSFile csFile) throws IOException {
		// transform into FileProperties
		FileProperties outProperties = new TeamSiteFileProperties();
    	try {
			outProperties.setPath(TeamSiteFileStoreAPI.fixCSFileVPath(csFile.getVPath()));
			outProperties.setFolder(false);
			outProperties.setFolder(csFile.getKind() == CSDir.KIND);
			if (outProperties.isFile()) {
				CSSimpleFile csSimpleFile = (CSSimpleFile)csFile;
				outProperties.setSize(csSimpleFile.getSize());
			}
			outProperties.setModificationTimeStamp(csFile.getContentModificationDate().getTime());
			outProperties.setAccessTimeStamp(csFile.getContentModificationDate().getTime());
			outProperties.setGroup(UserGroupCode.forId(csFile.getGroup().getName()));
			outProperties.setOwner(UserCode.forId(csFile.getOwner().getName()));
			outProperties.setPermission(FilePermission.createFromUNIXPermissionString(csFile.getPermissions()));
    	} catch(CSException csEx) {
    		throw TeamSiteFileStoreException.createFor("fileProperties",
    												   Path.from(csFile.getVPath()),
    												   csEx);
    	}
		return outProperties;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Path getSymLink() {
		log.debug("TeamSite does NOT supports symbolic links");
		return null;
	}
	@Override
	public void setSymLink(final Path path) {
		log.debug("TeamSite does NOT supports symbolic links");
	}
}
