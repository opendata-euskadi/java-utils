package r01f.services.shf;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignatureServiceGuiceModule 
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final SignatureServiceAPIData _signatureServiceApiData;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SignatureServiceGuiceModule(final SignatureServiceAPIData apiData) {
		_signatureServiceApiData = apiData;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// bind the config
		binder.bind(SignatureServiceAPIData.class)
			  .toInstance(_signatureServiceApiData);
	
		// bind the service
		if (_signatureServiceApiData.isMock()) {
			log.warn("Using a MOCK signature service. Change signature service properties to enable real SHF impl");
			binder.bind(SignatureService.class)
				  .to(SignatureServiceMockImpl.class)
				  .in(Singleton.class);
		} else {			
			binder.bind(SignatureService.class)
				  .to(SignatureServiceImpl.class)
				  .in(Singleton.class);
		}
	}
}
