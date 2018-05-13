package r01f.model.metadata;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Joiner;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.Storage;
import r01f.model.mock.MyOIDs.MyTestOID;
import r01f.model.mock.MyTestDependentModelObject;
import r01f.model.mock.MyTestEnum;
import r01f.types.Path;
import r01f.types.url.Url;

@MetaDataForType(modelObjTypeCode = 100,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Test model object"),
						@DescInLang(language=Language.BASQUE, value="[eu] Test model object"),
						@DescInLang(language=Language.ENGLISH, value="Test model object")
			     })
@Accessors(prefix="_")
public abstract class MetaDataForMyTestModelObject 
     		  extends TypeMetaDataForPersistableModelObjectBase<MyTestOID> 
  		   implements HasMetaDataForHasIDModelObject<MyTestOID>,
  		   			  HasMetaDataForHasLanguageModelObject,
  		   			  MetaDataForMyTestInterface {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Accessors(prefix="_")
	public enum SEARCHABLE_METADATA 
	 implements SearchableFieldID {
		NAME ("name"),
		ENUM ("enum"),
		URL ("url"),
		PATH ("path"),
		COL ("col"),
		MAP ("map"),
		SUB ("sub");
		
		@Getter private final IndexableFieldID _fieldId;
		
		SEARCHABLE_METADATA(final Object... ids) {
			_fieldId = IndexableFieldID.forId(Joiner.on(".").skipNulls().join(ids).toString());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="Name"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] Name"),
			   				  @DescInLang(language=Language.ENGLISH, value="Name")
			   		  },
			   		  storage = @Storage(indexed=true, 
								  		 stored=true,
								  		 tokenized=false))
	@Getter @Setter private String _name;
	
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="Enumeration"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] Enumeration"),
			   				  @DescInLang(language=Language.ENGLISH, value="Enumeration")
			   		  },
			   		  storage = @Storage(indexed=true, 
								  		 stored=true))
	@Getter @Setter private MyTestEnum _enum;
	
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="URL"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] URL"),
			   				  @DescInLang(language=Language.ENGLISH, value="URL")
			   		  },
			   		  storage = @Storage(indexed=true, 
								  		 stored=true))
	@Getter @Setter private Url _url;
	
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="Path"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] Path"),
			   				  @DescInLang(language=Language.ENGLISH, value="Path")
			   		  },
			   		  storage = @Storage(indexed=true, 
								  		 stored=true))
	@Getter @Setter private Path _path;
	
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="Collection"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] Collection"),
			   				  @DescInLang(language=Language.ENGLISH, value="Collection")
			   		  },
			   		  storage = @Storage(indexed=true, 
								  		 stored=true))
	@Getter @Setter private Collection<String> _col;
	
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="Map"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] Map"),
			   				  @DescInLang(language=Language.ENGLISH, value="Map")
			   		  },
			   		  storage = @Storage(indexed=true, 
								  		 stored=true))
	@Getter @Setter private Map<Integer,String> _map;
	
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="Sub"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] Sub"),
			   				  @DescInLang(language=Language.ENGLISH, value="Sub")
			   		  },
			   		  storage = @Storage(indexed=true, 
								  		 stored=true,
								  		 tokenized=false))
	@Getter @Setter private MyTestDependentModelObject _sub;
}
