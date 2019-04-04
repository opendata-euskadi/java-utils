package r01f.ejie;

import java.io.File;

import org.junit.Test;
import org.w3c.dom.Document;

import com.google.inject.Guice;
import com.google.inject.Injector;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.XLNetsGuiceModule;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.io.util.StringPersistenceUtils;
import r01f.services.pif.PifServiceAPIData;
import r01f.services.pif.PifServiceGuiceModule;
import r01f.services.shf.SignatureService;
import r01f.services.shf.SignatureServiceAPIData;
import r01f.services.shf.SignatureServiceGuiceModule;
import r01f.types.Path;
import r01f.xml.XMLUtils;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
public class SignatureServiceTest {

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	@SuppressWarnings("static-method")
	public void testSignatureService() {
		try {
			XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
																	 .notUsingCache()
																	 .forComponent(AppComponent.forId("test"));
			PifServiceAPIData pifApiData = new PifServiceAPIData(props,
																 "test");
			SignatureServiceAPIData signServiceApiData = new SignatureServiceAPIData(props,
																					 "test");
			Injector injector = Guice.createInjector(new XLNetsGuiceModule(props,
																		   "test"),
													 new PifServiceGuiceModule(pifApiData),						// signature service uses pif
					 								 new SignatureServiceGuiceModule(signServiceApiData));
			
			SignatureService service = injector.getInstance(SignatureService.class);

			AppCode requestorAppCode = AppCode.forId("aa88b");
			
			// Sign text
			String textToSign = "Hola mundo!";
			log.info("[Signature]: {}",textToSign);
			String signature = service.requiredBy(requestorAppCode)
									  .createXAdESSignatureOf(textToSign)
									  .asString();
			log.info("[Signature]: {}",signature);
			
			// verify
			log.info("[Verify signature]");
			service.requiredBy(requestorAppCode)
				   .verifyXAdESSignature(textToSign,signature);
			
			// Sign file
			Path filePath = Path.from("d:/temp_dev/r01fb/r01fbTestFile.txt");
			log.info("[File Signature]: {}",filePath);
			File file = new File(filePath.asAbsoluteString());
			StringPersistenceUtils.save(textToSign,	
										file);
			Document signatureXml = service.requiredBy(requestorAppCode)
										   .createXAdESSignatureOf(file).asXMLDocument();			
			log.info(XMLUtils.asString(signatureXml));
			
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}

}
