package r01f.ejb;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import lombok.extern.slf4j.Slf4j;
import r01f.reflection.ReflectionException;
import r01f.reflection.ReflectionUtils;

/**
 * ServiceLocator for EJB20
 */
@Slf4j
public class EJB2HomeLocator {
///////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
///////////////////////////////////////////////////////////////////////////////////////////
    private static final String LOCALHOME_INTERFACE_JNDINAME_PREFIX = "";
    private static final String LOCALHOME_INTERFACE_JNDINAME_SUFFIX = "Local";
    private static final String REMOTEHOME_INTERFACE_JNDINAME_PREFIX = "";
    private static final String REMOTEHOME_INTERFACE_JNDINAME_SUFFIX = "";
///////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Home references cache by their jndi name
     */
    private transient Map<String,Object> _ejbHomeReferencesCache = null;
///////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR 
///////////////////////////////////////////////////////////////////////////////////////////
    EJB2HomeLocator() {
        _ejbHomeReferencesCache = new Hashtable<String,Object>(20,0.5F);         
    }
    /**
     * Gets the locator instance
     * @return La instancia del ServiceLocator
     */
    public static EJB2HomeLocator getInstance() {
    	return EJB2HomeLocator.getInstance(null);
    }
    /**
     * Singleton
     * @param props 
     * @return 
     */
    public static EJB2HomeLocator getInstance(final Properties props) {
    	try {
    		JNDIContextLocator.getInstance().getInitialContext(props);	// Forzar la carga del contexto inicial en la cache de contextos...
        } catch (NamingException namEx) {
            log.error("The initial context could NOT be get from the JNDI tree: {}",
            		  namEx.getMessage(),
            		  namEx);
        }
        return LocatorSingletonHolder.instance;
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  SINGLETON HOLDER
///////////////////////////////////////////////////////////////////////////////////////////
    private static final class LocatorSingletonHolder {
        static final EJB2HomeLocator instance = new EJB2HomeLocator();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  HOME
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Gets the ejb's home inteface caching the retrieved home interface references
     * @param jndiContextProviderURL 
     * @param jndiName jndi name where the home interface can be found 
     * @param local true if the local home interface is looked after 
     * @return una referencia al interfaz home
     */
    private Object _retrieveHomeReference(final String jndiContextProviderURL,
    									  final String jndiName,final boolean local) {
    	String theJNDIName = null;
    	if (local) {
    		theJNDIName = LOCALHOME_INTERFACE_JNDINAME_PREFIX + jndiName + LOCALHOME_INTERFACE_JNDINAME_SUFFIX;
    	} else {
    		theJNDIName = REMOTEHOME_INTERFACE_JNDINAME_PREFIX + jndiName + REMOTEHOME_INTERFACE_JNDINAME_SUFFIX;
    	}
    	// See if the home reference is cached
        Object home = _ejbHomeReferencesCache.isEmpty() ? null
        												: _ejbHomeReferencesCache.get(theJNDIName);
        // If the home reference is NOT cached, look at the jndi tree
        if (home == null) {
            try {
            	home = local ? JNDIContextLocator.getInstance().getInitialContext()
            									 .lookup(theJNDIName)			
            				 : JNDIContextLocator.getInstance().getInitialContext(jndiContextProviderURL)
            				 					 .lookup(theJNDIName);
            	if (home != null) _ejbHomeReferencesCache.put(theJNDIName,home);		// Cache home reference
            } catch (NamingException namEx) {
            	log.error("Cannot get a jndi context: {}",
            			  namEx.getExplanation(),
            			  namEx);
            }
        }
        if (home == null) {
			log.error("The {} home interface for the ejb with jndi name = {} was NOT found",
					  local ? "local":"remote",
					  theJNDIName);
        }
        return home;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  LOCAL HOME
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Gets a local home reference
     * @param jndiName 
     * @return 
     */
    public EJBLocalHome getLocalHome(final String jndiName) {
    	Object home = _retrieveHomeReference(null,jndiName,true);
        return (EJBLocalHome)home;
    }
    /**
     * Gets a local home reference
     * @param jndiName 
     * @param homeType home interface type
     * @return
     */
    public EJBLocalHome getLocalHome(final String jndiName,
    								 final Class<?> homeType) {
        // get the untyped home reference
        EJBLocalHome home = this.getLocalHome(jndiName);
        // cast the home reference (portable cast for rmi-iiop)
        if (home != null) {
            try {
                home = (EJBLocalHome)PortableRemoteObject.narrow(home,homeType);
            } catch (ClassCastException ccEx) {
                log.error("Error while narrowing to {} the home interface with jndi name={} > {}",
                		  homeType.getName(),home.getClass().getName(),
                		  ccEx.getMessage());
            }
        }
        return home;
    }
    /**
     * Gets a local home reference
     * @param jndiName 
     * @param homeClassName home interface type 
     * @return 
     */
    public EJBLocalHome getLocalHome(final String jndiName,
    								 final String homeClassName) {
    	EJBLocalHome home = null;
        try {
            home = this.getLocalHome(jndiName,
            						 ReflectionUtils.typeFromClassName(homeClassName));
        } catch (ReflectionException cnfEx) {
            log.error("{} type was NOT found when trying to narrow home interface with jndi name={} > {}",
            		  homeClassName,jndiName,
            		  cnfEx.getMessage(),
            		  cnfEx);
        }
        return home;
    }
    /**
     * Checks an ejb's local home interface existence
     * @param jndiName
     * @return 
     */
    public boolean existsLocalHome(final String jndiName) {
        Object home = this.getLocalHome(jndiName);
        return home == null ? false:true;
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  REMOTE HOME
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Gets a remote home reference
     * @param jndiName 
     * @return 
     */
    public EJBHome getRemoteHome(String jndiName) {
        return this.getRemoteHome(null,
        						  jndiName);
    }
    /**
     * Gets a remote home reference 
     * @param jndiName 
     * @param homeType home interface type
     * @return 
     */
    public EJBHome getRemoteHome(String jndiName,Class<?> homeType) {
        return this.getRemoteHome(null,
        						  jndiName,homeType);
    }
    /**
     * Gets a remote home reference 
     * @param jndiContextProviderURL url del provider de contexto JNDI
     * @param jndiName 
     * @return 
     */
    public EJBHome getRemoteHome(final String jndiContextProviderURL,
    							 final String jndiName) {
        Object home = _retrieveHomeReference(jndiContextProviderURL,
        									 jndiName,false);
        return (EJBHome)home;
    }
    /**
     * Gets a remote home reference
     * @param jndiContextProviderURL 
     * @param jndiName 
     * @param homeType home interface type
     * @return 
     */
    public EJBHome getRemoteHome(final String jndiContextProviderURL,
    							 final String jndiName,final Class<?> homeType) {
        // find an untyped home reference
        EJBHome home = this.getRemoteHome(jndiContextProviderURL,
        								  jndiName);
        // cast the home reference (portable cast for rmi-iiop)
        if (home != null && homeType != null) {
            try {
                home = (EJBHome)PortableRemoteObject.narrow(home,homeType);
            } catch (ClassCastException ccEx) {
                log.error("Error while narrowing to {} the home interface with class={} and jndi name={} > {}",
                		  homeType.getName(),home.getClass().getName(),jndiName,
                		  ccEx.getMessage(),
                		  ccEx);
            }
        }
        return home;
    }
    /**
     * Gets a remote home reference
     * @param jndiContextProviderURL
     * @param jndiName 
     * @param homeClassName home interface type
     * @return El objeto tipado
     */
    public EJBHome getRemoteHome(final String jndiContextProviderURL,
    							 final String jndiName,final String homeClassName) {
    	EJBHome home = null;
        try {
            home = this.getRemoteHome(jndiContextProviderURL,
            						  jndiName,ReflectionUtils.typeFromClassName(homeClassName));
        } catch (ReflectionException cnfEx) {
            log.error("Cannot narrow home interface with jndi name={} to type {} > {}",
            		  jndiName,homeClassName,
            		  cnfEx.getMessage(),
            		  cnfEx);
        }
        return home;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  HANDLES
////////////////////////////////////////////////////////////////////////////////////////
}
