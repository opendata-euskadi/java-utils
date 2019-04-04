package r01f.bootstrap.core.users;


import com.google.inject.Binder;

/**
 * Interface to be implemented by {@link BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase} subtypes that
 * are interested in binding CRUD event listeners
 */
public interface ServicesBootstrapGuiceModuleBindsSecurityEventListeners {

	/**
	 * Binds the indexers (instances of {@link IndexerCRUDOKEventListener})
	 * @param binder
	 */
	public void bindSecurityEventListeners(final Binder binder);

}
