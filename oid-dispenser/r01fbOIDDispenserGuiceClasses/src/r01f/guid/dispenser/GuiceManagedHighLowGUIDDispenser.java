package r01f.guid.dispenser;

import java.util.Map;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;

import r01f.guid.dispenser.GUIDDispenserDef;
import r01f.guid.dispenser.HighLowGUIDDispenser;
import r01f.guid.dispenser.HighLowGUIDPersist;

public class GuiceManagedHighLowGUIDDispenser
	 extends HighLowGUIDDispenser {
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFAZ GUIDDispenserFactory utilizado en Guice AssistedInject para permitir
//  crear objetos GUIDDispenser en base a una definici�n GUIDDispenserDef que solo
//  se conoce en tiempo de ejecuci�n (ver documentaci�n de GUIDDispenserManagerGuiceModule)
//	Ver GUIDDispenserGuiceModule!!!
/////////////////////////////////////////////////////////////////////////////////////////
    static interface HighLowGUIDDispenserFactory 
             extends GUIDDispenserFlavourFactory {
    	/* empty */
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORES
/////////////////////////////////////////////////////////////////////////////////////////
    @Inject
    public GuiceManagedHighLowGUIDDispenser(@Assisted final GUIDDispenserDef dispDef,
    									  			  final Map<String,HighLowGUIDPersist> highLowGUIDPersistFactories) {
    	super(dispDef,
    		  highLowGUIDPersistFactories);
    }
}
