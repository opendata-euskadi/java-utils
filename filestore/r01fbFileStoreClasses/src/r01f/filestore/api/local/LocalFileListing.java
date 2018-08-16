package r01f.filestore.api.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Recursive file listing under a specified directory.
 */
public final class LocalFileListing {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Recursively walk a directory tree and return a List of all
	 * files found, the list is sorted using File.compareTo().
	 * @param aStartingDir is a valid directory, which can be read.
	 * @param filter the filter, <code>null</code> if filter is not applied.
	 * @throws FileNotFoundException
	 */
	public List<File> getFileListing(final File aStartingDir,
	                                 final FilenameFilter filter) throws FileNotFoundException {
		_validateDirectory(aStartingDir);
		
		List<File> result = _getFileListingNoSort(aStartingDir,
												  filter);
		Collections.sort(result);
		
		return result;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private List<File> _getFileListingNoSort(final File aStartingDir,
	                                         final FilenameFilter filter) throws FileNotFoundException {
		List<File> result = Lists.newArrayList();
		File[] filesAndDirs = aStartingDir.listFiles(filter);
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		
		for (File file : filesDirs) {
			result.add(file); // always add, even if directory
			if (!file.isFile()) {
				// must be a directory
				// recursive call!
				List<File> deeperList = _getFileListingNoSort(file,
															  filter);
				result.addAll(deeperList);
			}
		}
		return result;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 * @param aDirectory the dir.
	 * @throws FileNotFoundException
	 */
	private void _validateDirectory(final File aDirectory) throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		}
	}
}
