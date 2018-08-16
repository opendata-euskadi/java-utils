package r01f.ejie;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.login.XLNetsAuthTokenProvider;
import r01f.ejie.xlnets.login.XLNetsGuiceModule;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.io.util.StringPersistenceUtils;
import r01f.model.pif.PifFile;
import r01f.model.pif.PifFileInfo;
import r01f.services.pif.PifService;
import r01f.services.pif.PifServiceAPIData;
import r01f.services.pif.PifServiceGuiceModule;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
public class PifServiceTest {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Path TEST_FILE_PATH = Path.from("d:/temp_dev/r01fb/r01fbTestFile.txt");
/////////////////////////////////////////////////////////////////////////////////////////
//  Check at: http://svc.integracion.jakina.ejiedes.net/y31dBoxWAR/appbox
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testPifNOGuicd() throws IOException {
		// Crate a new pif service api data
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
																 .notUsingCache()
																 .forComponent(AppComponent.forId("test"));
		XLNetsAuthTokenProvider xlnetsAuthTokenProvider = new XLNetsAuthTokenProvider(props,
																					  "test");
		PifServiceAPIData pifApiData = new PifServiceAPIData(props,
															 "test");
		
		// Using the pif service api data create the PifService object
		PifService pifService = new PifService(pifApiData,
											   xlnetsAuthTokenProvider);
		_testPif(pifService);
	}
	@Test
	public void testPifGuice() throws IOException {		
		// Get a PIF service instance
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
																 .notUsingCache()
																 .forComponent(AppComponent.forId("test"));
		PifServiceAPIData pifServiceApiData = new PifServiceAPIData(props,
																    "test");
		Injector injector = Guice.createInjector(new XLNetsGuiceModule(props,
																	   "test"),
												 new PifServiceGuiceModule(pifServiceApiData));
		
		PifService pifService = injector.getInstance(PifService.class);
		_testPif(pifService);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static void _testPif(final PifService service) throws IOException {
		// Create a test file
		_createTestFile();
		FileInputStream input = new FileInputStream(new File(TEST_FILE_PATH.asAbsoluteString()));
		log.info("Upload file: {} with content << {} >>",
				 TEST_FILE_PATH,
				 Strings.removeNewlinesOrCarriageRetuns(StringPersistenceUtils.load(new File(TEST_FILE_PATH.asAbsoluteString()))));
		
		// Do upload
		Path pifDstPath = Path.from("/x42t/r01fb/r01fbTestFile.txt");
		PifFileInfo uploadedFileInfo = service.uploadFile(input,
											  			  pifDstPath,
											  			  true,						// preserve file name 
											  			  1L,TimeUnit.MINUTES);		// do not remove the file from pif for 1h
		log.info(">>UPLOADED!");
		
		// Download
		PifFile downloadedFile = service.downloadFile(uploadedFileInfo.getFilePath());
		
		log.info("Downloaded PIF file from {}: << {} >>",
				 pifDstPath,
				 Strings.removeNewlinesOrCarriageRetuns(downloadedFile.asString()));
	}
	private static String _createTestFile() throws IOException {
		String testFileContent = "Hello PIF World!";
		
		StringPersistenceUtils.save(testFileContent,
									TEST_FILE_PATH);
		
		return testFileContent;
	}

}
