package r01f.objectstreamer.jackson;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.codehaus.stax2.XMLOutputFactory2;
import org.junit.Test;

import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
 */
public class PolymorphicOIDTest2 {
/////////////////////////////////////////////////////////////////////////////////////////
//	Use Case: Serialize / deserialize a polymorphic type using jackson annotations
//			  or a custom jackson module
//			  This use-case is similar as Test1 = have the SAMLE problems
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

// As an alternative to annotations, the subtypes are registered in a module
// at mapper creation (see below)
//    @JsonSubTypes({
//        @JsonSubTypes.Type(value = MyOIDImpl.class,name = "myOid"),
//        @JsonSubTypes.Type(value = MyOtherOIDImpl.class,name = "myOtherOid")
//    })
	public static interface MyOID {
		public String getId();
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static abstract class MyOIDImplBase 
					  implements MyOID {
		@Getter @Setter private String _id;
	}
//	@JsonTypeName("myOid")			// as an alternative, the subtype name is registered in a module at mapper creation (see below)
	@Accessors(prefix="_")
	@NoArgsConstructor
	public static class MyOIDImpl 
	  		    extends MyOIDImplBase {
		public MyOIDImpl(final String oid) {
			super(oid);
		}
	}
//	@JsonTypeName("myOtherOid")		// as an alternative, the subtype name is registered in a module at mapper creation (see below)
	@Accessors(prefix="_")
	@NoArgsConstructor
	public static class MyOtherOIDImpl 
	  		    extends MyOIDImplBase {
		public MyOtherOIDImpl(final String oid) {
			super(oid);
		}
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class MyBean {
		// annotate locally a property!! (do NOT annotate globally the interface)
	    @JsonTypeInfo(include = As.PROPERTY, 
					  property = "type",
					  use = JsonTypeInfo.Id.NAME)
		@Getter @Setter private MyOID _abstractOid;
		
		@Getter @Setter private MyOIDImpl _oidImpl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	private ObjectMapper _createJsonMapper() {
		ObjectMapper jsonMapper = new ObjectMapper();
		
		// use only field annotations
		jsonMapper.setVisibility(PropertyAccessor.ALL, 	Visibility.NONE);
		jsonMapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
		
		jsonMapper.registerModule(_createModule());
		
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

		// use only field annotations
		xmlMapper.setVisibility(PropertyAccessor.ALL, 		Visibility.NONE);
		xmlMapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
		
		xmlMapper.registerModule(_createModule());
		
		return xmlMapper;
	}
	private static Module _createModule() {
		SimpleModule mod = new SimpleModule("MyModule");
		
		// an alternative to annotate the OID interface with @JsonSubTypes 
        final SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(MyOID.class,MyOIDImpl.class);
        resolver.addMapping(MyOID.class,MyOtherOIDImpl.class);
        mod.setAbstractTypes(resolver);
		
        // an alternative to annotate the OID subtype with @JsonTypeName
		mod.registerSubtypes(new NamedType(MyOIDImpl.class,"myOid"),
							 new NamedType(MyOtherOIDImpl.class,"myOtherOid"));	
		return mod;
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
												 + " / " + (readedFromJson.getOidImpl() != null ? readedFromJson.getOidImpl().getId() : null));
			
			// xml
			XmlMapper xmlMapper = _createXmlMapper();
			String beanXml = xmlMapper.writeValueAsString(bean);
			System.out.println(beanXml);
			
			MyBean readedFromXml = xmlMapper.readValue(beanXml, 
										   		 	   MyBean.class);
			System.out.println(bean.getClass() + " > " + (readedFromXml.getAbstractOid() != null ? readedFromXml.getAbstractOid().getId() : null)
												 + " / " + (readedFromXml.getOidImpl() != null ? readedFromXml.getOidImpl().getId() : null));
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][SERIALIZER / DESERIALIZER TEST------------------------------]");
	}
//	@Test
	public void testPolimorphicOIDSerialize() {
		System.out.println("[INIT][SERIALIZER TEST------------------------------]");
		try {
			ObjectMapper jsonMapper = _createJsonMapper();
			
			// Serialize an oid
			MyOID myOid = new MyOIDImpl("this_is_an_oid");
			String oidStr = jsonMapper.writeValueAsString(myOid);
			System.out.println(oidStr);
			
			// serialize a bean
			MyBean bean = new MyBean(new MyOIDImpl("this_is_an_abstract_oid"),
									 new MyOIDImpl("this_is_an_oid_impl"));
			
			String beanJson = jsonMapper.writeValueAsString(bean);
			System.out.println(beanJson);
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][SERIALIZER TEST------------------------------]");
	}
//	@Test
	public void testPolimorphicOIDDeserialize() {
		System.out.println("[INIT][DESERIALIZER TEST------------------------------]");
		try {
			ObjectMapper jsonMapper = _createJsonMapper();
			
			String absOidStr = "{" +
									"\"type\":\"MyOIDImpl\"" + "," +
									"\"_id\":\"this_is_an_abstract_oid\"" +
								"}";
			String implOidStr = "{" +
									"\"_id\":\"this_is_an_oid_impl\"" + 
								"}";
			
			// Deserialize an oid
			System.out.println(">>> " + implOidStr);
			MyOID oid = jsonMapper.readValue(implOidStr, 
											   MyOID.class);
			System.out.println(oid.getClass());
			
			// serialize a bean

			String json = "{" +
								"\"_abstractOid\":" + absOidStr + "," +
								"\"_oidImpl\":" + implOidStr +
						 "}";
			System.out.println(">>> " + json);	
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][DESERIALIZER TEST------------------------------]");
	}
}
