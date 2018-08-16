package r01f.services.latinia;

import javax.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;

import lombok.RequiredArgsConstructor;

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
	private final LatiniaServiceAPIData _cfg;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// bind the config
		binder.bind(LatiniaServiceAPIData.class)
			  .toInstance(_cfg);
	
		// bind the service
		binder.bind(LatiniaService.class)
		 	  .in(Singleton.class);
	}
}
