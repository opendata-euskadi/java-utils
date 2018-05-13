package r01f.marshalling.json;

import com.google.gson.Gson;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * Guice Mapping for {@link GsonMarshaller}
 */
public class GsonMarshallerGuiceModule
  implements Module {

	@Override
	public void configure(final Binder binder) {
		binder.bind(Gson.class).toProvider(GSonProvider.class)
			  .in(Singleton.class);
	}
}
