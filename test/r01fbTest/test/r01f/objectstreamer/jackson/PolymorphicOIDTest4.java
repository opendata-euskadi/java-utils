package r01f.objectstreamer.jackson;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.TypeNameIdResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * http://wiki.fasterxml.com/JacksonPolymorphicDeserialization
 * http://sunilkumarpblog.blogspot.com.es/2015/12/javajson-polymorphic-serialization-de.html
 * https://www.dilipkumarg.com/dynamic-polymorphic-type-handling-jackson/
 */
public class PolymorphicOIDTest4 {
/////////////////////////////////////////////////////////////////////////////////////////
//	Use Case: Serialize / deserialize a polymorphic type using a type-resolver set at
//			  an annotation at the interface
//			  Test3 uses a typeId-resolver instead of a type-resolver
//
//  Problems:
//		. The translation of the real type from the the type info is hard coded
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
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    			  include = JsonTypeInfo.As.PROPERTY,
    			  property = "type")
    @JsonTypeResolver(OIDTypeResolverAndDeserializerBuilder.class)		// will be called to resolve the type of the abstract / interface fields
	public static interface MyOID {
		public String getId();
		public void setId(final String id);
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static abstract class OIDImplBase 
					  implements MyOID {
		@Getter @Setter private String _id;
	}
	@Accessors(prefix="_")
	@NoArgsConstructor
	public static class MyOIDImpl 
	  		    extends OIDImplBase {
		public MyOIDImpl(final String oid) {
			super(oid);
		}
	}
	@Accessors(prefix="_")
	@NoArgsConstructor
	public static class MyOtherOIDImpl 
	  		    extends OIDImplBase {
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
//	TypeResolver: internally uses a typeId resolver
/////////////////////////////////////////////////////////////////////////////////////////
	public static class OIDTypeResolverAndDeserializerBuilder 	
		 		extends StdTypeResolverBuilder {
	    @Override
	    public TypeDeserializer buildTypeDeserializer(final DeserializationConfig config,
	    											  final JavaType baseType,final Collection<NamedType> subtypes) {
	        return new OIDTypeResolverAndDeserializer(baseType, 
				        							  // type name resolver: uses @JsonSubTypes annotation at base type and @JsonTypeName annotation at concrete types
				        						   	  //					  ... or the equivalent initialization at a module at mapper creation (see below)
				        						   	  TypeNameIdResolver.construct(config,
				        								   						   baseType,subtypes,
				        								   						   false,true),				// forSer, forDeSer
				        						   	  _typeProperty,
				        						   	  _typeIdVisible,
				        						   	  _defaultImpl != null ? config.getTypeFactory().constructType(_defaultImpl) : null);
	    }
	}
	/**
	 * Extends {@link AsPropertyTypeDeserializer} that uses a PROPERTY at the source JSON to deserialize the object
	 */
	public static class OIDTypeResolverAndDeserializer 
				extends AsPropertyTypeDeserializer {	// type resolver to be used when type metadata is included as a PROPERTY
		
		private static final long serialVersionUID = -5807959473358590620L;
		
		private final TypeIdResolver _typeIdResolver;		// gets the type from it's id (ie: MyOID = MyOID.class)
		
		// called from builder
		public OIDTypeResolverAndDeserializer(final JavaType bt,
							   				  final TypeIdResolver typeIdResolver,		// gets the type from it's id (ie: MyOID = MyOID.class)
							   				  final String typePropertyName,
							   				  final boolean typeIdVisible,
							   				  final JavaType defaultImpl) {
	        super(bt,typeIdResolver, 
	        	  typePropertyName,
	        	  typeIdVisible,
	        	  defaultImpl);
	        _typeIdResolver = typeIdResolver;
	    }
	    public OIDTypeResolverAndDeserializer(final AsPropertyTypeDeserializer src, 
	    					   				  final BeanProperty property,
	    					   				  final TypeIdResolver typeIdResolver) {
	        super(src,
	        	  property);
	        _typeIdResolver = typeIdResolver;
	    }
	    @Override
	    public TypeDeserializer forProperty(final BeanProperty prop) {
	    	System.out.println("____property > " + (_property != null ? _property.getName() : null) + " > " + (prop != null ? prop.getName() : null));
	    	return (prop == _property) ? this										// same property
	    							   : new OIDTypeResolverAndDeserializer(this,					// another property: clone this type resolver
	    												 	 				prop,
	    												 	 				_typeIdResolver);
	    }
	    @Override
	    public Object deserializeTypedFromObject(final JsonParser jp,
	    										 final DeserializationContext context) throws IOException {
	    	System.out.println(">>>>> Deserialize.....");
	    	// *****************************************************************
	    	// PROBLEM: reads all tree
	    	// *****************************************************************
	        JsonNode node = jp.readValueAsTree();	// <-- BEWARE!!! Reads all tree
	        
	    	// [1] - Find the java type	        
	        String typeId = node.get("type").asText();
	        JavaType  type = _typeIdResolver.typeFromId(context,
	        											typeId);	       	        
	        System.out.println("---->type from typeId=" + typeId + ": " + type.getGenericSignature());
	        
	        // [2] - Deserialize the id 
	        String id = node.get("id").asText();
	        MyOID outOid = null;
	        try {
	        	outOid = (MyOID)type.getRawClass().newInstance();
	        	outOid.setId(id);
	        } catch(Throwable th) {
	        	th.printStackTrace(System.out);
	        }
	        return outOid;
	
//	        // [2] - Parse as usual BUT ignore the type field
//	        JsonParser jsonParser = new TreeTraversingParser(node,jp.getCodec());
//	        if (jsonParser.getCurrentToken() == null) {
//	            jsonParser.nextToken();
//	        }
//	        JsonDeserializer<Object> deser = context.findContextualValueDeserializer(type,_property);
//	        return deser.deserialize(jsonParser,
//	        						 context);
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	private ObjectMapper _createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		
		SimpleModule mod = new SimpleModule("MyModule");
		
        // an alternative to annotate the OID subtype with @JsonTypeName
		mod.registerSubtypes(new NamedType(MyOIDImpl.class,MyOIDImpl.class.getSimpleName()),
							 new NamedType(MyOtherOIDImpl.class,MyOtherOIDImpl.class.getSimpleName()));
		
		objectMapper.registerModule(mod);
		
		return objectMapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testPolimorphicOIDSerialize() {
		System.out.println("[INIT][SERIALIZER TEST------------------------------]");
		try {
			ObjectMapper objectMapper = _createObjectMapper();
			
			// oid
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
			
			// oid
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
			System.out.println(bean.getClass() + " > " + bean.getAbstractOid().getId());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
		System.out.println("[END][DESERIALIZER TEST------------------------------]");
	}
}
