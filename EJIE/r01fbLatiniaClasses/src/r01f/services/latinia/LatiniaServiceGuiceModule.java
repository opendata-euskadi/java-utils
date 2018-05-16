package r01f.services.latinia;

import javax.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import lombok.RequiredArgsConstructor;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;

@RequiredArgsConstructor
public class LatiniaServiceGuiceModule 
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
// 	The latinia properties can be located at any XMLProperties file (the <latinia>...</latinia>
// 	can be in any component XML file with other properties (there does NOT exists an exclusive
// 	XMLProperties file for latinia, the latinia config section <latinia>...</latinia> is embeded
// 	in any other XMLProperties file)
//
// 	BUT the latinia service provider (see below) expect a XMLProperties component
// 	named 'latinia' so this component MUST be created here
/////////////////////////////////////////////////////////////////////////////////////////
	private final LatiniaConfig _cfg;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// Bind a latinia objects marshaller instance 
		binder.bind(Marshaller.class)
			  .annotatedWith(Names.named("latiniaObjsMarshaller"))
			  .toInstance(MarshallerBuilder.build());
		// Bind the latinia service provider
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Provides a {@link LatiniaService} implementation
	 * @param props
	 * @return
	 */
	@Provides @Singleton	// beware the service is a singleton
	LatiniaService _provideLatiniaService() {
		// Provide a new latinia service api data using the provider
		LatiniaServiceApiDataProvider latiniaApiServiceProvider = new LatiniaServiceApiDataProvider(_cfg);
		
		// Using the latinia service api data create the LatiniaService object
		LatiniaService outLatiniaService = new LatiniaService(latiniaApiServiceProvider.get());
		return outLatiniaService;
	}
}
