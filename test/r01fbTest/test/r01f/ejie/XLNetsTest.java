package r01f.ejie;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;
import n38c.exe.N38API;
import r01f.ejie.xlnets.api.XLNetsAPI;
import r01f.ejie.xlnets.api.XLNetsAPIBuilder;
import r01f.ejie.xlnets.config.XLNetsAppCfg;
import r01f.ejie.xlnets.config.XLNetsTargetCfg.ResourceItemType;
import r01f.ejie.xlnets.context.XLNetsAuthCtx;
import r01f.ejie.xlnets.context.XLNetsResourceAuthorization;
import r01f.ejie.xlnets.context.XLNetsResourceCtx;
import r01f.ejie.xlnets.model.XLNetsFuncion;
import r01f.ejie.xlnets.model.XLNetsSession;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.httpclient.HttpClient;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;
import r01f.xml.XMLUtils;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
/**
 * ERPI Consoles can help you to check process success:
 * DESARROLLO: svc.integracion.jakina.ejiedes.net/w43saConsolaWAR/
 * PRUEBAS: svc.integracion.jakina.ejiepru.net/w43saConsolaWAR/
 * PRODUCCION: svc.integracion.jakina.ejgvdns/w43saConsolaWAR/
 */
@Slf4j
public class XLNetsTest {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Test @SuppressWarnings("static-method")
	public void testConfig() {
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForAppComponent(AppCode.forId("r01fb"),
																			     	    AppComponent.forId("xlnets.fileUserToken"))
														  		 .notUsingCache();
		XLNetsAppCfg cfg = new XLNetsAppCfg(props);
		log.warn(cfg.toString());
	}
	@Test @SuppressWarnings("static-method")
	public void testXLNetsAppSession() {
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForAppComponent(AppCode.forId("r01fb"),
																			     		AppComponent.forId("test.xlnets.app"))
														  		 .notUsingCache();
		XLNetsAPI xlNetsApi = XLNetsAPIBuilder.createAsDefinedAt(props,"");
		Document xlnetsSessionTokenDoc = xlNetsApi.getXLNetsSessionTokenDoc();
		Assert.assertNotNull(xlnetsSessionTokenDoc);
		
		log.warn("XLNets session:\n{}",XMLUtils.asString(xlnetsSessionTokenDoc));
		
		XLNetsSession xlnetsSessionToken = new XLNetsSession(xlnetsSessionTokenDoc);
	}
	@Test @SuppressWarnings("static-method")
	public void testXLNetsSessionHttpProvided() {
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForAppComponent(AppCode.forId("r01fb"),
																			     		AppComponent.forId("test.xlnets.httpprovided"))
														  		 .notUsingCache();
		XLNetsAPI xlNetsApi = XLNetsAPIBuilder.createAsDefinedAt(props,"");
		Document xlnetsSessionTokenDoc = xlNetsApi.getXLNetsSessionTokenDoc();
		Assert.assertNotNull(xlnetsSessionTokenDoc);
		
		log.warn("XLNets session:\n{}",XMLUtils.asString(xlnetsSessionTokenDoc));
		
		XLNetsSession xlnetsSessionToken = new XLNetsSession(xlnetsSessionTokenDoc);
	}
	@Test @SuppressWarnings("static-method")
	public void testXLNetsMockFileSession() {
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForAppComponent(AppCode.forId("r01fb"),
																			     		AppComponent.forId("test.xlnets.mock"))
														  		 .notUsingCache();
		XLNetsAPI xlNetsApi = XLNetsAPIBuilder.createAsDefinedAt(props,"");
		Document xlnetsSessionTokenDoc = xlNetsApi.getXLNetsSessionTokenDoc();
		Assert.assertNotNull(xlnetsSessionTokenDoc);
		
		log.warn("XLNets session:\n{}",XMLUtils.asString(xlnetsSessionTokenDoc));
		
		XLNetsSession xlnetsSessionToken = new XLNetsSession(xlnetsSessionTokenDoc);
		
    	// add the user name: issues another n38 api call to get the user info
    	Document xlnetsUserInfoDoc = xlNetsApi.getXLNetsUserDoc(xlnetsSessionTokenDoc);
    	Assert.assertNotNull(xlnetsUserInfoDoc);
    	xlnetsSessionToken.setUserInfo(xlnetsUserInfoDoc.getDocumentElement());

    	Assert.assertNotNull(xlnetsSessionToken.getUser());
    	Assert.assertNotNull(xlnetsSessionToken.getUser().getOid());
    	
    	Document xlnetsAuthDoc = xlNetsApi.getAuthorizationDoc("R01F-FN-0001",ResourceItemType.FUNCTION);
    	Assert.assertNotNull(xlnetsAuthDoc);
    	XLNetsFuncion func = new XLNetsFuncion(xlnetsAuthDoc.getDocumentElement());
    	Assert.assertEquals(func.getItemSeguridad().getCommonName(),"R01F-FN-0001");
    	
    	
    	// Build the auth ctx
        XLNetsAuthCtx xlnetsAuthCtx = new XLNetsAuthCtx(xlnetsSessionToken);		// targets.... de momento nada... se va rellenando conforme se llama a authorize
		XLNetsResourceCtx resourceCtx = new XLNetsResourceCtx(func);

		Map<String,XLNetsResourceAuthorization> resAuth = resourceCtx.getAuthorizations();
		if (CollectionUtils.hasData(resAuth)) {
			for (Map.Entry<String,XLNetsResourceAuthorization> me : resAuth.entrySet()) {
				log.warn("===>{} > {}",me.getKey(),me.getValue().getProfileOid());
			}
		}
	}
	@Test @SuppressWarnings("static-method")
	public void testSessionTokenProvider() {
		try {
			String xlnetsSessionToken = HttpClient.forUrl("http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app=X42T")
												  .GET()
												  .loadAsString()
												  		.directNoAuthConnected();
			log.info("XLNetsSessionToken: {}",
					 xlnetsSessionToken);
			
			Document xlnetsSessionTokenDoc = XMLUtils.parse(new ByteArrayInputStream(xlnetsSessionToken.getBytes()));
			
			String xlnetsSessionTokenLinearized = XMLUtils.asStringLinearized(xlnetsSessionTokenDoc);
			
			log.info("XLNetsSessionTokenLinearized: {}",
					 xlnetsSessionTokenLinearized);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
