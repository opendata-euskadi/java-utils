
package r01f.ui.vaadin.i18n;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.vaadin.ui.UI;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.types.JavaPackage;
import r01f.ui.i18n.UII18NManagerBase;


@Slf4j
@Accessors(prefix="_")
public abstract class VaadinUII18NManager
              extends UII18NManagerBase {

    private static final long serialVersionUID = -4523459172009081309L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    @Getter List<String> _i18nBundleNames = new LinkedList<String>();
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
    public VaadinUII18NManager(final Collection<JavaPackage> pckgNames) {
        super(pckgNames);
    }
    public VaadinUII18NManager(final ClassLoader classLoader,
    						   final Collection<JavaPackage> pckgNames) {
        super(classLoader,
        	  pckgNames);
    }
/////////////////////////////////////////////////////////////////////////////////////////
// 	PUBLIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Locale getCurrentLocale() {
        Locale outLoc = UI.getCurrent().getLocale();
        if (outLoc == null) log.warn("NO current locale!! detault to {}",Locale.getDefault());
        return outLoc != null ? outLoc : Locale.getDefault();
    }
}
