package r01f.objectstreamer.jackson;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.types.Path;

@Slf4j
public class PathTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	PATH OBJECT
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	private static class WithPath {
		@JsonProperty("path") 
		@Getter @Setter private Path _path; 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERIALIZER / DESERIALIZER
/////////////////////////////////////////////////////////////////////////////////////////
	@NoArgsConstructor
	public static class PathJacksonSerializer 
		        extends JsonSerializer<Path> {
	    @Override
	    public void serialize(final Path value,
	    					  final JsonGenerator jgen,final SerializerProvider provider) throws IOException,
	    																						 JsonProcessingException {
	        jgen.writeString(value.asAbsoluteString());
	    }
	}
	public static class PathJacksonDeserializer 
		 		extends StdDeserializer<Path> { 

		private static final long serialVersionUID = -5273319484446776784L;

		public PathJacksonDeserializer() { 
	        super(Path.class); 
	    } 
	    @Override
	    public Path deserialize(final JsonParser jp,final DeserializationContext ctxt) throws IOException,
	    																					  JsonProcessingException {
	        JsonNode node = jp.getCodec().readTree(jp);
	        String pathStr = ((TextNode)node).asText();
	        return new Path(pathStr);
	    }
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MAPPER
/////////////////////////////////////////////////////////////////////////////////////////	
	private static ObjectMapper _createJSonObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		
		SimpleModule module = new SimpleModule();
		module.addSerializer(Path.class,new PathJacksonSerializer());
		module.addDeserializer(Path.class,new PathJacksonDeserializer());
		
		mapper.registerModule(module);
		
		return mapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TEST
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testPath() {
		try {
			ObjectMapper mapper = _createJSonObjectMapper();
			
			// alone
			Path path = Path.from("/a/b/c/d");
			String pathAsString = mapper.writeValueAsString(path);
			log.info("Path={}",pathAsString);
			
			Path readedPath = mapper.readValue(pathAsString,
									   		   Path.class);
			Assert.assertEquals(readedPath,path);
			
			// wrapped
			WithPath withPath = new WithPath(path);
			String withPathAsString = mapper.writeValueAsString(withPath);
			log.info("WithPath={}",withPathAsString);
			
			WithPath readedWithPath = mapper.readValue(withPathAsString,
													   WithPath.class);
			Assert.assertEquals(readedWithPath.getPath(),path);
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}
}
