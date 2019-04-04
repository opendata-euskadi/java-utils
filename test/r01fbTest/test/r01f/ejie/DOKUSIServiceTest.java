package r01f.ejie;

import java.io.IOException;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.XLNetsGuiceModule;
import r01f.ejie.xlnets.api.XLNetsAPI;
import r01f.ejie.xlnets.api.XLNetsAPIBuilder;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.io.util.StringPersistenceUtils;
import r01f.model.dokusi.DOKUSIOIDs.DOKUSIAuditID;
import r01f.model.dokusi.DOKUSIOIDs.DOKUSIDocumentID;
import r01f.model.dokusi.DOKUSIRetrievedDocument;
import r01f.services.dokusi.DOKUSIService;
import r01f.services.dokusi.DOKUSIServiceAPIData;
import r01f.services.dokusi.DOKUSIServiceGuiceModule;
import r01f.services.pif.PifService;
import r01f.services.pif.PifServiceAPIData;
import r01f.services.pif.PifServiceGuiceModule;
import r01f.types.Path;
import r01f.types.url.Url;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
public class DOKUSIServiceTest {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Path TEST_FILE_PATH = Path.from("d:/temp_dev/r01fb/r01fbTestFile.txt");
/////////////////////////////////////////////////////////////////////////////////////////
//  Check at: http://svc.integracion.jakina.ejiedes.net/y31dBoxWAR/appbox
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testDOKUSINOGuicd() throws IOException {
		// get an xlnets token
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
																 .notUsingCache()
																 .forComponent(AppComponent.forId("test"));
		XLNetsAPI xlNetsApi = XLNetsAPIBuilder.createAsDefinedAt(props,"test");
		// Create new DOKUSI service api data
		DOKUSIServiceAPIData dokusiApiData = new DOKUSIServiceAPIData(Url.from("http://svc.extra.integracion.jakina.ejiedes.net:80/ctxapp/t65bFsd"),
																	  DOKUSIAuditID.forId("X42T#X42T"),
																	  Path.from("/x42t/dokusi"));
		// Create a new PIF service api data
		PifServiceAPIData pifApiData = new PifServiceAPIData(props,
															"test");
		PifService pifService = new PifService(pifApiData,
											   xlNetsApi);
		
		// Using the DOKUSI service api data create the DOKUSIService object
		DOKUSIService dokusiService = new DOKUSIService(dokusiApiData,
														xlNetsApi,
														pifService);
		_testDOKUSI(dokusiService);
	}
	@Test
	public void testDOKUSIGuice() throws IOException {		
		// Get a DOKUSI service instance
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
																 .notUsingCache()
																 .forComponent(AppComponent.forId("test"));
		PifServiceAPIData pifApiData = new PifServiceAPIData(props,
															 "test");
		DOKUSIServiceAPIData dokusiServiceApiData = new DOKUSIServiceAPIData(props,
																			 "test");
		Injector injector = Guice.createInjector(new XLNetsGuiceModule(props,
																	   "test"),
												 new PifServiceGuiceModule(pifApiData),						// signature service uses pif
				 								 new DOKUSIServiceGuiceModule(dokusiServiceApiData));
		
		DOKUSIService dokusiService = injector.getInstance(DOKUSIService.class);
		_testDOKUSI(dokusiService);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static void _testDOKUSI(final DOKUSIService dokusiService) throws IOException {
		DOKUSIDocumentID docId = DOKUSIDocumentID.forId("09f424018074a36d"); 
		
		log.info("... downloading DOKUSI doc with id={}",
				 docId);
		DOKUSIRetrievedDocument doc = dokusiService.downloadDoc(docId);
		log.info("...downloaded!");
		String docCont = StringPersistenceUtils.load(doc.getStream());
		log.info("{}",doc);
	}
}
