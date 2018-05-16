package r01f.services.latinia;

import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.inject.Provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.login.XLNetsAuthenticatedServiceApiData;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.services.latinia.LatiniaServiceApiDataProvider.LatiniaServiceAPIData;
import r01f.types.url.Url;
import r01f.xml.XMLUtils;

/**
 * Provides a {@link LatiniaServiceAPIData} using a properties file info
 * For this provider to work, a properties file with the following config MUST be provided:
 * <pre class='xml'>
 * 	<latinia>
 *		<wsURL>http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/W91dSendSms?WSDL</wsURL>
 *		<authentication>
 *		  <enterprise>
 *		    		<login>INNOVUS</login>
 *		    		<user>innovus.superusuario</user>
 *		    		<password>MARKSTAT</password>
 *		  </enterprise>
 *		  <clientApp>
 *		    		<productId>X47B</productId>
 *		    		<contractId>2066</contractId>
 *		    		<password>X47N</password>
 *		  </clientApp>
 *		</authentication>
 *	</latinia>
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class LatiniaServiceApiDataProvider 
  implements Provider<LatiniaServiceAPIData> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final LatiniaServiceAPIData _apiData;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LatiniaServiceApiDataProvider(final LatiniaConfig cfg) {
		this(cfg,
			 MarshallerBuilder.build()); 
	}
	public LatiniaServiceApiDataProvider(final LatiniaConfig cfg,
								  		 final Marshaller marshaller) {
		Document latiniaAuthToken = _createLatiniaAuthToken(cfg);
		_apiData = new LatiniaServiceAPIData(cfg.getServiceUrl(), 
										 	 latiniaAuthToken,
										 	 null,					// no xlnets
										 	 marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  API DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public class LatiniaServiceAPIData 
	  implements XLNetsAuthenticatedServiceApiData {
		@Getter private final Url _webServiceUrl;
		@Getter private final Document _latiniaAuthToken;
		@Getter private final Document _XLNetsAuthToken;
		@Getter private final Marshaller _latiniaObjsMarshaller;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public LatiniaServiceAPIData get() {	
		return _apiData;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an XML access token to latinia services from applications that do not have N38 session token.
	 * @param cfg
	 * @return Document formatted with latinia user info.
	 * @throws ParserConfigurationException
	 */
	@SuppressWarnings("cast")
	private static Document _createLatiniaAuthToken(final LatiniaConfig cfg) {
		Document outAuthToken = null;
		try {
			log.debug("[Latinia] > Creating authentication token .........");


			log.info("[Latinia] > Token props: enterprise[login={}, user={}, passwd={}] / clientApp[product={}, contract={}, passwd={}]",
					 cfg.getEnterpriseLogin(),cfg.getPublicUser(),cfg.getPublicPassword(),
					 cfg.getProductId(),cfg.getContractId(),cfg.getPassword());


			// [2] - Create the Authentication token
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);

			DocumentBuilder builder = dbf.newDocumentBuilder();
			outAuthToken = builder.newDocument();

			Element authNode = outAuthToken.createElement("authenticationLatinia");

			Element enterpriseUserNode = outAuthToken.createElement("userLatinia");
			enterpriseUserNode.appendChild(outAuthToken.createTextNode(cfg.getPublicUser().asString()));
			authNode.appendChild(enterpriseUserNode);

			Element enterprisePasswordNode = outAuthToken.createElement("passwordLatinia");
			enterprisePasswordNode.appendChild(outAuthToken.createTextNode(cfg.getPublicPassword().asString()));
			authNode.appendChild(enterprisePasswordNode);

			Element clientProductNode = outAuthToken.createElement("refProduct");
			clientProductNode.appendChild(outAuthToken.createTextNode(cfg.getProductId().asString()));
			authNode.appendChild(clientProductNode);

			Element contractIdNode = outAuthToken.createElement("idContract");
			contractIdNode.appendChild(outAuthToken.createTextNode(cfg.getContractId().asString()));
			authNode.appendChild(contractIdNode);

			Element loginEnterpriseNode = outAuthToken.createElement("loginEnterprise");
			loginEnterpriseNode.appendChild(outAuthToken.createTextNode(cfg.getEnterpriseLogin().asString()));
			authNode.appendChild(loginEnterpriseNode);

			Element contractPasswordNode = outAuthToken.createElement("password");
			contractPasswordNode.appendChild(outAuthToken.createTextNode(cfg.getPassword().asString()));
			authNode.appendChild(contractPasswordNode);

			outAuthToken.appendChild((Node)authNode);

			log.debug("[Latinia] > Auth Token={}",XMLUtils.asStringLinearized(outAuthToken, Charset.forName("UTF-8")));

		} catch (Throwable th) {
			log.error("[Latinia] > Error while creating the latinia auth token: {}",th.getMessage(),th);
		}
		return outAuthToken;
	}
}
