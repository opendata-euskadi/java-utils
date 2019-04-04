package r01f.services.dokusi;

import java.io.IOException;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.api.XLNetsAPI;
import r01f.model.dokusi.DOKUSIOIDs.DOKUSIDocumentID;
import r01f.model.dokusi.DOKUSIRetrievedDocument;
import r01f.patterns.Memoized;
import r01f.services.pif.PifService;
import r01f.services.pif.PifServiceAPIData;
import r01f.xml.XMLUtils;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Encapsulates pif services
 * IMPORTANT!!!!!
 * 		Graphical user interface URL (ui console): http://svc.integracion.jakina.ejiedes.net/y31dBoxWAR/appbox
 * 
 * 
 * To build a {@link DOKUSIService} a {@link DOKUSIServiceAPIData}, a {@link PifServiceAPIData} and a {@link XLNetsAuthTokenProvider} are needed 
 * All {@link DOKUSIServiceAPIData},  {@link PifServiceAPIData} and {@link XLNetsAuthTokenProvider} can be built from an {@link XMLPropertiesForAppComponent} 
 * <pre class='brush:java'>
 *		// get an xlnets token
 *		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
 *																 .notUsingCache()
 *																 .forComponent(AppComponent.forId("test"));
 *		XLNetsAuthTokenProvider xlnetsAuthTokenProvider = new XLNetsAuthTokenProvider(props,
 *																					  "test");
 *		// Create new DOKUSI service api data
 *		DOKUSIServiceAPIData dokusiApiData = new DOKUSIServiceAPIData(Url.from("http://svc.extra.integracion.jakina.ejiedes.net:80/ctxapp/t65bFsd"),
 *																	  DOKUSIAuditID.forId("X42T#X42T"),
 *																	  Path.from("/x42t/dokusi"));
 *		// Create a new PIF service api data
 *		PifServiceAPIData pifApiData = new PifServiceAPIData(props,
 *															"test");
 *		PifService pifService = new PifService(pifApiData,
 *											   xlnetsAuthTokenProvider);
 *		
 *		// Using the DOKUSI service api data create the DOKUSIService object
 *		DOKUSIService dokusiService = new DOKUSIService(dokusiApiData,
 *														xlnetsAuthTokenProvider,
 *														pifService);
 * </pre>
 * 
 * Using guice:
 * <pre class='brush:java'>
 *		XMLPropertiesForAppComponent props = XMLPropertiesBuilder.createForApp(AppCode.forId("r01fb"))
 *																 .notUsingCache()
 *																 .forComponent(AppComponent.forId("test"));
 *		PifServiceAPIData pifApiData = new PifServiceAPIData(props,
 *															 "test");
 *		DOKUSIServiceAPIData dokusiServiceApiData = new DOKUSIServiceAPIData(props,
 *																			 "test");
 *		Injector injector = Guice.createInjector(new XLNetsGuiceModule(props,
 *																	   "test"),
 *												 new PifServiceGuiceModule(pifApiData),						// signature service uses pif
 *				 								 new DOKUSIServiceGuiceModule(dokusiServiceApiData));
 *		
 *		DOKUSIService dokusiService = injector.getInstance(DOKUSIService.class);
 * </pre>
 * 
 * For all this to work a properties file with the following config MUST be provided:
 * <pre class='xml'>
 * 		<dokusiService>
 *			<webServiceUrl>http://svc.extra.integracion.jakina.ejiedes.net:80/ctxapp/t65bFsd</webServiceUrl>
 * 			<auditId>X42T#X42T</auditId>
 *			<requestedPifPath>/x42t/dokusi/</requestedPifPath>	<!-- the path where the DOKUSI web service is asked to put the file -->
 * 		</dokusiService>
 * </pre>
 *
 */
@Singleton
@Slf4j
public class DOKUSIService {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	private static String DOKUSI_SERVICE_METHOD = "retrieveDocument";	
	private static String DOKUSI_TARGET_NAMESPACE = "http://www.ejie.es/webServiceClase/t65bFSDWar"; // "http://www.openuri.org/";
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final DOKUSIServiceAPIData _apiData;
	
