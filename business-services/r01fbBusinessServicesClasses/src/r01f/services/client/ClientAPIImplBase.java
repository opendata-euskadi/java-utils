package r01f.services.client;

import javax.inject.Provider;

import lombok.experimental.Accessors;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;



/**
 * Base type for every API implementation 
 */
@Accessors(prefix="_")
public abstract class ClientAPIImplBase<S extends ServiceProxiesAggregator> 
           implements ClientAPI {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR INJECTED
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Model objects marshaller
	 */
	protected final Marshaller _modelObjectsMarshaller;
	/**
	 * securityContext 
	 */
	protected final Provider<SecurityContext> _securityContextProvider;
	/**
	 * Service proxies aggregator 
	 */
	protected final S _serviceProxiesAggregator;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
//	public ClientAPIImplBase(final securityContext securityContext,
//						 	 final S servicesProxiesAggregator) {
//		this(securityContext,
//			 null,
//			 servicesProxiesAggregator);
//	}	
	public ClientAPIImplBase(final Provider<SecurityContext> securityContextProvider,
							 final Marshaller modelObjectsMarshaller,
						 	 final S servicesProxiesAggregator) {
		_securityContextProvider = securityContextProvider;
		_modelObjectsMarshaller = modelObjectsMarshaller;
		_serviceProxiesAggregator = servicesProxiesAggregator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Marshaller getModelObjectsMarshaller() {
		return _modelObjectsMarshaller;
	}
	@Override
	public Provider<SecurityContext> getSecurityContextProvider() {
		return _securityContextProvider;
	}
	@Override @SuppressWarnings("unchecked")
	public <U extends SecurityContext> U getSecurityContext() {
		return (U)_securityContextProvider.get();
	}
	@Override @SuppressWarnings("unchecked")
	public <T extends ServiceProxiesAggregator> T getServiceProxiesAggregator() {
		return (T)_serviceProxiesAggregator;
	}
	@Override @SuppressWarnings("unchecked")
	public <T extends ServiceProxiesAggregator> T getServiceProxiesAggregatorAs(final Class<T> aggregatorType) {
		return (T)_serviceProxiesAggregator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <A extends ClientAPI> A as(final Class<A> type) {
		return (A)this;
	}
}