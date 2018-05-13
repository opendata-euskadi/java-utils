package r01f.model.mock;

import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

public abstract class MyOIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="myTestOid")
//	@JsonSerialize(using=OIDSerializer.class) @JsonDeserialize(using=OIDDeserializer.class)
	public static class MyTestOID 
				extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -7707974037201898058L;
		
		public MyTestOID() {
			
		}
		public MyTestOID(final String oid) {
			super(oid);
		}
		public static MyTestOID valueOf(final String oid) {
			return new MyTestOID(oid);
		}
		public static MyTestOID forId(final String oid) {
			return new MyTestOID(oid);
		}
	}
	@MarshallType(as="myOtherTestOid")
//	@JsonSerialize(using=OIDSerializer.class) @JsonDeserialize(using=OIDDeserializer.class)
	public static class MyOtherTestOID 
				extends OIDBaseMutable<String> {
		private static final long serialVersionUID = 2580594432728786982L;

		public MyOtherTestOID() {
		
		}
		public MyOtherTestOID(final String oid) {
			super(oid);
		}
		public static MyOtherTestOID valueOf(final String oid) {
			return new MyOtherTestOID(oid);
		}
		public static MyOtherTestOID forId(final String oid) {
			return new MyOtherTestOID(oid);
		}
	}
}
