package r01f.filestore.api;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.filestore.api.local.LocalFileStoreAPI;
import r01f.filestore.api.local.LocalFileStoreFilerAPI;
import r01f.types.Path;

/**
 * Testing
 * 		[1] Copy winutils from http://public-repo-1.hortonworks.com/hdp-win-alpha/winutils.exe to HADOOP_HOME/bin
 * 		[2] Set at core-site.xml
 * 				   <property>
 * 				      <name>fs.defaultFS</name>
 *	   				  <value>file:///</value>
 *				   </property>
 * 				 
 */
@Accessors(prefix="_")
public class LocalFileStoreTest 
	 extends FileStoreTestBaseFile {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	private static final Path ROOT_PATH = Path.from("d:/temp_dev/r01_filestore_test/test_local");
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private Path _rootPath;
	@Getter private FileStoreAPI _fileStoreApi;
	@Getter private FileStoreFilerAPI _filerApi;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@BeforeClass
	public static void globalSetup() {
		System.setProperty("hadoop.home.dir","d:/hadoop");		// alternative to set HADOOP_HOME
	}
	@Before
	public void setup() throws IOException {
		_rootPath = ROOT_PATH;
		_fileStoreApi = new LocalFileStoreAPI(); 
		_filerApi = new LocalFileStoreFilerAPI();
	}
}
