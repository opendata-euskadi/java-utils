package r01f.persistence;

import r01f.model.persistence.PersistenceOperationError;
import r01f.model.persistence.PersistenceOperationOK;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.persistence.callback.PersistenceOperationCallbackBase;
import r01f.persistence.callback.spec.PersistenceOperationRESTCallbackSpec;
import r01f.securitycontext.SecurityContext;

public class PersistenceOperationCallbackTest {
	public static void main(final String[] args) {
		Marshaller m = MarshallerBuilder.build();
		
		PersistenceOperationRESTCallbackSpec restCBSpec = new PersistenceOperationRESTCallbackSpec();
		restCBSpec.setEndPointUrlTemplate("http://{site}:{port}/path/{oid}");
		
		String restCBSpecXml = m.forWriting().toXml(restCBSpec);
		System.out.println("XML:\n" + restCBSpecXml);
		PersistenceOperationRESTCallbackSpec restCBSpecFromXml = m.forReading().fromXml(restCBSpecXml,
																						PersistenceOperationRESTCallbackSpec.class);
		String restCBSpecJson = m.forWriting().toJson(restCBSpecFromXml);
		System.out.println("JSON:\n" + restCBSpecJson);
		
		
	}
	public static class MyPersistenceOperationCallback
		        extends PersistenceOperationCallbackBase {

		private static final long serialVersionUID = -547653115567354882L;

		@Override
		public void onPersistenceOperationOK(final SecurityContext securityContext,
											 final PersistenceOperationOK opOK) {
			
		}
		@Override
		public void onPersistenceOperationError(final SecurityContext securityContext,
												final PersistenceOperationError opError) {
			
		}
	}
}
