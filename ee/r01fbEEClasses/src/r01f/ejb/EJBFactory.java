package r01f.ejb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import lombok.extern.slf4j.Slf4j;
import r01f.reflection.Reflection;

/**
 * EJB3 factory
 */
@Slf4j
public class EJBFactory {
///////////////////////////////////////////////////////////////////////////////
// EJB3
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets an EJB reference
	 * @param jndiName 
	 * @param local 
	 * @param type 
	 * @return 
	 */
	public static <T> T createEJB3(final String jndiName,final boolean local,Class<T> type) {
		return EJBFactory.<T>createEJB3(null,jndiName,local,type);
	}
	/**
	 * Gets an EJB reference
	 * @param jndiContextProps 
	 * @param jndiName 
	 * @param local 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createEJB3(Properties jndiContextProps,
								   String jndiName,boolean local,Class<T> type) {
		T outEJB = null;			
		try {
			Context initialContext = JNDIContextLocator.getInstance().getInitialContext(jndiContextProps);
			String theJNDIName = jndiName + (local ? "Local" : "");	// usually local JNDI name has the "Local" suffix
			Object obj = initialContext.lookup(theJNDIName);
			if (local) { 
				outEJB = (T)obj;
			} else {
				outEJB = (T)PortableRemoteObject.narrow(obj,type);
			}
		} catch(NamingException namEx) {
			log.error("Could NOT get a jndi context to the LDAP tree: {}",
					  namEx.getMessage(),
					  namEx);
		}
		return outEJB;
	}
///////////////////////////////////////////////////////////////////////////////
// EJB2.x
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un EJB a partir de su nombre JNDI
	 * @param jndiName nombre JNDI
	 * @param local true si se trata de una referencia al EJB local
	 * @param homeType tipo del interfaz home
	 * @return la referencia al EJB
	 */
	public static <T> T createEJB2(String jndiName,boolean local,Class<?> homeType) {
		return EJBFactory.<T>createEJB2(null,jndiName,local,homeType);
	}
	/**
	 * Obtiene un EJB a partir de su nombre JNDI
	 * @param jndiContextProps propiedades para obtener una referencia al contexto jndi del LDAP
	 * @param jndiName nombre JNDI
	 * @param local true si se trata de una referencia al EJB local
	 * @param homeType tipo del interfaz home
	 * @return la referencia al EJB
	 */
	public static <T> T createEJB2(Properties jndiContextProps,
								   String jndiName,boolean local,Class<?> homeType) {
		T outEJB = null;			
		try {
			Context initialContext = JNDIContextLocator.getInstance().getInitialContext(jndiContextProps);
			String theJNDIName = jndiName + (local ? "Local" : "");	// Por "convenio" el nombre JNDI del EJB local tiene el sufijo "Local"
			Object home = initialContext.lookup(theJNDIName);
			Object homeNarrowed = PortableRemoteObject.narrow(home,homeType);
			outEJB = Reflection.of(homeNarrowed).method("create").<T>invoke();	// llamar al m�todo create en el objeto home
		} catch(NamingException namEx) {
			log.error("Could NOT get a jndi context to the LDAP tree: {}",
					  namEx.getMessage(),
					  namEx);
		}
		return outEJB;
	}	
}
