package r01f.ejie.nora;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.types.url.Url;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
class NORAServiceMethodInvoker {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Url _noraWSEndpointUrl;
/////////////////////////////////////////////////////////////////////////////////////////
//  INVOKER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sends a request to the NORA web service.
	 * @param methodName the NORA web service method name
	 * @return the <code>SOAPMessage</code> response
	 * @throws SOAPException if an exception occurs
	 */
	public final SOAPMessage invokeNORAMethod(final String methodName) throws SOAPException {
		return this.invokeNORAMethod(methodName,null,
									 true);	// invoke with parents
	}
	/**
	 * Sends a request to the NORA web service.
	 * @param methodName the NORA web service method name
	 * @param params the parameters
	 * @return the <code>SOAPMessage</code> response
	 * @throws SOAPException if an exception occurs
	 */
	public final SOAPMessage invokeNORAMethod(final String methodName,
											  final Map<String,String> params) throws SOAPException {
		return this.invokeNORAMethod(methodName,params,
									 true);	// invoke with parents
	}
	/**
	 * Sends a request to the NORA web service.
	 * @param methodName the NORA web service method name
	 * @param params the parameters
	 * @param responseWithParents ...
	 * 
	 * @return the <code>SOAPMessage</code> response
	 *   
	 * @throws SOAPException if an exception occurs
	 */
	public final SOAPMessage invokeNORAMethod(final String methodName,
											  final Map<String,String> params, 
										 	  final boolean responseWithParents) throws SOAPException {
		MessageFactory mfactory = MessageFactory.newInstance();
		
		// create soap message
		SOAPMessage soapMessage = mfactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();
		SOAPEnvelope soapEnvelop = soapPart.getEnvelope();
		soapEnvelop.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
		soapEnvelop.addNamespaceDeclaration("xsi","https://www.w3.org/2001/XMLSchema-instance");
		
		// Message body
		SOAPBody soapBody = soapEnvelop.getBody();
		
		// Method name && params
		Name soapMethodName = soapEnvelop.createName(methodName,
													 "m","http://www.ejie.es/webServiceEJB/t17iApiWSWar");	// namespace
		SOAPBodyElement soapMethodElement = soapBody.addBodyElement(soapMethodName);
		
		if (CollectionUtils.hasData(params)) {
			for (final Map.Entry<String,String> param : params.entrySet()) {
				SOAPElement soapParamElement = soapMethodElement.addChildElement(param.getKey(),		// param name
																				 "m");					// namespace
				
				Name soapParamTypeAttrName = soapEnvelop.createName("xsi:type");
				String soapParamType = "xsd:string";
				soapParamElement.addAttribute(soapParamTypeAttrName,soapParamType);
				
				String soapParamValue = param.getValue();
				soapParamElement.addTextNode(soapParamValue);
			}
		}
		if (responseWithParents) {
			Name soapResponseWithParentsName = soapEnvelop.createName("responseWithParents");
			SOAPElement soapResponseWithParentsElement = soapMethodElement.addChildElement(soapResponseWithParentsName);
			
			Name soapResponseWithParentsAttrName = soapEnvelop.createName("xsi:type");
			String soapResponseWithParentsType = "xsd:boolean";
			String soapResponseWithParentsValue = Boolean.toString(responseWithParents);
			
			soapResponseWithParentsElement.addAttribute(soapResponseWithParentsAttrName,soapResponseWithParentsType);
			soapResponseWithParentsElement.addTextNode(soapResponseWithParentsValue);
		}
		// log 
		if (log.isTraceEnabled()) {
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				soapMessage.writeTo(os);
				log.trace("NORA SOAP message={}",new String(os.toByteArray()));
				//System.out.println(new String(os.toByteArray()));
			} catch (IOException ioEx) {
				// ignored
			}
		}
		// do the ws call
		SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
		SOAPConnection con = factory.createConnection();
		
		log.info("...invoke NORA WS url={}",_noraWSEndpointUrl);
		SOAPMessage soapResponse = con.call(soapMessage, 
											_noraWSEndpointUrl.asString());
		return soapResponse;
	}
}
