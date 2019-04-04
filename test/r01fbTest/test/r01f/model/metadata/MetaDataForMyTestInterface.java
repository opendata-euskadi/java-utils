package r01f.model.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.Storage;

@MetaDataForType(modelObjTypeCode = 200,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Test interface"),
						@DescInLang(language=Language.BASQUE, value="[eu] Test interface"),
						@DescInLang(language=Language.ENGLISH, value="Test interface")
			     })
public interface MetaDataForMyTestInterface 
         extends HasFieldsMetaData {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA 
	 implements FieldIDToken {
		TEXTS ("description"),;
		
		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="Description"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] Description"),
			   				  @DescInLang(language=Language.ENGLISH, value="Description")
			   		  },
			   		  storage = @Storage(indexed=true, 
								  		 stored=false,
								  		 tokenized=true))
	public LanguageTexts getDescription();
}
