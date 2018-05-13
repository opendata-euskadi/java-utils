package r01f.marshalling;


import r01f.marshalling.annotations.ReusableJaxbMarshaller;
import r01f.marshalling.annotations.ReusableSimpleMarshaller;
import r01f.marshalling.annotations.SingleUseJaxbMarshaller;
import r01f.marshalling.annotations.SingleUseSimpleMarshaller;
import r01f.marshalling.jaxb.JAXBMarshallerReusableImpl;
import r01f.marshalling.jaxb.JAXBMarshallerSingleUseImpl;
import r01f.marshalling.simple.SimpleMarshallerMappings;
import r01f.marshalling.simple.SimpleMarshallerBuilder.SimpleMarshallerReusableImpl;
import r01f.marshalling.simple.SimpleMarshallerBuilder.SimpleMarshallerSingleUseImpl;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class MarsallerGuiceModule 
  implements Module {
	
	@Override
	public void configure(final Binder binder) {
		// Create TWO MarshallerMappingsLoader bindings
		// ----------------------------------------------------------------------------------------
		// 1.- SINGLETON to be used to marshall / unmarshall once and again a certain branch of objects
		binder.bind(MarshallerMappings.class).annotatedWith(Names.named("SimpleMarshallerMappingsSINGLETON"))
											 .to(SimpleMarshallerMappings.class)
											 .in(Singleton.class);		// <-- importante!!!
		
		// 2.- A single-use marshaller instance (created on the fly) to do a single marshall/unmarshall operation 
		//	   that will not be repeated
		binder.bind(MarshallerMappings.class).annotatedWith(Names.named("SimpleMarshallerMappingsnNEWINSTANCE"))
											 .to(SimpleMarshallerMappings.class);	// <-- NO es singleton!!!
		
		
		// Bind
		// ---------------------------------------------------------------------------------------
		binder.bind(Marshaller.class).annotatedWith(ReusableSimpleMarshaller.class)
									 .to(SimpleMarshallerReusableImpl.class);
		binder.bind(Marshaller.class).annotatedWith(SingleUseSimpleMarshaller.class)
									 .to(SimpleMarshallerSingleUseImpl.class);
		binder.bind(Marshaller.class).annotatedWith(ReusableJaxbMarshaller.class)
									 .to(JAXBMarshallerReusableImpl.class);
		binder.bind(Marshaller.class).annotatedWith(SingleUseJaxbMarshaller.class)
									 .to(JAXBMarshallerSingleUseImpl.class);
			
	}
}
