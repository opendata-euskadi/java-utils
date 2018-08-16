package r01f.ejie;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.model.latinia.LatiniaRequest;
import r01f.model.latinia.LatiniaRequestMessage;
import r01f.model.latinia.LatiniaResponse;
import r01f.model.latinia.LatiniaResponseMessage;
import r01f.model.latinia.LatiniaResponsePhone;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.services.latinia.LatiniaService;
import r01f.services.latinia.LatiniaServiceAPIData;
import r01f.services.latinia.LatiniaServiceGuiceModule;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
/**
 * ERPI Consoles can help you to check process success:
 * DESARROLLO: svc.integracion.jakina.ejiedes.net/w43saConsolaWAR/
 * PRUEBAS: svc.integracion.jakina.ejiepru.net/w43saConsolaWAR/
 * PRODUCCION: svc.integracion.jakina.ejgvdns/w43saConsolaWAR/
 */
public class LatiniaTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	LATINIA SERVICE
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testLatiniaServiceNOGuice() {
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
																			.notUsingCache()
																			.forComponent(AppComponent.forId("test"));
		LatiniaServiceAPIData latiniaApiData = LatiniaServiceAPIData.createFrom(props,
																				"test");
		
		LatiniaService latiniaService = new LatiniaService(latiniaApiData);
		
		LatiniaRequestMessage msg = _createRequestMessage("TEST");
		System.out.println("=====> " + latiniaService.getLatiniaRequestMessageAsXml(msg));
		latiniaService.sendNotification(msg);
	}
	@Test
	public void testLatiniaServiceUsingGuice() {
		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
																			.notUsingCache()
																			.forComponent(AppComponent.forId("test"));
		LatiniaServiceAPIData latiniaApiData = LatiniaServiceAPIData.createFrom(props,
																				"test");
		
		Injector injector = Guice.createInjector(new LatiniaServiceGuiceModule(latiniaApiData));

		LatiniaService latiniaService = injector.getInstance(LatiniaService.class);
		
		LatiniaRequestMessage msg = _createRequestMessage("TEST");
		System.out.println("=====> " + latiniaService.getLatiniaRequestMessageAsXml(msg));
		latiniaService.sendNotification(msg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LATINIA OBJECST MARSHALLING
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testLatiniaRequestMarshalling() {
		Marshaller marshaller = MarshallerBuilder.build();
		
		LatiniaRequest req = _createLatiniaRequest();
		String reqXml = marshaller.forWriting().toXml(req);
		System.out.println("Latinia serialized request as XML:\n " + reqXml);
		
		LatiniaRequest readedReq = marshaller.forReading().fromXml(reqXml,
																   LatiniaRequest.class);
		System.out.println("Latinia deserialized request from XML:\n " + reqXml);		
	}
	@Test
	public void testLatiniaResponseMarshalling() {
		Marshaller marshaller = MarshallerBuilder.build();
		
		LatiniaResponse resp = _createLatiniaResponse();
		String reqXml = marshaller.forWriting().toXml(resp);
		System.out.println("Latinia serialized response as XML:\n " + reqXml);
		
		LatiniaResponse readedResp = marshaller.forReading().fromXml(reqXml,
																     LatiniaResponse.class);
		System.out.println("Latinia deserialized response from XML:\n " + reqXml);		
	}
	private static LatiniaRequest _createLatiniaRequest() {
		LatiniaRequest req = new LatiniaRequest();
		req.addMessage(_createRequestMessage("1"));
		req.addMessage(_createRequestMessage("2"));
		return req;
	}
	private static LatiniaRequestMessage _createRequestMessage(final String id) {
		LatiniaRequestMessage latiniaMsg = new LatiniaRequestMessage();
		latiniaMsg.setAcknowledge("S");
		latiniaMsg.setMessageContent("TEST MESSAGE " + id);
		latiniaMsg.setReceiverNumbers("688671967");
//		latiniaMsg.setExpireTime("5min");
		return latiniaMsg;
	}
	private static LatiniaResponse _createLatiniaResponse() {
		LatiniaResponse resp = new LatiniaResponse();
		resp.addMessage(_createResonseMessage("TEST"));
		return resp;
	}
	private static LatiniaResponseMessage _createResonseMessage(final String id) {
		LatiniaResponseMessage latiniaMsg = new LatiniaResponseMessage();
		latiniaMsg.setReceiverNumber("688671967");
		latiniaMsg.addResponsePhone(_createResonsePhone("688671967"))
				  .addResponsePhone(_createResonsePhone("688671991"));
		return latiniaMsg;
	}
	private static LatiniaResponsePhone _createResonsePhone(final String phone) {
		LatiniaResponsePhone responsePhone = new LatiniaResponsePhone();
		responsePhone.setReceiverNumber(phone);
		responsePhone.setMessageId("msg-" + phone);
		responsePhone.setErrorCode("-No error-");
		responsePhone.setErrorMessage("-No error message-");
		responsePhone.setResult("OK");
		return responsePhone;
	}
}
