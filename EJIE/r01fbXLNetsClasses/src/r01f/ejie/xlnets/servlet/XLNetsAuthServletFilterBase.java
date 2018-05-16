/*
 * @author Alex Lara Garachana
 * Created on 16-may-2004
 */
package r01f.ejie.xlnets.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.servlet.XLNetsTargetCfg.ResourceAccess;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.AppCode;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLPropertiesComponent;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Filtro de entrada para control de la seguridad basada en XLNets de forma declarativa.
 * La configuraci�n de seguridad se declara en el fichero [codApp].xlnets.properties.xml incluyendo una
 * seccion como la del siguiente ejemplo (ver {@link XLNetsAppCfg})
 * <pre class='brush:xml'>
 * 	<xlNets token='n38' login='user'>
 *     <authCfg useSession='true/false' override='true/false'>
 *     		<target id='theId' kind='restrict|allow'>
 *     			<uri>[Expresion regular para machear la uri que se solicita]</uri>
 *     			<resources>
 *     				<resource type='[itemType]' mandatory='true/false' oid='[itemOID]'>
 *     					<es>[Nombre en castellano]</es>
 *     					<eu>[Nombre en euskera]</eu>
 *     				</resource>
 *     				<resource type='[itemType]' mandatory='true/false' oid='[itemOID]'>
 *     					<es>[Nombre en castellano]</es>
 *     					<eu>[Nombre en euskera]</eu>
 *     				</resource>
 *     				....
 *     			</resources>
 *     		</target>
 *     		....
 *     </authCfg>
 * 	</xlNets>
 * </pre>
 * Notas:
 * <pre>
 * 		useSession:			Indica si la informaci�n de autorizacion se almacena en memoria o bien hay que
 * 							volver a obtenerla cada vez que se accede al recurso
 * 		override:			Indica si se ha de ignorar la configuracion de seguridad (no hay seguridad)
 *      provider:           Configuraci�n del provider de seguridad
 * 		   className: 	    La clase que se encarga de consultar el almac�n de seguridad
 * 							      Puede utilizarse XLNets o un povider que obtiene la seguridad de BD
 * 		target:			    Un recurso que se protege
 *          id:             Identificador del recurso
 *          kind:           Tipo de protecci�n
 *                              allow   -> Permitir el acceso
 *                              restrict-> restringir el acceso
 * 			uri:			Una expresi�n regular con la url del recurso.
 * 			resources:		Elementos sobre los que hay que comprobar si el usuario tiene acceso
 *                          NOTA: Si el tipo es allow, NO se comprueban los recursos
 *              resource    Elemento individual sobre el que hay que comprobar si el usuario tiene acceso
 * 				  oid:		El oid del objeto de seguridad
 * 							En el caso de XLNets el oid puede corresponder al uid de una funci�n o un
 * 							tipo de objeto
 *                mandatory	true/false: Indica si este item es OBLIGATORIO, lo cual implica que en caso de
 * 							no tener acceso, se prohibir� el acceso al recurso.
 * 							Si en todos los items mandatory es false, se permitir� el acceso aunque
 * 							no haya autorizaci�n a los items, sin embargo, la informaci�n de seguridad se
 * 							dejar� en sesion.
 * 				  type:		El tipo de elemento a comprobar
 * 							En el caso de XLNets el tipo puede ser
 * 								function: Una funcion
 * 								object: Un tipo de objeto
 * 				  es/eu		La descripcion en euskera y castellano del item de seguridad
 *
 * </pre>
 * Se pueden definir m�ltiples recursos en una aplicacion, cada uno de ellos tendr� asociada una expresi�n regular
 * con la URI.
 * Cuando llega una petici�n, se aplicar� la configuraci�n de seguridad del primer recurso cuya uri
 * machee la url solicitada al filtro.
 *
 * La secuencia de autorizacion es la siguiente:
 * <pre>
 * 1.- INSTANCIAR EL PROVIDER DE AUTORIZACION ESPECIFICADO EN LA CONFIGURACION
 * 2.- COMPROBAR SI EL USUARIO EST� AUTENTICADO
 * 	   Se llama al m�todo getContext() para ver si el usuario est� autenticado y si es as� obtener el contexto de
 *     autenticaci�n de usuario
 *     Aqu� a su vez se pueden dar dos casos:
 * 	   2.1 - EL USUARIO NO ESTA AUTENTICADO
 * 	           Si el usuario NO est� autenticado se le dirige a la p�gina de login llamando
 *             al metodo redirectToLogin() del provider de autorizacion
 *     2.2 - EL USUARIO ESTA AUTENTICADO PERO NO HAY INFORMACION DE CONTEXTO DE AUTORIZACION EN LA SESION
 *             El usuario ha hecho login, pero es la primera vez que entra al recurso y no hay informaci�n de
 *             contexto de autorizacion en la sesion. El provider en la llamada a al funcion getContext() devuelve
 *             todos los datos del usuario y de la sesi�n de seguridad en la que esta autenticado
 *             Ahora ya se est� como en el caso 2.3 (siguiente caso)
 * 	   2.3 - EL USUARIO EST� AUTENTICADO Y HAY INFORMACION DE CONTEXTO DE AUTORIZACION EN LA SESION
 *             Si en la sesi�n hay informaci�n de contexto, se busca en el contexto la informacion de autorizacion
 *             del destino solicitado. Pueden darse dos casos:
 *             2.3.1 - En el contexto NO hay informacion de autorizacion del destino (es la primera vez que se accede)
 *                        Se llama a la funcion authorize() del provider de seguridad que se encarga obtener
 *                        la autorizacion correspondiente.
 *                        A partir de este momento, esta informaci�n de autorizaci�n se mantiene en sesi�n
 *                        y no es necesario volver a pedirla al provider de seguridad
 *             2.3.2 - En el contexto HAY informacion de autorizacion del destino (ya se ha accedido al recurso)
 *                        Directamente se devuelve la informacion de autorizacion almacenada en sesion
 * </pre>
 */
