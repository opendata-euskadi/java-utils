package r01f.ejie;

import java.io.File;

import org.junit.Test;
import org.w3c.dom.Document;

import com.google.inject.Guice;
import com.google.inject.Injector;

import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.io.util.StringPersistenceUtils;
import r01f.services.shf.SignatureService;
import r01f.services.shf.SignatureServiceGuiceModule;
import r01f.types.Path;
import r01f.xml.XMLUtils;
import r01f.xmlproperties.XMLPropertiesGuiceModule;

@Slf4j
public class SignatureServiceTest {

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	@SuppressWarnings("static-method")
	public void testSignatureService() {
		try {
			AppCode requestorAppCode = AppCode.forId("aa88b");
			
			Injector injector = Guice.createInjector(new XMLPropertiesGuiceModule(),
					 								 new SignatureServiceGuiceModule(AppCode.forId("r01fb"),
					 										 						 AppComponent.forId("test"),
															 				   		 "test"));
			
			SignatureService service = injector.getInstance(SignatureService.class);
			
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
