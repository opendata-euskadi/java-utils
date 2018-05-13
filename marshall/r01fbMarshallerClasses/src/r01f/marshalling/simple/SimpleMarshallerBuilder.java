package r01f.marshalling.simple;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.marshalling.Marshaller;
import r01f.marshalling.Marshaller.MarshallerMappingsSearch;
import r01f.marshalling.MarshallerMappings;
import r01f.patterns.IsBuilder;

/**
 * Marshaller java objects <-> xml
 * The usual usage is:
 * 	<pre class='brush:java'>
 * 		// Get the marshaller instances
 * 		Marshaller marshaller = SimpleMarshaller.createForTypes(MyObj.class)
 * 												.getForSingleUse()
 * 		// Transforma a java object to xml string
 * 		MyObj myObjInstance = marshaller.beanFrom(xml);
 * 		// Transform a xml string into a java objects
 * 		String xml = marshaller.xmlFrom(myObjInstance);
 * </pre>
 */
public class SimpleMarshallerBuilder 
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER FOR SINGLE USE
/////////////////////////////////////////////////////////////////////////////////////////
	public static MarshallerTypeFactory create() {
		MarshallerMappings mappings = SimpleMarshallerMappings.createFrom(( Class<?>[])null);
		return new MarshallerTypeFactory(mappings);
	}
	public static MarshallerTypeFactory createForTypes(final Class<?>... annotatedTypes) {
		MarshallerMappings mappings = SimpleMarshallerMappings.createFrom(annotatedTypes);
		return new MarshallerTypeFactory(mappings);
	}
	public static MarshallerTypeFactory createForPackages(final String... packages) {
		MarshallerMappings mappings = new SimpleMarshallerMappings();
		mappings.loadFromAnnotatedTypes((Object[])MarshallerMappingsSearch.inPackages(packages));
		return new MarshallerTypeFactory(mappings);
	}
	public static MarshallerTypeFactory createForMappings(final File mapFile) {
		MarshallerMappings mappings = SimpleMarshallerMappings.createFrom(mapFile);
		return new MarshallerTypeFactory(mappings);
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static class MarshallerTypeFactory {
		private final MarshallerMappings _mappings;
		public Marshaller getForSingleUse() {
			return new SimpleMarshallerSingleUseImpl(_mappings);
		}
		public Marshaller getForMultipleUse() {
			return new SimpleMarshallerReusableImpl(_mappings);
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////
//  MULTIPLE USAGE
///////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Extemds {@link SimpleMarshallerBase} by making the {@link r01f.marshalling.MarshallerMappings}
	 * instance a singleton: reuses the java object <-> xml mappingss (cached)
	 */
	public static class SimpleMarshallerReusableImpl 
	            extends SimpleMarshallerBase {
		/**
		 * see the marshaller guice modules
		 * Guice injects a {@link MarshallerMappings} instance
		 * @param mappingss
		 */
		@Inject
		public SimpleMarshallerReusableImpl(@Named("SimpleMarshallerMappingsSINGLETON") final MarshallerMappings mappings) {
			_mappings = mappings;
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////
// 	SINGLE USAGE
///////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Extends {@link SimpleMarshallerBase} by using a NEW {@link r01f.marshalling.MarshallerMappings}
	 * instance: the java object <-> xml mappings are NOT reused (not cached) 
	 */
	public static class SimpleMarshallerSingleUseImpl 
	            extends SimpleMarshallerBase {         
		/**
		 * see the marshaller guice modules
		 * Guice injects a NEW {@link MarshallerMappings} instance
		 * @param mappingss
		 */
		@Inject
		public SimpleMarshallerSingleUseImpl(@Named("SimpleMarshallerMappingsnNEWINSTANCE") final MarshallerMappings mappings) {
			_mappings = mappings;
		}
	}

}
