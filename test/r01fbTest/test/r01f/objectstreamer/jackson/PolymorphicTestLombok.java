package r01f.objectstreamer.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


public class PolymorphicTestLombok {
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
	@JsonTypeName("dog")
	@Accessors(prefix="_")
	@NoArgsConstructor 
	public static class Dog 
		 	 	extends AnimalBase {
		public Dog(final String name) {
			super(name);
		}
	}
	@JsonTypeName("cat")
	@Accessors(prefix="_")
	@NoArgsConstructor 
	public static class Cat 
	  	 extends AnimalBase {
		public Cat(final String name) {
			super(name);
		}
	}
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
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static ObjectMapper _createObjectMapper() {
		ObjectMapper jsonMapper = new ObjectMapper();
		
		// BUG: comment this line (do not use a property naming strategy) and everything works
		// naming strategy
		jsonMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		
		// use only field annotations
		jsonMapper.setVisibility(PropertyAccessor.ALL, 		Visibility.NONE);
		jsonMapper.setVisibility(PropertyAccessor.FIELD, 	Visibility.ANY);
		
		return jsonMapper;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		try {
			ObjectMapper jsonMapper = _createObjectMapper();
			
			// serialize
			DogAndCat dogAndCat = new DogAndCat(new Dog("bruce"),
												new Cat("patty"));;
			String dogAndCatJson = jsonMapper.writeValueAsString(dogAndCat);
			System.out.println(">>>"  + dogAndCatJson);
			
			// deserialize
			DogAndCat fromDogAndCatJson = jsonMapper.readValue(dogAndCatJson,
														       DogAndCat.class);
			System.out.println(">>>"  + jsonMapper.writeValueAsString(fromDogAndCatJson));
			
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}
}
