package r01f.services.dokusi;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.model.dokusi.DOKUSIOIDs.DOKUSIAuditID;
import r01f.types.Path;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Config data for DOKUSI service
 * Requires a properties file with the following section
 * <pre class='xml'>
 * 		<dokusiService>
 *			<webServiceUrl>http://svc.extra.integracion.jakina.ejiedes.net:80/ctxapp/t65bFsd</webServiceUrl>
 * 			<auditId>X42T#X42T</auditId>
 *			<requestedPifPath>/x42t/dokusi/</requestedPifPath>	<!-- the path where the DOKUSI web service is asked to put the file -->
 * 		</dokusiService>
 * </pre>
 */
@Accessors(prefix="_")
public class DOKUSIServiceAPIData
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Url _webServiceUrl;
	@Getter private final DOKUSIAuditID _auditId;
	@Getter private final Path _requestedPifPath;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DOKUSIServiceAPIData(final Url serviceUrl,
							    final DOKUSIAuditID auditId,
							    final Path requestedPifPath) {
		_webServiceUrl = serviceUrl;
		_auditId = auditId;
		_requestedPifPath = requestedPifPath;
	}
	public DOKUSIServiceAPIData(final XMLPropertiesForAppComponent props,
								final String propsRootXPath) {
		Url webServiceUrl = props.propertyAt(propsRootXPath + "/dokusiService/webServiceUrl")
						      	 .asUrl("http://svc.extra.integracion.jakina.ejiedes.net:80/ctxapp/t65bFsd");
		DOKUSIAuditID auditId = props.propertyAt(propsRootXPath + "/dokusiService/auditId")
									 .asOID(DOKUSIAuditID.class,
											DOKUSIAuditID.forId("R01F#R01F"));
		Path requestedPifPath = props.propertyAt(propsRootXPath + "/dokusiService/requestedPifPath")
									 .asPath(Path.from("/r01fb/dokusi"));
									
		_webServiceUrl = webServiceUrl;
		_auditId = auditId;
		_requestedPifPath = requestedPifPath;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("url={} audit id={} requested pif path={}",
								  _webServiceUrl,_auditId,_requestedPifPath);
	}
}
