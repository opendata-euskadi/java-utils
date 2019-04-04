package r01f.filestore.api.teamsite;

import com.interwoven.cssdk.common.CSClient;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.filesys.CSAreaRelativePath;
import com.interwoven.cssdk.filesys.CSBranch;
import com.interwoven.cssdk.filesys.CSDir;
import com.interwoven.cssdk.filesys.CSFile;
import com.interwoven.cssdk.filesys.CSHole;
import com.interwoven.cssdk.filesys.CSNode;
import com.interwoven.cssdk.filesys.CSRoot;
import com.interwoven.cssdk.filesys.CSSimpleFile;
import com.interwoven.cssdk.filesys.CSStaging;
import com.interwoven.cssdk.filesys.CSStore;
import com.interwoven.cssdk.filesys.CSVPath;
import com.interwoven.cssdk.filesys.CSWorkarea;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteServer;
import r01f.filestore.api.teamsite.TeamSiteStorageObjects.TeamSiteStore;
import r01f.filestore.api.teamsite.TeamSiteStorageObjectsPaths.TeamSiteAreaStagingPath;
import r01f.filestore.api.teamsite.TeamSiteStorageObjectsPaths.TeamSiteWorkAreaPath;
import r01f.filestore.api.teamsite.TeamSiteStorageObjectsPaths.TeamSiteWorkAreaRelativePath;
import r01f.types.Path;
import r01f.util.types.Strings;

/**
 * Interwoven TeamSite utils
 */
