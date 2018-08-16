package r01f.filestore.api;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;

import lombok.experimental.Accessors;
import r01f.filestore.api.teamsite.TeamSiteAuthData;
import r01f.filestore.api.teamsite.TeamSiteCSSDKClientWrapper;
import r01f.filestore.api.teamsite.TeamSiteFileStoreAPI;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.guids.CommonOIDs.UserRole;
import r01f.types.Path;

/**
 * Use: http://www.contenidos.servicios.jakinaplus.ejiedes.net/iw-cc/
 */
@Accessors(prefix="_")
public class TeamSiteFileStoreApiTest
	 extends FileStoreFileAPITestBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	private static final Path ROOT_PATH = Path.from("/iwmnt/euskadiplus/main/r01_formacion/WORKAREA/wr0fog1/test_teamsite_file_store10");
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TeamSiteFileStoreApiTest() throws IOException {
		super(ROOT_PATH,
			  new TeamSiteFileStoreAPI(_createTSCSSDKClient()));
	}
	private static TeamSiteCSSDKClientWrapper _createTSCSSDKClient() {
		// TeamSite storage
		TeamSiteAuthData authData = new TeamSiteAuthData()
											.login(UserCode.forId("r01d"),
												   Password.forId("r01d"),
												   UserRole.forId("Editor"))
											.forContentServicesUrl("http://iwdes01.ejgvdns:80");
		TeamSiteCSSDKClientWrapper cssdkClientWrapper = TeamSiteCSSDKClientWrapper.createCachingClient(authData);
		return cssdkClientWrapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@BeforeClass
	public static void globalSetup() {
		// nothing
	}
	@Before
	public void setup() throws IOException {
		// nothing
	}
}
