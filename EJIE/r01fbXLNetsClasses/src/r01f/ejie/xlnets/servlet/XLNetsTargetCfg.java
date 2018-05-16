/*
 * Created on 09-jul-2004
 *
 * @author IE00165H
 * (c) 2004 EJIE: Eusko Jaurlaritzako Informatika Elkartea
 */
package r01f.ejie.xlnets.servlet;

import java.io.Serializable;
import java.util.Collection;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.locale.LanguageTexts;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Modela la configuracion de seguridad de una URI
 */
@Accessors(prefix="_")
public class XLNetsTargetCfg 
  implements Serializable {
	
    private static final long serialVersionUID = -3619681298463900886L;
///////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTES
///////////////////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    @RequiredArgsConstructor
    public enum ResourceAccess 
     implements EnumWithCode<String,ResourceAccess> {
    	RESTRICT("restrict"),
    	ALLOW("allow");
    	
    	@Getter private final String _code;
    	@Getter private final Class<String> _codeType = String.class;
    	
    	private static EnumWithCodeWrapper<String,ResourceAccess> _enums = EnumWithCodeWrapper.wrapEnumWithCode(ResourceAccess.class);
		@Override
		public boolean isIn(final ResourceAccess... els) {
			return _enums.isIn(this,els);
		}
		@Override
		public boolean is(final ResourceAccess el) {
			return _enums.is(this,el);
		}
		public static ResourceAccess fromName(final String name) {
			return _enums.fromName(name);
		}
		public static ResourceAccess fromCode(final String code) {
			return _enums.fromCode(code);
		}
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  MIEMBROS
///////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private Pattern _urlPathPattern = null;					// Expresion regular que machea el path de la url a la que se aplica la seguridad
    @Getter @Setter private ResourceAccess _kind = ResourceAccess.RESTRICT;	// Tipo restrictivo por defecto
    @Getter @Setter private Collection<ResourceCfg> _resources = null;		// Elementos de los que hay que verificar la seguridad

///////////////////////////////////////////////////////////////////////////////////////////
//  GET & SET
///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_kind).append(" URLPath pattern=").append(_urlPathPattern);
        if (CollectionUtils.hasData(_resources)) {
            for (ResourceCfg resCfg : _resources) {
                sb.append("\n\t").append(resCfg.toString());
            }
        }
        return sb.toString();
    }

/////////////////////////////////////////////////////////////////////////////////////////
//  INNER CLASS QUE REPRESENTA UN ITEM DEL RECURSO
/////////////////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    @RequiredArgsConstructor
    public enum ResourceItemType 
     implements EnumWithCode<String,ResourceItemType> {
    	FUNCTION("function"),
    	OBJECT("object");
    	
    	@Getter private final String _code;
    	@Getter private final Class<String> _codeType = String.class;
    	
    	private static EnumWithCodeWrapper<String,ResourceItemType> _enums = EnumWithCodeWrapper.wrapEnumWithCode(ResourceItemType.class);
		@Override
		public boolean isIn(final ResourceItemType... els) {
			return _enums.isIn(this,els);
		}
		@Override
		public boolean is(final ResourceItemType el) {
			return _enums.is(this,el);
		}
		public static ResourceItemType fromName(final String name) {
			return _enums.fromName(name);
		}
		public static ResourceItemType fromCode(final String code) {
			return _enums.fromCode(code);
		}
    }
    /**
     * Elemento de configuracion de seguridad de un recurso
     */
    @Accessors(prefix="_")
    @RequiredArgsConstructor
    public class ResourceCfg 
      implements Serializable {
        private static final long serialVersionUID = -5044952456280782506L;
        
        @Getter private final String _oid;
        @Getter private final ResourceItemType _type;		// function / object
        @Getter private final boolean _mandatory;
        @Getter private final LanguageTexts _name;
        
        @Override
        public String toString() {
            return Strings.customized("{} {} {}: {}",
            					      (_mandatory ? " mandatory":""),_type,_oid,_name);
        }
    }
}
