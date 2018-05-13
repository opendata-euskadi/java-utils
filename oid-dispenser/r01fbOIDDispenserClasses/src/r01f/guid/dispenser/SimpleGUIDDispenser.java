package r01f.guid.dispenser;

import java.security.SecureRandom;


/**
 * OID (guid) generator
 * The OIDs to be generated config is at a properties file (see {@link GUIDDispenserDef})
 */
public class SimpleGUIDDispenser 
  implements GUIDDispenser {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static String LETTERS = "0123456789abcdefghijklmnopqrstuvxyz";
	
///////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
    private final GUIDDispenserDef _dispDef;		// Dispenser definition
    
///////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & FACTORY
///////////////////////////////////////////////////////////////////////////////////////////
    public SimpleGUIDDispenser(final GUIDDispenserDef def) {
    	_dispDef = def;
    }
    public static SimpleGUIDDispenser create(final GUIDDispenserDef def) {
    	return new SimpleGUIDDispenser(def);
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  INTERFAZ GUIDDispenser
///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String generateGUID() {
        int guidLength = _dispDef.getLength();

        SecureRandom randomGen = new SecureRandom();
        
        // Get a random sequence built upon three parts: 
        //		- timeStamp
        //		- unique identifier (machine dependent)
        //		- a random
        long timeStampLong = new java.util.Date().getTime();		// TimeStamp	
        int objectHashCode = System.identityHashCode(this);			// HashCode
        long secureInt = randomGen.nextLong();						// Random
        String uniqueId = Long.toHexString(timeStampLong) + Integer.toHexString(objectHashCode) + Long.toHexString(secureInt);
        
        // Create an byte array with the size of the guid filled with 
        //		- random chars from the left 
        //		- the previous random sequence from the right
        char[] resultCharArray = new char[guidLength];
        // - left pad with random chars
        for (int i = 0; i < guidLength - uniqueId.length(); i++) resultCharArray[i] = LETTERS.charAt(randomGen.nextInt(LETTERS.length()));		
        // - the previously generated sequence inverted
        int cont = uniqueId.length() - 1;								
        for (int i = guidLength; i > 0; i--) {						
            if (cont >= 0) resultCharArray[i - 1] = uniqueId.charAt(cont);
            cont--;
        }
        return _dispDef.guidPrefix() + new String(resultCharArray);
    }
}
