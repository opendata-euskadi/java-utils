package r01f.ejie.xlnets.login;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import n38a.exe.N38APISesion;
import n38c.exe.N38API;
import n38c.exe.N38Estructura;
import r01f.ejie.xlnets.XLNetsSession;
import r01f.ejie.xlnets.servlet.XLNetsTargetCfg.ResourceItemType;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.httpclient.HttpClient;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.types.Path;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryStringParam;
import r01f.types.url.Urls;
import r01f.util.types.Strings;
import r01f.xml.XMLUtils;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Provides XLNets session tokens
 * Requires a properties file with a section like:
 *		<xlnets loginAppCode='theAppCode' token='httpProvided'>	<!-- token=file/httpProvided/loginApp -->
 *	 		<!--
 *			Token types:
 *				n38api			: use the http request cookies and N38 API
 *				mockFile 		: use a classpath-stored file with the xlnets session / user / auth / etc data 
 *											> the [sessionToken] element contains the session token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *											> the [userDataToken] element contains the user info token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *											> the [authToken] element contains the auth token's path (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate a token)
 *				httpProvided	: Using a service that provides xlnets session tokens
 *											> the [loginAppCode] attribute is mandatory
 *											> the [sessionToken] element contains the url of the service that provides tokens (ie: http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app=appId)
 *			-->
 *			<!--
 *			Login types:
 *				user			: user login
 *				app				: app login
 *			-->
 *			<sessionToken>
 *				if token=file: 			...path to a mock xlnets token (use https://xlnets.servicios.jakina.ejiedes.net/xlnets/servicios.htm to generate one)
 *				if token=httpProvided:  ...url to the url that provides the token (ie: http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app=appId)
 *				if token=loginApp		...not used 
 *			</sessionToken>
 *		</xlnets>
 */
@Slf4j
public class XLNetsAuthTokenProvider {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final XMLPropertiesForAppComponent _props;
	private final String _propsRootNode;
	private final XLNetsTokenSource _tokenSource;
	private final XLNetsLoginType _loginType;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public XLNetsAuthTokenProvider(final XMLPropertiesForAppComponent props) {
		this(props,null);
	}
	public XLNetsAuthTokenProvider(final XMLPropertiesForAppComponent props,
													 final String propsRootNode) {
		_props = props;
		_propsRootNode = Strings.isNOTNullOrEmpty(propsRootNode) ? propsRootNode : "";

		_tokenSource = _props.propertyAt(_propsRootNode + "/xlnets/@token").asEnumFromCode(XLNetsTokenSource.class,
																						   XLNetsTokenSource.N38API);
		_loginType = _props.propertyAt(_propsRootNode + "/xlnets/@login").asEnumFromCode(XLNetsLoginType.class,
																						 XLNetsLoginType.APP);

		log.warn("[XLNetsAuthenticatedService]: {} XLNets token",_tokenSource);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Uses XLNets API to get the session as a XML {@link Document}
	 */
	public Document getXLNetsSessionTokenDoc() {
		return this.getXLNetsSessionTokenDoc(null);
	}
	/**
	 * Uses XLNets API to get the session as a XML {@link Document}
	 */
	public Document getXLNetsSessionTokenDoc(final HttpServletRequest req) {
		Document outSessionToken = null;
		try {
			log.debug("[XLNetsAuthenticatedService] > Creating session token .........");

			// USER LOGIN
			if (_loginType.is(XLNetsLoginType.USER)) {
				if (_tokenSource == XLNetsTokenSource.N38API) {
					// api
					if (req == null) throw new IllegalArgumentException("The servlet request cannot be null in order to create a XLNets user session!");

					log.warn("Geting a xlnets user session token using N38 API");
			    	N38API n38API = new N38API(req);
			    	outSessionToken = n38API.n38ItemSesion();
				}
				else if (_tokenSource.is(XLNetsTokenSource.MOCK_FILE)) {
					if (req.getParameter(XLNetsMockParams.MOCK_USER_CODE.getCode()) == null) {
						outSessionToken = _loadAndParseXLNetsFile("/xlnets/sessionToken");
					}else {
						log.warn("Get mock user code for " + req.getParameter(XLNetsMockParams.MOCK_USER_CODE.getCode()));
						outSessionToken = _loadAndParseXLNetsFile(_pathForMockUserCode("/xlnets/sessionToken",
								                                  req.getParameter(XLNetsMockParams.MOCK_USER_CODE.getCode())));
					}
				}
				else if (_tokenSource.is(XLNetsTokenSource.HTTP_PROVIDED)) {
					// http provided token
					throw new IllegalStateException("The http provided token source cannot be used with user login type!!");
				}
			}
			// APP LOGIN
			else if (_loginType.is(XLNetsLoginType.APP)) {

				String loginAppId = _props.propertyAt(_propsRootNode + "/xlnets/@loginAppCode").asString();
				if (loginAppId == null) {
					throw new IllegalStateException("The properties file DOES NOT contains " +
													"the appCode for which the session tokens will be provided (xpath=" + _propsRootNode + "/xlnets/@loginAppCode)");
				}
				if (_tokenSource == XLNetsTokenSource.N38API) {
					// api
					if (req == null) throw new IllegalArgumentException("The servlet request cannot be null in order to create a XLNets user session!");

					log.warn("Geting a xlnets app session for appCode={} using N38 API",loginAppId);
					N38API n38API = new N38API(new N38APISesion().n38APISesionCrearApp(loginAppId));
					outSessionToken =  n38API.n38ItemSesion();
				}
				else if (_tokenSource == XLNetsTokenSource.MOCK_FILE) {
					// File
					outSessionToken = _loadAndParseXLNetsFile("/xlnets/sessionToken");
				}
				else if (_tokenSource == XLNetsTokenSource.HTTP_PROVIDED) {
					// http provided (ie: http://svc.intra.integracion.jakina.ejiedes.net/ctxapp/Y31JanoServiceXlnetsTokenCreatorServlet?login_app=X42T)
					Url xlnetsProviderBaseUrl = _props.propertyAt(_propsRootNode + "/xlnets/sessionToken")
														    .asUrl();
					if (xlnetsProviderBaseUrl == null) {
						throw new IllegalStateException("The properties file DOES NOT contains " +
														"the url that provides xlnets session tokens at xpath " + _propsRootNode + "/xlnets/sessionToken");
					}
					Url xlnetsProviderUrl = Urls.join(xlnetsProviderBaseUrl,
													  new UrlQueryStringParam("login_app",loginAppId));
					if (xlnetsProviderUrl == null) {
						throw new IllegalStateException("The properties file DOES NOT contains " +
														"the app code at xpath " + _propsRootNode + "/xlnets/@loginAppCode");
					}
					log.warn("Geting a xlnets auth token for appCode={} from url={}",loginAppId,xlnetsProviderUrl);
					outSessionToken = XMLUtils.parse(HttpClient.forUrl(xlnetsProviderUrl)
																	.GET()
																	.loadAsStream()
																		.notUsingProxy().withoutTimeOut().noAuth());
				}
			}
		} catch (Throwable th) {
			log.error("Error while creating the xlnets session token: {}",th.getMessage(),th);
		}
		return outSessionToken;
	}
	/**
	 * Uses XLNets API to get the user info as a XML {@link Document}
	 * @param req
	 * @param xlNetsSessionDoc
	 * @return
	 */
	public Document getXLNetsUserDoc(final HttpServletRequest req,
									 final Document xlNetsSessionDoc) {
		// Ger the user id from the session document
		String userIdXPath = (_loginType == XLNetsLoginType.APP ? XLNetsSession.XLNETS_APP_SESSION_BASE_XPATH
																: XLNetsSession.XLNETS_USER_SESSION_BASE_XPATH) + XLNetsSession.PERSONA;
        Node xlnetsSessionUserIdNode = null;
        try {
            xlnetsSessionUserIdNode = XMLUtils.nodeByXPath(xlNetsSessionDoc.getDocumentElement(),userIdXPath); 	// XPathAPI.selectSingleNode(xlNetsSessionDoc.getDocumentElement(),
            									   																//							 userIdXPath);
        } catch (XPathExpressionException transEx) {
            log.error("Error while retrieving the user data using xPath {} on xlnets session doc: {}",
            		  userIdXPath,transEx.getMessage(),transEx);
        }
        // Use the api to get the user info
        Document outUserDoc = null;
        try {
	        if (xlnetsSessionUserIdNode != null) {
	        	if (_tokenSource == XLNetsTokenSource.MOCK_FILE) {
	        		if (req.getParameter(XLNetsMockParams.MOCK_USER_CODE.getCode()) == null) {
	        			outUserDoc = _loadAndParseXLNetsFile("/xlnets/userDataToken");
					} else {
						log.warn("Get mock user code for " + req.getParameter(XLNetsMockParams.MOCK_USER_CODE.getCode()));
						outUserDoc = _loadAndParseXLNetsFile(_pathForMockUserCode("/xlnets/userDataToken",
								                             req.getParameter(XLNetsMockParams.MOCK_USER_CODE.getCode())));
					}
	        	} else {
		        	String userId = xlnetsSessionUserIdNode.getNodeValue();
		        	N38API n38API = new N38API(req);
		        	outUserDoc = n38API.n38ItemObtenerPersonas("uid=" + userId);
	        	}
	        }
        } catch(Throwable th) {
			log.error("Error while retrieving the user info from xlnets: {}",th.getMessage(),th);
        }
        return outUserDoc;
	}


	/**
	 * Uses XLNets API to get the user info as a XML {@link Document}
	 * @param req
	 * @param xlNetsSessionDoc
	 * @return
	 */
	public Document getXLNetsItemOrg(final HttpServletRequest req,
			                         final XLNetsOrganizationType type,
			                         final String uid){
        // Use the api to get the org info
        Document organizationDoc = null;
        log.warn("...get org type {} of uid {}", type, uid);
        try {
        	if (_tokenSource == XLNetsTokenSource.MOCK_FILE){
        		if  (type.equals(XLNetsOrganizationType.ORGANIZATION)){
        			organizationDoc = _loadAndParseXLNetsFile("/xlnets/orgDataToken[@for='" + uid + "']");
        			log.warn("load ok!");
        		} else if (type.equals(XLNetsOrganizationType.GROUP)){
        			organizationDoc = _loadAndParseXLNetsFile("/xlnets/groupDataToken[@for='" + uid + "']");
        			log.warn("load ok!");
        		} else if (type.equals(XLNetsOrganizationType.CENTER)){
        			organizationDoc = _loadAndParseXLNetsFile("/xlnets/groupDivisionDataToken[@for='" + uid + "']");
        			log.warn("load ok!");
        		} else {
        			throw new IllegalStateException(Strings.customized("Any trouble getting info of org type {} of uid {} ",type,uid));
        		}
        	} else {
	        	N38API n38API = new N38API(req);
	        	N38Estructura estructura = new N38Estructura();
	        	estructura.setTipoEntrada(type.getCode());
	        	estructura.setUidEntrada(uid);
	        	organizationDoc = n38API.n38ItemOrganizacion(estructura);
        	}
        } catch(Throwable th) {
			log.error("Error while retrieving the Organization {} of type \"{}\",  info from xlnets: {}",type,uid,th.getMessage(),th);
        }
        return organizationDoc;
	}


	/**
	 * Uses XLNets API to get the auth info for a resource
	 * @param req
	 * @param authResourceOid
	 * @param resourceItemType
	 * @return
	 */
	public Document getAuthorization(final HttpServletRequest req,
									 final String authResourceOid,final ResourceItemType resourceItemType) {
		Document outAuthToken = null;
		try {
			log.warn("[XLNetsAuthenticatedService] > Creating authentication token for {} .........",authResourceOid);
			// depending on the token to use create the xlnets auth token
			if (_tokenSource.is(XLNetsTokenSource.MOCK_FILE)) {
			   log.warn("Getting a xlnets auth token for {} using MOCK FILE",authResourceOid);
				outAuthToken = _loadAndParseXLNetsFile("/xlnets/authToken[@for='" + authResourceOid + "']");
			} else {
				if (req == null)
					throw new IllegalArgumentException("The servlet request cannot be null in order to create a XLNets auth token for " + authResourceOid);
				log.warn("CHECKING ACCESS VIA N38 : Getting a xlnets auth token for {} ",authResourceOid);
		    	N38API n38API = new N38API(req);
		    	if (resourceItemType == ResourceItemType.FUNCTION) {
		    		outAuthToken = n38API.n38ItemObtenerAutorizacion(authResourceOid);
		    	} else if (resourceItemType == ResourceItemType.OBJECT) {
		    		outAuthToken = n38API.n38ItemSeguridad(authResourceOid);
		    	}
			}
		} catch (Throwable th) {
			log.error("Error while creating the xlnets auth token: {}",th.getMessage(),th);
		}
		if (outAuthToken != null) {
			String outAuthTokenAsString =  XMLUtils.asString(outAuthToken);
			if (outAuthTokenAsString == null) {
				log.warn("Auth Token AS String is NULL");
			} else {
				log.warn("Auth Token For Function {}", outAuthTokenAsString);
			}
		} else {
			log.warn("Auth Token AS String is NULL");
		}
		return outAuthToken;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private Document _loadAndParseXLNetsFile(final String filePathPropertyXPath) throws IOException,
																						SAXException {
			// File
			Path xlnetsMockTokenPath = _props.propertyAt(_propsRootNode + filePathPropertyXPath)
											 .asPath();
			log.warn("xlnets mock token path from properties file: {}.{}.properties.xml at xpath={} is {}",
					 _props.getAppCode(),_props.getAppComponent(),filePathPropertyXPath,
					 xlnetsMockTokenPath);
			return _loadAndParseXLNetsFile(xlnetsMockTokenPath);
	}
	private Document _loadAndParseXLNetsFile(final Path xlnetsMockTokenPath) throws IOException,
																				    SAXException {
			if (xlnetsMockTokenPath == null) {
				log.error("Could NOT load xlnets mock token path from properties file: {}.{}.properties.xml at xpath={}",
						 _props.getAppCode(),_props.getAppComponent(),xlnetsMockTokenPath);
				throw new IllegalStateException("There's NO path for a mock xlnets file at " + _propsRootNode + xlnetsMockTokenPath);
			}
			InputStream is = ResourcesLoaderBuilder.DEFAULT_RESOURCES_LOADER
												   .getInputStream(xlnetsMockTokenPath);
			if (is == null) throw new IOException("There does NOT exists the xlnets file at classpath location " + xlnetsMockTokenPath +
												 " as set at the properties file xpath " + _propsRootNode + xlnetsMockTokenPath);
			return XMLUtils.parse(is);
	}
	private Path _pathForMockUserCode(final String filePathPropertyXPath,final String userCode){
		String stringAsPath = Strings.customized("{}-id-{}.xml",
								                FilenameUtils.removeExtension(_props.propertyAt(_propsRootNode + filePathPropertyXPath).asString()),
								                userCode);
		return Path.valueOf(stringAsPath);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// ENUMS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
		 enum XLNetsLoginType
   implements EnumWithCode<String,XLNetsLoginType> {
		USER("user"),
		APP("app");

		@Getter private final String _code;
		@Getter private final Class<String> _codeType = String.class;

		private static EnumWithCodeWrapper<String,XLNetsLoginType> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(XLNetsLoginType.class);

		@Override
		public boolean isIn(final XLNetsLoginType... els) {
			return WRAPPER.isIn(this,els);
		}
		@Override
		public boolean is(final XLNetsLoginType el) {
			return WRAPPER.is(this,el);
		}
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor
		 enum XLNetsTokenSource
		   implements EnumWithCode<String,XLNetsTokenSource> {
				N38API("n38api"),
				MOCK_FILE("mockFile"),
				HTTP_PROVIDED("httpProvided");

				@Getter private final String _code;
				@Getter private final Class<String> _codeType = String.class;

				private static EnumWithCodeWrapper<String,XLNetsTokenSource> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(XLNetsTokenSource.class);

				@Override
				public boolean isIn(final XLNetsTokenSource... els) {
					return WRAPPER.isIn(this,els);
				}
				@Override
				public boolean is(final XLNetsTokenSource el) {
					return WRAPPER.is(this,el);
				}
	}

	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum XLNetsOrganizationType
		   implements EnumWithCode<String,XLNetsOrganizationType> {
			ORGANIZATION("O"), //value for n38api O means "organizacion"
			GROUP("GO"),//value for n38api GO means "grupo organico"
			CENTER("CO");//value for n38api CO means "centro organico"

			@Getter private final String _code;
			@Getter private final Class<String> _codeType = String.class;

			private static EnumWithCodeWrapper<String,XLNetsOrganizationType> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(XLNetsOrganizationType.class);

			@Override
			public boolean isIn(final XLNetsOrganizationType... els) {
				return WRAPPER.isIn(this,els);
			}
			@Override
			public boolean is(final XLNetsOrganizationType el) {
				return WRAPPER.is(this,el);
			}
	}

	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public  enum XLNetsMockParams
		implements EnumWithCode<String,XLNetsMockParams> {
			MOCK_USER_CODE("n38mockUserCode"),
			MOCK_APP_CODE("n38mockAppCode");

			@Getter private final String _code;
			@Getter private final Class<String> _codeType = String.class;

			private static EnumWithCodeWrapper<String,XLNetsMockParams> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(XLNetsMockParams.class);

			@Override
			public boolean isIn(final XLNetsMockParams... els) {
				return WRAPPER.isIn(this,els);
			}
			@Override
			public boolean is(final XLNetsMockParams el) {
				return WRAPPER.is(this,el);
			}
		}


}
