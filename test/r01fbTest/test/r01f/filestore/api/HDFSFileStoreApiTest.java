package r01f.filestore.api;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;

import lombok.experimental.Accessors;
import r01f.filestore.api.hdfs.HDFSFileStoreAPI;
import r01f.types.Path;

/**
 * Testing
 * 		[1] Copy winutils from http://public-repo-1.hortonworks.com/hdp-win-alpha/winutils.exe to HADOOP_HOME/bin
 * 		[2] Set at core-site.xml
 * 				   <property>
 * 				      <name>fs.defaultFS</name>
 *	   				  <value>file:///</value>
 *				   </property>
 */
@Accessors(prefix="_")
public class HDFSFileStoreApiTest 
	 extends FileStoreFileAPITestBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	private static final Path ROOT_PATH = Path.from("d:/temp_dev/r01_filestore_test/test_local");
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HDFSFileStoreApiTest() throws IOException {
		this("local");
	}
	public HDFSFileStoreApiTest(final String env) throws IOException {
		super(ROOT_PATH,
			  new HDFSFileStoreAPI(_buildConfig(env)));
	}
	private static Configuration _buildConfig(final String env) {
		Configuration conf = new Configuration();
		conf.addResource("hadoop/" + env + "/core-site.xml");
		conf.addResource("hadoop/" + env + "/hdfs-site.xml");
		return conf;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@BeforeClass
	public static void globalSetup() {
		System.setProperty("hadoop.home.dir","d:/hadoop");		// alternative to set HADOOP_HOME
	}
	@Before
	public void setup() throws IOException {
		// nothing
	}
}
