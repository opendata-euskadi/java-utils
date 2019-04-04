package r01f.filestore.api;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import lombok.extern.slf4j.Slf4j;
import r01f.file.FileProperties;
import r01f.file.util.Files;
import r01f.file.util.FolderContentReporter;
import r01f.file.util.FolderContentReporter.FolderContentReportDiff;
import r01f.file.util.FolderContentReporter.FolderContentReportItem;
import r01f.filestore.api.local.LocalFileStoreAPI;
import r01f.filestore.api.local.LocalFileStoreFilerAPI;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.OIDs;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.types.Path;

@Slf4j
public class FolderReporterTest {	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testFolderReport() throws IOException {
		FileStoreAPI fileApi = new LocalFileStoreAPI();
		FileStoreFilerAPI filerApi = new LocalFileStoreFilerAPI();
		
		Marshaller marshaller = MarshallerBuilder.findTypesToMarshallAt(AppCode.forId("r01f"))
												 .build();
		
		// create some files
		final Path rootPath = Path.from("d:/temp_dev/r01fb").joinedWith("test_file_report");
		try {
			int numItems = _setUp(fileApi,filerApi,
								  rootPath);

			// create the report
			Set<FolderContentReportItem> items1 = _createReport(fileApi,filerApi,
																rootPath);
			Assert.assertEquals(items1.size(),numItems);
			System.out.println("===>" + items1.size());
			System.out.println("===>Report XML:\n" + marshaller.forWriting()
															   .toXml(items1));	
			
			// delete some items
			FileProperties[] files = filerApi.listFolderContents(rootPath,
																 new FileFilter() {
																		@Override
																		public boolean accept(final Path path) {
																			return true;
																		}
																		@Override
																		public boolean accept(final FileProperties props) {
																			return props.isFile();
																		}
																 },
																 false);
			for (FileProperties file : files) fileApi.deleteFile(file.getPath());
			// ... and add another
			for (int i=0; i < 5; i++) _createJunkFile(fileApi,
													  rootPath);
			
			// create the report again
			Set<FolderContentReportItem> items2 = _createReport(fileApi,filerApi,
																rootPath);
			System.out.println("===>Report XML:\n" + marshaller.forWriting()
															   .toXml(items2));
			
			// Diff test
			FolderContentReportDiff diff = FolderContentReporter.diff(items1,items2);
			
			Assert.assertEquals(diff.getDeleted().size(),files.length);
			Assert.assertEquals(diff.getNewOrUpdated().size(),5);
			Assert.assertEquals(diff.getUntouched().size(),items1.size() - diff.getDeleted().size());
			System.out.println("===>" + diff.debugInfo());
			System.out.println("===>Report Diff XML:\n" + marshaller.forWriting()
															   		.toXml(diff));
		} finally {
			// tear down
			_tearDown(filerApi,
					  rootPath);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static Set<FolderContentReportItem> _createReport(final FileStoreAPI fileApi,final FileStoreFilerAPI filerApi,
															  final Path rootPath) throws IOException {
		// reporter
		FolderContentReporter reporter = new FolderContentReporter(filerApi);
		
		// create the report
		log.info("Start creating folder report from {}",
				 rootPath);
		Stopwatch sw = Stopwatch.createStarted();
		Set<FolderContentReportItem> outItems = reporter.reportFrom(rootPath);
		log.info("Report created: {} items ({} seconds)",
				 outItems.size(),sw.elapsed(TimeUnit.SECONDS));
		sw.stop();
		
		// debug
		for (FolderContentReportItem item : outItems) {
			log.info("====>{}",item);
		}
		
		return outItems;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _tearDown(final FileStoreFilerAPI filerApi,
								  final Path rootPath) throws IOException {
		filerApi.deleteFolder(rootPath);
	}
	private static int _setUp(final FileStoreAPI fileApi,final FileStoreFilerAPI filerApi,
							  final Path rootPath) throws IOException {
		// create a test folder
		int maxDepth = 3;
		_recursiveCreateFileStruct(fileApi,filerApi,
								   rootPath,
								   1,maxDepth);
		return 10 * 5 * (int)ArithmeticUtils.factorial(maxDepth) + 10;	// 10 folders in each folder * 5 folders / iteration * fact(maxDepth) + 10 folders in root 
	}
	private static void _recursiveCreateFileStruct(final FileStoreAPI fileApi,final FileStoreFilerAPI filerApi,
												   final Path folder,
												   final int depth,final int maxDepth) throws IOException {
		filerApi.createFolder(folder);
		// ... some files
		for (int i=0; i<10; i++) _createJunkFile(fileApi,
												 folder);
		if (depth == maxDepth) return;
		
		// ... some folders recursively
		for (int i=0; i<5; i++) {
			Path innerFolderPath = folder.joinedWith(OIDs.supplyOid());
			_recursiveCreateFileStruct(fileApi,filerApi,
									   innerFolderPath,
									   depth+1,maxDepth);
		}
	}
	private static void _createJunkFile(final FileStoreAPI fileApi,
										final Path containerFolderPath) throws IOException {
		Path filePath = containerFolderPath.joinedWith(OIDs.supplyOid());
		Files.wrap(fileApi)
			 .forOverwriting(filePath)
			 .write("Just junk data written at " + System.nanoTime());
	}
}