@Singleton
@Accessors(prefix="_")
@Slf4j
public abstract class XLNetsAuthServletFilterBase
  implements Filter {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTES
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String AUTHCTX_SESSIONATTR = "XLNetsAuthCtx";	// Contexto de usuario en la sesion
	public static final String AUTHCTX_REQUESTATTR = "XLNetsAuthCtx";	// Contexto de usuario en la request
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * C�digo de aplicaci�n
	 */
	private final AppCode _appCode;
	/**
	 * XLNets config
	 */
	@SuppressWarnings("unused")
	private final XMLPropertiesForAppComponent _props;
	/**
	 * Configuraci�n de seguridad de la aplicacion
	 */
	private final XLNetsAppCfg _appCfg;
	/**
	 * Provider de autenticaci�n
	 */
	@Getter protected final XLNetsAuthProvider _authProvider;
	/**
	 * Configuraci�n del filtro (web.xml)
	 */
	private FilterConfig _servletFilterConfig = null;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public XLNetsAuthServletFilterBase(@XMLPropertiesComponent("xlNets") final XMLPropertiesForAppComponent props) {
		_props = props;
		_appCode = props.getAppCode();
		_appCfg = new XLNetsAppCfg(props);	// Cargar la configuraci�n de autorizaci�n del fichero properties de la aplicaci�n
		_authProvider = new XLNetsAuthProvider(props);
	}
///////////////////////////////////////////////////////////////////////////////////////////
//  METODOS
///////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void destroy() {
		_servletFilterConfig = null;
	}
	@Override
	public void init(final FilterConfig config) throws ServletException {
		_servletFilterConfig = config;
	}
	@Override
	public void doFilter(final ServletRequest request,final ServletResponse response,
						 final FilterChain chain) throws IOException,
						 								 ServletException {
		log.warn(">>>>>>>>>> Inicio: Filtro de Autorizacion >>>>>>>>>>>>>>>>>>>>>>");
		try {
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse res = (HttpServletResponse)response;

	        // Obtener la URI solicitada y ver si la url a la que se quiere acceder verifica
	        // alguno de los patrones establecidos en la configuracion de autorizacion
	        UrlPath urlPath = UrlPath.from(_fullURI(req));//getRequestURI only returns URL BEFORE query string -- UrlPath.from(req.getRequestURI());
	        log.warn("[XLNetsAuth]-req.getRequestURI(): {}", req.getRequestURI());
	        log.trace("[XLNetsAuth]-UrlPath: {}", urlPath);
	        XLNetsTargetCfg targetCfg = _resourceThatFirstMatches(_appCfg,
	        													  urlPath);
	        if (targetCfg == null) throw new ServletException(Throwables.message("El filtro de seguridad XLNetsAuthServletFilter NO ha verificado ning�n patron para la uri '{}'.\nRevisa la seccion <authCfg> del fichero {}.xlnets.properties.xml",
	        												   					 urlPath,_appCode));
	        log.warn( "Resource that first match {}", targetCfg.toString());

		    XLNetsAuthCtx authCtx = null;
		    log.warn("{}",(_appCfg.isOverride() ? "NO se comprueba la autorizacion: Parametro override=true"
		    									: "Comprobando autorizacion...") );
		    if (!_appCfg.isOverride()
		     && targetCfg.getKind() == ResourceAccess.RESTRICT ) {

		    	log.warn("La url {} tiene el acceso protegido: authCfg.override={} targetCfg.kind={}",
	            		 urlPath,_appCfg.isOverride(),targetCfg.getKind());

				// Obtener el contexto de la sesi�n (si hay sesi�n)
		    	authCtx = _retrieveSessionStoredAuthCtx(_appCfg,req);		// Contexto de autorizacion

			    // No hay informaci�n de contexto de autorizacion. Pueden pasar dos cosas
			    //		1.- El usuario se ha autenticado y es la primera vez que entra al target
			    //		2.- El usuario NO se ha autenticado
				if (authCtx == null) {
				    log.warn("No hay contexto de autorizacion:[1]- Es la primera vez o [2]- No hay autenticacion");
				    // create a new auth context
				    authCtx = _authProvider.getAuthContext(req);
				    if (authCtx == null) {
				        // No hay sesi�n de usuario: Redirigir a la p�gina de login indicando la url a la que se quiere acceder ahora
				        log.warn("El usuario NO se ha autenticado, redirigir a la pagina de login");
				        _redirToLoginPage(_authProvider,
				        				  req,res);
	                    return;		// FIN!!
	                }
				}
				log.warn(" Auth Context from  Session {}", authCtx.toString() );
				// Aqu� ya hay un contexto de usuario, bien porque ya estaba en la sesi�n, bien porque se ha creado nuevo
				log.warn("El usuario ya est� autenticado y tiene los perfiles {}.\n ===> Comprobar la autorizaci�n de acceso para la uri: {}",
						 authCtx.getUserProfiles(),urlPath);

				XLNetsTargetCtx targetCtx = authCtx.getTargetAuth(targetCfg.getUrlPathPattern().pattern());	// La informaci�n de autorizaci�n para el target puede estar en YA en el contexto de autorizaci�n



				if (targetCtx == null) targetCtx = _authorize(targetCfg,
															  authCtx,
															  req,res);


				// Aqui ya hay informacion de autorizacion y todo el mondongo
	            if (targetCtx == null || CollectionUtils.isNullOrEmpty(targetCtx.getAuthorizedResources()) ) {
	            	// No se ha podido obtener la informacion de autorizacion o bien no se tiene acceso
			        log.warn("NO se ha podido cargar la info de autorizacion del recurso: [1.-] No hay acceso [2.-] El provider de autorizacion no ha funcionado");
			        res.sendError(HttpServletResponse.SC_FORBIDDEN,
			        			  "El filtro de seguridad R01F NO ha permitido el acceso al recurso!");
			        return;		// A la porra!!!
	            }

			    // Antes de pasar el testigo al recurso que toca si se utiliza session, poner el contexto de
			    // autorizacion en session.
			    // En cualquier caso, tanto si se utiliza session el contexto se pasa al recurso como un atributo
			    // de la request
	            log.warn("Guardando el contexto de autenticaci�n en sesi�n... {}",authCtx.toString());
		    	req.setAttribute(AUTHCTX_REQUESTATTR,
		    					 authCtx);
		    	if (_appCfg.isUseSession()) {
			        HttpSession ses = req.getSession(true);		// Crear la session por huevos.
			        ses.setAttribute(AUTHCTX_SESSIONATTR,
			        				 authCtx);
		    	}


			} else {
			    // Dado que no se comprueba la autorizacion, hay que crear unos contextos 'virtuales'
			    // El recurso al que se llama deber�a comprobar el parametro appCfg.override para comprobar si se est� chequeando la autorizacion
	            log.warn("NO se ha comprobado la seguridad: appCfg.override={} targetCfg.kind={}: " +
	                     "El atributo override=true o bien para el target que machea {} se ha configurado kind={}",
	                     _appCfg.isOverride(),targetCfg.getKind(),urlPath,ResourceAccess.ALLOW);
			    authCtx = new XLNetsAuthCtx();
			}

		    // Permitir el acceso...
	    	log.warn("Autorizado!!!!");

		    //__________________________ PASAR EL TESTIGO ________________________________
		    // Siguiente eslabon de la cadena.....
	    	_attachBusinessModelObjectToLocalThreadIfNeeed(req); //Attach some businnes modell object to a local thread
		    chain.doFilter(request,response);
			log.warn(">>>>>>>>>> Fin: Filtro de Autorizacion >>>>>>>>>>>>>>>>>>>>>>\n\n\n\n");
		} finally {
			_doFinallyAfterFilter();
		}
	}
	protected String getInitParameter(String s) {
		return _servletFilterConfig.getInitParameter(s);
	}

