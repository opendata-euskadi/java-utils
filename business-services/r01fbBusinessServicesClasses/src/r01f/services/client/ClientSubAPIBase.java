package r01f.services.client;

import javax.inject.Provider;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.model.ModelObject;
import r01f.objectstreamer.HasMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

/**
 * Base for every sub-api
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class ClientSubAPIBase<S extends ClientAPI,
									   P extends ServiceProxiesAggregator> 
		   implements HasMarshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//  STATUS (injected by constructor)
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * User context
	 */
	protected final Provider<SecurityContext>  _securityContextProvider;
	/**
	 * Marshaller
	 */
	protected final Marshaller _modelObjectsMarshaller;
	/**
	 * Reference to the client-apis
	 * it's normal that another sub-api must be used from a sub-api
	 */
	protected final S _clientAPIs;
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return other sub-apis
	 */
	public S getClientAPIs() {
		return _clientAPIs;
	}
	/**
	 * @return  an aggregator of proxies for the services real services impl
	 */
	public P getServicesProxiesAggregator() {
		P clientProxy = _clientAPIs.<P>getServiceProxiesAggregator();
		return clientProxy;
	}
	/**
	 * @return a provider of the security context
	 */
	public Provider<SecurityContext> getSecurityContextProvider() {
		return _securityContextProvider;
	}
	/**
	 * @return the provided security context
	 */
	@SuppressWarnings("unchecked")
	public <U extends SecurityContext> U getSecurityContext() {
		return (U)_securityContextProvider.get();
	}
	/**
	 * @return the {@link ModelObject}s {@link Marshaller}
	 */
	public Marshaller getModelObjectsMarshaller() {
		return _modelObjectsMarshaller;
	}
}