	private final XLNetsAPI _xlNetsApi;	
	private final Memoized<Document> _xlnetsAuthToken = new Memoized<Document>() {
																		@Override
																		protected Document supply() {
																			return _xlNetsApi.getXLNetsSessionTokenDoc();
																		}
																};
	private final PifService _pifService;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public DOKUSIService(final DOKUSIServiceAPIData apiData,
						 final XLNetsAPI xlNetsApi,
						 final PifService pifService) {
		_apiData = apiData;
		_xlNetsApi = xlNetsApi;
		_pifService = pifService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DOKUSI INVOCATION
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Retrieves a DOKUSI document using the retrieveDocument web service
	 * The document is left at a PIF path by the DOKUSI web service and this method picks it from that PIF location
	 * @param documentId DOKUSI document to be retrieved
	 * @return the retrieved document
	 * @throws SOAPException 
	 */
	public DOKUSIRetrievedDocument downloadDoc(final DOKUSIDocumentID documentId) throws IOException {
    	log.info("Download DOKUSI document with id={}",
    			 documentId);
    	try {
	    	// [1] - Create the SOAP message
	    	SOAPMessage message = MessageFactory.newInstance()
	    									   .createMessage();
	    	SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
	    	SOAPBody body = envelope.getBody();
	    	Name name = envelope.createName(DOKUSI_SERVICE_METHOD,		// retrieve document
	    									"",
	    									DOKUSI_TARGET_NAMESPACE);	// target namespace
	    	SOAPBodyElement element = body.addBodyElement(name);
			String[] params = {"integrationToken",
							   "auditUser",
							   "documentID",
							   "keysList",
							   "content"};
			String [] values = {XMLUtils.asStringLinearized(_xlnetsAuthToken.get()),
								_apiData.getAuditId().asString(),
							    "<documentID><id>" + documentId + "</id><version></version></documentID>",
							    null,																									// keylist
							    "<content><pifId>" + _apiData.getRequestedPifPath().asAbsoluteString() + "</pifId><format></format></content>"};    		// a pif location where the document will be left
	    	for (int i=0;i<params.length && i  <values.length;i++) {
	    		SOAPElement param = element.addChildElement(envelope.createName(params[i]));
	    		if (values[i] != null) param.addTextNode(values[i]);
	    	}
	
	    	// [2] - Invoke the service
	    	log.info("\t... invoking DOKUSI web service at {} with auditId={}",
	    			  _apiData.getWebServiceUrl(),_apiData.getAuditId());
	    	SOAPConnection con = SOAPConnectionFactory.newInstance()
	    											  .createConnection();
	    	SOAPMessage response = con.call(message,
	    									_apiData.getWebServiceUrl().asString());	
	    	log.info("\t... got response from DOKUSI web service");
			SOAPBodyElement soapBodyElement = _getSOAPBodyElement(response);
			
			// [3] - Handle the web service response
			if (soapBodyElement instanceof SOAPFault) {
				// 3.1) the web service invocation failed
				SOAPFault fault = (SOAPFault)soapBodyElement;
				String faultAsString = _soapFaultAsString(fault);
				log.error("Error while downloading DOKUSI doc id={} from {} using auditId={}\n{}",
						  documentId,
						  _apiData.getWebServiceUrl(),_apiData.getAuditId(),
						  faultAsString);
				throw new IOException("Error while downloading DOKUSI doc id=" + documentId + " from " + _apiData.getWebServiceUrl() + ": " + fault.getFaultString());
			}
			else { 
				// 3.2) the web service invocation succeeded and left the document at a PIF location	
				DOKUSIRetrievedDocument retrievedDoc = DOKUSIRetrievedDocumentBuilder.from(soapBodyElement)
																					 .loadUsing(_pifService);
				log.info("\t...retrieved DOKUSI doc id={} ({} bytes)",
						 documentId,retrievedDoc.getSize());
				return retrievedDoc;
			}
    	} catch(SOAPException soapEx) {
    		log.error("Error while invoking DOKUSI web service at {} to download doc id={}: {}",
    				  _apiData.getWebServiceUrl(),
    				  documentId,
    				  soapEx.getMessage(),
    				  soapEx);
    		throw new IOException("Error while invoking DOKUSI web service at " + _apiData.getWebServiceUrl() + " to download doc id=" + documentId + ": " + soapEx.getMessage());
    	}
	}
	/**
	 * Extract the SOAPBOdyElement from a SOAPMessage 
	 * (a SOAPMessage can contain a SOAPBodyElement or a SOAPFault)
	 * @param soapMessage
	 * @return 
	 * @throws SOAPException  
	 */
	private static SOAPBodyElement _getSOAPBodyElement(final SOAPMessage soapMessage) throws SOAPException {
		SOAPBodyElement bodyEl = null;
		Iterator<?> iterator = soapMessage.getSOAPPart().getEnvelope()
														.getBody()
														.getChildElements();
		while (iterator.hasNext()) {
			Object oUndefined = iterator.next();
			if (oUndefined instanceof SOAPBodyElement) {
				bodyEl = (SOAPBodyElement)oUndefined;
				break;
			}
		}
		return bodyEl;
	}	
	/**
	 * Formats a SOAPFault 
	 * @param fault 
	 */
	private static String _soapFaultAsString(final SOAPFault fault) {  
		StringBuilder errStr = new StringBuilder(44);        
		errStr.append("SOAPFault!\n");
		errStr.append("\t faultActor: " + fault.getFaultActor() + "\r\n");        
		errStr.append("\t  faultCode: " + fault.getFaultCode() + "\r\n");
		errStr.append("\tfaultString: " + fault.getFaultString() + "\r\n");
		errStr.append("\tfaultDetail: \r\n");
		Detail detail = fault.getDetail();             
		Iterator<?> it = detail.getDetailEntries();        
		while (it.hasNext()) {            
			DetailEntry detailEntry = (DetailEntry)it.next();            
			errStr.append("\t\t-" + detailEntry.getValue() + "\r\n");        
		}
		return errStr.toString();
	}
}