///////////////////////////////////////////////////////////////////////////////////////////
//	METHODS TO IMPLEMENT
///////////////////////////////////////////////////////////////////////////////////////////
	protected abstract void _attachBusinessModelObjectToLocalThreadIfNeeed(final HttpServletRequest request);
	protected abstract void _doFinallyAfterFilter();
///////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////////////////
	private static XLNetsAuthCtx _retrieveSessionStoredAuthCtx(final XLNetsAppCfg authCfg,
										   		 			   final HttpServletRequest req) {
		log.debug("_retrieveSessionStoredAuthCtx for {}", authCfg.toString());
		XLNetsAuthCtx authCtx = null;
		if (authCfg.isUseSession()) {
			HttpSession ses = req.getSession(false);	// do not create a new session
			if (ses != null) {
                authCtx = (XLNetsAuthCtx)ses.getAttribute(AUTHCTX_SESSIONATTR);

                log.warn("Contexto de seguridad obtenido de la sesi�n web {}!",ses.getId());
            } else {
                log.warn("NO existe el contexto de seguridad en la sesi�n web!");
            }
		} else {
		    log.warn("La informacion de autorizacion NO se guarda en session http");
		}
		return authCtx;		// devuelve null si NO se utiliza sesi�n
	}
	private XLNetsTargetCtx _authorize(final XLNetsTargetCfg targetCfg,
									   final XLNetsAuthCtx authCtx,
									   final HttpServletRequest req,final HttpServletResponse res) {

		log.warn("Checking access for URI {}",
				 req.getRequestURI());

	    XLNetsTargetCtx targetCtx = _authProvider.authorize(authCtx,
	    								   				   targetCfg,
	    								   				   _appCfg.isOverride(),
	    								   				   req);

        // A�adir el contexto de autorizacion del recurso en el contexto de autorizaci�n de la aplicacion,
	    // as� est� disponible para futuras llamadas.
	    if (targetCtx != null) {
	        log.warn("Introducir la informacion de autorizacion para el patr�n de url {} en el contexto de autorizacion global en sesion",
	        		 targetCtx.getTargetCfg().getUrlPathPattern());
	        if (authCtx.getAuthorizedTargets() == null)  authCtx.setAuthorizedTargets(new HashMap<String,XLNetsTargetCtx>());
	        log.warn(" [>>>>>>>>>>>>>>>>>>>>>>>>>] Setting {} pattern as authorized !!!", targetCtx.getTargetCfg().getUrlPathPattern().pattern());
	        authCtx.getAuthorizedTargets().put(targetCtx.getTargetCfg().getUrlPathPattern().pattern(),
	        								   targetCtx);
	    }

        // Devolver el contexto del destino
        return targetCtx;
	}
	private void _redirToLoginPage(final XLNetsAuthProvider authProvider,
								   final HttpServletRequest req,final HttpServletResponse res) {
        // primer intento: configuraci�n de seguridad de la aplicaci�n
		System.out.println("XLNetsAuthServletFilterBase._redirToLoginPage");
        Url loginPage = _appCfg.getLoginUrl();
        // segundo intento: parametro del filtro
        if (loginPage == null) {
        	String filterConfigLoginUrlParam = _servletFilterConfig.getInitParameter("xlnetsLoginURL");
        	if (Strings.isNOTNullOrEmpty(filterConfigLoginUrlParam)) loginPage = Url.from(filterConfigLoginUrlParam.trim());
        }
       log.warn("Login Page {} ",loginPage);
        // ERROR
        if ( loginPage == null ) {
            log.warn("NO se ha podido encontrar la url de login. El orden de b�squeda ha sido: (1)-Propiedad xlNets/authCfg/provider/loginPage de la aplicacion, (2)-Parametro xlnetsLoginURL de la configuraci�n del filtro en el fichero web.xml");
        } else {
            // �APA: XLNets necesita el parametro N38API con la url a la que se quiere ir...

            Url theLoginPage = loginPage.joinWith(UrlQueryString.fromParams(UrlQueryStringParam.of("N38API",
            																					   _fullURI(req))));
            log.warn("redirecting to login page: {}",theLoginPage);
            authProvider.redirectToLogin(res,
            							 theLoginPage);
        }
	}
    private static XLNetsTargetCfg _resourceThatFirstMatches(final XLNetsAppCfg appCfg,
    														 final UrlPath urlPath) {
    	UrlPath theUrlPath = urlPath;
        if (theUrlPath == null) {
            theUrlPath = UrlPath.from("/");
            log.warn("La uri suministrada es nula... se toma una uri dummy, as� que se machear� unicamente el target con uriPattern * (si lo hay)");
        }
        log.warn("Intentando casar la URI: {} con los patrones de uri especificados en el fichero de properties",theUrlPath);

        XLNetsTargetCfg outTargetCfg = null;
        if (CollectionUtils.hasData(appCfg.getTargets())) {
            for (XLNetsTargetCfg cfg : appCfg.getTargets() ) {
                if ( _matches(theUrlPath,
                			  cfg.getUrlPathPattern()) ) {
                    // Se ha encontrado una configuraci�n de seguridad para la uri a la que se quiere acceder
                    log.warn("pattern: {} MATCHES!!!",cfg.getUrlPathPattern());
                    outTargetCfg = cfg;
                    break;
                }
            }
        }

        if (outTargetCfg == null)
        	log.warn("No se ha encontrado ningun patron para la uri {}",theUrlPath.asAbsoluteString());
        log.warn("outTargetCfg {}", outTargetCfg.toString());
        return outTargetCfg;
    }
	private static boolean _matches(final UrlPath urlPath,
									final Pattern pattern) {
		// Utilizar expresiones regulares para machear la uri recibida
	    // con la especificada en el fichero de prop�edades
		Matcher m = pattern.matcher(urlPath.asAbsoluteString());
		boolean matches = m.find();
		return matches;
	}

	private String _fullURI(HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
	    String queryString = request.getQueryString();

	    if (queryString == null) {
	        return requestURL.toString();
	    } else {
	        return requestURL.append('?').append(queryString).toString();
	    }
	}
}