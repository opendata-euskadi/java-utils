package r01f.objectstreamer.jackson;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OIDTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	OID OBJECT
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
		@Getter @Setter private MyOIDImpl _oidImpl;
		@Getter @Setter private MyOIDImpl _oidOtherImpl;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MAPPER
/////////////////////////////////////////////////////////////////////////////////////////	
	private static ObjectMapper _createJSonObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		
		// use only field annotations
		mapper.setVisibility(PropertyAccessor.ALL, 		Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
		
		// module
		SimpleModule module = new SimpleModule();
		mapper.registerModule(module);
		
		return mapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testOIDSerialization() {
		try {
			ObjectMapper mapper = _createJSonObjectMapper();
			
			// alone
			MyOID myOidImpl = new MyOIDImpl("myOid_impl");
			String oidAsString = mapper.writeValueAsString(myOidImpl);
			log.info("oid={}",oidAsString);
			
			MyOID readedOid = mapper.readValue(oidAsString,
									   		   MyOIDImpl.class);
			Assert.assertEquals(readedOid.getId(),myOidImpl.getId());
			
			// wrapped
			MyBean bean = new MyBean(new MyOIDImpl("myOid_impl"),
									 new MyOIDImpl("myOtherOid_impl"));
			String beanAsString = mapper.writeValueAsString(bean);
			log.info("bean={}",beanAsString);
			
			MyBean readedBean = mapper.readValue(beanAsString,
												 MyBean.class);
			Assert.assertEquals(readedBean.getOidImpl().asString(),bean.getOidImpl().asString());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}
}
