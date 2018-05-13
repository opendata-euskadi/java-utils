package r01f.filestore.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.types.Path;

/**
 *
 */
@Slf4j
public abstract class FileStoreTestBaseFolder {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	protected FileStoreObjectTest _fileStoreObjectTest;

/////////////////////////////////////////////////////////////////////////////////////////
//  EXISTENCE
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testExistFolderHappyPath() {
		// Happy path
		try {
			_fileStoreObjectTest.getFilerApi().existsFolder(_fileStoreObjectTest.getRootPath().joinedWith("test_folder"));
		} catch (IOException e1) {
			Assert.fail(e1.getMessage());
		}
	}
	@Test
	public void testExistNullFolder() {
		// Null test
		try {
			_fileStoreObjectTest.getFilerApi().existsFolder(null);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The path cannot be null"));
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}
	}
	@Test
	public void existsEmptyTest() {
		// Empty test
		try {
			_fileStoreObjectTest.getFilerApi().existsFolder(new Path());
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
	public void testCopyFolder() throws IOException {
		log.info("... test copy folder");

		_testCopyOrMoveFolder(true);
	}
	@Test
	public void testMoveFolder() throws IOException {
		log.info("... test move folder");

		_testCopyOrMoveFolder(false);
	}
	private void _testCopyOrMoveFolder(final boolean copy) throws IOException {
		// create a folder with 2 files inside
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);

		String file1Content = "This is a test file (1)";
		Path file1Path = containerFolderPath.joinedWith("test1_file.txt");
		_fileStoreObjectTest.getFileStoreApi()
							.writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
							   	  		 file1Path,
							   	  		 true);	// overwrite
		String file2Content = "This is a test file (2)";
		Path file2Path = containerFolderPath.joinedWith("test2_file.txt");
		_fileStoreObjectTest.getFileStoreApi()
							.writeToFile(new ByteArrayInputStream(file2Content.getBytes()),
										 file2Path,
										 true);		// overwrite

		// copy or move the folder
		Path containerFolderNewPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder_" + (copy ? "copy" : "moved"));
		if (copy) {
			_fileStoreObjectTest.getFilerApi()
								.copyFolder(containerFolderPath,containerFolderNewPath,
										    null,			// no file filter
										    false);		// DO NOT overwrite
		} else {
			_fileStoreObjectTest.getFilerApi()
								.moveFolder(containerFolderPath,containerFolderNewPath,
										  	false);		// DO NOT overwrite
		}

		// check the folder existence and it contents
		Path file1NewPath = containerFolderNewPath.joinedWith("test1_file.txt");
		Path file2NewPath = containerFolderNewPath.joinedWith("test2_file.txt");
		Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderNewPath)
					   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(containerFolderNewPath).isFolder());
		Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath)
				       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).isFile()
				       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).getSize() == file1Content.length());
		Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file2NewPath)
				       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file2NewPath).isFile()
				       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file2NewPath).getSize() == file2Content.length());

		// Check that copying when the new folder exists is NOT alowed!
		boolean catchedIllegal = false;
		try {
			if (copy) {
				_fileStoreObjectTest.getFilerApi()
									.copyFolder(containerFolderPath,containerFolderNewPath,
											  	null,			// no file filter
											    false);		// DO NOT overwrite
			} else {
				_fileStoreObjectTest.getFilerApi()
									.moveFolder(containerFolderPath,containerFolderNewPath,
											    false);		// DO NOT overwrite
			}
		} catch (IOException ioEx) {
			catchedIllegal = true;
		}
		Assert.assertTrue(catchedIllegal);


		// tear down
		if (copy) {
			_fileStoreObjectTest.getFileStoreApi().deleteFile(file1Path);
			_fileStoreObjectTest.getFileStoreApi().deleteFile(file2Path);
			_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderPath);
			Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(file1Path));
			Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(file2Path));
			Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(containerFolderPath));
		}

		_fileStoreObjectTest.getFileStoreApi().deleteFile(file1NewPath);
		_fileStoreObjectTest.getFileStoreApi().deleteFile(file2NewPath);
		_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderNewPath);
		Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath));
		Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(file2NewPath));
		Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(containerFolderNewPath));
	}
	@Test
	public void testCopyFolderWithOneFile() {
		//Happy case with two files
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		Path containerFolderNewPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder_copied");
		String file1Content = "This is a test file (1)";

		Path file1Path = containerFolderPath.joinedWith("test1_file.txt");

		Path file1NewPath = containerFolderNewPath.joinedWith("test1_file.txt");

		try {

			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);

			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
											 file1Path,
											 true);	// overwrite

			_fileStoreObjectTest.getFilerApi()
								.copyFolder(containerFolderPath,containerFolderNewPath,
											null,			// no file filter
											true);		// overwrite
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderNewPath)
					       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(containerFolderNewPath).isFolder());
			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath)
					       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).isFile()
					       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).getSize() == file1Content.length());


		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} finally {
			try {
				_fileStoreObjectTest.getFileStoreApi().deleteFile(file1Path);

				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(file1Path));
				Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(containerFolderPath));


				_fileStoreObjectTest.getFileStoreApi().deleteFile(file1NewPath);
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderNewPath);
				Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath));
				Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(containerFolderNewPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}

	@Test
	public void testCopyNullFolder() {
		// Null test
		try {
			_fileStoreObjectTest.getFilerApi().copyFolder(null, null, null, false);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The source & destination paths cannot be null"));
		}
	}
	@Test
	public void testCopyEmptyPathFolder() {
		//Empty test
		try {
			_fileStoreObjectTest.getFilerApi().copyFolder(new Path(), new Path(), null, false);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			//Assert.fail(e.getMessage());
		} catch (IllegalArgumentException e1) {
			Assert.assertThat(e1.getMessage(), (CoreMatchers.anyOf(CoreMatchers.is("Can not create a Path from an empty string"),
							  CoreMatchers.is("The path " + new Path() + " is NOT a valid WORKAREA path"))));
		}
	}
	/*@Test
	public void testCopyToReadOnlyFolder() {
		//No permission test
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		Path nonWritableFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("read_only_folder");
		try {

			_fileStoreObjectTest.getFilerApi().createFolder(nonWritableFolderPath);
			File readOnlyFolder = new File(nonWritableFolderPath.asAbsoluteString());
			readOnlyFolder.setReadOnly();
			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);
			_fileStoreObjectTest.getFilerApi().copyFolder(containerFolderPath, nonWritableFolderPath, null, true);
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Destination '"+nonWritableFolderPath+"' cannot be written to"));
		}
		finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderPath));
			}
			catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}*/
	@Test
	public void testCopyNonExistingFolder() {
		// Non existing folder test
		Path sourcePath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		try {
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(sourcePath));
			_fileStoreObjectTest.getFilerApi().copyFolder(sourcePath,sourcePath,null,false);
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The source folder at "+sourcePath+" does NOT exists (or maybe it's a regular file)"));
		}

		// Copy folder to itself test
		sourcePath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(sourcePath);
			_fileStoreObjectTest.getFilerApi().copyFolder(sourcePath, sourcePath, null, true);
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("Cannot copy"));
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(sourcePath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(sourcePath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCopyFolderToAFile() {
		//Copy folder to a file test
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		String file1Content = "This is a test file (1)";
		String file2Content = "This is a test file (2)";
		Path file1Path = containerFolderPath.joinedWith("test1_file.txt");
		Path file2Path = containerFolderPath.joinedWith("test2_file.txt");
		try {

			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
											 file1Path,
											 true);	// overwrite
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file2Content.getBytes()),
											 file2Path,
											 true);		// overwrite
			_fileStoreObjectTest.getFilerApi()
								.copyFolder(containerFolderPath,file1Path,
											null,			// no file filter
											false);		// DO NOT overwrite
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), 
							  CoreMatchers.is("The destination folder at " + file1Path + " already exists as a file!"));
		}
		finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderPath));
			}
			catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testMergeFolders() {
		//Merge folders test
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		Path containerFolderNewPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder_copied");
		String file1Content = "This is a test file (1)";
		String file2Content = "This is a test file (2)";
		Path file1Path = containerFolderPath.joinedWith("test1_file.txt");
		Path file2Path = containerFolderPath.joinedWith("test2_file.txt");
		Path file1NewPath = containerFolderNewPath.joinedWith("test1_file.txt");
		Path file2NewPath = containerFolderNewPath.joinedWith("test2_file.txt");
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);

			_fileStoreObjectTest.getFileStoreApi()
							    .writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
							    			 file1Path,
							    			 true);	// overwrite

			_fileStoreObjectTest.getFileStoreApi()
							    .writeToFile(new ByteArrayInputStream(file2Content.getBytes()),
							    			 file2Path,
							    			 true);		// overwrite

			String file3Content = "This is a test file (3)";
			Path file3Path = containerFolderPath.joinedWith("test3_file.txt");
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file3Content.getBytes()),
											 file3Path,
											 true);		// overwrite

			_fileStoreObjectTest.getFilerApi()
								.copyFolder(containerFolderPath,containerFolderNewPath,
											null,			// no file filter
											true);		// overwrite

			Path file3NewPath = containerFolderNewPath.joinedWith("test3_file.txt");
			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath)
					       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).isFile()
					       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).getSize() == file1Content.length());
			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file2NewPath)
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file2NewPath).isFile()
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file2NewPath).getSize() == file2Content.length());
			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath)
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).isFile()
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).getSize() == file1Content.length());
			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file3NewPath)
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file3NewPath).isFile()
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file3NewPath).getSize() == file3Content.length());


		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderPath));
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderNewPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderNewPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCopyFolderFilteringFiles() {
		// Copy only files test
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		Path containerFolderNewPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder_copied");
		String file1Content = "This is a test file (1)";
		String file2Content = "This is a test file (2)";
		Path file1Path = containerFolderPath.joinedWith("test1_file.txt");
		Path file2Path = containerFolderPath.joinedWith("test2_file.txt");
		Path file1NewPath = containerFolderNewPath.joinedWith("test1_file.txt");
		Path file2NewPath = containerFolderNewPath.joinedWith("test2_file.txt");
		try {

			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);
			Path childFolder = containerFolderPath.joinedWith("child_folder");
			_fileStoreObjectTest.getFilerApi().createFolder(childFolder);

			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
											 file1Path,
											 true);	// overwrite

			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file2Content.getBytes()),
											 file2Path,
											 true);		// overwrite

			_fileStoreObjectTest.getFilerApi()
								     .copyFolder(containerFolderPath,containerFolderNewPath,
								    		 	 new FileFilter() {
														@Override
														public boolean accept(final Path path) {
															try {
																return _fileStoreObjectTest.getFileStoreApi()
																						   .getFileProperties(path)
																						   .isFile();
															} catch(IOException ioEx) {
																ioEx.printStackTrace();
															}
															return false;
														}
												 },
								    		 	 false);					// do NOT overwrite

			Path childFolderNewPath = containerFolderNewPath.joinedWith("child_folder");

			_fileStoreObjectTest.getFilerApi().deleteFolder(childFolderNewPath);
			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath)
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).isFile()
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).getSize() == file1Content.length());
			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file2NewPath)
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file2NewPath).isFile()
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file2NewPath).getSize() == file2Content.length());
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(childFolderNewPath));
		} catch (IllegalArgumentException ex) {
			Assert.assertThat(ex.getMessage(), CoreMatchers.is("Not supported filter"));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderPath));
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderNewPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderNewPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testMoveFolderWithOneFile() {
		//Happy case with two files
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		Path containerFolderNewPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder_moved");
		String file1Content = "This is a test file (1)";
		Path file1Path = containerFolderPath.joinedWith("test1_file.txt");
		Path file1NewPath = containerFolderNewPath.joinedWith("test1_file.txt");
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
											 file1Path,
											 true);	// overwrite

			_fileStoreObjectTest.getFilerApi()
								.moveFolder(containerFolderPath,containerFolderNewPath,
											false);		// DO NOT overwrite
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderNewPath)
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(containerFolderNewPath).isFolder());
			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath)
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).isFile()
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).getSize() == file1Content.length());

		} catch (IOException e1) {
			Assert.fail(e1.getMessage());
		} finally {
			try {
				_fileStoreObjectTest.getFileStoreApi().deleteFile(file1NewPath);
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderNewPath);
				Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath));
				Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(containerFolderNewPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testMoveNullFolder() {
		// Null test
		try {
			_fileStoreObjectTest.getFilerApi()
								.moveFolder(null,null,false);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), 
							  CoreMatchers.is("The source & destination paths cannot be null"));
		}
	}
	@Test
	public void testMoveFolderToItself() {
		// Move to itself test
		Path sourcePath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(sourcePath);
			_fileStoreObjectTest.getFilerApi().moveFolder(sourcePath, sourcePath, false);
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The destination folder at "+sourcePath+" already exists!"));
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(sourcePath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(sourcePath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testMoveNonExistingFolder() {
		// Non existing folder test
		Path sourcePath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		try {
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(sourcePath));
			_fileStoreObjectTest.getFilerApi().moveFolder(sourcePath, sourcePath, false);
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), 
							  CoreMatchers.is("The source folder at " + sourcePath + " does NOT exists (or maybe it's a regular file)"));
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(sourcePath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(sourcePath));
			}
			catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testMoveFolderToFile() {
		//Move folder to file test
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		String file1Content = "This is a test file (1)";
		String file2Content = "This is a test file (2)";
		Path file1Path = containerFolderPath.joinedWith("test1_file.txt");
		Path file2Path = containerFolderPath.joinedWith("test2_file.txt");
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
																	  file1Path,
																	  true);	// overwrite
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file2Content.getBytes()),
											 file2Path,
											 true);		// overwrite
			_fileStoreObjectTest.getFilerApi()
								.moveFolder(containerFolderPath,file1Path,
											false);		// DO NOT overwrite
			Assert.fail("Expected an IOException to be thrown");

		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The destination folder at " + file1Path + " already exists as a file!"));
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderPath));
			}
			catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testMoveFolderOverringContents() {
		//Move folder to existing folder overrite=false test
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		Path containerFolderNewPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder_moved");
		try {

			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);
			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderNewPath);
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderPath));
			_fileStoreObjectTest.getFilerApi()
								.moveFolder(containerFolderPath,containerFolderNewPath,
											false);		// overwrite
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The destination folder at "+containerFolderNewPath+" already exists!"));
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderPath));
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderNewPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderNewPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testMoveFolderFilteringFiles() {
		//Move only files test
		Path containerFolderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		Path containerFolderNewPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder_moved");
		String file1Content = "This is a test file (1)";
		String file2Content = "This is a test file (2)";
		Path file1Path = containerFolderPath.joinedWith("test1_file.txt");
		Path file2Path = containerFolderPath.joinedWith("test2_file.txt");
		Path file1NewPath = containerFolderNewPath.joinedWith("test1_file.txt");
		Path file2NewPath = containerFolderNewPath.joinedWith("test2_file.txt");
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(containerFolderPath);
			Path childFolder = containerFolderPath.joinedWith("child_folder");
			_fileStoreObjectTest.getFilerApi().createFolder(childFolder);
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
											 file1Path,
											 true);	// overwrite
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file2Content.getBytes()),
											 file2Path,
											 true);		// overwrite

			_fileStoreObjectTest.getFilerApi()
								.moveFolder(containerFolderPath,containerFolderNewPath,
											false);		// do NOT overwrite
			Path childFolderNewPath = containerFolderNewPath.joinedWith("child_folder");
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderPath));
			Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(file1Path));
			Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(file2Path));

			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file1NewPath)
					       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).isFile()
					       && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file1NewPath).getSize() == file1Content.length());
			Assert.assertTrue(_fileStoreObjectTest.getFileStoreApi().existsFile(file2NewPath)
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file2NewPath).isFile()
						   && _fileStoreObjectTest.getFileStoreApi().getFileProperties(file2NewPath).getSize() == file2Content.length());
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(childFolderNewPath));

		} catch (IllegalArgumentException ex) {
			Assert.assertThat(ex.getMessage(), CoreMatchers.is("Not supported filter"));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderPath));
				_fileStoreObjectTest.getFilerApi().deleteFolder(containerFolderNewPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(containerFolderNewPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testRenameFolder()  {

		log.info("... test rename folder");

		//Happy path test
		String folderName="test_folder";
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith(folderName);
		String newNameText="test_renamed_folder";

		try {
			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
			FileNameAndExtension newName= new FileNameAndExtension(newNameText);
			_fileStoreObjectTest.getFilerApi().renameFolder(folderPath, newName);
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} finally {
			try {
				Path newNameFolderPath = _fileStoreObjectTest.getRootPath().joinedWith(newNameText);
				_fileStoreObjectTest.getFilerApi().deleteFolder(newNameFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(newNameFolderPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testRenameExistingFolder() {
		// Existing folder test
		String folderName="test_folder";
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith(folderName);
		String existingFolderName="test_existing_folder";
		Path existingFolderPath = _fileStoreObjectTest.getRootPath().joinedWith(existingFolderName);
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);

			FileNameAndExtension newName= new FileNameAndExtension(existingFolderName);

			_fileStoreObjectTest.getFilerApi().createFolder(existingFolderPath);

			_fileStoreObjectTest.getFilerApi().renameFolder(folderPath, newName);
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), 
							  CoreMatchers.is("The destination folder at " + existingFolderPath + " already exists!"));
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
				_fileStoreObjectTest.getFilerApi().deleteFolder(existingFolderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(existingFolderPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testRenameNullFolder() {
		//Null test
		try {
			_fileStoreObjectTest.getFilerApi().renameFolder(null, null);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e) {
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), 
							  CoreMatchers.is("The path cannot be null"));
		} catch (NullPointerException e) {
			Assert.assertTrue(true);
		}
	}
	@Test
	public void testRenameFolderToSameName() {
		// Same name test
		String folderName="test_folder";
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith(folderName);
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
			_fileStoreObjectTest.getFilerApi().renameFolder(folderPath, new FileNameAndExtension(folderName));
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(),
							  CoreMatchers.is("The destination folder at "+folderPath+" already exists!"));
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testRenameFolderToEmpty() {
		//Empty new name test
		String folderName="test_folder";
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith(folderName);
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
			_fileStoreObjectTest.getFilerApi().renameFolder(folderPath, new FileNameAndExtension(""));
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(), 
							  CoreMatchers.is("The destination folder at " + 
							 				  _fileStoreObjectTest.getRootPath() +
							 				  " already exists!"));
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testRenameFolderToDot() {

		//Dot name test
		String folderName="test_folder";
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith(folderName);try {

			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
			_fileStoreObjectTest.getFilerApi().renameFolder(folderPath, new FileNameAndExtension("."));
			Assert.fail("Expected an IOException to be thrown");
		} catch (IOException e) {
			Assert.assertThat(e.getMessage(),
							  CoreMatchers.is("The destination folder at " + 
									  		  _fileStoreObjectTest.getRootPath().joinedWith(".") + 
									  		  " already exists!"));
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The name cannot be empty"));
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
				Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////

//		@Test
//		public void testCreateFolder() throws IOException {
//			log.info("... test create folder");
//
//			// create a folder
//			Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
//			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
//
//			// check the folder existence
//			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
//
//			// try to create the same folder again
//			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
//
//			// tear down
//			_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
//			Assert.assertFalse(_fileStoreObjectTest.getFileStoreApi().existsFile(folderPath));
//		}
	@Test
	public void testCreateFolder() {
		// Happy path
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		try {

			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
			_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCreateNullFolder() {
		// Create folder null
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(null);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		}
		catch (IOException e1) {
			Assert.fail(e1.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The path cannot be null"));
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}
	}
	@Test
	public void testCreateFolderWithAcutes() {
		// Create folder with extrange characters
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		folderPath = _fileStoreObjectTest.getRootPath().joinedWith("test pëquêñá.##");
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
			_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCreateFolderWhithWhiteSpaceName() {
		//Create folder with extrange characters
			Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		folderPath = _fileStoreObjectTest.getRootPath().joinedWith(" ");
		try {
			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
			_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testCreateEmptyPathFolder() {
		//Create folder with empty path
		try {
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().createFolder(new Path()));
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(new Path()));
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testCreateWhiteSpaceFolder() {
		//Create space character folder
		try {
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().createFolder(new Path(" ")));
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(new Path(" ")));
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}

	}
	@Test
	public void testDeleteFolder() {
		//Happy path
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		try {
			_fileStoreObjectTest.getFilerApi()
								.createFolder(folderPath);
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
			_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testDeleteNonExistingFolder() {
		//Non existing folder test
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		try {
			Assert.assertFalse(_fileStoreObjectTest.getFilerApi().existsFolder(folderPath));
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testDeleteNullFolder() {
		// Null test
		try {
			_fileStoreObjectTest.getFilerApi().deleteFolder(null);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		}
		catch (IOException e1) {
			Assert.fail(e1.getMessage());
		}
		catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The path cannot be null"));
		}
		catch (NullPointerException e) {
			Assert.assertTrue(true);
		}
	}
	@Test
	public void testDeleteEmptyFolder() {
		//Empty test
		try {
			Assert.assertTrue(_fileStoreObjectTest.getFilerApi().deleteFolder(new Path()));
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Can not create a Path from an empty string"));
		} catch (IOException e1) {
			Assert.fail(e1.getMessage());
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LIST
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testListNullFolderContents() {
		try {
			_fileStoreObjectTest.getFilerApi().listFolderContents(null, null);
			Assert.fail("Expected an IllegalArgumentException to be thrown");
		} catch (IOException e1) {
			Assert.fail(e1.getMessage());
		} catch (IllegalArgumentException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("The path cannot be null"));
		}
	}
	@Test
	public void testListFolderContents() {
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		Path childFolderPath = folderPath.joinedWith("test_child_folder");

		try {
			Path file1Path = folderPath.joinedWith("test1_file.txt");
			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
			_fileStoreObjectTest.getFilerApi().createFolder(childFolderPath);
			String file1Content = "This is a test file (1)";
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
											 file1Path,
											 true);	// overwrite
			String file2Content = "This is a test file (2)";
			Path file2Path = folderPath.joinedWith("test2_file.txt");
												   _fileStoreObjectTest.getFileStoreApi().writeToFile(new ByteArrayInputStream(file2Content.getBytes()),
												   file2Path,
												   true);		// overwrite
			FileProperties[] result = _fileStoreObjectTest.getFilerApi()
														  .listFolderContents(folderPath, null);
																			  //childfolder + file1 + file2
			Assert.assertEquals(3, result.length);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
	@Test
	public void testListFolderContentsFilteringFiles() {
		Path folderPath = _fileStoreObjectTest.getRootPath().joinedWith("test_folder");
		Path childFolderPath = folderPath.joinedWith("test_child_folder");try {
			Path file1Path = folderPath.joinedWith("test1_file.txt");
			_fileStoreObjectTest.getFilerApi().createFolder(folderPath);
			_fileStoreObjectTest.getFilerApi().createFolder(childFolderPath);
			String file1Content = "This is a test file (1)";
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file1Content.getBytes()),
											 file1Path,
											 true);	// overwrite
			String file2Content = "This is a test file (2)";
			Path file2Path = folderPath.joinedWith("test2_file.txt");
			_fileStoreObjectTest.getFileStoreApi()
								.writeToFile(new ByteArrayInputStream(file2Content.getBytes()),
											 file2Path,
											 true);		// overwrite
			FileProperties[] result = _fileStoreObjectTest.getFilerApi()
														 .listFolderContents(folderPath,
																  			 new FileFilter() {
																					@Override
																					public boolean accept(final Path path) {
																						try {
																							return _fileStoreObjectTest.getFileStoreApi()
																													   .getFileProperties(path)
																													   .isFile();
																						} catch(IOException ioEx) {
																							ioEx.printStackTrace();
																						}
																						return false;
																					}
																			 });
			//file1 + file2
			Assert.assertEquals(2, result.length);
		} catch (IllegalArgumentException ex) {
			Assert.assertThat(ex.getMessage(), CoreMatchers.is("Not supported filter"));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			try {
				_fileStoreObjectTest.getFilerApi().deleteFolder(folderPath);
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
}
