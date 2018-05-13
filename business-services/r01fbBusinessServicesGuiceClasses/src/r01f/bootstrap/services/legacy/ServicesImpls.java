package r01f.bootstrap.services.legacy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenRESTExposed;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenServletExposed;
import r01f.bootstrap.services.config.core.ServicesCoreGuiceBootstrapConfig;
import r01f.bootstrap.services.core.BeanImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.RESTImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.services.core.ServicesCoreBootstrapGuiceModule;
import r01f.bootstrap.services.core.ServletImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.exceptions.Throwables;
import r01f.reflection.ReflectionUtils;
import r01f.services.interfaces.ProxyForEJBImplementedService;
import r01f.services.interfaces.ProxyForMockImplementedService;
import r01f.services.interfaces.ProxyForRESTImplementedService;
import r01f.services.interfaces.ServiceProxyImpl;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServicesImpls {
/////////////////////////////////////////////////////////////////////////////////////////
//  CLIENT
/////////////////////////////////////////////////////////////////////////////////////////
	public static Class<? extends ServiceProxyImpl> getServiceProxyTypeFrom(final ServicesImpl impl) {
		Class<? extends ServiceProxyImpl> outProxyType = null;
		switch(impl) {
		case Bean:
			break;
		case REST:
			outProxyType = ProxyForRESTImplementedService.class;
			break;
		case EJB:
			outProxyType = ProxyForEJBImplementedService.class;	
			break;
		case Servlet:
			throw new UnsupportedOperationException(Throwables.message("{} is NOT a full-fledged service since it's NOT consumed using a client api; it's called from a web browser so it has NO associated client-proxy",
																	   ServicesImpl.Servlet));
		case Mock:
			outProxyType = ProxyForMockImplementedService.class;
			break;
		case Default:
		default:
			throw new IllegalStateException(Throwables.message("NO {} for {}",
															   ServiceProxyImpl.class,impl.getClass()));
		}
		return outProxyType;
	}
	public static ServicesImpl fromServiceProxyType(final Class<? extends ServiceProxyImpl> type) {
		ServicesImpl outImpl = null;
		if (ReflectionUtils.isSubClassOf(type,ProxyForRESTImplementedService.class)) {
			outImpl = ServicesImpl.REST;
		} else if (ReflectionUtils.isSubClassOf(type,ProxyForEJBImplementedService.class)) {
			outImpl = ServicesImpl.EJB;
		} else {
			throw new IllegalStateException(Throwables.message("The {} implementation {} is NOT of one of the supported types {}",
															   ServiceProxyImpl.class,type,ServicesImpl.values()));
		}
		return outImpl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	CORE 
/////////////////////////////////////////////////////////////////////////////////////////
	public static Class<? extends ServicesCoreBootstrapGuiceModule> getCoreGuiceModuleTypeFrom(final ServicesImpl impl) {
		Class<? extends ServicesCoreBootstrapGuiceModule> outCoreGuiceModule = null;
		switch(impl) {
		case Bean:
			outCoreGuiceModule = BeanImplementedServicesCoreBootstrapGuiceModuleBase.class;
			break;
		case REST:
			outCoreGuiceModule = RESTImplementedServicesCoreBootstrapGuiceModuleBase.class;
			break;
		case Servlet:
			outCoreGuiceModule = ServletImplementedServicesCoreBootstrapGuiceModuleBase.class;
			break;
		case Mock:
		case Default:
		default:
			throw new IllegalStateException();
		}
		return outCoreGuiceModule;
	}
	/**
	 * Gets the {@link ServicesImpls} from a type extending {@link ServicesCoreBootstrapGuiceModule}
	 * <pre class='brush:java'>
	 * 		public class MyBindingModule
	 * 		 	 extends BeanImplementedServicesGuiceBindingModule {
	 * 			...
	 * 		}
	 * </pre>
	 * @param type
	 * @return
	 */
	public static ServicesImpl fromBindingModule(final Class<? extends ServicesCoreBootstrapGuiceModule> type) {
		ServicesImpl outImpl = null;
		if (ReflectionUtils.isSubClassOf(type,BeanImplementedServicesCoreBootstrapGuiceModuleBase.class)) {
			outImpl = ServicesImpl.Bean;
		} else if (ReflectionUtils.isSubClassOf(type,RESTImplementedServicesCoreBootstrapGuiceModuleBase.class)) {
			outImpl = ServicesImpl.REST;
		} else if (ReflectionUtils.isSubClassOf(type,ServletImplementedServicesCoreBootstrapGuiceModuleBase.class)) {
			outImpl = ServicesImpl.Servlet;
		} else {
			throw new IllegalStateException(Throwables.message("The {} implementation {} is NOT of one of the supported types {}",
															   ServicesCoreBootstrapGuiceModule.class,type,ServicesImpl.values()));
		}
		return outImpl;
	}	
	public static ServicesImpl fromCoreBootstrapConfig(final ServicesCoreGuiceBootstrapConfig<?,?> cfg) {
		ServicesImpl outImpl = null;
		if (cfg instanceof ServicesCoreBootstrapConfigWhenBeanExposed) {
			outImpl = ServicesImpl.Bean;
		} else if (cfg instanceof ServicesCoreBootstrapConfigWhenRESTExposed) {
			outImpl = ServicesImpl.REST;
		} else if (cfg instanceof ServicesCoreBootstrapConfigWhenServletExposed) {
			outImpl = ServicesImpl.Servlet;
		} else {
			throw new IllegalStateException(Throwables.message("The {} implementation {} is NOT of one of the supported types {}",
															   ServiceProxyImpl.class,cfg.getClass().getSimpleName(),ServicesImpl.values()));
		}
		return outImpl;
	}

}