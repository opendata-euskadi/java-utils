package r01f.objectstreamer.jackson;

import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.codehaus.stax2.XMLOutputFactory2;
import org.junit.Test;

import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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
public class PolymorphicOIDTest7 {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonSubTypes({
		@JsonSubTypes.Type(MyOIDImpl.class),
		@JsonSubTypes.Type(MyOtherOIDImpl.class)
	})
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
		
		@Override
		public String asString() {
			return this.getId();
		}
	}
	@JsonTypeName("MyOIDImpl")
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
	@JsonTypeName("MyOtherOIDImpl")
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
		@JsonTypeInfo(include=As.PROPERTY,
					  property="typeId",
					  use=Id.NAME,
					  visible=false)
		@JsonProperty 
		@Getter @Setter private MyOID _abstractOid;
		
		// use the default serializer / deserializer
		@JsonSerialize(using=MyOIDSerializer.class) @JsonDeserialize(using=MyOIDJacksonDeSerializer.class)
		@JsonProperty
		@Getter @Setter private MyOIDImpl _oidImpl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor
	public static class MyOIDSerializer 
		        extends JsonSerializer<MyOID> {
	    @Override
	    public void serialize(final MyOID value,
	    					  final JsonGenerator jgen,final SerializerProvider provider) throws IOException,
	    																						 JsonProcessingException {
	        jgen.writeString(value.asString());
	    }
	}
	public static class MyOIDJacksonDeSerializer 
		 		extends StdDeserializer<MyOID> { 

		private static final long serialVersionUID = -5273319484446776784L;

		public MyOIDJacksonDeSerializer() { 
	        super(MyOID.class); 
	    } 
	    @Override
	    public MyOID deserialize(final JsonParser jp,final DeserializationContext ctxt) throws IOException,
	    																					   JsonProcessingException {
	        JsonNode node = jp.getCodec().readTree(jp);
	        String pathStr = ((TextNode)node).asText();
	        return new MyOIDImpl(pathStr);
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CREATE OBJECT MAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	private ObjectMapper _createJsonMapper() {
		ObjectMapper jsonMapper = new ObjectMapper();
		
//		SimpleModule mod = new SimpleModule("MyModule");
//		mapper.registerModule(mod);

		_configureMapper(jsonMapper);
		return jsonMapper;
	}
	private XmlMapper _createXmlMapper() {
		XmlMapper xmlMapper = new XmlMapper(new WstxInputFactory(),
							 	  		    new WstxOutputFactory());
		// Deserialize: use namespaces
		XMLInputFactory xmlInputFactory = xmlMapper.getFactory().getXMLInputFactory();
		xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE,Boolean.TRUE);				// beware namespaces
		xmlInputFactory.setProperty(WstxInputProperties.P_RETURN_NULL_FOR_DEFAULT_NAMESPACE,Boolean.TRUE);
		
		// Serialize: 
		XMLOutputFactory outputFactory = xmlMapper.getFactory().getXMLOutputFactory();
//		outputFactory.setProperty(XMLOutputFactory2.P_AUTOMATIC_NS_PREFIX,"r01");					// the xmlns prefix automatically added to annotated fields (the prefix is generated like prefix{xx})					
		outputFactory.setProperty(XMLOutputFactory2.IS_REPAIRING_NAMESPACES,Boolean.TRUE);			// do now fail when xmlns='' (namespace-reparing mode)

		_configureMapper(xmlMapper);
		return xmlMapper;
	}
	private void _configureMapper(final ObjectMapper mapper) {
		// naming strategy
//		mapper.setPropertyNamingStrategy(new RemoveStartingUnderscoreJacksonPropertyNamingStrategy());
		
		// use only field annotations
		mapper.setVisibility(PropertyAccessor.ALL, 		Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TEST
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testPolimorphicOIDSerializeAndDeSerialize() {
		System.out.println("[INIT][SERIALIZER / DESERIALIZER TEST------------------------------]");
		try {
			MyBean bean = new MyBean(new MyOIDImpl("this_is_an_abstract_oid"),
									 new MyOIDImpl("this_is_an_oid_impl"));
			// json
			ObjectMapper jsonMapper = _createJsonMapper();
			String beanJson = jsonMapper.writeValueAsString(bean);
			System.out.println(beanJson);
			
			MyBean readedFromJson = jsonMapper.readValue(beanJson, 
										   		 		 MyBean.class);
			System.out.println(bean.getClass() + " > " + (readedFromJson.getAbstractOid() != null ? readedFromJson.getAbstractOid().getId() : null)
												 + " / " + (readedFromJson.getOidImpl() != null ? readedFromJson.getOidImpl().asString() : null));
			
			// xml
			XmlMapper xmlMapper = _createXmlMapper();
			String beanXml = xmlMapper.writeValueAsString(bean);
			System.out.println(beanXml);
			
			MyBean readedFromXml = xmlMapper.readValue(beanXml, 
										   		 	   MyBean.class);
			System.out.println(bean.getClass() + " > " + (readedFromXml.getAbstractOid() != null ? readedFromXml.getAbstractOid().getId() : null)
												 + " / " + (readedFromXml.getOidImpl() != null ? readedFromXml.getOidImpl().asString() : null));
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][SERIALIZER / DESERIALIZER TEST------------------------------]");
	}
}
