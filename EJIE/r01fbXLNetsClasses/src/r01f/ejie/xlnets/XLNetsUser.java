/*
 * Created on 26-jul-2004
 *
 * @author IE00165H
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Usuario
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class XLNetsUser implements Serializable {
    private static final long serialVersionUID = 7643780567483567591L;
///////////////////////////////////////////////////////////////////////////////////////////
//  MIEMBROS
///////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private String _oid = "user-unknown";
    @Getter @Setter private boolean _loginApp;
    @Getter @Setter private String _login;
    @Getter @Setter private String _persona;
    @Getter @Setter private String _puesto;
    @Getter @Setter private String _name = "user-name-unknown";
    @Getter @Setter private String _surname = "user-surname-unknown";
    @Getter @Setter private String _displayName = "user-dispaly-name-unknown";
    @Getter @Setter private String _dni;
    @Getter @Setter private String _home;
    @Getter @Setter private String _mail;
    @Getter @Setter private Language _language;
    @Getter @Setter private String _ip;
    @Getter @Setter private Map<String,String> _attributes = null;		// Atributos del usuario (dni, login, puesto)

///////////////////////////////////////////////////////////////////////////////////////////
//  GET & SET
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Devuelve un atributo del usuario (dni, login, puesto, etc)
     * @param attrName: El nombre del atributo
     * @return: El atributo (String)
     */
    public String getAttribute(final String attrName) {
        if (attrName == null) return null;
        return CollectionUtils.hasData(_attributes) ? _attributes.get(attrName)
        											: null;
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  VALIDEZ
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Comprueba si el objeto es valido
     * @return: true si el objeto es valido y false si no es asín
     */
    public boolean isValid() {
        if (Strings.isNullOrEmpty(_login)) return false;
        if (Strings.isNullOrEmpty(_oid)) return false;
        return true;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString() {
		StringBuilder dbg = new StringBuilder();
		if (Strings.isNOTNullOrEmpty(_dni)) 	dbg.append("DNI: ").append(_dni).append("\n");
		if (Strings.isNOTNullOrEmpty(_name)) 	        dbg.append("    name: ").append(_name).append("\n");
		if (Strings.isNOTNullOrEmpty(_surname)) 	    dbg.append(" surname: ").append(_surname).append("\n");
		if (Strings.isNOTNullOrEmpty(_displayName) )dbg.append(" displayName: ").append(_displayName).append("\n");
		if (Strings.isNOTNullOrEmpty(_mail)) 	    dbg.append("        mail: ").append(_mail).append("\n");
		if (Strings.isNOTNullOrEmpty(_login)) 	    dbg.append("       Login: ").append(_login).append("\n");
		if (Strings.isNOTNullOrEmpty(_persona)	) 	dbg.append("     Persona: ").append(_persona).append("\n");
		if (Strings.isNOTNullOrEmpty(_puesto)) 		dbg.append("      Puesto: ").append(_puesto).append("\n");
		if (_language != null) 						dbg.append("      Idioma: ").append(_language).append("\n");
		if (Strings.isNOTNullOrEmpty(_ip)) 			dbg.append("          IP: ").append(_ip).append("\n");
													dbg.append("   Login App: ").append(_loginApp).append("\n");
		if (Strings.isNOTNullOrEmpty(_home)) 		dbg.append("        Home: ").append(_home).append("\n");
	    return dbg.toString();
	}
}
