package r01f.filestore.api;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import lombok.experimental.Accessors;
import r01f.filestore.api.hdfs.HDFSFileStoreAPI;
import r01f.filestore.api.hdfs.HDFSFileStoreFilerAPI;
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
public class FileStoreTestHelper {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Path ROOT_PATH = Path.from("/tmp/actemp_dev/r01_filestore_test/test_hdfs");

	public static enum CONFIGURATION_TYPE { HDFS_LOCAL_CONFIGURATION, HDFS_DESA_CONFIGURATION, HDFS_DESA_WEB_CONFIGURATION};

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static Path getRootPath() {
		return ROOT_PATH;
	}

	public static FileStoreObjectTest getFileStoreObjectTest(final CONFIGURATION_TYPE configurationType) throws IOException {
		switch(configurationType) {
			case HDFS_LOCAL_CONFIGURATION:
				return getHDFSFileStoreObjectTest("local");
			case HDFS_DESA_CONFIGURATION:
				return getHDFSFileStoreObjectTest("desa");
			case HDFS_DESA_WEB_CONFIGURATION:
				return getHDFSFileStoreObjectTest("desaweb");
			default:
				throw new IllegalArgumentException("Configuration Type Not found");
		}
	}


/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static FileStoreObjectTest getHDFSFileStoreObjectTest(final String enviroment) throws IOException {
		final Configuration conf = new Configuration();
		conf.addResource("hadoop/"+enviroment+"/core-site.xml");
		conf.addResource("hadoop/"+enviroment+"/hdfs-site.xml");
		return new FileStoreObjectTest (FileStoreTestHelper.getRootPath(),
										new HDFSFileStoreAPI(conf),
										new HDFSFileStoreFilerAPI(conf));
	}

}
