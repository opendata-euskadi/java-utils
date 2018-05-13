package r01f.bootstrap.services.config;

import java.lang.reflect.Field;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.bootstrap.services.client.ServiceInterfaceTypesToImplOrProxyMappings;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.ServicesImpl;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfig;
import r01f.bootstrap.services.config.client.ServicesClientGuiceBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.patterns.IsBuilder;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.ReflectionUtils.FieldAnnotated;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


/**
 * Builder for ServicesConfig
 * Usage: 
 * <pre class='brush:java'>
 * 
 * </pre>
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class ServicesBootstrapConfigBuilder 
	       implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILD 
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServicesBootstrapConfigCoreModulesStep forClient(final ServicesClientBootstrapConfig clientCfg) {
		return new ServicesBootstrapConfigBuilder() { /* nothing */ }
						.new ServicesBootstrapConfigCoreModulesStep(clientCfg);
	}
	public static ServicesBootstrapConfigCoreModulesStep noClient() {
		return new ServicesBootstrapConfigBuilder() { /* nothing */ }
						.new ServicesBootstrapConfigCoreModulesStep(null);	// no client
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class ServicesBootstrapConfigCoreModulesStep {
		private final ServicesClientBootstrapConfig _clientCfg;
		
		public ServicesBootstrapConfig ofCoreModules(final ServicesCoreBootstrapConfig<?,?>... coreModsCfg) {
			ServicesBootstrapConfig outCfg = new ServicesBootstrapConfig(_clientCfg,
																		 Lists.newArrayList(coreModsCfg));
			if (outCfg.getClientConfig() != null) _checkMapOfServiceInterfacesToProxyOrImpl(outCfg);
			return outCfg;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private void _checkMapOfServiceInterfacesToProxyOrImpl(final ServicesBootstrapConfig cfg) {
		Class<? extends ServiceInterfaceTypesToImplOrProxyMappings> srvcIfaceToProxyOrImplMapType = cfg.getClientConfigAs(ServicesClientGuiceBootstrapConfig.class)
																									   .getServiceInterfaceTypesToImplOrProxyMappingsType();
		FieldAnnotated<? extends com.google.inject.name.Named>[] gNamedFields = ReflectionUtils.fieldsAnnotated(srvcIfaceToProxyOrImplMapType,
																											    com.google.inject.name.Named.class);
		FieldAnnotated<? extends javax.inject.Named>[] jxNamedFields = ReflectionUtils.fieldsAnnotated(srvcIfaceToProxyOrImplMapType,
																									   javax.inject.Named.class);
		for (ServicesCoreBootstrapConfig<?,?> coreModCfg : cfg.getCoreModulesConfig()) {
			if (coreModCfg.getImplType().isIn(ServicesImpl.REST,ServicesImpl.Servlet)) continue;	// ignore servlet or rest cores
			
			String key = Strings.customized("{}.{}",
											coreModCfg.getCoreAppCode(),coreModCfg.getCoreModule());
			boolean found = false;
			if (!found && CollectionUtils.hasData(gNamedFields)) {
				for (FieldAnnotated<? extends com.google.inject.name.Named> gNamedField : gNamedFields) {
					com.google.inject.name.Named named = (com.google.inject.name.Named)gNamedField.getAnnotation();
					if (named.value().equals(key)
					 && _isFieldInjectAnnotated(gNamedField.getField())) {
						found = true;
						break;
					}
				}
			}
			if (!found && CollectionUtils.hasData(jxNamedFields)) {
				for (FieldAnnotated<? extends javax.inject.Named> jxNamedField : jxNamedFields) {
					javax.inject.Named named = (javax.inject.Named)jxNamedField.getAnnotation();
					if (named.value().equals(key)
					 && _isFieldInjectAnnotated(jxNamedField.getField())) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				throw new IllegalStateException("Service Interface to proxy or impl type " + srvcIfaceToProxyOrImplMapType + " " +
												"MUST contain an injected and @Named(" + key + ")-annotated Map<Class,ServiceInterface> field!");
			}
		}
	}
	private static boolean _isFieldInjectAnnotated(final Field f) {
		return f.getAnnotation(javax.inject.Inject.class) != null
			|| f.getAnnotation(com.google.inject.Inject.class) != null;
	}
}
