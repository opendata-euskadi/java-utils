package r01f.services.client.api.delegates;

import r01f.securitycontext.HasSecurityContext;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ServiceProxiesAggregator;

public abstract class ClientAPIDelegateBase<P extends ServiceProxiesAggregator> 
		   implements HasSecurityContext {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The user context
	 */
	protected final SecurityContext _securityContext;
	/**
	 * a type that aggregates fine-grained proxies 
	 */
	protected final P _serviceProxiesAggregator;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected ClientAPIDelegateBase(final SecurityContext securityContext,
								 	final P servicesProxy) {
		_securityContext = securityContext;
		_serviceProxiesAggregator = servicesProxy;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <U extends SecurityContext> U getSecurityContext() {
		return (U)_securityContext;
	}
	public P getServiceProxiesAggregator() {
		return _serviceProxiesAggregator;
	}
}
