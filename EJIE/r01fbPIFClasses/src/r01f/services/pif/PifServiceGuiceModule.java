package r01f.services.pif;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PifServiceGuiceModule 
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final PifServiceAPIData _pifServiceApiData;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// bind the config
		binder.bind(PifServiceAPIData.class)
			  .toInstance(_pifServiceApiData);
	
		// bind the service
		binder.bind(PifService.class)
			  .in(Singleton.class);
	}
}
