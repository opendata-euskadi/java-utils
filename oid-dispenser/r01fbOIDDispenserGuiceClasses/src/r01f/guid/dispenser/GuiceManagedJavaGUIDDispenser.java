package r01f.guid.dispenser;

import javax.inject.Inject;

import r01f.guid.dispenser.JavaUUIDDispenser;

public class GuiceManagedJavaGUIDDispenser 
	 extends JavaUUIDDispenser {
///////////////////////////////////////////////////////////////////////////////////////////
//  INTERFACE GUIDDispenserFactory used by Guice AssistedInject to create GUIDDispenser  
//  objects using a GUIDDispenserDef definition that's only known at runtime
//  (see GUIDDispenserGuiceModule)
///////////////////////////////////////////////////////////////////////////////////////////
    static interface JavaGUIDDispenserFactory 
             extends GUIDDispenserFlavourFactory {
    	/* empty */
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & FACTORY
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor used by Guice AssistedInject to inject the definition
     */
    @Inject
    public GuiceManagedJavaGUIDDispenser() {
    	super();
    }
}
