package r01f.filestore.api.hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import com.google.common.collect.Lists;

/**
 * Recursive file listing under a specified directory.
 */
public final class HDFSFileListing {

	/**
	 * Recursively walk a directory tree and return a List of all
	 * @param aStartingDir is a valid directory, which can be read.
	 * @param filter the filter
	 */
	public FileStatus[] getFileListing(final FileSystem fs,
	                                   final Path aStartingDir,
	                                   final PathFilter filter) throws IOException {
		_validateDirectory(fs, aStartingDir);
		return _getFileListing(fs, aStartingDir, filter).toArray(new FileStatus[0]);
	}

	private List<FileStatus> _getFileListing(final FileSystem fs,
			                                 final Path aStartingDir,
			                                 final PathFilter filter) throws IOException {
		
		List<FileStatus> result = Lists.newArrayList();
		
		FileStatus[] filesAndDirs;
		if(filter != null) {
			filesAndDirs = fs.listStatus(aStartingDir, filter);
		} else {
			filesAndDirs = fs.listStatus(aStartingDir);
		}
		
		List<FileStatus> filesDirs = Arrays.asList(filesAndDirs);		
		for (final FileStatus file : filesDirs) {
			result.add(file); // always add, even if directory
			if (!file.isFile()) {
				// must be a directory
				// recursive call!
				List<FileStatus> deeperList = _getFileListing(fs, file.getPath(), filter);
				result.addAll(deeperList);
			}
		}
		
		return result;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 */
	private void _validateDirectory(final FileSystem fs,
	                                final Path aDirectory) throws IOException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!fs.exists(aDirectory)) {
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}
		if (!fs.isDirectory(aDirectory)) {
			throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		}
	}
}
