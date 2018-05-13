package r01f.objectstreamer.jackson;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;

@Slf4j
public class TestOIDUsingJackson {
/////////////////////////////////////////////////////////////////////////////////////////
//	OID OBJECT
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonDeserialize(using = OIDJacksonDeserializer.class)
	private static interface MyOID 
					 extends OID {
		// marker interface
	}
	@JsonDeserialize(as = MyOIDImpl.class)
	private static class MyOIDImpl
		  	     extends OIDBaseMutable<String>
			  implements MyOID {
		private static final long serialVersionUID = 2337417872385494960L;
		
		public MyOIDImpl() {
			// default constructor
		}
		public MyOIDImpl(final String oidStr) {
			super(oidStr);
		}
		public static MyOIDImpl valueOf(final String oidStr) {
			return new MyOIDImpl(oidStr);
		}
	}
	private static class MyOtherOIDImpl
		  	     extends OIDBaseMutable<String> 
			  implements MyOID {
		private static final long serialVersionUID = -91038142658613713L;
		public MyOtherOIDImpl() {
			// default constructor
		}
		public MyOtherOIDImpl(final String oidStr) {
			super(oidStr);
		}
		public static MyOtherOIDImpl valueOf(final String oidStr) {
			return new MyOtherOIDImpl(oidStr);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	private static class WithOID<O extends MyOID> {
		@JsonProperty("oid") 
		@Getter @Setter private MyOIDImpl _oid;
		
		@JsonProperty("paramOid")
		@Getter @Setter private O _paramOid;
		
		
//		@JsonProperty("anyOid")
//		@JsonSerialize(using = PolymorphicOIDJacksonSerializer.class)
//	    @JsonTypeInfo(
//			use = JsonTypeInfo.Id.NAME, 
//			include = As.PROPERTY, 
//			property = "type")
//	    @JsonSubTypes({
//			@JsonSubTypes.Type(value = MyOIDImpl.class, name = "myOidImpl"),
//			@JsonSubTypes.Type(value = MyOtherOIDImpl.class, name = "myOtherOidImpl")
//	    })
//		@Getter @Setter private MyOID _anyOid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testOid() {
		try {
			ObjectMapper mapper = _createJSonObjectMapper();
			
			// test alone
			MyOID oid = MyOIDImpl.valueOf("myOid");
			String oidAsString = mapper.writeValueAsString(oid);
			log.info("oid={}",oidAsString);
			
			MyOID readedOid = mapper.readValue(oidAsString,
									   		   MyOIDImpl.class);
			Assert.assertEquals(oid,readedOid);
			
			
			// test as field
			WithOID<MyOIDImpl> withOid = new WithOID<MyOIDImpl>((MyOIDImpl)oid,(MyOIDImpl)oid);
			String withOidAsString = mapper.writeValueAsString(withOid);
			log.info("withOid={}",withOidAsString);
			
			WithOID<MyOIDImpl> readedWithOid = mapper.readValue(withOidAsString,
													 			new TypeReference<WithOID<MyOIDImpl>>() {});
			Assert.assertEquals(readedWithOid.getOid(),oid);
			
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private static ObjectMapper _createJSonObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
//		mapper.getSerializationConfig().getDefaultVisibilityChecker()
//									            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
//									            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
//									            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
//									            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.ALL,		Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
		
		SimpleModule module = new SimpleModule();
		module.addSerializer(MyOID.class,new OIDJacksonSerializer());
		module.addDeserializer(MyOID.class,new OIDJacksonDeserializer());
		mapper.registerModule(module);
		
		return mapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
//	@JsonSerialize(using = OIDJacksonSerializer.class)
//	public static abstract class OIDJacksonAnnotationsMixin {
//		@JsonUnwrapped abstract Object getId();
//		@JsonIgnore abstract Object getRaw(); // we don't need it!
//	}
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
	@NoArgsConstructor
	public static class OIDJacksonSerializer 
		        extends JsonSerializer<MyOID> {
	    @Override
	    public void serialize(final MyOID value,
	    					  final JsonGenerator jgen,
	    					  final SerializerProvider provider) throws IOException,
	    																JsonProcessingException {
	        jgen.writeString(value.asString());
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
	public static class OIDJacksonDeserializer 
		 		extends StdDeserializer<MyOID> { 

		private static final long serialVersionUID = -5273319484446776784L;

		public OIDJacksonDeserializer() { 
	        super(MyOID.class);
	    } 
	    @Override
	    public MyOID deserialize(final JsonParser jp,
	    						 final DeserializationContext ctxt) throws IOException,
	    																   JsonProcessingException {
	    	System.out.println("=====>");
	        JsonNode node = jp.getCodec().readTree(jp);
	        String oidStr = ((TextNode)node).asText();
	        return new MyOIDImpl(oidStr);
	    }
	}
}
