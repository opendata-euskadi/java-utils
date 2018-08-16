package r01f.services.dokusi;

import javax.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DOKUSIServiceGuiceModule 
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final DOKUSIServiceAPIData _apiData;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// bind the config
		binder.bind(DOKUSIServiceAPIData.class)
			  .toInstance(_apiData);
	
		// bind the service
		binder.bind(DOKUSIService.class)
			  .in(Singleton.class);
	}
}
