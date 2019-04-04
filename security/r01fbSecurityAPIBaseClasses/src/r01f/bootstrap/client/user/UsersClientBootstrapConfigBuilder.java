package r01f.bootstrap.client.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.api.interfaces.user.SecurityServiceInterface;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfig;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfigBuilder;
import r01f.client.api.security.UsersAPI;
import r01f.patterns.IsBuilder;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class UsersClientBootstrapConfigBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServicesClientBootstrapConfig buildClientBootstrapConfig(final ClientApiAppCode appCode) {

		return ServicesClientBootstrapConfigBuilder.forClientApiAppCode(appCode)
							  .exposingApi(UsersAPI.class)
							  .ofServiceInterfacesExtending(SecurityServiceInterface.class)
							  //.withProxiesToCoreImplAggregatedAt(UsersProxiesAggregator.class)
							  .bootstrappedWith(UsersClientBootstrapGuiceModule.class)
						  .build();
	}
}
