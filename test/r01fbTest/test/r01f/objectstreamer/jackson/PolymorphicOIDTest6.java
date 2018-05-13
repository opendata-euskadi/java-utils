package r01f.objectstreamer.jackson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
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
public class PolymorphicOIDTest6 {
/////////////////////////////////////////////////////////////////////////////////////////
//	Use case: explicitly set the serializer / deserializer to be used annotating the
//			  type's field
//	Objective:
//		- Type's fields defined with an abstract / interface type should be ser / deser
//		  using a custom ser / deser that uses type info
//				public class MyBean {
//					@JsonSerialize(using=PolymorphicSerializer.class) @JsonProperty @JsonDeserialize(using=OIDDesSerializer.class)		// <-- THIS IS MANDATORY since is an abstract type
//					@Getter @Setter private MyOID _abstractOid;	<-- field defined using an abstract / interface type
//				}													MUST include type info in serialized format
//		- Type's fields defined with a concrete type should be ser / deser as usual
//		  (do NOT include type info)
//				public class MyBean {
//					@JsonProperty
//					@Getter @Setter private MyOIDImpl _oidImpl;	<-- field defined using a concrete type
//				}													SHOULD NOT include type info in serialized format
// 	Solution:
//		Each type's field MUST be independently annotated as described above
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
		public String asString();
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static abstract class MyOIDBase 
					  implements MyOID {
		@Getter @Setter private String _id;
		
		@JsonValue		// do NOT wrap the _id field as {_id=xxx}
		@Override
		public String asString() {
			return this.getId();
		}
	}
	@Accessors(prefix="_")
	public static class MyOIDImpl 
	  		    extends MyOIDBase {
		public MyOIDImpl() {
			// default constructor
		}
		@JsonCreator	// use this constructor to create from a string
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
		@JsonCreator
		public MyOtherOIDImpl(final String oid) {
			super(oid);
		}
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class MyBean {
		// explicitly set the serializer / deserializer to be used
		@JsonSerialize(using=PolymorphicSerializer.class) @JsonProperty @JsonDeserialize(using=OIDDesSerializer.class)		// <-- THIS IS MANDATORY since is an abstract type
		@Getter @Setter private MyOID _abstractOid;
		
		// use the default serializer / deserializer
		@JsonProperty 
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
	    	System.out.println("[serialize]: START > " + jgen.getOutputContext().getCurrentName());		// the field name
	    	
	    	jgen.writeStartObject();
	    	
	    	// [1] - include the type field
	    	jgen.writeStringField("typeId",value.getClass().getSimpleName());

	    	// [2] - Serialize the oid delegating to the "normal" oid serializer                       
            jgen.writeFieldName("value");
            
            JsonSerializer<Object> delegatedSerializer = null;
            
            JavaType javaType = provider.constructType(value.getClass());
            BeanDescription beanDesc = provider.getConfig().introspect(javaType);
//            delegatedSerializer = BeanSerializerFactory.instance.findBeanSerializer(provider,
//																		  		    javaType,
//																		  		    beanDesc);
            delegatedSerializer = provider.findValueSerializer(javaType);
            delegatedSerializer.unwrappingSerializer(null)
            				   .serialize(value,
						           		  jgen,
						            	  provider);
	    	
	        System.out.println("[serialize]: END");
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////	
	private abstract static class PolymorphicDeSerializerBase<T> 
	     				  extends StdDeserializer<T> 
					   implements ContextualDeserializer {

		private static final long serialVersionUID = -5499594627107292692L;
		
		// the registry of unique field names to Class types
		private final Map<String,Class<? extends T>> _typeById;
	
		protected PolymorphicDeSerializerBase(final Class<?> handledType) {		
			super(handledType);
			_typeById = new HashMap<String,Class<? extends T>>();
		}
		public void registerType(final String typeId,Class<? extends T> clazz) {
			_typeById.put(typeId,clazz);
		}
		@Override
		public JsonDeserializer<?> createContextual(final DeserializationContext ctxt,
													final BeanProperty property) throws JsonMappingException {
			// gets called for each property
			if (property != null) System.out.println(".... create contextual deserializer for: " + property.getName() + " > " + property.getType().isAbstract());
			return this;
		}
	    @Override
	    public T deserialize(final JsonParser jp,
	    					 final DeserializationContext context) throws IOException {
	    	System.out.println("[deserialize]: START");

	        // [1] - read the 'typeId' token (MUST be the first token)
	    	
	    	// see AsPropertyTypeDeserializer#deserializeTypedFromObject()
	    	// TODO see AsPropertyTypeDeserializer#deserializeTypedFromObject() to have edge cases into account
	    	
	        if (jp.getCurrentToken() != JsonToken.START_OBJECT) throw new IllegalArgumentException();
	        
	        jp.nextToken();		// start object skip
	        jp.nextToken();		// type property name
	        String propertyName = jp.getCurrentName();	        
            if (!propertyName.equals("typeId")) throw new IllegalArgumentException();                        	
        	String typeId = jp.getText();	            	
        	Class<?> type = _typeById.get(typeId);
        	JavaType javaType = context.constructType(type);
        	System.out.println("\t...got concrete type for subtype of " + _valueClass + " from typeId='" + typeId + "' > " + javaType.getGenericSignature());
        	
        	jp.nextToken();		// skip typeId
        	jp.nextToken();		// skip value property
        	
        	// [2] - Deserialize the value
        	// create a deserializer for the java type            	
        	JsonDeserializer<Object> deserializer = context.findRootValueDeserializer(javaType);	// BEWARE!!! the root deserializer... not the contextual one
            T outValue = (T)deserializer.deserialize(jp,
            				     			   		 context);
	    	System.out.println("[deserialize]: END");
	    	return outValue;
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
		
//		mapper.setPropertyNamingStrategy(new PropertyNamingStrategy() {
//												private static final long serialVersionUID = 8310912907941951750L;
//												public String nameForField(final MapperConfig<?> config,
//																		   final AnnotatedField field,
//																		   final String defaultName) {
//													String outName = defaultName;
//													if (defaultName.startsWith("_")) outName = defaultName.substring(1);
//													return outName;
//												}
//										 });
		// use only field annotations
		mapper.setVisibility(PropertyAccessor.ALL, 		Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
		
//		SimpleModule mod = new SimpleModule("MyModule");
//		mapper.registerModule(mod);

		return mapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TEST
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testPolimorphicOIDSerialize() {
		System.out.println("[INIT][SERIALIZER TEST------------------------------]");
		try {
			ObjectMapper mapper = _createObjectMapper();
			
			MyBean bean = new MyBean(new MyOIDImpl("this_is_an_abstract_oid"),
									 new MyOIDImpl("this_is_an_oid_impl"));
			
			String beanStr = mapper.writeValueAsString(bean);
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
			ObjectMapper mapper = _createObjectMapper();			
			
			String fieldPrefix = "_";
			String absOidStr = "{" +
									"\"typeId\":\"MyOIDImpl\"" + "," +
									"\"value\":\"this_is_an_abstract_oid\"" +
								"}";
			String implOidStr = "\"this_is_an_oid_impl\"";
			String json = "\t" + "{" + "\n" + 
						  "\t\t" +		"\"" + fieldPrefix + "oidImpl\":" + implOidStr + "," + "\n" + 
						  "\t\t" +		"\"" + fieldPrefix + "abstractOid\":" + absOidStr + "\n" + 
						  "\t" + "}";
			System.out.println(">>>JSON to parse:\n" + json);			
			
			MyBean bean = mapper.readValue(json, 
										   MyBean.class);
			System.out.println(bean.getClass() + " > " + (bean.getAbstractOid() != null ? bean.getAbstractOid().getId() : null)
												 + " / " + (bean.getOidImpl() != null ? bean.getOidImpl().asString() : null));
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][DESERIALIZER TEST------------------------------]");
	}
	@Test
	public void testPolimorphicOIDSerializeAndDeSerialize() {
		System.out.println("[INIT][SERIALIZER / DESERIALIZER TEST------------------------------]");
		try {
			ObjectMapper mapper = _createObjectMapper();
			
			MyBean bean = new MyBean(new MyOIDImpl("this_is_an_abstract_oid"),
									 new MyOIDImpl("this_is_an_oid_impl"));
			
			String beanStr = mapper.writeValueAsString(bean);
			System.out.println(beanStr);
			
			MyBean readedBean = mapper.readValue(beanStr, 
										   		 MyBean.class);
			System.out.println(bean.getClass() + " > " + (readedBean.getAbstractOid() != null ? readedBean.getAbstractOid().getId() : null)
												 + " / " + (readedBean.getOidImpl() != null ? readedBean.getOidImpl().asString() : null));
			
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][SERIALIZER / DESERIALIZER TEST------------------------------]");
	}
}
