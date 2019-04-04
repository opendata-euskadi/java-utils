package r01f.filestore.api.teamsite;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.interwoven.cssdk.client.axis.filesys.CSExtendedAttributeAdapter;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.filesys.CSAreaRelativePath;
import com.interwoven.cssdk.filesys.CSDir;
import com.interwoven.cssdk.filesys.CSExtendedAttribute;
import com.interwoven.cssdk.filesys.CSFile;
import com.interwoven.cssdk.filesys.CSFileCmpResult;
import com.interwoven.cssdk.filesys.CSFileNotLockedException;
import com.interwoven.cssdk.filesys.CSHole;
import com.interwoven.cssdk.filesys.CSNode;
import com.interwoven.cssdk.filesys.CSPathCommentPair;
import com.interwoven.cssdk.filesys.CSPathStatus;
import com.interwoven.cssdk.filesys.CSSimpleFile;
import com.interwoven.cssdk.filesys.CSSubmitResult;
import com.interwoven.cssdk.filesys.CSUpdateResult;
import com.interwoven.cssdk.filesys.CSVPath;
import com.interwoven.cssdk.filesys.CSWorkarea;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.file.FileNameAndExtension;
import r01f.filestore.api.teamsite.TeamSiteStorageObjectsPaths.TeamSiteWorkAreaRelativePath;
import r01f.io.ChunkedInputStream;
import r01f.io.ChunkedInputStreamChunksProducer;
import r01f.io.ChunkedOutputStream;
import r01f.io.ChunkedOutputStreamChunksConsumer;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Interwoven TeamSite utils
 */
@Slf4j
@NoArgsConstructor
public class TeamSiteFileStoreUtils {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Pattern 
	 */
	static final Pattern WA_PATTERN = Pattern.compile("/iwmnt" +	//
													  "/([^/]+)" +	// [1]  store 
													  "/main" +
													  "/([^/]+)" +	// [2]  area
													  "/WORKAREA" + 
													  "/([^/]+)" + 	// [3]  workarea
													  "/?(.*)");	// [4]  file / folder
    /**
     * Enable file locking (when using the same user is not necessary to enable them).
     */
    private static final boolean LOCK_ENABLED = false;
    /**
     * Read/write buffer (block) size 8M 
     */
    public static final int RW_BLOCK_SIZE = 8*1024;  // buffer de 8M
/////////////////////////////////////////////////////////////////////////////////////////
//  UTILITY METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    private static final Function<TeamSiteWorkAreaRelativePath,CSAreaRelativePath> _createWorkAreaRelPathToCSAreaRelPathTransformFunction(final CSWorkarea csWorkArea) { 
    	return new Function<TeamSiteWorkAreaRelativePath,CSAreaRelativePath>() {
						@Override
						public CSAreaRelativePath apply(final TeamSiteWorkAreaRelativePath waRelPath) {
							CSVPath workAreaCSVPath = csWorkArea.getVPath();
							Path areaRelPath = Path.from(workAreaCSVPath.getAreaRelativePath())
														  .joinedWith(waRelPath);
							return new CSAreaRelativePath(areaRelPath.asRelativeString());
						}
   				};
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns an OutputStream to the file
     * @param csWorkArea
     * @param fileWARelPath
     * @param writeStartPosition the position within the file where to start writing
     * @param createParentFolders
     * @param append
     * @param overwrite
     * @return
     * @throws TeamSiteFileStoreException
     */
    public static OutputStream getFileOutputStreamForWriting(final CSWorkarea csWorkArea,final TeamSiteWorkAreaRelativePath fileWARelPath,
    														 final long writeStartPosition,
    														 final boolean createParentFolders,
    														 final boolean overwrite) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - OutputStream for writing file {} at {} (overwrite: {} / create parent folders:{})",
    			  fileWARelPath,csWorkArea.getVPath(),
    			  overwrite,createParentFolders);
    	if (writeStartPosition < 0) throw new IllegalArgumentException("File write start position MUST be >= 0");
    		
        _checkCSStorageNode(csWorkArea);
        
        OutputStream outOS = null;
        CSSimpleFile csFile = null;
//        synchronized (fileWARelPath) {                        
            try {
            	// Prepare the file to be written (throws an exception if the file already exists and overwrite = false)
            	csFile = _prepareFileToBeWritten(csWorkArea,
            									 fileWARelPath, 
            									 createParentFolders,
            									 false,				// NOT appending?
            									 overwrite);		// overwriting?
            	// File writing
                // BEWARE!!! 	The transfer to TeamSite is ALWAYS CHUNKED
                //				... even if a stream is used to hand the data,
                //					this stream is converted into a CHUNKED
                //					writing to TeamSite
                log.trace("\t...writing data to file {} at {}",
                		  fileWARelPath,csWorkArea.getVPath());
                
                // outOS = csFile.getOutputStream(overwrite); <-- does NOT work!!!!!!!
                // use a chunked output stream
                outOS = new TeamSiteChunkedOutputStream(csWorkArea,fileWARelPath,
                										csFile,
                										writeStartPosition,
                										overwrite);
            } catch (CSException csEx) {
                throw TeamSiteFileStoreException.createFor("writeToFile",
                								   		   Path.from(csWorkArea.getVPath(),fileWARelPath),
                								   		   csEx);
            } 
//		}	// synchronized
        return outOS;
    }
    /**
     * Returns an OutputStream to the file
     * @param csWorkArea
     * @param fileWARelPath
     * @param writeStartPosition the position within the file where to start writing
     * @param createParentFolders
     * @param append
     * @param overwrite
     * @return
     * @throws TeamSiteFileStoreException
     */
    public static OutputStream getFileOutputStreamForAppending(final CSWorkarea csWorkArea,final TeamSiteWorkAreaRelativePath fileWARelPath,
    														   final boolean createParentFolders) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - OutputStream for appending file {} at {} (create parent folders:{})",
    			  fileWARelPath,csWorkArea.getVPath(),
    			  createParentFolders);
    		
        _checkCSStorageNode(csWorkArea);
        
        OutputStream outOS = null;
        CSSimpleFile csFile = null;
