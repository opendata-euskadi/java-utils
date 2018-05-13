package r01f.services.client;

import javax.inject.Provider;

import r01f.model.API;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;


/**
 * Client API 
 */
public interface ClientAPI
		 extends API {
	/**
	 * @return the model objects marshaller
	 */
	public Marshaller getModelObjectsMarshaller();
	/**
	 * @return the user context
	 */
	public <U extends SecurityContext> U getSecurityContext();
	/**
	 * @return the user context provider
	 */
	public Provider<SecurityContext> getSecurityContextProvider();
	/**
	 * @return an aggregator of proxies for the services real services impl
	 */
	public <S extends ServiceProxiesAggregator> S getServiceProxiesAggregator();
	/**
	 * @param aggregatorType
	 * @return an aggregator of proxies for the services real services impl
	 */
	public <S extends ServiceProxiesAggregator> S getServiceProxiesAggregatorAs(Class<S> aggregatorType);
	/**
	 * Returns the {@link ClientAPI} typed
	 * @param type
	 * @return
	 */
	public <A extends ClientAPI> A as(final Class<A> type);
}