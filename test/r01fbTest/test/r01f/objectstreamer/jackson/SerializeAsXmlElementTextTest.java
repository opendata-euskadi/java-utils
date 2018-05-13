package r01f.objectstreamer.jackson;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.MarshallerMapperForXml;

@Slf4j
public class SerializeAsXmlElementTextTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@JsonRootName("asText")
	@Accessors(prefix="_")
	public static class AsText {
			
		@JsonProperty("text") @JacksonXmlText
		@Getter private final String _text;
		
		@JsonCreator
		public AsText(final String text) {
			_text = text;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void test() throws IOException {
		MarshallerMapperForXml mapper = new MarshallerMapperForXml();

		AsText obj = new AsText("_text_");
		
		// write as xml
		log.warn("[INIT][SERIALIZER TEST of instance of {}]",AsText.class);
		log.warn("Write as Xml:");
		String xml = mapper.writeValueAsString(obj);
		log.warn("Serialized Xml\n{}",xml);
		log.warn("[END][SERIALIZER TEST of instance of {}]",AsText.class);
		
		// Read from xml
		log.warn("[INIT][DESERIALIZER TEST of instance of {}]",AsText.class);
		log.warn("Read from Xml:");
		AsText objReadedFromXml = mapper.readValue(xml,
								  			       AsText.class);
		log.warn("Obj readed from serialized xml: {}",
				 objReadedFromXml.getClass().getName());
		
		log.warn("[END][DSERIALIZER TEST of instance of {}]",AsText.class);
	}
}
