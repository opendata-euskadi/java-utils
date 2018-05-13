package r01f.bootstrap.services.config.core;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServicesClientProxyForCoreBeanExposed
  implements ServicesClientProxyToCoreServices {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return "";
	}
}
