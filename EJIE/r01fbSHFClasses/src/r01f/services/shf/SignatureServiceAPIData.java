package r01f.services.shf;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * {@link SignatureService} config data
 * Requires a properties file with the following section
 * <pre class='xml'>
 * 	<signatureService mock='false'>
 *		<wsURL>http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/X43FNSHF2?WSDL</wsURL>
 *		<certificateId>0035</certificateId>
 *	</signatureService>
 * </pre>
 */
@Accessors(prefix="_")
public class SignatureServiceAPIData 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final boolean _mock;
	@Getter private final Url _webServiceUrl;
	@Getter private final String _certificateId;	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SignatureServiceAPIData(final Url webServiceUrl,
								   final String certificateId) {
		_mock = false;
		_webServiceUrl = webServiceUrl;
		_certificateId = certificateId;
	}
	public SignatureServiceAPIData(final XMLPropertiesForAppComponent props,
								   final String propsRootXPath) {		
		// signature properties
		boolean mock = props.propertyAt(propsRootXPath + "/signatureService/@mock")
							.asBoolean(false);
		if (!mock) {
			// [1] Provide a new signature service api data 
			Url webServiceUrl = props.propertyAt(propsRootXPath + "/signatureService/wsURL")
							      	 .asUrl("http://svc.intra.integracion.jakina.ejgvdns/ctxapp/X43FNSHF2?WSDL");
			String certificateId = props.propertyAt(propsRootXPath + "/signatureService/certificateId").asString("0035");
			
			_mock = false;
			_webServiceUrl = webServiceUrl;
			_certificateId = certificateId;
		} else {
			_mock = true;
			_webServiceUrl = null;
			_certificateId = null;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("url={} audit certificate id={}",
								  _webServiceUrl,_certificateId);
	}
}
