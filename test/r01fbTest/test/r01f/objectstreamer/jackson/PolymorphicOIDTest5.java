package r01f.objectstreamer.jackson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
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
 * 
 * http://www.robinhowlett.com/blog/2015/03/19/custom-jackson-polymorphic-deserialization-without-type-metadata/
 */
public class PolymorphicOIDTest5 {
/////////////////////////////////////////////////////////////////////////////////////////
//	Use case: explicitly set the serializer / deserializer to be used annotating the
//			  type's field
//	Objective:
//		- Type's fields defined with an abstract / interface type should be ser / deser
//		  using a custom ser / deser that uses type info
//				public class MyBean {
//					@Getter @Setter private MyOID _abstractOid;	<-- field defined using an abstract / interface type
//				}													MUST include type info in serialized format
//		- Type's fields defined with a concrete type should be ser / deser as usual
//		  (do NOT include type info)
//				public class MyBean {
//					@Getter @Setter private MyOIDImpl _oidImpl;	<-- field defined using a concrete type
//				}													SHOULD NOT include type info in serialized format
// 	Solution:
//		Each type's field MUST be independently annotated
// 		If using a global ser / deser at a module:
// 				mod.addSerializer(new OIDSerializer(MyOID.class));
//				mod.addDeserializer(MyOID.class,
//									new OIDDeSerializer(MyOID.class));
//		each concrete field's serialization / deserialization cannot be controlled
//		(all MyOID-typed fields will be serialized using the SAME ser / deser)
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface MyOID {
		public String getId();
		public void setId(final String id);
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static abstract class MyOIDBase 
					  implements MyOID {
		@Getter @Setter private String _id;
	}
	@Accessors(prefix="_")
	public static class MyOIDImpl 
	  		    extends MyOIDBase {
		public MyOIDImpl() {
			// default constructor
		}
		public MyOIDImpl(final String oid) {
			super(oid);
		}
	}
	@Accessors(prefix="_")
	public static class MyOtherOIDImpl 
	  		    extends MyOIDBase {
		public MyOtherOIDImpl() {
			// default constructor
		}
		public MyOtherOIDImpl(final String oid) {
			super(oid);
		}
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class MyBean {
		// explicitly set the serializer / deserializer to be used
		@JsonSerialize(using=PolymorphicSerializer.class) @JsonDeserialize(using=OIDDesSerializer.class)		// <-- THIS IS MANDATORY since is an abstract type
		@Getter @Setter private MyOID _abstractOid;
		
		// use the default serializer / deserializer
		@Getter @Setter private MyOIDImpl _oidImpl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	public static class PolymorphicSerializer 
		        extends StdSerializer<Object> {
		private static final long serialVersionUID = -8388831283190643004L;
		
		public PolymorphicSerializer() {
			super(Object.class);
		}
		public PolymorphicSerializer(final Class<?> handledType) {
			this();
		}
	    @Override
	    public void serialize(final Object value,
	    					  final JsonGenerator jgen,
	    					  final SerializerProvider provider) throws IOException,
	    																JsonProcessingException {
	    	System.out.println("====>serialize: " + jgen.getOutputContext().getCurrentName());		// the field name
	    	jgen.writeStartObject();
	    	
	    	// [1] - include the type field
	    	jgen.writeStringField("type",value.getClass().getSimpleName());

	    	// [2] - Serialize the oid
	    	// Option 1: manually --> not very flexible
//			jgen.writeStringField("id",oid.getId());	        
	    	
	    	// Option2: delegate to the "normal" oid serializer
            JavaType javaType = provider.constructType(value.getClass());
            BeanDescription beanDesc = provider.getConfig().introspect(javaType);
            JsonSerializer<Object> delegatedSerializer = BeanSerializerFactory.instance.findBeanSerializer(provider,
                    																			  		   javaType,
                    																			  		   beanDesc);
            // delegate
            delegatedSerializer.unwrappingSerializer(null)
		            		   .serialize(value, 
		            				  	  jgen, 
		            				  	  provider);
	    	
	        jgen.writeEndObject();
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DESERIALIZER
// 	http://www.robinhowlett.com/blog/2015/03/19/custom-jackson-polymorphic-deserialization-without-type-metadata/
/////////////////////////////////////////////////////////////////////////////////////////	
	private abstract static class PolymorphicDeSerializerBase<T> 
	     				  extends StdDeserializer<T> {

		private static final long serialVersionUID = -5499594627107292692L;
		
		private static final String ID = "id";
	    private static final String TYPE = "type";
	    
		// the registry of unique field names to Class types
		private final Map<String,Class<? extends T>> _typeById;
	
		protected PolymorphicDeSerializerBase(final Class<?> handledType) {		
			super(handledType);
			_typeById = new HashMap<String,Class<? extends T>>();
		}
		public void registerType(final String type,Class<? extends T> clazz) {
			_typeById.put(type,clazz);
		}
	    @Override
	    public T deserialize(final JsonParser jp,
	    					 final DeserializationContext context) throws IOException {
	    	System.out.println("====>deserialize");

	        // [1] - read the 'type' token (MUST be the first token)
	        String type = null;	        
	        JsonToken typeToken = jp.nextToken();
	        if (typeToken == JsonToken.FIELD_NAME
	         && jp.getCurrentName().equals("type")) {
	        	type = jp.nextTextValue();
	        } else {
	        	throw new JsonParseException(jp,"'type' field expected",jp.getCurrentLocation());
	        }
	        
	        // get the concrete type 
	        Class<? extends T> clazz = _typeById.get(type);
	        JavaType javaType = context.constructType(clazz);
	        
	        // [2] - Deserialize the 'value'
	        JsonToken valueToken = jp.nextToken();			// value field
	        JsonToken startValueToken = jp.nextToken();		// start object or directly the value
	        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
	        T outObj = mapper.readValue(jp,javaType);
	        
	        return outObj;
	    }
	}
	public static class OIDDesSerializer
				extends PolymorphicDeSerializerBase<MyOID> {

		private static final long serialVersionUID = -4769193701695392587L;
		
		protected OIDDesSerializer() {
			super(MyOID.class);
			
			// register the types
			this.registerType(MyOIDImpl.class.getSimpleName(),MyOIDImpl.class);
			this.registerType(MyOtherOIDImpl.class.getSimpleName(),MyOtherOIDImpl.class);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE OBJECT MAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	private ObjectMapper _createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		
		// use only field annotations
		mapper.setVisibility(PropertyAccessor.ALL, 		Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
		
		SimpleModule mod = new SimpleModule("MyModule");
		mapper.registerModule(mod);

		return mapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TEST
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testPolimorphicOIDSerialize() {
		System.out.println("[INIT][SERIALIZER TEST------------------------------]");
		try {
			ObjectMapper objectMapper = _createObjectMapper();
			
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
			
			String absOidStr = "{" +
									"\"type\":\"MyOIDImpl\"" + "," +
									"\"value\":{\"_id\":\"this_is_an_abstract_oid\"}" + 
								"}";
			String implOidStr = "{" +
									"\"_id\":\"this_is_an_oid_impl\"" +
								"}";
			String json = "{" +
								"\"_abstractOid\":" + absOidStr + "," +
								"\"_oidImpl\":" + implOidStr +
						 "}";
			System.out.println(">>> " + json);			
			
			MyBean bean = objectMapper.readValue(json, 
											   	 MyBean.class);
			System.out.println(bean.getClass() + " > " + bean.getAbstractOid().getId());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][DESERIALIZER TEST------------------------------]");
	}
}