//        synchronized (fileWARelPath) {                        
            try {
            	// Prepare the file to be written (throws an exception if the file already exists and overwrite = false)
            	csFile = _prepareFileToBeWritten(csWorkArea,
            									 fileWARelPath, 
            									 createParentFolders,
            									 true,		// appending
            									 false);	// NOT overwriting?
            	// File writing
                // BEWARE!!! 	The transfer to TeamSite is ALWAYS CHUNKED
                //				... even if a stream is used to hand the data,
                //					this stream is converted into a CHUNKED
                //					writing to TeamSite
                log.trace("\t...appending data to file {} at {}",
                		  fileWARelPath,csWorkArea.getVPath());
                
                // outOS = csFile.getOutputStream(overwrite); <-- does NOT work!!!!!!!
                // use a chunked output stream
                outOS = new TeamSiteChunkedOutputStream(csWorkArea,fileWARelPath,
                										csFile,
                										csFile.getSize(),	// write start position
                										false);				// NOT overwriting
            } catch (CSException csEx) {
                throw TeamSiteFileStoreException.createFor("writeToFile",
                								   		   Path.from(csWorkArea.getVPath(),fileWARelPath),
                								   		   csEx);
            } 
//		}	// synchronized
        return outOS;
    }
    /**
     * BEWARE!!! 	The transfer to TeamSite is ALWAYS CHUNKED
	 *				... even if a stream is used to hand the data to TeamSite,
	 *	                this stream is converted into a CHUNKED writing to TeamSite
     */
    public static class TeamSiteChunkedOutputStream
    			extends ChunkedOutputStream {
    	private final CSWorkarea _csWorkArea;
    	private final CSSimpleFile _csFile;
    	
    	private TeamSiteChunkedOutputStream(final CSWorkarea csWorkArea,final TeamSiteWorkAreaRelativePath fileWARelPath,
										    final CSSimpleFile csFile,
										    final long writeStartPosition,
										    final boolean overwrite) throws CSException {
    		super(RW_BLOCK_SIZE,		// chunk size...
    			  new ChunkedOutputStreamChunksConsumer() {
						@Override
						public boolean put(final long offset,
										   final byte[] srcChunk) throws IOException {
			            	// File writing
			                // BEWARE!!! 	The transfer to TeamSite is ALWAYS CHUNKED
			                //				... even if a stream is used to hand the data to TeamSite,
			                //					this stream is converted into a CHUNKED writing to TeamSite
							try {
								int srcChunkSize = srcChunk.length;
			                    if (srcChunkSize > 0) {
			                    	// the offset
			                    	long theOffset = writeStartPosition + offset;
			                    	
			                    	// if overwrite = true, only set the effOverwrite = true when
			                    	// writing the FIRST CHUNK (dstFileOffset = 0)
			                    	boolean effOverwrite = overwrite == false ? false
			                    											  : theOffset == 0 ? true 	// the first written chunk overwrites
			                    													  		   : false;	// ... the next chunks does NOT overwrite
			                    	csFile.write(srcChunk,
			                    				 theOffset,srcChunkSize,
			                    				 effOverwrite);				// truncate (overwrite) = true
			                    	return true;
			                    }
							} catch(CSException csEx) {
								throw TeamSiteFileStoreException.createFor("writeToFile",
																		   Path.from(csWorkArea.getVPath(),fileWARelPath),
																		   		     csEx);
							}
							return false;
						}
				});
    		// store the file to release it once finished writing data
    		_csWorkArea = csWorkArea;
    		_csFile = csFile;
    	}
		@Override
		public void close() throws IOException {
			super.close();
			// ensure the file lock is released!!!!!
        	_releaseBeingWritenOrAppendedFile(_csWorkArea,
        							          _csFile);
		}
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Writes to a file; if it does NOT exists, it creates it; if it does previously exist, it gets overwritten
     * @param srcIs source data.
     * @param csWorkArea 
     * @param fileWARelPath
     * @param writeStartPosition the position within the file where the data is going to be start written
     * @param createParentFolders if true it creates the parent folder structure that contains the file if it does NOT existed previously
     * @param overwrite if false and the file already exists, an exception is thrown when trying to write 
     * @throws TeamSiteFileStoreException 
     */
    public static void writeToFile(final InputStream srcIs,
    							   final CSWorkarea csWorkArea,final TeamSiteWorkAreaRelativePath fileWARelPath,
    							   final long writeStartPosition,
                                   final boolean createParentFolders,
                                   final boolean overwrite) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - writeToFile {} at {} (overwrite: {} / create parent folders:{})",
    			  fileWARelPath,csWorkArea.getVPath(),
    			  overwrite,createParentFolders);
    	// [1] - Create an output stream to the file
    	OutputStream os = TeamSiteFileStoreUtils.getFileOutputStreamForWriting(csWorkArea,fileWARelPath,
    																		   writeStartPosition,
    																		   createParentFolders,	
    																		   overwrite);
    	// [2] - Flush the source inputStream into the file outputStream
        log.trace("\t...writing data to file {} at {}",
        		  fileWARelPath,csWorkArea.getVPath());
        try {
	        byte[] srcChunk = new byte[RW_BLOCK_SIZE];  // buffer de 8k
	        int srcChunkSize = -1;
	        int dstFileOffset = 0;
	        do {
				srcChunkSize = srcIs.read(srcChunk);					// read from the source
	            if (srcChunkSize > 0) {
	            	os.write(srcChunk,
	            			 dstFileOffset,srcChunkSize);
	            	dstFileOffset += srcChunkSize;
	            }
	        } while(srcChunkSize != -1);
        } catch(IOException ioEx) {
        	throw new TeamSiteFileStoreException(ioEx);
        } finally {
        	try {
        		os.close();
        	} catch(IOException ioEx) {
        		throw new TeamSiteFileStoreException(ioEx);	
        	}
        }
    }
    /**
     * Appends a data chunk to a file; if the file does NOT exists it creates a new one
     * @param srcDataChunk 
     * @param csWorkArea 
     * @param fileWARelPath 
     * @param createParentFolders if true it creates the parent folder structure that contains the file if it does NOT existed previously
     * @throws TeamSiteFileStoreException 
     */
    public static void appendChunkToFile(final byte[] srcDataChunk,
    									 final CSWorkarea csWorkArea,final TeamSiteWorkAreaRelativePath fileWARelPath,
                                         final boolean createParentFolders) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - writeChunkToFile {} at {} (create parent folders:{})",
    			  fileWARelPath,csWorkArea.getVPath(),
    			  createParentFolders);
    	
        if (srcDataChunk == null || srcDataChunk.length == 0) throw new TeamSiteFileStoreException("The data to be written to file " + fileWARelPath.asRelativeString() + " at workArea " + csWorkArea.getVPath() + " cannot be null");
        
    	// [1] - Create an output stream to the file
    	OutputStream os = TeamSiteFileStoreUtils.getFileOutputStreamForAppending(csWorkArea,fileWARelPath,
    																		   	 createParentFolders);
    	// [2] - Flush the source bytes into the file outputStream
        log.trace("\t...writing data to file {} at {}",
        		  fileWARelPath,csWorkArea.getVPath());
        try {
        	
        	os.write(srcDataChunk);
        	
        } catch(IOException ioEx) {
        	throw new TeamSiteFileStoreException(ioEx);
        } finally {
        	try {
        		os.close();
        	} catch(IOException ioEx) {
        		throw new TeamSiteFileStoreException(ioEx);	
        	}
        }
    }
    
    
    
    private static CSSimpleFile _prepareFileToBeWritten(final CSWorkarea csWorkArea,final TeamSiteWorkAreaRelativePath fileWARelPath,
				    								    final boolean createParentFolders,
				    								    final boolean append,final boolean overwrite) throws CSException {
        // Check if the file already exists
    	CSSimpleFile prevExistCSFile = TeamSiteFileStoreFindUtils.findSimpleFile(csWorkArea,
    																             fileWARelPath);
    	boolean newCSFile = prevExistCSFile == null;        

    	// if the file already exists and overwrite = false >> error
    	if (!newCSFile && !append && !overwrite) throw new TeamSiteFileStoreException(Strings.customized("Cannot overwrite file {} at {}: the file already existed and overwrite=false",
    																		   				  			 fileWARelPath,csWorkArea.getVPath()));
    
    	// Prepare the file 
        CSSimpleFile outCSFile = null;
        
    	// Create a new file if it does NOT previously exist
        if (newCSFile || prevExistCSFile.getKind() == CSHole.KIND) {
        	// Create a NEW file
		    // a) Check if the parent folder exists
	    	CSDir parentCSFolder = _findFileParentFolder(csWorkArea,
	    												 fileWARelPath);
	    	// if the parent folder does NOT exists, create it
	    	if (parentCSFolder == null) {	    		
	    		TeamSiteWorkAreaRelativePath parentFolderWARelPath = TeamSiteWorkAreaRelativePath.create(Path.from(fileWARelPath.getPathElementsExceptLast()));
	    		log.trace("\t...creating the file parent folders {} at {}",
	    				  parentFolderWARelPath,csWorkArea.getVPath());
	    		
	    		parentCSFolder = _createFolder(csWorkArea,
	    									   parentFolderWARelPath,
	    									   createParentFolders);
	    	}
    		// b) Create the file
    		outCSFile = parentCSFolder.createChildSimpleFile(fileWARelPath.getLastPathElement());
        } else {
        	// return the existing file        	
        	outCSFile = prevExistCSFile;
        }
        
        // Lock the file 
        if (LOCK_ENABLED) _lockFile(outCSFile);

        // return the file
        return outCSFile;
    }
    private static void _releaseBeingWritenOrAppendedFile(final CSWorkarea csWorkArea,final CSSimpleFile csFile) {
    	if (LOCK_ENABLED) _unLockFile(csFile);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  LOCK & UNLOCK FILE
/////////////////////////////////////////////////////////////////////////////////////////
    private static void _lockFile(final CSSimpleFile theFile) throws TeamSiteFileStoreException {
	    try {
	    	if (theFile != null && !theFile.isLocked() ) {
	            log.trace("\t...lock file {}",theFile.getVPath());
			    theFile.lock("R01M-WritingToFile-" + (new Date()).getTime());
	    	}
	    } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("_lockFile",
            								   		   Path.from(theFile.getVPath()),
            								   		   csEx);
	    }
    }
    private static void _unLockFile(final CSSimpleFile theFile) throws TeamSiteFileStoreException {
        try {
            if (theFile != null && theFile.isValid()) {
            	log.trace("\t...unlock file {}",theFile.getVPath());
                try {
                    theFile.unlock();
                } catch (CSFileNotLockedException fnlEx) {/* ignore */}
            }
        } catch (CSException csEx) {
            log.error("\tError while unlocking file {}: > {}",
            		  theFile.getVPath(),csEx.getMessage(),
            		  csEx);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  READ
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Reads from a file
     * @param csWorkArea 
     * @param fileWARelPath 
     * @param readStartPosition the offset within the file where to start reading
     * @return stream an stream with the file
     * @throws TeamSiteFileStoreException si ocurre algún error al acceder al DataStore.
     */
    public static InputStream readFromFile(final CSWorkarea csWorkArea,final TeamSiteWorkAreaRelativePath fileWARelPath,
    									   final long readStartPosition) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - readFromFile {} at {}",
    			  fileWARelPath,csWorkArea.getVPath());
    	if (readStartPosition < 0) throw new IllegalArgumentException("File read start position MUST be >= 0");
        // Find the file
        final CSSimpleFile csFile = TeamSiteFileStoreFindUtils.findSimpleFile(csWorkArea,
        															          fileWARelPath);

        // Read it
        if (csFile == null) throw new TeamSiteFileStoreException("Could NOT find file " + fileWARelPath + " at work area " + csWorkArea.getVPath());
                    
        // Since the data from TeamSite can only be readed in a chunked way, in order to abstract 
        // the client, an ChunkedInputStream is returned
        ChunkedInputStream outIs = new ChunkedInputStream(new ChunkedInputStreamChunksProducer() {
																	@Override
																	public byte[] get(final long offset) throws IOException {
																		try {
																			byte[] readBuffer = csFile.read(readStartPosition + offset,
																											RW_BLOCK_SIZE);
																			return readBuffer;
																        } catch (CSException csEx) {
																            throw TeamSiteFileStoreException.createFor("readChunkFromFile",
																            								   		   Path.from(csWorkArea.getVPath(),fileWARelPath),
																            								   		   csEx);
																        }
																	}
        												  }); 
//	A VERY INEFFICENT WAY (reads all file in memory!!!)
//            final CSInputStream csInputStream = (CSInputStream)csFile.getInputStream(true);
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			  byte[] readBuffer = new byte[RW_BLOCK_SIZE];   // buffer de 8k
//            int readed;
//            while ((readed = csInputStream.read(readBuffer)) != -1) {
//                outputStream.write(readBuffer,0,readed);
//            }
//            csInputStream.close();
//            outputStream.flush();
//            outputStream.close();

            return outIs;
    }
    /**
     * Reads a file chunk
     * @param csWorkArea 
     * @param fileWARelPath 
     * @param offset position from where the file is read
     * @param length the number of bytes to be read
     * @return a byte array with the read chunk or null if nothing is available
     * @throws TeamSiteFileStoreException 
     */
    public static byte[] readChunkFromFile(final CSWorkarea csWorkArea,final TeamSiteWorkAreaRelativePath fileWARelPath,
    									   final long offset,final int length) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - readChunkFromFile {} at {}",
    			  fileWARelPath,csWorkArea.getVPath());
        try {
            // Find the file
            CSSimpleFile csFile = TeamSiteFileStoreFindUtils.findSimpleFile(csWorkArea,
            															    fileWARelPath);

            if (csFile == null) throw new TeamSiteFileStoreException("Could NOT find file " + fileWARelPath + " at work area " + csWorkArea.getVPath());
            
            // Reads file
            return csFile.read(offset,
            				   length);

        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("readChunkFromFile",
            								   		   Path.from(csWorkArea.getVPath(),fileWARelPath),
            								   		   csEx);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  EXT ATTRS
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Returns the extended attributes
     * @param csWorkArea 
     * @param fileWARelPath 
     * @return an attr-value map
     * @throws TeamSiteFileStoreException 
     */
    public static Properties getFileExtendedAttributes(final CSWorkarea csWorkArea,
    												   final TeamSiteWorkAreaRelativePath fileWARelPath) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - getFileExtendedAttributes of {} at {}",
    			  fileWARelPath,csWorkArea.getVPath());
    	
        Properties outExtAttrs = null;
        
        // Find the file...
        CSSimpleFile file = TeamSiteFileStoreFindUtils.findSimpleFile(csWorkArea,
        														  	  fileWARelPath);
        if (file == null) throw new TeamSiteFileStoreException("Could NOT find file " + fileWARelPath + " at work area " + csWorkArea.getVPath());
        try {
            CSExtendedAttribute[] extAttrsArray = file.getExtendedAttributes(null);
            if (extAttrsArray != null && extAttrsArray.length > 0) {
                outExtAttrs = new Properties();
                for (int i=0; i<extAttrsArray.length; i++) {
                    outExtAttrs.put(extAttrsArray[i].getName(),
                                    extAttrsArray[i].getValue());
                }
            }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("getFileExtendedAttributes",
            								   		   Path.from(csWorkArea.getVPath(),fileWARelPath),
            								   		   csEx);
        }
        return outExtAttrs;
    }
    /**
     * Sets the extended attributes
     * @param csWorkArea 
     * @param fileWARelPath 
     * @param extAttrs an attr-value map
     * @throws TeamSiteFileStoreException 
     */
    public static void setFileExtendedAttributes(final CSWorkarea csWorkArea,
    										     final TeamSiteWorkAreaRelativePath fileWARelPath,
    										     final Properties extAttrs) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - setFileExtendedAttributes of {} at {}",
    			  fileWARelPath,csWorkArea.getVPath());
    	
        if (null == extAttrs|| extAttrs.size() == 0) return;

        // Find the file
        CSSimpleFile csFile = TeamSiteFileStoreFindUtils.findSimpleFile(csWorkArea,
        														        fileWARelPath);
        if (csFile == null) throw new TeamSiteFileStoreException("Could NOT find file " + fileWARelPath + " at work area " + csWorkArea.getVPath());
        
        // get the existing attributes
        Properties extAttrsMap = TeamSiteFileStoreUtils.getFileExtendedAttributes(csWorkArea,
        																		  fileWARelPath);
        // add the new attributes
        for (Map.Entry<Object, Object> me1 : extAttrs.entrySet()) {
            extAttrsMap.setProperty((String)me1.getKey(),(String)me1.getValue()); // Overwrites the prev attr value
        }
        // Create the ext attrs array with the new attrs
        int i=0;
        com.interwoven.cssdk.client.axis.filesys.CSExtendedAttributeAdapter[] csExtAttrAdapterArray = new com.interwoven.cssdk.client.axis.filesys.CSExtendedAttributeAdapter[extAttrsMap.size()];
        for (Map.Entry<Object, Object> me2 : extAttrs.entrySet()) {
            com.interwoven.cssdk.client.axis.generated.CSExtendedAttribute csExtAttr = new com.interwoven.cssdk.client.axis.generated.CSExtendedAttribute();
            csExtAttr.setName((String)me2.getKey());
            csExtAttr.setValue((String)me2.getValue());

            CSExtendedAttributeAdapter extAttrAdapter = new CSExtendedAttributeAdapter(csExtAttr);
            csExtAttrAdapterArray[i] = extAttrAdapter;
        }

        // Set back the ext attrs
        _setFileExtendedAttributes(csFile,
        						   csExtAttrAdapterArray);
    }
    private static void _setFileExtendedAttributes(final CSSimpleFile theFile,
    											   final CSExtendedAttribute[] extendedAttributes) throws TeamSiteFileStoreException {
        try {
			if (CollectionUtils.hasData(extendedAttributes)) {
		        theFile.setExtendedAttributes(extendedAttributes);
		    }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("setFileExtendedAttributes",
            								   		   Path.from(theFile.getVPath()),
            								   		   csEx);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Deletes a file
     * <pre>
     * BEWARE:  When a file is deleted from a workArea and that file was previously submitted to the staging area,
     * 			a "hole" is left at the workarea with the same name as the original deleted file
     * 			The hole only disappears if it's submitted to the staging area
     * </pre>
     * @param csWorkArea 
     * @param waRelPath 
     * @param consolidate submit the hole to the staging area
     * @throws TeamSiteFileStoreException 
     */
    public static void delete(final CSWorkarea csWorkArea,
    						  final TeamSiteWorkAreaRelativePath waRelPath,
    						  final boolean consolidate) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - deleteFile {} at {}",
    			  waRelPath,csWorkArea.getVPath());
    	
    	// find the file
        CSFile theFile = TeamSiteFileStoreFindUtils.findFolderOrFile(csWorkArea,
        														     waRelPath);
        if (theFile == null) throw new TeamSiteFileStoreException("Could NOT find file " + waRelPath + " at work area " + csWorkArea.getVPath());
        
        // delete it
        try {
            if (theFile.getKind() == CSHole.KIND) {
                log.trace("\tel the file or folder {} at {} is already deleted (it's a HOLE), the only thing left to do is submmit the hole to the staging area",
                		  waRelPath,csWorkArea.getVPath());
            } else {
                theFile.delete();
            }
            // Submit the hole to the staging area
            CSHole theHole = TeamSiteFileStoreFindUtils.findHole(csWorkArea,
            													 waRelPath);
            if (theHole != null && consolidate) {
                // The file or folder was submitted to the staging area so in order to remove the hole, this hole MUST also be submitted
                log.trace("\tfile {} is already at the STAGING area, so the hole is left in the workarea; to remove this hole, submmit it",
                		  waRelPath);

                TeamSiteFileStoreUtils.submmitToStaging(csWorkArea,new TeamSiteWorkAreaRelativePath[] { waRelPath },
                                                        "R01M-File or Folder Deletion Submmit",(new Date()).toString());
            } else if (consolidate) {
                log.trace("\tthe file or folder {} is NOT at the STAGING area: there's NO hole at the workArea.",
                		  waRelPath);
            }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("deleteFile",
            								   		   Path.from(theFile.getVPath()),
            								   		   csEx);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE DIR
/////////////////////////////////////////////////////////////////////////////////////////
    public static void createFolder(final CSWorkarea csWorkArea,final TeamSiteWorkAreaRelativePath folderWARelPath,
                                    final boolean createParentFolders) throws TeamSiteFileStoreException {
    	log.debug("DATASTORE MANAGER (IW) - createFolder {} at {}",
    			  folderWARelPath,csWorkArea.getVPath());
    	try {
	    	CSDir csDir = _createFolder(csWorkArea, 
	    								folderWARelPath,
	    								createParentFolders);
    	} catch(CSException csEx) {
            throw TeamSiteFileStoreException.createFor("createFolder",
            								   		   Path.from(csWorkArea.getVPath(),folderWARelPath),
            								   		   csEx);
    	}
    }
    private static CSDir _createFolder(final CSWorkarea csWorkArea,
    								   final TeamSiteWorkAreaRelativePath folderWARelativePath,
    								   final boolean createParentFolders) throws CSException {
    	// check the folder existence
	    CSDir csFolder = TeamSiteFileStoreFindUtils.findFolder(csWorkArea,
	    												   	   folderWARelativePath);  
	    // if the folder does NOT exists, create it
	    if (csFolder == null) {
	    	if (folderWARelativePath.getPathElementCount() == 1) {
	    		csFolder = csWorkArea.createDirectory(new CSAreaRelativePath(folderWARelativePath.asRelativeString()));
	    	} else if (createParentFolders) {
		    	// Create the folder structure
	            for (int i=0; i < folderWARelativePath.getPathElementCount(); i++) {
	            	// create a wa rel path with the first i path elements
	            	Path currFolderWARelPath = Path.from(folderWARelativePath.getFirstNPathElements(i));
	            	// check if the folder exists
	                CSDir currCSFolder = TeamSiteFileStoreFindUtils.findFolder(csWorkArea,
	                													   	   TeamSiteWorkAreaRelativePath.create(currFolderWARelPath));
	                // ... if it does not, create it
	                if (currCSFolder == null) currCSFolder = csWorkArea.createDirectory(new CSAreaRelativePath(currFolderWARelPath.asRelativeString()));
	            }
	    	} else {
	    		throw new TeamSiteFileStoreException("Cannot create folder " + folderWARelativePath + " at workArea " + csWorkArea.getVPath() + ": createParentFolders=false!");
	    	}
            // check if the folder is now present
            csFolder = TeamSiteFileStoreFindUtils.findFolder(csWorkArea,
	    												 	 folderWARelativePath);   // folder containing the file
            if (csFolder == null) throw new TeamSiteFileStoreException("The folder " + folderWARelativePath + " does NOT exists at " + csWorkArea.getVPath());
	    }
	    return csFolder;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  MOVE / COPY / RENAME
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Moves a bunch of files from a given workArea to another workArea maintaining the original paths
     * @param srcCSWorkArea 
     * @param srcWARelPaths workarea-relative paths of the files to be moved
     * @param dstCSWorkArea 
     * @param dstWARelPath workare-relative path of the files once moved
     * @throws TeamSiteFileStoreException 
     */
    public static void moveFilesOrFolders(final CSWorkarea srcCSWorkArea,final TeamSiteWorkAreaRelativePath[] srcWARelPaths,
                                 		  final CSWorkarea dstCSWorkArea,final TeamSiteWorkAreaRelativePath[] dstWARelPaths) throws IOException {

    	if (CollectionUtils.isNullOrEmpty(srcWARelPaths)) return;
    	
		log.debug("DATASTORE MANAGER (IW) - Moving {} files from {} to {}",
				  srcWARelPaths.length,srcCSWorkArea.getVPath(),dstCSWorkArea.getVPath());
		for (int i=0;i<srcWARelPaths.length;i++) {
			log.debug("\t-path: {}",(srcWARelPaths[i] == null ? "[NULL]":srcWARelPaths[i].toString()));
		}
		if (srcWARelPaths.length != dstWARelPaths.length) throw new IllegalArgumentException("The source workarea relative paths collection has NOT the same size as the destination one!");
		
		try {
			// check source and target paths
			_checkWorkAreaRelativePathsBeforeCopyOrMove(srcCSWorkArea,
														srcWARelPaths);
			_checkWorkAreaRelativePathsBeforeCopyOrMove(dstCSWorkArea,
														dstWARelPaths);
			// beware that the source WorkArea can be the same as the target workArea > the CSSDK op is different
			// a) SAME WORKAREA
		    if (srcCSWorkArea.getVPath().equals(dstCSWorkArea.getVPath())) {
				for (int i=0; i< srcWARelPaths.length; i++) {
					_copyOrMoveWithinWorkArea(srcCSWorkArea,
											  srcWARelPaths[i],dstWARelPaths[i],
											  false);	// mpve
				}
		    }
		    // b) DIFFERENT WORKAREA
			else { 
				// transform to CSAreaRelativePath
				CSAreaRelativePath[] srcCSAreaRelPaths = FluentIterable.from(Lists.newArrayList(srcWARelPaths))
																       .transform(_createWorkAreaRelPathToCSAreaRelPathTransformFunction(srcCSWorkArea))
																       .toArray(CSAreaRelativePath.class);
			    // Move files
				CSUpdateResult csUpdateResult = dstCSWorkArea.update(srcCSAreaRelPaths,
																  	 srcCSWorkArea,
																  	 CSWorkarea.OVERWRITE_ALL);
	
				// debug
				if (csUpdateResult != null) {
					log.debug("Result of the movement of {} files from {} to {}",
							  srcWARelPaths.length,srcCSWorkArea.getVPath(),dstCSWorkArea.getVPath());
					_debugCSUpdateResult(csUpdateResult);
				}
	
				// Delete the source files
				CSPathStatus[] deletedCSFilesStatus = srcCSWorkArea.deleteFiles(srcCSAreaRelPaths);
				if (deletedCSFilesStatus != null) {
					log.debug("Result of the deletion of {} files from {}",
							  srcWARelPaths.length,dstCSWorkArea.getVPath());
	    			_debugCSFileStatus(deletedCSFilesStatus);
				}
			}
		} catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("moveFiles",
            								   		   Path.from(srcCSWorkArea.getVPath()),
            								   		   csEx);
		}
    }
    /**
     * Copies a bunch of files from a given workarea to another
     * The workArea relative path of the copied files can be distinct from the ones at the source workarea
     * @param srcCSWorkArea 
     * @param srcWARelPaths 
     * @param dstCSWorkArea 
     * @param dstWARelPaths 
     * @throws TeamSiteFileStoreException 
     */
    public static void copyFilesOrFolders(final CSWorkarea srcCSWorkArea,final TeamSiteWorkAreaRelativePath[] srcWARelPaths,
    							 		  final CSWorkarea dstCSWorkArea,final TeamSiteWorkAreaRelativePath[] dstWARelPaths) throws IOException {

    	if (CollectionUtils.isNullOrEmpty(srcWARelPaths)) return;
    	if (CollectionUtils.isNullOrEmpty(dstWARelPaths) || srcWARelPaths.length != dstWARelPaths.length) throw new IllegalArgumentException("The source files path collection has NOT the same size as the target files one");
    	
		log.debug("DATASTORE MANAGER (IW) - Copying {} files from {} to {}",
				  srcWARelPaths.length,srcCSWorkArea.getVPath(),dstCSWorkArea.getVPath());
		for (int i=0;i<srcWARelPaths.length;i++) {
			log.debug("\t- {} to {}",srcWARelPaths[i],dstWARelPaths[i]);
		}
		if (srcWARelPaths.length != dstWARelPaths.length) throw new IllegalArgumentException("The source workarea relative paths collection has NOT the same size as the destination one!");
				
		try {
			// check source and target paths
			_checkWorkAreaRelativePathsBeforeCopyOrMove(srcCSWorkArea,
														srcWARelPaths);
			_checkWorkAreaRelativePathsBeforeCopyOrMove(dstCSWorkArea,
														dstWARelPaths);
			// beware that the source WorkArea can be the same as the target workArea > the CSSDK op is diferent			
		    if (srcCSWorkArea.getVPath().equals(dstCSWorkArea.getVPath())) {
				// SAME WORKAREA
				for (int i=0; i< srcWARelPaths.length; i++) {
					_copyOrMoveWithinWorkArea(srcCSWorkArea,
											  srcWARelPaths[i],dstWARelPaths[i],
											  true);	// copy
				}
			} else {
				// ANOTHER WORKAREA
				// 1) Copy all files with the same relative path as in the source
				CSAreaRelativePath[] srcCSAreaRelativePaths = FluentIterable.from(Lists.newArrayList(srcWARelPaths))
																    .transform(_createWorkAreaRelPathToCSAreaRelPathTransformFunction(srcCSWorkArea))
																    .toArray(CSAreaRelativePath.class);
				dstCSWorkArea.update(srcCSAreaRelativePaths,
									 srcCSWorkArea,CSWorkarea.OVERWRITE_ALL);
				// 2) Move files at the target workArea
				//	  BEWARE!! the src and dst workare is the same
				TeamSiteFileStoreUtils.moveFilesOrFolders(dstCSWorkArea,srcWARelPaths,	
												 		  dstCSWorkArea,dstWARelPaths);	
			}
		} catch (CSException csEx) {
			throw TeamSiteFileStoreException.createFor("copyFiles",
											   		   Path.from(srcCSWorkArea.getVPath()),
											   		   csEx);
		}
    }
    private static void _copyOrMoveWithinWorkArea(final CSWorkarea srcCSWorkArea,
    											  final TeamSiteWorkAreaRelativePath srcWARelPath,final TeamSiteWorkAreaRelativePath dstWARelPath,
    											  final boolean copy) throws IOException,
    														   				 CSException {
		// Check that the target does NOT exists					
		CSFile dstCSFile = TeamSiteFileStoreFindUtils.findFolderOrFile(srcCSWorkArea,
																	   dstWARelPath);
		if (dstCSFile != null) throw new IOException(Strings.customized("Destination file / folder {} at {} already exists: file / folder {} at {} cannot be copied!",
															    		srcWARelPath,srcCSWorkArea.getVPath(),
															    		srcWARelPath,srcCSWorkArea.getVPath()));
    	
		// Source file
		CSFile srcCSFile = TeamSiteFileStoreFindUtils.findFolderOrFile(srcCSWorkArea,
															 	 	   srcWARelPath);
        if (srcCSFile == null || srcCSFile.getKind() == CSHole.KIND) {
            log.warn("File {} does NOT exists or is a hole!!!!",
            		 srcWARelPath);
            return;    // holes cannot be copied
        } 

		// Target file: check if the parent folder exists > if the parent folder does NOT exists, create it
    	CSDir parentCSFolder = _findOrCreateFileParentFolder(srcCSWorkArea,
    												 		 dstWARelPath); 
    	// Copy the file (overwrite)
        CSAreaRelativePath targetCSArea = _createWorkAreaRelPathToCSAreaRelPathTransformFunction(srcCSWorkArea)
        										.apply(dstWARelPath);
        if (srcCSFile.getKind() == CSDir.KIND) {
        	// create the folder copy
        	srcCSWorkArea.createDirectory(targetCSArea);
        	
        	// if it's a dir copy the contents
        	CSDir srcCSDir = (CSDir)srcCSFile;
        	CSNode[] csChildNodes = srcCSDir.getChildren();
        	if (CollectionUtils.hasData(csChildNodes)) {
        		for (CSNode csChildNode : csChildNodes) {
        			if (csChildNode.getKind() == CSHole.KIND) {
                        log.warn("\t\t-{} is a hole; it'll be ignored",csChildNode.getVPath());
                        continue;    // holes cannot be copied
        			} else if (csChildNode.getKind() == CSSimpleFile.KIND) {
        				log.trace("\t\t-{} {}",csChildNode.getKind(),csChildNode.getVPath());
        				if (copy) {
							((CSSimpleFile)csChildNode).copy(targetCSArea,
										     				 true);	// overwrite
        				} else {
							((CSSimpleFile)csChildNode).move(targetCSArea,
										     				 true);	// overwrite        					
        				}
        			} else if (csChildNode.getKind() == CSDir.KIND) {
        				log.trace("\t\t-{} {}",csChildNode.getKind(),csChildNode.getVPath());
        				if (copy) {
							((CSDir)csChildNode).copy(targetCSArea,
										     		  true);	// overwrite
        				} else {
							((CSDir)csChildNode).move(targetCSArea,
										     		  true);	// overwrite        					
        				}
        			}                   			
        		}
        	}
        } else {
        	// if it's a file, simply copy
			srcCSFile.copy(targetCSArea,
						   true);	// overwrite
        }
    }
    /**
     * Validates a collection of source workarea relative paths to avoid errors moving complete typos
     */
    private static void _checkWorkAreaRelativePathsBeforeCopyOrMove(final CSWorkarea srcCSWorkArea,
    																final TeamSiteWorkAreaRelativePath[] waRelPaths) {
    	for (TeamSiteWorkAreaRelativePath waRelPath : waRelPaths) {
	        Path fullPath = Path.from(srcCSWorkArea.getVPath())
	        					.joinedWith(waRelPath);
	        // count 
	        int numDelimiters = Splitter.on(CSAreaRelativePath.PATH_DELIMITER) 
	        							.splitToList(fullPath.asAbsoluteString())
	        							.size();
			// Como máximo se permite copiar contenidos. Esto sería con un path del tipo:
			// 		//ejlp013/euskadi/main/consumo/WORKAREA/wconsg2/ayuda_subvencion/1600_2013
			// Es decir, que como mínimo debe haber 8 niveles de profundidad (8 directorios)
			if (numDelimiters < 8) {
				throw new TeamSiteFileStoreException("Invalid workArea relative path to be a copied to another workArea: " + fullPath.asAbsoluteString());
			}
	
			// A este método le puede llegar la copia de una DCR del templatedata. En ese caso la limitación es más restrictiva.
			// Le llegará:
			//
			// 		//ejlp013/euskadi/main/consumo/WORKAREA/wconsg2/templatedata/editorial/informacion/data/en_r01dpd013585d97c3114460c1dc2f9e89cae4659b
			//
			// Es decir que como mínimo deben venirle 11 niveles.
			if (fullPath.containsPathElement("templatedata")
			 && numDelimiters < 11) {
				throw new TeamSiteFileStoreException("Invalid workArea relative path to be a copied to another workArea: " + fullPath.asAbsoluteString());
			}
    	}
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  RENAME
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Renames a file or folder
     * @param csWorkArea 
     * @param waRelPath 
     * @param newName 
     * @param overwrite 
     * @throws TeamSiteFileStoreException 
     */
    public static void rename(final CSWorkarea csWorkArea,
                              final TeamSiteWorkAreaRelativePath waRelPath,
                              final FileNameAndExtension newName,
                              final boolean overwrite) throws TeamSiteFileStoreException {
        log.debug("DATASTORE MANAGER (IW) - Renaming {} to {} (overwrite={})",
        		  Path.from(csWorkArea.getVPath(),waRelPath),Path.from(csWorkArea.getVPath(),newName),
        		  overwrite);
        try {
            CSFile theFile = TeamSiteFileStoreFindUtils.findFolderOrFile(csWorkArea,
            														 	 waRelPath);
            if (null == theFile) throw new TeamSiteFileStoreException("WorkArea " + csWorkArea.getVPath() + " DOES NOT contains a file with name " + waRelPath + ": cannot rename the file to " + newName);
            if (theFile.getKind() == CSHole.KIND) {
                return;    // holes cannot be renamed!
            }
            // rename
            theFile.rename(newName.asString(),
            			   overwrite);
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("rename",
            								   		   Path.from(csWorkArea.getVPath(),waRelPath),
            								   		   csEx);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  SUBMMIT
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Submits a file or folder to the staging area
     * @param csWorkArea 
     * @param waRelPaths 
     * @param comment 
     * @param infoComment 
     * @throws TeamSiteFileStoreException
     */
    public static void submmitToStaging(final CSWorkarea csWorkArea,
    									final TeamSiteWorkAreaRelativePath[] waRelPaths,
                                        final String comment,final String infoComment) throws TeamSiteFileStoreException {

    	if (CollectionUtils.isNullOrEmpty(waRelPaths)) return;     // Nothing to summit

    	log.info("DATASTORE MANAGER (IW) - Submmit {} files/folders from {} to staging area",
    			 waRelPaths.length,csWorkArea.getVPath());
    	
        // Create the path and comment list
        CSPathCommentPair[] csPathAndCommentList = FluentIterable.from(Lists.newArrayList(waRelPaths))
        												  // transform to CSAreaRelativePath
        												  .transform(_createWorkAreaRelPathToCSAreaRelPathTransformFunction(csWorkArea))
        												  // transform to CSPathCommentPair
        												  .transform(new Function<CSAreaRelativePath,CSPathCommentPair>() {
																			@Override
																			public CSPathCommentPair apply(final CSAreaRelativePath waRelPath) {
																				return new CSPathCommentPair(waRelPath,(new Date()).toString());
																			}
        												  			})
        												  .toArray(CSPathCommentPair.class);
        try {
            log.trace("\t...lock files before submmiting them");

            if (LOCK_ENABLED) csWorkArea.lockFiles(csPathAndCommentList);

            // Submit files ....
            log.trace("\t...submmit files");
            CSSubmitResult submmitResult = csWorkArea.submitDirect(comment,infoComment,
                                                                   csPathAndCommentList,
                                                                   CSWorkarea.OVERWRITE_CONFLICTS);
            // submmit result
            CSPathStatus[] summittedItemsStatus = submmitResult.getResultOfSubmit();
            if (null != summittedItemsStatus) {
            	log.trace("\t...submmit result:");
                for (int i=0; i < summittedItemsStatus.length; i++) {
                    log.trace("\t\t-file {} PrimaryCode={} / SecondaryCode={} / Message={}",
                    		  summittedItemsStatus[i].getAreaRelativePath(),
                    		  summittedItemsStatus[i].getPrimaryStatusCode(),summittedItemsStatus[i].getSecondaryStatusCode(),
                    		  summittedItemsStatus[i].getStatusMessage());
                }
            }
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("submmitToStaging",
            								   		   Path.from(csWorkArea.getVPath()),
            								   		   csEx);
        } finally {
            try {
                // Unlock the files
                log.trace("\t...unlock submmited files");
                if (LOCK_ENABLED) {
                    try {
                        csWorkArea.unlockFiles(FluentIterable.from(Lists.newArrayList(waRelPaths))
        												  	 .transform(_createWorkAreaRelPathToCSAreaRelPathTransformFunction(csWorkArea))
        												  	 .toArray(CSAreaRelativePath.class));
                    } catch (CSFileNotLockedException fnlEx) {/* ignorar */}
                }
            } catch (CSException csEx) {
                log.error("Error unlocking submmited files: {}",csEx.getMessage(),csEx);
            }
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE FUNCTIONS
/////////////////////////////////////////////////////////////////////////////////////////
    private static CSDir _findFileParentFolder(final CSWorkarea csWorkArea,
    										   final TeamSiteWorkAreaRelativePath fileWARelPath) {
    	TeamSiteWorkAreaRelativePath parentFolderWARelPath = TeamSiteWorkAreaRelativePath.create(Path.from(fileWARelPath.getPathElementsExceptLast()));
	    CSDir parentCSFolder = TeamSiteFileStoreFindUtils.findFolder(csWorkArea,
	    															 parentFolderWARelPath);   // folder containing the file
	    return parentCSFolder;
    }
    private static CSDir _findOrCreateFileParentFolder(final CSWorkarea csWorkArea,
    												   final TeamSiteWorkAreaRelativePath fileWARelPath) throws CSException {
    	CSDir parentCSFolder = _findFileParentFolder(csWorkArea,
    												 fileWARelPath);
    	// if the parent folder does NOT exists, create it
    	if (parentCSFolder == null) {
    		TeamSiteWorkAreaRelativePath parentFolderWARelPath = TeamSiteWorkAreaRelativePath.create(Path.from(fileWARelPath.getPathElementsExceptLast()));
    		parentCSFolder = _createFolder(csWorkArea,
    									   parentFolderWARelPath,
    									   true);	// create parent folders
    	} 
    	return parentCSFolder;
    }
    private static void _checkCSStorageNode(final CSNode node) throws TeamSiteFileStoreException {
    	try {
    		// just issue an operation on the onde
			node.getKind();	
        } catch (CSException csEx) {
            throw TeamSiteFileStoreException.createFor("_checkCSStorageNode",
            								   		   Path.from(node.getVPath()),
            								   		   csEx);
        }
    }
    private static void _debugCSUpdateResult(final CSUpdateResult csUpdateResult) {
		CSFileCmpResult[] csFileOpResults = csUpdateResult.getResultOfUpdate();
		if (csFileOpResults != null) {
			for (int i=0; i<csFileOpResults.length; i++) {
				CSFileCmpResult csFileOpResult = csFileOpResults[i];
                log.warn("\t-{} primary code={} / secondaryCode={} > {}",
                		 csFileOpResult.getAreaRelativePath(),csFileOpResult.getPrimaryStatusCode(),csFileOpResult.getSecondaryStatusCode(),csFileOpResult.getStatusMessage());
			}
		}
    }
    private static void _debugCSFileStatus(final CSPathStatus[] csStatus) {
    	if (csStatus != null) {
			for (int i=0;i<csStatus.length;i++) {
				CSPathStatus result = csStatus[i];
	            log.warn("\t-{} primary code={} / secondaryCode={} > {}",
	            		 result.getAreaRelativePath(),result.getPrimaryStatusCode(),result.getSecondaryStatusCode(),result.getStatusMessage());
			}
    	}
    }
}