@Slf4j
@NoArgsConstructor
public class TeamSiteFileStoreFindUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Finds a server
     * @param root 
     * @param store
     * @return 
     * @throws TeamSiteFileStoreException 
     */
    public static CSStore findServer(final CSRoot root,
    								 final TeamSiteServer server) throws TeamSiteFileStoreException {
        if (root == null) {
            return null;
        }
        CSStore outServer = null;
        try {
            log.debug("\t...find root store {} at {}",
            		  root.getVPath(),server);

            CSStore[] stores = root.getStores();
            for (int i = 0; i < stores.length; i++) {
                outServer = stores[i];
                if (outServer.getName().equals(server.getId())) {
                    break;
                }
            }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("findStore",
            								   		   Path.from(root.getVPath(),server),
            								   		   csEx);
        }
        return outServer;
    }
    /**
     * Finds a datastore 
     * @param store 
     * @param branch 
     * @return 
     * @throws TeamSiteFileStoreException
     */
    public static CSBranch findStore(final CSStore server,
    								 final TeamSiteStore store) throws TeamSiteFileStoreException {
        if (server == null) {
            return null;
        }
        CSBranch outStore = null;
        try {
            log.debug("\t...find a store {} at server {}",
            		  store,server.getName());

            CSBranch[] stores = server.getBranches();
            for (int i = 0; i < stores.length; i++) {
                if (stores[i].getName().equals(store.getId())) {
                	outStore = stores[i];
                    break;
                }
            }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("findBranch",
            								   		   Path.from(server.getVPath(),store),
            								   		   csEx);
        }
        return outStore;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Finds a workarea
     * @param csClient 
     * @param workAreaPath 
     * @return 
     * @throws TeamSiteFileStoreException
     */
    public static CSWorkarea findWorkArea(final CSClient csClient,
    									  final TeamSiteWorkAreaPath workAreaPath) throws TeamSiteFileStoreException {
        CSWorkarea outWorkArea = null;
        try {
            log.debug("\t...find workArea {}",
            		  workAreaPath);

            CSVPath waPath = new CSVPath("/" + workAreaPath.asAbsoluteString());	// the workArea path MUST be like //iwmnt/store/area/WORKAREA/workArea
            outWorkArea = csClient.getWorkarea(waPath,
            								   true);
            if (outWorkArea == null) {
                throw new TeamSiteFileStoreException("Could NOT find workArea " + workAreaPath);
            }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("findWorkArea",
            							 	   		   workAreaPath,
            							 	   		   csEx);
        }
        return outWorkArea;
    }
    /**
     * Finds a staging object.
     * @param csClient 
     * @param stagingPath 
     * @return 
     * @throws TeamSiteFileStoreException 
     */
    public static CSStaging findStaging(final CSClient csClient,
    									final TeamSiteAreaStagingPath stagingPath) throws TeamSiteFileStoreException {
        try {
            log.debug("\t...find staging area {}",
            		  stagingPath);

            CSVPath stagingCSVPath = new CSVPath("/" + stagingPath.asAbsoluteString());	// the staging area path MUST be like //iwmnt/store/area/STAGING/
            CSStaging outStaging = csClient.getStaging(stagingCSVPath, true);

            if (null == outStaging) {
                throw new TeamSiteFileStoreException("Could NOT find the staging path " + stagingPath);
            }
            return outStaging;
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("findStaging",
            								   		   stagingPath,
            								   		   csEx);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Finds a file or a folder at a workarea
     * @param csWorkArea 
     * @param waRelPath 
     * @return 
     * @throws TeamSiteFileStoreException 
     */
    public static CSFile findFolderOrFile(final CSWorkarea csWorkArea,
    									  final TeamSiteWorkAreaRelativePath waRelPath) throws TeamSiteFileStoreException {

        if (null == csWorkArea || null == waRelPath) throw new IllegalArgumentException("Area and area relative path are mandatory");
        try {
            log.trace("\t...find file or folder {} at {}",
            		  waRelPath,csWorkArea.getVPath());

            return csWorkArea.getFile(new CSAreaRelativePath(waRelPath.asRelativeString()));
            
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("findFolderOrFile",
            								   		   Path.from(csWorkArea.getVPath(),waRelPath),
            								   		   csEx);
        }
    }
    /**
     * Finds a folder at a workarea; if the folder does NOT exists or it's not folder, it returns null
     * @param csWorkArea 
     * @param folderWARelPath
     * @return
     * @throws TeamSiteFileStoreException 
     */
    public static CSDir findFolder(final CSWorkarea csWorkArea,
    							   final TeamSiteWorkAreaRelativePath folderWARelPath) throws TeamSiteFileStoreException {
        log.trace("\t...find folder {} at {}",
        		  folderWARelPath,csWorkArea.getVPath());
        CSFile csFile = TeamSiteFileStoreFindUtils.findFolderOrFile(csWorkArea,
        													        folderWARelPath);
        try {
            if (csFile != null 
            && csFile.getKind() == CSDir.KIND) {
                return (CSDir)csFile;
            }             
            else if (csFile != null
            	 && csFile.getKind() == CSHole.KIND) {
            	if (((CSHole)csFile).getPreviousKind() == CSDir.KIND) log.warn("Folder {} at {} is now a hole",
	            			 												   folderWARelPath,csWorkArea.getVPath());
            	return null;	// it's NOT a folder //throw new TeamSiteFileStoreException(folderWARelPath + " is NOT a folder!");
            } 
            else if (csFile != null) {
            	log.warn("file {} at {} is NOT a folder, it's a {}",
            			 folderWARelPath,csWorkArea.getVPath(),csFile.getKind());
                return null;    // the object does NOT exists
            } 
            else {
            	return null;
            }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("findFolder",
            								   		   Path.from(csWorkArea.getVPath(),folderWARelPath),
            								   		   csEx);
        }
    }
    /**
     * Finds a file at a workarea; if the file does NOT exists or it's not a file, it returns null
     * @param csWorkArea
     * @param fileWorkAreaRelPath 
     * @return 
     * @throws TeamSiteFileStoreException 
     */
    public static CSSimpleFile findSimpleFile(final CSWorkarea csWorkArea,
    										  final TeamSiteWorkAreaRelativePath fileWorkAreaRelPath) throws TeamSiteFileStoreException {
        return TeamSiteFileStoreFindUtils.findSimpleFile(csWorkArea,
        											     fileWorkAreaRelPath,
        											     false);   // do not ignore holes
    }
    /**
     * Finds a file at a workarea; if the file does NOT exists or it's not a file, it returns null
     * @param csWorkArea 
     * @param fileWARelPath 
     * @param ignoreHoles 
     * @return 
     * @throws TeamSiteFileStoreException 
     */
    public static CSSimpleFile findSimpleFile(final CSWorkarea csWorkArea,
    										  final TeamSiteWorkAreaRelativePath fileWARelPath,
    										  final boolean ignoreHoles) throws TeamSiteFileStoreException {
        log.trace("\t...find simple file {} at {}",
        		  fileWARelPath,csWorkArea.getVPath());
        CSFile csFile = TeamSiteFileStoreFindUtils.findFolderOrFile(csWorkArea,
        														    fileWARelPath);
        try {
            if (csFile != null && (csFile.getKind() == CSFile.KIND || csFile.getKind() == CSSimpleFile.KIND)) {
                return (CSSimpleFile)csFile;
            } else if (ignoreHoles && null != csFile && csFile.getKind() == CSHole.KIND) {
                return null;    // the file does NOT exists now it's a hole
            } else if (csFile != null && !(csFile.getKind() == CSHole.KIND && ((CSHole)csFile).getPreviousKind() == CSSimpleFile.KIND)) {
                throw new TeamSiteFileStoreException(fileWARelPath + " is NOT a file!");
            } else {
                return null;    // The file does NOT exists
            }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("findSimpleFile",
            								   		   Path.from(csWorkArea.getVPath(),fileWARelPath),
            								   		   csEx);
        }
    }
    /**
     * Finds a hole at a workarea; if the hole does NOT exists or it's not a hole, it returns null
     * @param csWorkArea 
     * @param holeWARelPath 
     * @return 
     * @throws TeamSiteFileStoreException 
     */
    public static CSHole findHole(final CSWorkarea csWorkArea,
    							  final TeamSiteWorkAreaRelativePath holeWARelPath) throws TeamSiteFileStoreException {
        log.trace("\t...find a hole {} at {}",
        		  holeWARelPath,csWorkArea.getVPath());
        CSFile csFile = TeamSiteFileStoreFindUtils.findFolderOrFile(csWorkArea,holeWARelPath);
        try {
            if (csFile != null && csFile.getKind() == CSHole.KIND) {
                return (CSHole)csFile;
            } else if (csFile != null){
                throw new TeamSiteFileStoreException(holeWARelPath + " is NOT a hole!");
            } else {
                return null;    // Object does NOT exists
            }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("findHole",
            								   		   Path.from(csWorkArea.getVPath(),holeWARelPath),
            								   		   csEx);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Lists folder contents
     * @param csWorkArea
     * @param folderWorkAreaRelPath
     * @return
     * @throws TeamSiteFileStoreException
     */
    public static CSNode[] listFolderContents(final CSWorkarea csWorkArea,
    										  final TeamSiteWorkAreaRelativePath folderWorkAreaRelPath) throws TeamSiteFileStoreException {
        log.trace("\t...listing folder {} at {}",
        		  folderWorkAreaRelPath,csWorkArea.getVPath());
        
        CSFile csFile = TeamSiteFileStoreFindUtils.findFolder(csWorkArea,folderWorkAreaRelPath);

    	if (csFile == null) throw new TeamSiteFileStoreException(Strings.customized("The folder {} at {} does NOT exists or it's NOT a folder",
    																				folderWorkAreaRelPath,csWorkArea.getVPath()));
    	try {
	    	CSDir csDir = (CSDir)csFile;
		    return  csDir.getChildren();
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("findFolderChildren",
            								   		   Path.from(csWorkArea.getVPath(),folderWorkAreaRelPath),
            								   		   csEx);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND CHILD
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Finds a folder's child file
     * @param folder 
     * @param childName 
     * @return 
     * @throws CSException error.
     */
    private static CSNode _findChildFile(final CSDir folder,
    								     final String childName) throws CSException {
        CSNode child = null;
        CSNode[] children = folder.getChildren();

        // Check all nodes 
        for (int i = 0; i < children.length; i++) {
            if ( children[i].getKind() == CSDir.KIND && ((CSDir)children[i]).getName().equals(childName) ) {
                child = children[i];
                break;
            } else if ( children[i].getKind() == CSSimpleFile.KIND && ((CSSimpleFile)children[i]).getName().equals(childName) ) {
                child = children[i];
                break;
            }
        }
        return child;
    }
}