package r01f.services.pif;

import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Requires a properties file with the following section
 * <pre class='xml'>
 * 		<pifService>
 * 			<uiConsoleUrl>http://svc.integracion.jakina.ejiedes.net/y31dBoxWAR/appbox</uiConsoleUrl>
 *		</pifService>
 * </pre>
 */
@Accessors(prefix="_")
public class PifServiceAPIData
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public PifServiceAPIData(final XMLPropertiesForAppComponent props,
							 final String propsRootPath) {
		// nothing
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public CharSequence debugInfo() {
		return "";
	}
}
