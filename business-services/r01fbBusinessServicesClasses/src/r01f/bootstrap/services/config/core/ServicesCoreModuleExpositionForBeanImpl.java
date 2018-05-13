package r01f.bootstrap.services.config.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.services.core.CoreService;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServicesCoreModuleExpositionForBeanImpl 
  implements ServicesCoreModuleExposition {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The packages where the service interfaces BEAN implementantions can be found
	 * BEWARE!
	 * org.reflections is used to scan subtypes of CoreService. This library requires
	 * ALL the packages in the type hierarchy to be given to the scan methods:
	 * <pre class='brush:java'>
	 * 		CoreService
	 * 			|-- interface 1
	 * 					|--  interface 2
	 * 							|-- all the core service impl
	 * </pre> 
	 * The packages where CoreService, interface 1 and interface 2 resides MUST be handed 
	 * to the subtypeOfScan method of org.reflections
	 */
	@Getter private final Class<? extends CoreService> _coreServicesBaseType;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ServicesImpl getServiceImpl() {
		return ServicesImpl.Bean;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("BEAN exposed: service interfaces extending {}",
								  _coreServicesBaseType);
	}
}
