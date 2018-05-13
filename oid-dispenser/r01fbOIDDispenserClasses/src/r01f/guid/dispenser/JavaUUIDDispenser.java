package r01f.guid.dispenser;

import java.util.UUID;

public class JavaUUIDDispenser 
  implements GUIDDispenser {
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	public static JavaUUIDDispenser create() {
		return new JavaUUIDDispenser();
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String generateGUID() {
		UUID uuid = UUID.randomUUID();	
        return uuid.toString();
	}

}