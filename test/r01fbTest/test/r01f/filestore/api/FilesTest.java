package r01f.filestore.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import r01f.file.util.Files;
import r01f.filestore.api.local.LocalFileStoreAPI;
import r01f.io.ChunkedInputStreamChunksProducer;
import r01f.io.Streams;
import r01f.types.Path;

public class FilesTest {
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	private final static Path BASE_TMP_PATH = Path.from("d:/temp_dev/r01fb/test/filestore");
	private final static Path TEST_FILE_PATH = BASE_TMP_PATH.joinedWith("testFile");
	private final static String TEST_TEXT = "El veloz murciélago hindú comía feliz cardillo y kiwi. La cigüeña toca el saxofón detrás del palenque de paja";
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	LOAD
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testLoad() throws IOException {
		FileStoreAPI api = new LocalFileStoreAPI();
		
		// Read the test file contents
		String readedText = Files.wrap(api)
								 .forLoading(TEST_FILE_PATH)
								 .asString();
		Assert.assertEquals(TEST_TEXT,
							readedText);
	}
	@Test
	public void testLoadChunked() throws IOException {
		FileStoreAPI api = new LocalFileStoreAPI();
		
		// Read the test file contents
		InputStream is = Files.wrap(api)
							  .forLoading(TEST_FILE_PATH)
							  .asChunkedInputStream(5);	// chunks of 5 bytes
		String readedText = Streams.inputStreamAsString(is);
		Assert.assertEquals(TEST_TEXT,
							readedText);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	APPEND
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testAppend() throws IOException {
		FileStoreAPI api = new LocalFileStoreAPI();
		
		// Append to the file
		Files.wrap(api)
			 .forAppendingTo(TEST_FILE_PATH)
			 .append(TEST_TEXT);
		// Read the file again and test
		Assert.assertEquals(TEST_TEXT + TEST_TEXT,
						    Files.wrap(api)
								 .forLoading(TEST_FILE_PATH)
								 .asString());
	}
	@Test
	public void testAppendChunked() throws IOException {
		FileStoreAPI api = new LocalFileStoreAPI();
		
		// Append to the file
		Files.wrap(api)
			 .forAppendingTo(TEST_FILE_PATH)
			 .append(new ChunkedInputStreamChunksProducer() {
								@Override
								public byte[] get(final long offset) throws IOException {
									long available = TEST_TEXT.length() - offset;
									if (available <= 0) return null;
									String outStr = available >= 5 ? TEST_TEXT.substring((int)offset,(int)(offset + 5))
														  		   : TEST_TEXT.substring((int)offset);
									return outStr.getBytes();
								}
			 		 });
		// Read the file again and test
		Assert.assertEquals(TEST_TEXT + TEST_TEXT,
						    Files.wrap(api)
								 .forLoading(TEST_FILE_PATH)
								 .asString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	OVERWRITE
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testWrite() throws IOException {
		FileStoreAPI api = new LocalFileStoreAPI();
		
		// Overwrite the file
		Files.wrap(api)
			 .forOverwriting(TEST_FILE_PATH)
			 .write(TEST_TEXT);
		// Read the file again and test
		Assert.assertEquals(TEST_TEXT,
						    Files.wrap(api)
								 .forLoading(TEST_FILE_PATH)
								 .asString());
	}
	@Test
	public void testWriteChunked() throws IOException {
		FileStoreAPI api = new LocalFileStoreAPI();
		
		// Append to the file
		Files.wrap(api)
			 .forOverwriting(TEST_FILE_PATH)
			 .write(new ChunkedInputStreamChunksProducer() {
								@Override
								public byte[] get(final long offset) throws IOException {
									long available = TEST_TEXT.length() - offset;
									if (available <= 0) return null;
									String outStr = available >= 5 ? TEST_TEXT.substring((int)offset,(int)(offset + 5))
														  		   : TEST_TEXT.substring((int)offset);
									return outStr.getBytes();
								}
			 		 });
		// Read the file again and test
		Assert.assertEquals(TEST_TEXT,
						    Files.wrap(api)
								 .forLoading(TEST_FILE_PATH)
								 .asString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	@Before
	public void _beforeEachTest() throws IOException {
		_createTestFile(new LocalFileStoreAPI(),
						TEST_FILE_PATH);
	}
	@After
	public void _afterEachTest() throws IOException {
		_deleteTestFile(new LocalFileStoreAPI(),
						TEST_FILE_PATH);
	}
	private static void _createTestFile(final FileStoreAPI api,
								   		final Path testFilePath) throws IOException {
		api.writeToFile(new ByteArrayInputStream(TEST_TEXT.getBytes()), 
						testFilePath,
						true);
		Assert.assertTrue(api.existsFile(testFilePath));
	}
	private static void _deleteTestFile(final FileStoreAPI api,
								   		final Path testFilePath) throws IOException {
		api.deleteFile(testFilePath);
		Assert.assertTrue(!api.existsFile(testFilePath));
	}
}
