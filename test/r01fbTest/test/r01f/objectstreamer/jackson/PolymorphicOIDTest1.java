package r01f.objectstreamer.jackson;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * http://wiki.fasterxml.com/JacksonPolymorphicDeserialization
 * http://sunilkumarpblog.blogspot.com.es/2015/12/javajson-polymorphic-serialization-de.html
 * http://www.baeldung.com/jackson-advanced-annotations
 * https://www.dilipkumarg.com/dynamic-polymorphic-type-handling-jackson/
 */
public class PolymorphicOIDTest1 {
/////////////////////////////////////////////////////////////////////////////////////////
//	Use Case: Serialize / deserialize a polymorphic type using a custom ser / deser
//			  registrered globally at a jackson module
//  Problems:
//		. When de-serializing, the type info to real type translation is hard-coded 
//		  (see Test2 & Test4 & Test 5)
//		- All polymorphic types (ie MyOID) are serialized the SAME way: the type info
//		  is always included even if it's NOT necessary since the type is known and hence,
//		  the type info is NOT needed
//				public class MyBean {
//					@Getter @Setter private MyOID _abstractOid;	<-- field defined using an abstract / interface type
//				}													MUST include type info in serialized format
//				public class MyBean {
//					@Getter @Setter private MyOIDImpl _oidImpl;	<-- field defined using a concrete type
//				}													the type info is NOT needed
//		  (see Test5)
/////////////////////////////////////////////////////////////////////////////////////////
//	@JsonDeserialize(using = OIDDeSerializer.class)											// alternatively configured at mapper module
	public static interface MyOID {
		public String getId(); 
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static abstract class MyOIDBase 
					  implements MyOID {
		@Getter @Setter private String _id;
	}
//	@JsonSerialize(using=OIDSerializer.class) @JsonDeserialize(as = MyOIDImpl.class)		// alternatively configured at mapper module
	@Accessors(prefix="_")
	public static class MyOIDImpl 
	  		    extends MyOIDBase {
		public MyOIDImpl(final String oid) {
			super(oid);
		}
	}
//	@JsonSerialize(using=OIDSerializer.class) @JsonDeserialize(as = MyOtherOIDImpl.class)	// alternatively configured at mapper module
	@Accessors(prefix="_")
	public static class MyOtherOIDImpl 
	  		    extends MyOIDBase {
		public MyOtherOIDImpl(final String oid) {
			super(oid);
		}
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class MyBean {
		@Getter @Setter private MyOID _abstractOid;
		
		@Getter @Setter private MyOIDImpl _oidImpl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	// Serialize oids as id:value
	// Jackson by default serializes OIDs like:
	// {
	// 		oid {
	// 			id : "xxxx"
	//      }
	// }
	// ... but it's more elegant to serialize as:
	// {
	// 		oid : "xxx"
	// }
	public static class OIDSerializer 
		        extends StdSerializer<MyOID> {
		private static final long serialVersionUID = -8388831283190643004L;
		
		public OIDSerializer(final Class<MyOID> handledType) {
			super(handledType);
		}
	    @Override
	    public void serialize(final MyOID oid,
	    					  final JsonGenerator jgen,
	    					  final SerializerProvider provider) throws IOException,
	    																JsonProcessingException {
	    	System.out.println("====>serialize");
	    	jgen.writeStartObject();
	    	
	    	jgen.writeStringField("id",oid.getId());
	    	jgen.writeStringField("type",oid.getClass().getSimpleName());
	        
	        jgen.writeEndObject();
	    }
		@Override
		public Class<MyOID> handledType() {
			return super.handledType();
		}
		@Override
		public boolean usesObjectId() {
			return super.usesObjectId();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////	
	public static class OIDDeSerializer 
	     		extends StdDeserializer<MyOID> {

		private static final long serialVersionUID = -5499594627107292692L;
		
		private static final String ID = "id";
	    private static final String TYPE = "type";
	
		protected OIDDeSerializer(final Class<?> handledType) {		
			super(handledType);
		}
	    
	    @Override
	    public MyOID deserialize(final JsonParser jp,
	    						 final DeserializationContext context) throws IOException {
	    	System.out.println("====>deserialize");
	    	
	        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
	        
	        String id = null;
	        String type = null;
	        
	        // impl 1
	        JsonToken currentToken = null;
	        while ((currentToken = jp.nextValue()) != null) {
                if (jp.getCurrentName().equals("id")) {
                    id = jp.getText();
                } else if (jp.getCurrentName().equals("type")) {
                	type = jp.getText();
                }
	        }

//	        // impl 2: BEWARE!!! reads all tree
//	        ObjectNode root = mapper.readTree(jp);		// BEWARE!!! reads all tree
//	        if (root.has(TYPE)) {
//	            JsonNode typeNode = root.get(TYPE);
//	            type = typeNode.asText();
//	            
//	            JsonNode idNode = root.get(ID);
//	            id = idNode.asText();
//	        }
	        
	        // Build the id
	        // **************************************************************************
	        // PROBLEM: the type info to real type translation is hard-coded 
	        //		Test2 & Test4: the sub-types are registered during initialization
	        //					   or using annotations
	        //		Test5 solves this problem using a Map at the deserializer that 
	        //			  correlates the typeId with the concrete type
	        //			  this Map is initialized when creating the deserializer
	        // **************************************************************************
            if (type.equals(MyOIDImpl.class.getSimpleName())) {
                return new MyOIDImpl(id);
            } else if (type.equals(MyOtherOIDImpl.class.getSimpleName())) {
                return new MyOtherOIDImpl(id);
            }
	        
	        throw context.mappingException("Failed to de-serialize oid, as type property not exist");
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OBJECT MAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	private ObjectMapper _createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		
		SimpleModule mod = new SimpleModule("MyModule");		
		mod.addSerializer(new OIDSerializer(MyOID.class));
		mod.addDeserializer(MyOID.class,
							new OIDDeSerializer(MyOID.class));
		
		objectMapper.registerModule(mod);
		return objectMapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TEST
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testPolimorphicOIDSerialize() {
		System.out.println("[INIT][SERIALIZER TEST------------------------------]");
		try {
			ObjectMapper objectMapper = _createObjectMapper();
			
			// serialize an oid
			MyOID myOid = new MyOIDImpl("this_is_an_oid");						
			String oidStr = objectMapper.writeValueAsString(myOid);			
			System.out.println(oidStr);
			
			// serialize a bean
			MyBean bean = new MyBean(new MyOIDImpl("this_is_an_abstract_oid"),
									 new MyOIDImpl("this_is_an_oid_impl"));
			
			String beanStr = objectMapper.writeValueAsString(bean);
			System.out.println(beanStr);
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][SERIALIZER TEST------------------------------]");
	}
	@Test
	public void testPolimorphicOIDDeserialize() {
		System.out.println("[INIT][DESERIALIZER TEST------------------------------]");
		try {
			ObjectMapper objectMapper = _createObjectMapper();
			
			// deserialize an oid
			String oidStr = "{" +
								"\"id\":\"this_is_an_oid\"," +
								"\"type\":\"MyOIDImpl\"" +
							"}";
			System.out.println(">>> " + oidStr);			
			MyOID oid = objectMapper.readValue(oidStr, 
											 MyOID.class);
			
			System.out.println(oid.getClass());
			
			// deserialize a bean
			String absOidStr = "{" +
									"\"id\":\"this_is_an_abstract_oid\"" + "," +
									"\"type\":\"MyOIDImpl\"" +
								"}";
			String implOidStr = "{" +
									"\"id\":\"this_is_an_oid_impl\"" + "," +
									"\"type\":\"MyOIDImpl\"" +
								"}";
			String json = "{" +
								"\"abstractOid\":" + absOidStr + "," +
								"\"oidImpl\":" + implOidStr +
						 "}";
			System.out.println(">>> " + json);			
			
			MyBean bean = objectMapper.readValue(json, 
											   	 MyBean.class);
			System.out.println(bean.getClass());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][DESERIALIZER TEST------------------------------]");
	}
}
