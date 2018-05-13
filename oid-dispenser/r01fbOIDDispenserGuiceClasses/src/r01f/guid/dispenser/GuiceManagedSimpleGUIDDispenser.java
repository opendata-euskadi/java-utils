package r01f.guid.dispenser;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;

import r01f.guid.dispenser.GUIDDispenserDef;
import r01f.guid.dispenser.SimpleGUIDDispenser;

public class GuiceManagedSimpleGUIDDispenser 
	 extends SimpleGUIDDispenser {
///////////////////////////////////////////////////////////////////////////////////////////
//  INTERFACE GUIDDispenserFactory used by Guice AssistedInject to create GUIDDispenser  
//  objects using a GUIDDispenserDef definition that's only known at runtime
//  (see GUIDDispenserGuiceModule)
///////////////////////////////////////////////////////////////////////////////////////////
    static interface SimpleGUIDDispenserFactory 
             extends GUIDDispenserFlavourFactory {
    	/* empty */
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & FACTORY
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor used by Guice AssistedInject to inject the definition
     * @param def
     */
    @Inject
    public GuiceManagedSimpleGUIDDispenser(@Assisted final GUIDDispenserDef def) {
    	super(def);
    }
}
