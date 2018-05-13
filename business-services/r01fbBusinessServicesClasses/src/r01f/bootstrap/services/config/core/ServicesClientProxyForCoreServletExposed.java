package r01f.bootstrap.services.config.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.services.interfaces.ServiceProxyImpl;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServicesClientProxyForCoreServletExposed
  implements ServicesClientProxyToCoreServices {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Class<? extends ServiceProxyImpl> _serviceProxyImplsBaseType;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("Servlet core impl proxies extending {}",
								  _serviceProxyImplsBaseType);
	}
}
