package r01f.marshalling.json;

import r01f.patterns.IsBuilder;

public class GsonMarshallerBuilder 
  implements IsBuilder {
///////////////////////////////////////////////////////////////////////////////
//  BUILDER
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Builder
	 * <b>BEWARE!</b>
	 * It's advisable to inject the JSonMarshaller using GUICE since the created object
	 * is cached as a singleton
	 * @return el objeto {@link GsonMarshaller}
	 */
	public static GsonMarshaller create() {		
		GSonProvider gsonProvider = new GSonProvider();
		GsonMarshaller outMarshaller = new GsonMarshaller(gsonProvider.get());
		return outMarshaller;
	}

}
