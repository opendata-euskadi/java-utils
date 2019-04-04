package r01f.ejb;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Singleton holder de los contextos
 */
public final class JNDIContextLocator {
    private static final String DEFAULT_JNDI_CONTEXT_URL = "default";
    
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    private Map<String,Context> _contextCache = null;
    
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
    JNDIContextLocator() {
        _contextCache = new Hashtable<String,Context>(3,0.7F);         // Mapa de referencias a home objects
    }
    public static JNDIContextLocator getInstance() {
        return JNDIContextCacheSingletonHolder.instance;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  SINGLETON HOLDER
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Singleton holder
     *      There's NO sinchronization BUT it's considered "thread safe"
     *      when the VM tries to load a type it's guaranteed that while the class is loaded
     *      no other thread can access the instance
     */
    private static final class JNDIContextCacheSingletonHolder {
        static final JNDIContextLocator instance = new JNDIContextLocator();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Obtiene el contexto JNDI
     * @return el contexto
     * @throws NamingException 
     */
    public Context getInitialContext() throws NamingException {
    	Context outCtx = this.getInitialContext((Properties)null);
        return outCtx;
    }
    /**
     * Obtiene el contexto JNDI
     * @param contextURL
     * @return el contexto
     * @throws NamingException
     */
    public Context getInitialContext(final String contextURL) throws NamingException {
    	Context outCtx = null;
    	if (contextURL != null) {
	    	Properties props = new Properties();
	    	props.put("url",contextURL);
	    	outCtx = this.getInitialContext(props);
    	} else {
    		outCtx = this.getInitialContext((Properties)null);
    	}
        return outCtx;
    }
    /**
     * Obtiene el contexto JNDI
     * @param props las propiedades para inicializar el contexto jndi
     * @return El contexto JNDI
     * @throws NamingException
     */
    public Context getInitialContext(final Properties props) throws NamingException {
    	Context outCtx = null;
    	if (props == null) {
    		// Contexto por defecto
    		outCtx = _contextCache.get(DEFAULT_JNDI_CONTEXT_URL);
    		if (outCtx == null) {
        		outCtx = new InitialContext();
        		_contextCache.put(DEFAULT_JNDI_CONTEXT_URL,outCtx);		// cachear
    		}
    	} else {
    		// Contexto personalizado
        	String url = props.get("url") != null ? props.get("url").toString() : null;	
    		String factory = props.get("factory") != null ? props.get("factory").toString() : null;
        	String user = props.get("user") != null ? props.get("user").toString() : null;
        	String password = props.get("password") != null ? props.get("password").toString() : null;
        	
        	if (url != null) {
        		outCtx = _contextCache.get(url);
        	} else {
        		outCtx = _contextCache.get(DEFAULT_JNDI_CONTEXT_URL);
        	}
    	    if (outCtx == null && url != null) {
        		Properties h = new Properties();
        								h.put(Context.PROVIDER_URL,url);
        	    if (factory != null) 	h.put(Context.INITIAL_CONTEXT_FACTORY,factory);
        	    if (user != null) 		h.put(Context.SECURITY_PRINCIPAL, user);
        	    if (password != null) 	h.put(Context.SECURITY_CREDENTIALS, password);
        	    outCtx = new InitialContext(h);
    	    } else if (outCtx == null && url == null) {
    	    	outCtx = new InitialContext();
    	    }
    	    _contextCache.put(url,outCtx);							// cachear
		}
    	return outCtx;
    }
}
