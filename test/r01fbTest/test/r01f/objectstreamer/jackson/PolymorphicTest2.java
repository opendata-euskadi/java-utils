package r01f.objectstreamer.jackson;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


public class PolymorphicTest2 {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////

//  BUG: If @JsonTypeInfo annotates the interface everything works
//		 (just uncomment this annotation and comment the one annotating the
//		  dog field of DogAndCat below to make the test work)
//	@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY,
//				  use = JsonTypeInfo.Id.NAME,
//				  property = "type")
	@JsonSubTypes({
	    @JsonSubTypes.Type(Dog.class),
	    @JsonSubTypes.Type(Cat.class)
	})
	@Accessors(prefix="_")
	public static interface Animal {
		public String getName();
	}
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static abstract class AnimalBase
					  implements Animal {
		@Getter @Setter private String _name;
	}
	@JsonRootName("dog") @JsonTypeName("dog")
	@Accessors(prefix="_")
	@NoArgsConstructor 
	public static class Dog 
		 	 	extends AnimalBase {
		public Dog(final String name) {
			super(name);
		}
	}
	@JsonRootName("cat") @JsonTypeName("cat")
	@Accessors(prefix="_")
	@NoArgsConstructor 
	public static class Cat 
	  	 extends AnimalBase {
		public Cat(final String name) {
			super(name);
		}
	}
	@JsonRootName("dogAndCat")
	@Accessors(prefix="_")
	@NoArgsConstructor @AllArgsConstructor
	public static class DogAndCat {
//  	BUG: If @JsonTypeInfo annotates the field, the test FAILS!
//		 	 (just comment this annotation and uncomment the one annotating the
//		  	  Animal interface above to have everything working ok)
		@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY,
					  use = JsonTypeInfo.Id.NAME,
					  property = "type")
		@Getter @Setter private Animal _dog;	
		
		@Getter @Setter private Cat _cat;
		
		@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY,
					  use = JsonTypeInfo.Id.NAME,
					  property = "type")
	    @JacksonXmlElementWrapper(localName="_animals",useWrapping = true)
	    @JacksonXmlProperty(localName = "_animalItem")
		@Getter @Setter private Collection<Animal> _animals;
		
	    @JacksonXmlElementWrapper(localName="_dogs",useWrapping = true)
	    @JacksonXmlProperty(localName = "_dogItem")
		@Getter @Setter private Collection<Dog> _dogs;
	    
	    @JacksonXmlElementWrapper(localName="_properties",useWrapping = true)
	    @JacksonXmlProperty(localName = "_property")
	    @Getter @Setter private Collection<String> _properties;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static ObjectMapper _createJsonMapper() {
		ObjectMapper jsonMapper = new ObjectMapper();
		_configureMapper(jsonMapper);
		return jsonMapper;
	}
	private static XmlMapper _createXmlMapper() {
		XmlMapper xmlMapper = new XmlMapper();
		_configureMapper(xmlMapper);	
		xmlMapper.setDefaultUseWrapper(true);
		return xmlMapper;
	}
	private static void _configureMapper(final ObjectMapper mapper) {
		// BUG: comment this line (do not use a property naming strategy) and everything works
		// naming strategy
//		jsonMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		
		// use only field annotations
		mapper.setVisibility(PropertyAccessor.ALL, 		Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		try {
			// Create object
			DogAndCat dogAndCat = new DogAndCat(new Dog("bruce"),
												new Cat("patty"),
												Lists.<Animal>newArrayList(new Dog("charles"),
																   		   new Cat("bitchie")),
												Lists.<Dog>newArrayList(new Dog("sammy"),
																   		new Dog("culprit")),
												Lists.<String>newArrayList("property_\"1\"",
																   		   "property_\"2\""));
			// JSON------------------------
			ObjectMapper jsonMapper = _createJsonMapper();
			_doTest(jsonMapper,
					dogAndCat);
			
			// XML------------------------
			XmlMapper xmlMapper = _createXmlMapper();
			_doTest(xmlMapper,
					dogAndCat);
			
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}
	private static void _doTest(final ObjectMapper mapper,
								final DogAndCat dogAndCat) throws JsonProcessingException,
																  IOException {
			// a) serialize
			String dogAndCatSerialized = mapper.writerWithDefaultPrettyPrinter()
											   .writeValueAsString(dogAndCat);
			System.out.println(">>>"  + dogAndCatSerialized);
			
			// b) deserialize
			DogAndCat fromDogAndCatSerialized = mapper.readValue(dogAndCatSerialized,
														       	 DogAndCat.class);
			System.out.println(">>>"  + mapper.writeValueAsString(fromDogAndCatSerialized));
	}
}
