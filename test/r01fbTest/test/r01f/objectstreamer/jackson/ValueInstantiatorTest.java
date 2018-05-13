package r01f.objectstreamer.jackson;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonValueInstantiator;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.MarshallerMapperForJson;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.reflection.ReflectionUtils;

@Slf4j
public class ValueInstantiatorTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallType(as="file")
	@JsonValueInstantiator(FileNameValueInstantiator.class)
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public static class FileName
			 implements Serializable {
		
		private static final long serialVersionUID = -7901960255575168878L;
		
		@MarshallField(as="name",whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final String _fileName;
		
		public static FileName of(final String fileName) {
			return new FileName(fileName);
		}
		public static FileName valueOf(final String fileName) {
			return new FileName(fileName);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VALUE INSTANTIATOR
/////////////////////////////////////////////////////////////////////////////////////////
	public static class FileNameValueInstantiator
				extends ValueInstantiator {
        @Override
        public String getValueTypeDesc() {
            return FileName.class.getName();
        }
        
        @Override
        public boolean canCreateFromObjectWith() {
        	return true; 
        }
        @Override
        public CreatorProperty[] getFromObjectArguments(final DeserializationConfig cfg) {
            return  new CreatorProperty[] {
						                    	new CreatorProperty(PropertyName.construct("fileName"),TypeFactory.defaultInstance().constructType(String.class),null,
						                    						null,		// type deserializer
						                    						null,		// context annotations
						                    						null,		// annotated param
						                    						0,			// index
						                    						null,		// injectable value
						                    						null)		// property metadata
            							  };
        }
        @Override
        public Object createFromObjectWith(final DeserializationContext ctxt,
        								   final Object[] args) {
            try {
                return ReflectionUtils.createInstanceFromString(FileName.class,(String)args[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testJsonStreamer() {
		try {
			MarshallerMapperForJson mapper = new MarshallerMapperForJson();

			FileName obj = FileName.of("d:/data/a.txt");
			
			// write as xml
			log.warn("[INIT][SERIALIZER TEST------------------------------]");
			log.warn("Write as Json:");
			String json = mapper.writeValueAsString(obj);
			log.warn("Serialized Json\n{}",json);
			log.warn("[END][SERIALIZER TEST------------------------------]");
			
			// Read from xml
			log.warn("[INIT][DESERIALIZER TEST------------------------------]");
			log.warn("Read from Json:");
			FileName objReadedFromJson = mapper.readValue(json,
									  			   		  FileName.class);
			log.warn("Obj readed from serialized json: {}",
					 objReadedFromJson.getClass().getName());
			
			Assert.assertEquals(obj.getFileName(),objReadedFromJson.getFileName());
			
			log.warn("[END][DSERIALIZER TEST------------------------------]");
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}
}
