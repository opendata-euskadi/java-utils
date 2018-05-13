package r01f.objectstreamer.jackson;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
public class PolymorphicOIDTest3 {
/////////////////////////////////////////////////////////////////////////////////////////
//	Use Case: Serialize / deserialize a polymorphic type using a typeId-resolver set at
//			  an annotation at the interface
//			  Test4 uses a type-resolver instead of a typeId-resolver
//  Problems:
//		. The translation of the real type from the the type id info is hard coded
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
				  include = As.PROPERTY, 
				  property = "type")
    @JsonTypeIdResolver(OIDTypeResolverFromId.class)	// BEWARE! typeId resolver (Test4 uses a TypeResolver)
	public static interface MyOID {
		public String getId();
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static abstract class MyOIDImplBase 
					  implements MyOID {
		@Getter @Setter private String _id;
	}
	@Accessors(prefix="_")
	@NoArgsConstructor
	public static class MyOIDImpl 
	  		    extends MyOIDImplBase {
		public MyOIDImpl(final String oid) {
			super(oid);
		}
	}
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
		@Getter @Setter private MyOID _abstractOid;
		@Getter @Setter private MyOIDImpl _oidImpl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	http://www.baeldung.com/jackson-advanced-annotations
/////////////////////////////////////////////////////////////////////////////////////////
	public static class OIDTypeResolverFromId 
				extends TypeIdResolverBase {
     
	    private JavaType _superType;
	 
	    @Override
	    public void init(final JavaType baseType) {
	        _superType = baseType;
	    }
	    @Override
	    public Id getMechanism() {
	        return Id.NAME;
	    }
	    @Override
	    public String idFromValue(final Object obj) {
	        return this.idFromValueAndType(obj,
	        							   obj.getClass());
	    }
	    @Override
	    public String idFromValueAndType(final Object obj,
	    								 final Class<?> subType) {
	        String typeId = subType.getSimpleName();
	        System.out.println("---->id from value and type=" + subType + ": " + typeId);
	        return typeId;
	    }
	    @Override
	    public JavaType typeFromId(final DatabindContext context,
	    						   final String id) {
	        Class<?> subType = null;
	        
	        // PROBLEM: The type from id resolving is hard-coded
	        if (id.equals("MyOIDImpl")) {
	            subType = MyOIDImpl.class;
	        } else if (id.equals("MyOtherOIDImpl")) {
	            subType = MyOtherOIDImpl.class;
	        }
//	        JavaType outType = context.constructType(subType);
	        JavaType outType = context.constructSpecializedType(_superType,
	        													subType);
	        System.out.println("---->type from id=" + id + ": " + outType.getGenericSignature());
	        return outType;
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OBJECT MAPPER
/////////////////////////////////////////////////////////////////////////////////////////
	private ObjectMapper _createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		
		SimpleModule mod = new SimpleModule("MyModule");
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
