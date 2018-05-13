package r01f.filestore.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FileStoreTest
	 extends FileStoreTestBaseFile {

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

	@BeforeClass
	public static void globalSetup() {
		System.setProperty("hadoop.home.dir",
						   "D:/hadoop/hadoop-2.7.1");		// alternative to set HADOOP_HOME
	}

	@Parameters
    public static Collection<FileStoreTestHelper.CONFIGURATION_TYPE> data() {
        return Arrays.asList(
        		FileStoreTestHelper.CONFIGURATION_TYPE.HDFS_DESA_WEB_CONFIGURATION,
        		FileStoreTestHelper.CONFIGURATION_TYPE.HDFS_LOCAL_CONFIGURATION,
        		FileStoreTestHelper.CONFIGURATION_TYPE.HDFS_DESA_CONFIGURATION
        	);
    }


	public FileStoreTest(final FileStoreTestHelper.CONFIGURATION_TYPE configurationType) throws IOException {
		this._fileStoreObjectTest = FileStoreTestHelper.getFileStoreObjectTest(configurationType);
	}

}
