package r01f.filestore.api.teamsite;

import com.interwoven.cssdk.access.CSAuthorizationException;
import com.interwoven.cssdk.access.CSExpiredSessionException;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.common.CSIllegalOpException;
import com.interwoven.cssdk.common.CSInvalidNameException;
import com.interwoven.cssdk.common.CSObjectNotFoundException;
import com.interwoven.cssdk.common.CSRemoteException;
import com.interwoven.cssdk.common.CSServerBusyException;
import com.interwoven.cssdk.common.CSUnsupportedOpException;
import com.interwoven.cssdk.filesys.CSConflictException;
import com.interwoven.cssdk.filesys.CSObjectAlreadyExistsException;
import com.interwoven.cssdk.filesys.CSOverwriteFailedException;
import com.interwoven.cssdk.filesys.CSReadOnlyFileSystemException;

import r01f.types.Path;
import r01f.util.types.Strings;

public class TeamSiteFileStoreException
	 extends RuntimeException {

	private static final long serialVersionUID = -5568273177938443695L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	TeamSiteFileStoreException(final Throwable th) {
		super(th);
	}
	TeamSiteFileStoreException(final String msg) {
		super(msg);
	}
	TeamSiteFileStoreException(final String msg,final Object... vars) {
		this(Strings.customized(msg,vars));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public static TeamSiteFileStoreException createFor(final String operation,
    												   final CSException csEx) {
    	return TeamSiteFileStoreException.createFor(operation,
    												null,
    												csEx);
    }
    public static TeamSiteFileStoreException createFor(final String operation,
    												   final Path path,
    												   final CSException csEx) {
		if (csEx instanceof CSRemoteException) {
			return new TeamSiteFileStoreException("[" + operation + "] CANNOT CONNECT WITH SERVER > " + csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSServerBusyException) {
			return new TeamSiteFileStoreException("[" + operation + "] SERVER BUSY > " + csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSExpiredSessionException) {
			return new TeamSiteFileStoreException("[" + operation + "] CLIENT SESSION EXPIRED > " + csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSReadOnlyFileSystemException) {
			return new TeamSiteFileStoreException("[" + operation + "] FILESTORE IS FROZEN > " + csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSUnsupportedOpException) {
			return new TeamSiteFileStoreException("[" + operation + "] UNSUPPORTED OPERATION " + (path != null ? path : "") + " > " + csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSIllegalOpException) {
			return new TeamSiteFileStoreException("[" + operation + "] ILLEGAL OPERATION " + (path != null ? path : "") + " > " + csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSAuthorizationException) {
			return new TeamSiteFileStoreException("[" + operation + "] UNAUTHORIZED " + (path != null ? path : "") + " > " + csEx.getMessage(),csEx);
		} 
		else if (csEx instanceof CSObjectNotFoundException) {
			return new TeamSiteFileStoreException("[" + operation + "] OBJECT NOT FOUND " + (path != null ? path : "") + " > "+ csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSObjectAlreadyExistsException) {
			return new TeamSiteFileStoreException("[" + operation + "] ALREADY EXISTS" + (path != null ? path : "") + " > " + csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSInvalidNameException) {
			return new TeamSiteFileStoreException("[" + operation + "] INVALID NAME " + (path != null ? path : "") + " > " + csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSOverwriteFailedException) {
			return new TeamSiteFileStoreException("[" + operation + "] OVERWRITE FAILED " + (path != null ? path : "") + " > " + csEx.getMessage(),csEx);
		}
		else if (csEx instanceof CSConflictException) {
			return new TeamSiteFileStoreException("[" + operation + "] " + (path != null ? path : "") + ": conflict > " + csEx.getMessage(),csEx);
		}  
		else {
			throw new TeamSiteFileStoreException(csEx.getMessage());
		}
    }
}
