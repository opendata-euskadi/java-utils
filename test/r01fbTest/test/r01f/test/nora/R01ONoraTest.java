package r01f.test.nora;

import java.io.ByteArrayOutputStream;

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
public class R01ONoraTest {
	public static void main(String[] args) {
		test();
		
	}
	
	public static final void test() {
        try {		
			/* Creamos el mensaje */
			MessageFactory mfactory = MessageFactory.newInstance();
			SOAPMessage message = mfactory.createMessage();
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
		
			/* Cuerpo del mensaje */
			SOAPBody body = envelope.getBody();
					
			envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
		
			/* Nombre del metodo */	
			Name name = envelope.createName("provincia_getByDesc", "m", "http://www.ejie.es/webServiceEJB/t17iApiWSWa");
			SOAPElement otherName = envelope.addNamespaceDeclaration("xsi","https://www.w3.org/2001/XMLSchema-instance");
			SOAPBodyElement element = body.addBodyElement(name);
		
			/* Parametros del metodo */
			SOAPElement provincias = element.addChildElement(envelope.createName("value"));
			provincias.addAttribute(envelope.createName("xsi:type"),"xsd:string" );
			provincias.addTextNode("");
		
			SOAPElement parents = element.addChildElement(envelope.createName("responseWithParents"));
			/* para mandar un valor null --> parents.addAttribute(envelope.createName("xsi:nil"),"true" ); */
			parents.addAttribute(envelope.createName("xsi:type"),"xsd:boolean" );
			parents.addTextNode("true");
			
			SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
			SOAPConnection con = factory.createConnection();
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			message.writeTo(os);
			System.out.println(" message " +  new String(os.toByteArray()));
			/* Invocamos la llamada */
			SOAPMessage response = con.call(message, 
											"http://svc.inter.integracion.jakina.ejiedes.net/ctxapp/t17iApiWS");
					
			/* Volcando los resultados */
			response.writeTo(System.out);
	   } catch (SOAPException e) {
		   System.out.println(e.getMessage());
	   } catch (java.io.IOException e) {
			System.out.println(e.getMessage());
	   }
	}
}
