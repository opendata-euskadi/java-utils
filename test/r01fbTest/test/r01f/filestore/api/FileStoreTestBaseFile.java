package r01f.filestore.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileProperties;
import r01f.io.util.StringPersistenceUtils;
import r01f.types.Path;

/**
 *
 */
@Slf4j
public abstract class FileStoreTestBaseFile
			 extends FileStoreTestBaseFolder {

/*****************************************************************************************************
 * FILE STORE API
 ******************************************************************************************************/

/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTENCE
/////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testExistsFile() {
		//Happy path
		log.info("... test create file");
		String fileContent = "This is a file file";
		try {
			Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite
			this._fileStoreObjectTest.getFileStoreApi().existsFile(path);
		} catch (IOException e1) {
			Assert.fail(e1.getMessage());
		}
	}
	@Test
	public void testExistsNullFile() {
		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().existsFile(null);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The fileId MUST NOT be null!"));
		}
	}
	@Test
	public void testExistEmptyPathFile() {
		//Empty test
		try {
			this._fileStoreObjectTest.getFileStoreApi().existsFile(new Path());
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COPY
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testCopyFile() {

		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().copyFile(null, null, false);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The fileId MUST NOT be null!"));
		}
	}
	@Test
	public void testCopyEmptyPathFile() {
		//Empty test (root path), no write permission
		try {
			this._fileStoreObjectTest.getFileStoreApi().copyFile(new Path(), new Path(), false);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e1) {
			Assert.assertThat(e1.getMessage(), (CoreMatchers.anyOf(CoreMatchers.is("Can not create a Path from an empty string"),
						CoreMatchers.is("The path "+new Path()+" is NOT a valid WORKAREA path"))));
		}
	}
	/* Se puede copiar siendo el origen y destino el mismo fichero
	@Test
	public void testCopyFileToItself() {
		//Same path test
		String fileContent = "This is a copied file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {

			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite
			this._fileStoreObjectTest.getFileStoreApi().copyFile(path, path, true);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		}
		catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e1) {
			Assert.assertThat(e1.getMessage(), CoreMatchers.is("Source /"+path+" and destination /"+path+" must be different"));
		}
	}*/
	@Test
	public void testCopyNonExistingFile() {
		//Overrite non existing file
		String newPathString="/test_destination_file.txt";
		Path newPath = this._fileStoreObjectTest.getRootPath().joinedWith(newPathString);
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String fileContent = "This is a copied file";
		try {
			path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite

			this._fileStoreObjectTest.getFileStoreApi().copyFile(path, newPath, true);
		}
		catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(newPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCopyToExistingFileNotOverriting() {
		//No Overrite existing file
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String fileContent = "This is a copied file";
		String newPathString="/test_destination_file.txt";
		Path newPath = this._fileStoreObjectTest.getRootPath().joinedWith(newPathString);
		try {
			path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   newPath,
										   true);		// overwrite

			this._fileStoreObjectTest.getFileStoreApi().copyFile(path, newPath, false);
			Assert.fail("Expected an IOException to be thrown");
		}
		catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The destination file "+newPath+" already exists!"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(newPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}

	}
	@Test
	public void testCopyAndOverriteFile() {
		//Happy path overrite=true
		String newPathString="/test_destination_file.txt";
		Path destinationFilePath = this._fileStoreObjectTest.getRootPath().joinedWith(newPathString);
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String fileContent = "This is a copied file";
		Path newPath = this._fileStoreObjectTest.getRootPath().joinedWith(newPathString);
		try {
			path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite
			String destinationFileContent = "This is a file";

			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(destinationFileContent.getBytes()),
										   destinationFilePath,
										   true);		// overwrite

			this._fileStoreObjectTest.getFileStoreApi().copyFile(path, newPath, true);
			String readedFileContent=StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));
			Assert.assertTrue(fileContent.equals(readedFileContent));
		}
		catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(newPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}

	}
	@Test
	public void testCopyAndNotOverriteFile() {
		//Happy path overrite=false
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String fileContent = "This is a copied file";
		String newPathString="/test_destination_file.txt";
		Path newPath = this._fileStoreObjectTest.getRootPath().joinedWith(newPathString);
		try {
			path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite

			this._fileStoreObjectTest.getFileStoreApi().copyFile(path, newPath, false);
		}
		catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(newPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	/*@Test
	public void testCopyAndOverriteToReadOnlyFile() {

		//Overrite readOnly file
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String newPathString="/test_destination_file.txt";
		Path destinationFilePath = this._fileStoreObjectTest.getRootPath().joinedWith(newPathString);
		String fileContent = "This is a copied file";
		Path newPath = this._fileStoreObjectTest.getRootPath().joinedWith(newPathString);
		try {
			path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite
			String destinationFileContent = "This is a file";

			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(destinationFileContent.getBytes()),
										   destinationFilePath,
										   true);		// overwrite
			File file1 = new File(destinationFilePath.asAbsoluteString());
			file1.setReadOnly();
			Assert.assertFalse(file1.canWrite());
			this._fileStoreObjectTest.getFileStoreApi().copyFile(path, newPath, true);
			Assert.fail("Expected an IOException to be thrown");
			String readedFileContent=StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));
			Assert.assertTrue(fileContent.equals(readedFileContent));
		}
		catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is(newPath.asAbsoluteString()+" (Permission denied)"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(newPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}

	}*/
	@Test
	public void testRenameNullFile() {

		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().renameFile(null, null);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The fileId MUST NOT be null!"));
		}
	}
	@Test
	public void testRenameFileToItself() {
		//Same path test
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("/test_file.txt");
		String fileContent = "This is a test file";
		try {

			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);// overwrite

			this._fileStoreObjectTest.getFileStoreApi().renameFile(path, path);
			Assert.fail("Expected an IOException to be thrown");
		}
		catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The destination file "+path+" already exists!"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCopyFileAndDontOverrideToExistingFile() {
		//Existing path test
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("/test_file.txt");
		Path newPath = this._fileStoreObjectTest.getRootPath().joinedWith("/moved/test_file(copy).txt");
		String fileContent = "This is a copied file";
		String destinationContent = "This is a test file";
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);// overwrite
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(destinationContent.getBytes()),
										   newPath,
										   true);// overwrite
			this._fileStoreObjectTest.getFileStoreApi().copyFile(path, newPath, false);
			Assert.fail("Expected an IOException to be thrown");

		}
		catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The destination file "+newPath+" already exists!"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(newPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCopyFileAndOverwrite() {
	//Happy path overwrite=true
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("/test_file.txt");
	Path newPath = this._fileStoreObjectTest.getRootPath().joinedWith("/moved/test_file(copy).txt");
	String fileContent = "This is a copied file";
	String destinationContent = "This is a test file";
	try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);// overwrite
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(destinationContent.getBytes()),
										   newPath,
										   true);// overwrite
			this._fileStoreObjectTest.getFileStoreApi().copyFile(path, newPath, true);
		}
		catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(newPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCopyFileAndNotOverwrite() {
		//Happy path overwrite=false
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("/test_file.txt");
		Path newPath = this._fileStoreObjectTest.getRootPath().joinedWith("/moved/test_file(copy).txt");
		String fileContent = "This is a copied file";
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);// overwrite
			this._fileStoreObjectTest.getFileStoreApi().copyFile(path, newPath, false);
		}
		catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(newPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  WRITE
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testGetNullFileOutputStreamForWriting() {
		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().getFileOutputStreamForWriting(null,
																 false); //overwrite

			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The fileId MUST NOT be null!"));
		}
	}
	@Test
	public void testGetEmpyPathFileOutputStreamForWriting() {
		//Empty test
		try {
			this._fileStoreObjectTest.getFileStoreApi().getFileOutputStreamForWriting(new Path(),
																false); //overwrite

			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		}
	}
	@Test
	public void testGetExistingFileOutputStreamForWritingAndOverwrite() {
		// Overrite=false with existing file test
		String fileContent = "This is a test file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("/test_file.txt");
		try {
			String additionalContent=" with aditional text added";

			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite
			Assert.assertTrue(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			OutputStream stream=this._fileStoreObjectTest.getFileStoreApi().getFileOutputStreamForWriting(path, false);
			Assert.assertNotNull(stream);
			stream.write(additionalContent.getBytes());
			stream.flush();
			stream.close();
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The file "+path+" cannot be written: already exists!"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testGetFileOutputStreamForWritingAndDontOverwrite() {
		//Happy path overrite=false
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("/test_file.txt");
		try {
			String additionalContent=" with aditional text added";
			Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			OutputStream stream=this._fileStoreObjectTest.getFileStoreApi().getFileOutputStreamForWriting(path, false);
			Assert.assertNotNull(stream);
			stream.write(additionalContent.getBytes());
			stream.flush();
			stream.close();
			String readedFileContent = StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));
			Assert.assertTrue(additionalContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testGetFileOutputStreamForWritingAndOverwrite() {
		//Happy path overrite=true
		String fileContent = "This is a test file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("/test_file.txt");
		try {
			String additionalContent=" with aditional text added";
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite
			OutputStream stream=this._fileStoreObjectTest.getFileStoreApi().getFileOutputStreamForWriting(path, true);
			Assert.assertNotNull(stream);
			stream.write(additionalContent.getBytes());
			stream.flush();
			stream.close();
			String readedFileContent = StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));
			Assert.assertTrue(additionalContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCreateFile() throws IOException {
		log.info("... test create file");
		String fileContent = "This is a file file";

		// create a file
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite
		// read the file content
		InputStream fileIs = this._fileStoreObjectTest.getFileStoreApi().readFromFile(path);
		String readedFileContent = StringPersistenceUtils.load(fileIs);
		Assert.assertTrue(fileContent.equals(readedFileContent));


		// tear down
		this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
		Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
	}
	@Test
	public void testWriteToFile() {

		//Happy path
		String fileContent = "This is a file file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
											   path,
											   true); // overwrite
			InputStream fileIs = this._fileStoreObjectTest.getFileStoreApi().readFromFile(path);
			String readedFileContent = StringPersistenceUtils.load(fileIs);
			Assert.assertTrue(fileContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testOverwriteNonExistingFile() {
		//Overwrite a non existing file
		String fileContent = "This is a file file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
											   path,
											   true); // overwrite
			InputStream fileIs = this._fileStoreObjectTest.getFileStoreApi().readFromFile(path);
			String readedFileContent = StringPersistenceUtils.load(fileIs);
			Assert.assertTrue(fileContent.equals(readedFileContent));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testWriteNullFile() {
		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(null,
											   null,
											   true); // overwrite
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			//Assert.assertThat(e.getMessage(), CoreMatchers.is("The source stream is null or the destination FileID is null or it's NOT a Path"));
		}
	}
	@Test
	public void testWriteEmptyPathFile() {
		//Empty (root path, no write permission)
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(new byte[] {}),
											   new Path(),
											   false); // overwrite
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		}

	}
	@Test
	public void testWriteInAnExistingFileAndNotOverwrite() {
		//Not overrite an existing file
		String fileContent = "This is a file file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
											   path,
											   false); // overwrite
			Assert.assertTrue(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
											   path,
											   false); // overwrite
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.anyOf(CoreMatchers.is("The file "+path+" cannot be written: already exists!"),
					CoreMatchers.startsWith("Cannot overrite file ")));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	/*@Test
	public void testWriteInAReadOnlyFile() {
		//No write permissions test
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String fileContent1 = "This is a read-only file";
		Path path1 = this._fileStoreObjectTest.getRootPath().joinedWith("readOnly_test_file.txt");
		try {
			File file1 = new File(path1.asAbsoluteString());
			file1.setReadOnly();
			Assert.assertFalse(file1.canRead());
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent1.getBytes()),
											   path1,
											   true); // overwrite
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is(path1.asAbsoluteString()+" (Permission denied)"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}*/
	@Test
	public void testGetNullFileOutputStreamForAppending() {
		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().getFileOutputStreamForAppending(null); //overwrite

			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The fileId MUST NOT be null!"));
		}
	}
	@Test
	public void testGetEmptyPathFileOutputStreamForAppending() {
		//Empty test
		try {
			this._fileStoreObjectTest.getFileStoreApi().getFileOutputStreamForAppending(new Path());
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		}
	}
	@Test
	public void testGetExistingFileOutputStreamForAppending() {
		//Happy path append text
		String fileContent = "This is a test file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String additionalContent=" with aditional text added";

		try {

			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
										   path,
										   true);		// overwrite
			Assert.assertTrue(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			OutputStream stream=this._fileStoreObjectTest.getFileStoreApi().getFileOutputStreamForAppending(path);
			Assert.assertNotNull(stream);
			stream.write(additionalContent.getBytes());
			stream.flush();
			stream.close();
			//Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The file "+path+" cannot be written: already exists!"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testGetNewFileOutputStreamForAppending() {
		//Happy path new file
		//String fileContent = "This is a test file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String additionalContent=" with aditional text added";
		try {
			Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			//String fileContent = StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));
			//String expectedResultContent=fileContent+additionalContent;
			OutputStream stream=this._fileStoreObjectTest.getFileStoreApi().getFileOutputStreamForWriting(path, false);
			Assert.assertNotNull(stream);
			stream.write(additionalContent.getBytes());
			stream.flush();
			stream.close();
			String readedFileContent = StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));
			Assert.assertTrue(additionalContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testAppendToFile() {
		//Happy path
		String fileContent = "This is a file file";
		String additionalContent=" with aditional text added";
		String expectedResultContent=fileContent+additionalContent;

		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
											   path,
											   true); // overwrite
			this._fileStoreObjectTest.getFileStoreApi().appendToFile(new ByteArrayInputStream(additionalContent.getBytes()),
											   path); // overwrite
			InputStream fileIs = this._fileStoreObjectTest.getFileStoreApi().readFromFile(path);
			String readedFileContent = StringPersistenceUtils.load(fileIs);
			Assert.assertTrue(expectedResultContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testAppendToNonExistingFile() {
		//Non existing file test
		//String fileContent = "This is a file file";
		String additionalContent=" with aditional text added";

		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			this._fileStoreObjectTest.getFileStoreApi().appendToFile(new ByteArrayInputStream(additionalContent.getBytes()),
											   path);
			InputStream fileIs = this._fileStoreObjectTest.getFileStoreApi().readFromFile(path);
			String readedFileContent = StringPersistenceUtils.load(fileIs);
			Assert.assertTrue(additionalContent.equals(readedFileContent));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testAppendToNullFile() {
		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().appendToFile(null,
											   null);
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The source input stream cannot be null"));
		}
	}
	@Test
	public void testAppendToEmpyPathFile() {
		//Empty (root path, no write permission)
		try {
			this._fileStoreObjectTest.getFileStoreApi().appendToFile(new ByteArrayInputStream(new byte[] {}),
											   new Path()); // overwrite
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		 catch (IllegalArgumentException e) {
			 Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		 }

	}
	/*@Test
	public void testAppendToReadOnlyFile() {
		//No write permissions test
		String fileContent1 = "This is a read-only file";
		Path path1 = this._fileStoreObjectTest.getRootPath().joinedWith("readOnly_test_file.txt");

		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			File file1 = new File(path1.asAbsoluteString());
			file1.setReadOnly();
			Assert.assertFalse(file1.canRead());
			this._fileStoreObjectTest.getFileStoreApi().appendToFile(new ByteArrayInputStream(fileContent1.getBytes()),
											   path1);
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is(path1.asAbsoluteString()+" (Permission denied)"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}*/
	@Test
	public void testCreateFileByChunks() throws IOException {
		log.info("... test create file by chunks");
		String fileContent = "This is a test file";
		// create a file
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {

			this._fileStoreObjectTest.getFileStoreApi().appendChunkToFile("This is a ".getBytes(),
													 path);
			StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));

			log.info("\t...FIRST CHUNK OK!!!");
			this._fileStoreObjectTest.getFileStoreApi().appendChunkToFile("test file".getBytes(),
										 			 path);
			log.info("\t...SECOND CHUNK OK!!!");

			// read the file content
			String readedFileContent = StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));
			Assert.assertTrue(fileContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}

		}
	}
	@Test
	public void testAppendChunkToFile() {

		//Happy path (two chunks)
		String fileContent = "This is a test file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String firstChunk="This is a ";
		String secondChunk="test file";
		String expectedResult=fileContent+firstChunk+secondChunk;
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
											   path,
											   true); // overwrite
			this._fileStoreObjectTest.getFileStoreApi().appendChunkToFile(firstChunk.getBytes(),
													 path);
			Assert.assertNotNull(StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path)));
			this._fileStoreObjectTest.getFileStoreApi().appendChunkToFile(secondChunk.getBytes(),
									 			 path);
			String readedFileContent = StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));
			Assert.assertTrue(expectedResult.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}

		}
	}
	@Test
	public void testAppendChunkToNonExistingFile() {
		//Non existing file test
		String fileContent = "This is a test file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String firstChunk="This is a ";
		String secondChunk="test file";
		try {
			this._fileStoreObjectTest.getFileStoreApi().appendChunkToFile(firstChunk.getBytes(),
													 path);
			Assert.assertNotNull(StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path)));
			this._fileStoreObjectTest.getFileStoreApi().appendChunkToFile(secondChunk.getBytes(),
									 			 path);
			String readedFileContent = StringPersistenceUtils.load(this._fileStoreObjectTest.getFileStoreApi().readFromFile(path));
			Assert.assertTrue(fileContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}

		}
	}
	@Test
	public void testAppendChunkToNullFile() {

		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().appendChunkToFile(null,
													 null);
				Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		 catch (IllegalArgumentException e) {
			 Assert.assertThat(e.getMessage(), CoreMatchers.is("The path cannot be null"));
		 }
		catch (NullPointerException e1) {
			Assert.fail(e1.getMessage());
		}
	}
	@Test
	public void testAppendChunkToEmptyPathFile() {
		//Empty
		String firstChunk="This is a ";
		try {
			this._fileStoreObjectTest.getFileStoreApi().appendChunkToFile(firstChunk.getBytes(),
											   new Path());
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		 catch (IllegalArgumentException e) {
			 Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		 }

	}
	/*@Test
	public void testAppendChunkToReadOnlyFile() {
		//No write permissions test
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String firstChunk="This is a ";
		Path path1 = this._fileStoreObjectTest.getRootPath().joinedWith("readOnly_test_file.txt");
		try {
			File file1 = new File(path1.asAbsoluteString());
			file1.setReadOnly();
			Assert.assertFalse(file1.canRead());
			this._fileStoreObjectTest.getFileStoreApi().appendChunkToFile(firstChunk.getBytes(),
											   path1);
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is(path1.asAbsoluteString()+" (Permission denied)"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}

	}*/
/////////////////////////////////////////////////////////////////////////////////////////
//  READ
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testReadFromFile() {
		//Happy path
		String fileContent = "This is a file file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
					path,
					true); // overwrite
			InputStream fileIs = this._fileStoreObjectTest.getFileStoreApi().readFromFile(path);
			String readedFileContent = StringPersistenceUtils.load(fileIs);
			Assert.assertTrue(fileContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testReadNonExistingFileTest() {
		//Non existing file test
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			this._fileStoreObjectTest.getFileStoreApi().readFromFile(path);
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.anyOf(CoreMatchers.is("Cannot read from the file "+path.asString()+": it does NOT exists (or maybe it's a folder)!"),
					CoreMatchers.startsWith("Could NOT find file ")));
		}
	}
	@Test
	public void testReadNullFile() {
		//Null test
		String fileContent = "This is a file file";
		try {
			InputStream fileIs = this._fileStoreObjectTest.getFileStoreApi().readFromFile(null);
			Assert.fail("Expected an IOException to be thrown");
			String readedFileContent = StringPersistenceUtils.load(fileIs);
			Assert.assertTrue(fileContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The fileId MUST NOT be null!"));
		}
	}
	@Test
	public void testReadEmptyPathTest() {
		//Empty test (root path, no read permissions)
		String fileContent = "This is a file file";
		try {
			InputStream fileIs = this._fileStoreObjectTest.getFileStoreApi().readFromFile(new Path());
			String readedFileContent = StringPersistenceUtils.load(fileIs);
			Assert.assertTrue(fileContent.equals(readedFileContent));
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		 catch (IllegalArgumentException e) {
			 Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		 }
	}
	/*@Test
	public void testReadNonAllowedFile() {
		//No permission to read test
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String fileContent = "This is a file file";
		String fileContent1 = "This is a non readable file";
		Path path1 = this._fileStoreObjectTest.getRootPath().joinedWith("test_file1.txt");
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent1.getBytes()),
					path1,
					true); // overwrite
			File file = new File(path1.asAbsoluteString());
			file.setReadable(false);

			InputStream fileIs = this._fileStoreObjectTest.getFileStoreApi().readFromFile(path);
			Assert.fail("Expected an IOException to be thrown");
			String readedFileContent = StringPersistenceUtils.load(fileIs);
			Assert.assertTrue(fileContent.equals(readedFileContent));

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is(path1.asAbsoluteString()+" (Permission denied)"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path1);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path1));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}

	}*/
	@Test
	public void testReadChunkFromFile() {
		//Happy path
		String fileContent = "This is a test file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String firstChunk="This is a ";
		String secondChunk="test file";
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
					path,
					true); // overwrite new String(secondChunk).equals(firstChunk)
			byte[] fistReadedChunk = this._fileStoreObjectTest.getFileStoreApi().readChunkFromFile(path, 0, firstChunk.length());
			byte[] secondReadedChunk = this._fileStoreObjectTest.getFileStoreApi().readChunkFromFile(path, firstChunk.length(), secondChunk.length());

			Assert.assertTrue(new String(fistReadedChunk).equals(firstChunk));
			Assert.assertTrue(new String(secondReadedChunk).equals(secondChunk));


		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testReadChunkFromNonExistingFile() {
		//Non existing file test
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		String firstChunk="This is a ";
		try {
			this._fileStoreObjectTest.getFileStoreApi().readChunkFromFile(path, 0, firstChunk.length());
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.anyOf(CoreMatchers.is("Cannot read from the file "+path+": it does NOT exists (or maybe it's a folder)!"),
					CoreMatchers.startsWith("Could NOT find file ")));
		}
	}
	@Test
	public void testReadChunkFromNullFile() {

		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().readChunkFromFile(null, 0, 0);
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The fileId MUST NOT be null!"));
		}
	}
	@Test
	public void testReadChunkFromEmptyPathFile() {
		//Empty test (root path, no read permissions)
		String firstChunk="This is a ";
		try {
			this._fileStoreObjectTest.getFileStoreApi().readChunkFromFile(new Path(), 0, firstChunk.length());

			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		 catch (IllegalArgumentException e) {
			 Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		 }
	}
	/*@Test
	public void testReadChunkFromNotAllowedFile() {
		//No permission to read test
		String firstChunk="This is a ";
		String fileContent1 = "This is a non readable file";
		Path path1 = this._fileStoreObjectTest.getRootPath().joinedWith("test_file1.txt");
		try {
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent1.getBytes()),
					path1,
					true); // overwrite
			File file = new File(path1.asAbsoluteString());
			file.setReadable(false);

			this._fileStoreObjectTest.getFileStoreApi().readChunkFromFile(new Path(path1.asAbsoluteString()), 0, firstChunk.length());
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is(path1.asAbsoluteString()+" (Permission denied)"));
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path1);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path1));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}*/
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testDeleteFile() {
		//Happy path
		String fileContent = "This is a file file";
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
											   path,
											   false); // overwrite
			Assert.assertTrue(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
			Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testNonExistingFilePath() {
		//Non existing file test
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			Assert.assertTrue(this._fileStoreObjectTest.getFileStoreApi().deleteFile(path));
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.anyOf(CoreMatchers.is("File "+path+" does NOT exists (or maybe it's a folder)!"),
						CoreMatchers.startsWith(("Could NOT find file "+new Path()+" at work area"))));
		}
	}
	@Test
	public void testDeleteNullFile() {

		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().deleteFile(null);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		}
		catch (IOException e1) {
			Assert.fail(e1.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The fileId MUST NOT be null!"));
		}
	}
	@Test
	public void testDeleteEmptyPathFile() {
		//Empty test
		try {
			Assert.assertTrue(this._fileStoreObjectTest.getFileStoreApi().deleteFile(new Path()));
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		} catch (IOException e1) {
			Assert.assertThat(e1.getMessage(), CoreMatchers.is("File "+new Path()+" does NOT exists (or maybe it's a folder)!"));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PROPERTIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testGetFileProperties() {

		//Happy path
		Path path = this._fileStoreObjectTest.getRootPath().joinedWith("test_file.txt");
		try {
			String fileContent = "This is a test file";
			this._fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(fileContent.getBytes()),
								   	  		   path,
								   	  		   true); // overwrite
			FileProperties props = this._fileStoreObjectTest.getFileStoreApi().getFileProperties(path);
			log.info("\n{}",props.debugInfo());
			Assert.assertTrue(props.getPath().equals(path));
			Assert.assertTrue(props.getSize() == fileContent.length());
			Assert.assertFalse(props.isFolder());

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				this._fileStoreObjectTest.getFileStoreApi().deleteFile(path);
				Assert.assertFalse(this._fileStoreObjectTest.getFileStoreApi().existsFile(path));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testGetNullFileProperties() {
		//Null test
		try {
			this._fileStoreObjectTest.getFileStoreApi().getFileProperties(null);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("fileId MUST NOT be null!"));
		}
	}
	@Test
	public void testGetEmptyPathFileProperties() {
		//Empty test
		try {
			this._fileStoreObjectTest.getFileStoreApi().getFileProperties(new Path());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
				Assert.assertThat(e.getMessage(), (CoreMatchers.anyOf(CoreMatchers.is("Can not create a Path from an empty string"),
						CoreMatchers.is("The path "+new Path()+" is NOT a valid WORKAREA path"))));

		}
	}
	@Test
	public void testGetDotNameFileProperties() {
		//Dot name test
		try {
			this._fileStoreObjectTest.getFileStoreApi().getFileProperties(new Path("."));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		catch (IllegalArgumentException e) {
				Assert.assertThat(e.getMessage(), (CoreMatchers.anyOf(CoreMatchers.is("Path cannot be empty"),
						CoreMatchers.is("The path "+new Path()+" is NOT a valid WORKAREA path"))));
		}

	}
}
