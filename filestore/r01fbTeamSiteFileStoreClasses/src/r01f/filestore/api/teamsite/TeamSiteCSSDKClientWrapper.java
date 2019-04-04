package r01f.filestore.api.teamsite;

import java.util.Locale;
import java.util.Properties;

import com.interwoven.cssdk.access.CSExpiredSessionException;
import com.interwoven.cssdk.access.CSInvalidSessionStringException;
import com.interwoven.cssdk.common.CSClient;
import com.interwoven.cssdk.common.CSException;
import com.interwoven.cssdk.factory.CSFactory;
import com.interwoven.cssdk.filesys.CSRoot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TeamSiteCSSDKClientWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Client cached?
     */
    private final boolean _clientCacheEnabled;
    /**
     * Auth info
     */
    private final TeamSiteAuthData _authInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * CSSDK client objects factory
     */
    private CSFactory _cssdkFactory;
    /**
     * The cached client (expires at a given timeout)
     */
    private CSClient _cssdkClient;
    /**
     * Cached client timeout
     */
    private long _clientExpirationDateTS = System.currentTimeMillis();
    /**
     * The cached client session string
     */
    private TeamSiteSession _cssdkClientSession;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
    public TeamSiteCSSDKClientWrapper(final boolean clientCacheEnabled,
    								  final TeamSiteAuthData authInfo) {
    	_clientCacheEnabled = clientCacheEnabled;
    	_authInfo = authInfo;
    	log.warn("The CSSDK Client will{}be CACHED!",
    			 _clientCacheEnabled ? " " : " NOT ");
    	log.warn("The auth info that will be used to create the CSSDK client is\n{}",
    			 _authInfo.debugInfo());
    	
    }
    public static TeamSiteCSSDKClientWrapper createCachingClient(final TeamSiteAuthData authInfo) {
    	return new TeamSiteCSSDKClientWrapper(true,			// cache
    										  authInfo);
    }
    public static TeamSiteCSSDKClientWrapper createNOTCachingClient(final TeamSiteAuthData authInfo) {
    	return new TeamSiteCSSDKClientWrapper(false,		// do NOT cache
    										  authInfo);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a CSSDK factory
     * @param authInfo
     * @return
     */
    public CSFactory getOrCreateCSSDKClientFactory() {
        log.debug("DATASTORE MANAGER (IW) - init>> Create the CSSDK factory");
        
        if (_cssdkFactory != null) {
        	log.trace("\t...returning the cached cssdk factory");
        	return _cssdkFactory;
        }

        // Factory properties
        Properties props = new Properties();
        props.put("serviceBaseURL",_authInfo.getCsServiceUrl());
        props.put(_authInfo.getCsSDKFactoryInterfaceType().getName(),
        		  _authInfo.getCsSDKFactoryImplType().getName());

        // Factory create
        CSFactory outFactory = CSFactory.getFactory(props);
        log.trace("\t...created cssdk factory {}",_authInfo.getCsSDKFactoryImplType());
        
        // factory cache
        _cssdkFactory = outFactory;
        
        return outFactory;
    }
    /**
     * <pre>
     * Obtiene un cliente de ContentServices para un usuario.
     * Aqu� se realiza la autenticaci�n en el DataStore.
     *      Normalmente, el DataStore devuelve alg�n tipo de contexto que es necesario
     *      para realizar posteriores llamadas.
     *      En este m�todo se establecer�a una variable global para guardar dicho contexto
     *      que adem�s sirve para verificar si el usuario est� autenticado.
     *      (ver {@link #findStore(CSRoot, String)}).
     * Los par�metros necesarios son:
     * <ul>
     * <li>Nombre de Usuario, en Windows DOMINIO\\userName.</li>
     * <li>Role con el cual el usuario se conectar�, p.e. od-admin, od-user.</li>
     * <li>Password del usuario.</li>
     * <li>Localizaci�n, p.e. es_ES, en_US.</li>
     * <li>Nombre de la aplicaci�n que accede.</li>
     * <li>Nombre del TeamSite server, si es null asume que es el instalado en la propia m�quina
     *     donde est�n instalados los Content Services, nombre indicado en la variable '<i>defaultTSServer</i>'
     *     del fichero <i>cssdk.cfg</i>.</li>
     * </ul>
     * </pre>
     * @return Un objeto CSClient con el cliente.
     * @throws TeamSiteFileStoreException si no se puede autenticar.
     */
    public CSClient getOrCreateCSSDKClient() throws TeamSiteFileStoreException {
    	
        log.debug("DATASTORE MANAGER (IW)>> Get client for user {} with role {} ",
        		  _authInfo != null ? _authInfo.getLoginUser() : "null",_authInfo != null ? _authInfo.getLoginUserRole() : "null");
        
        // Options to get the client
        // 1) The client is cached
        // 2) The client is not cached or has expired and a new one can be created reusing the connection string
        // 3) The client is not cached or has expired and a new one can be created authenticating the user again
        if (_clientCacheEnabled 
         && _cssdkClient != null 
         && _cssdkClient.isValid()
         && _clientExpirationDateTS > System.currentTimeMillis() + 30000) {		// Se da un margen respecto a la fecha de expiraci�n de 30seg para evitar que se utilice un objeto caducado.            
        	log.trace("\t...Returning teamsite cached client (expires at {})",
        	          _cssdkClient.getExpirationDate());
        	return _cssdkClient;
        }
        
        // Create or get the previously created cssdk client factory
        CSFactory cssdkClientFactory = this.getOrCreateCSSDKClientFactory();
        CSClient outClient = null;
        try {
	        // The client is NOT cached or has expired: try to reuse the connection string
	        if ((_cssdkClient == null || _cssdkClient.isValid() == false) && _cssdkClientSession!=null) {
	        	outClient = _createCSClientReusingSession(cssdkClientFactory,
	        											  _authInfo,
														  _cssdkClientSession);
	        }
	        // The client is NOT cached or has expired: try to create a new one authenticating the user
	        if ((outClient == null || outClient.isValid() == false)
	         && _authInfo != null) {
	        	outClient = _createFreshNewCSClient(cssdkClientFactory,
	        										_authInfo);
	        	if (outClient == null || outClient.isValid() == false) {
	        		throw new TeamSiteFileStoreException("Could NOT get a valid CSSDK client");
	        	}
	        }
        } catch(CSException csEx) {
        	throw TeamSiteFileStoreException.createFor("obtainClient",csEx);
        }
        if (outClient == null) throw new TeamSiteFileStoreException("Could NOT create a CSSDK client!");
        
        // cache client
        if (_clientCacheEnabled) {
            log.trace("\t... CACHING created client (expires at {})",outClient.getExpirationDate());
            _clientExpirationDateTS = outClient.getExpirationDate().getTime();
            _cssdkClient = outClient;
            _cssdkClientSession = new TeamSiteSession(_cssdkClient.getContext().getSessionString());
        }
        return outClient;
    }
    private static CSClient _createCSClientReusingSession(final CSFactory clientFactory,
    													  final TeamSiteAuthData authData,
    													  final TeamSiteSession session) throws CSException {
    	CSClient outClient = null;
	    try {
	        log.trace("\t...REUSING user session string {} to create the CSSDK client",
	        		  session);
	        outClient = clientFactory.getClient(session.asString(),
	                                            Locale.getDefault(),
	                                            authData.getCsSDKFactoryImplType().getName(),
	                                            null);		// ???
	        if (outClient != null) {
	            log.debug("\t...CREATED REUSED CSSDK client (client version={} / server version={})" ,
	                      clientFactory.getClientVersion(),clientFactory.getServerVersion());
	        }
	    } catch(CSInvalidSessionStringException invSesEx) {
	        log.warn("User session string={} could NOT be reused to create a client (it's NOT VALID): a new session must be created!!!",session);
	        outClient = null;
	    } catch (CSExpiredSessionException sesExc) {
	        log.warn("User session string={} could NOT be reused to create a client (it has EXPIRED): a new session must be created!!!",session);
	        outClient = null;
	    } 
	    return outClient;
    }
    private static CSClient _createFreshNewCSClient(final CSFactory clientFactory,
    												final TeamSiteAuthData authData) throws CSException {
    	CSClient outClient = null;
        log.trace("\t...CREATING a FRESH NEW CSSDK client with user info auth:\n{} ",
        		  authData.debugInfo());
        outClient = clientFactory.getClient(authData.getLoginUser().asString(),
                                            authData.getLoginUserRole().asString(),
                                            authData.getLoginUserPassword().asString(),
                                            Locale.getDefault(),
                                            authData.getCsSDKFactoryImplType().getName(),
                                            null);
        if (outClient != null) {
            log.debug("\t...CREATED FRESH NEW CSSDK client (client version={} / server version={})" +
                      clientFactory.getClientVersion(),clientFactory.getServerVersion());
        }
        return outClient;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Returns the client auth cookie
     * @return cookie
     */
    public String getSessionAuthenticationCookie() throws TeamSiteFileStoreException{
    	CSClient cssdkClient = this.getOrCreateCSSDKClient();
    	return cssdkClient.getContext().getSessionString();
    }
}
