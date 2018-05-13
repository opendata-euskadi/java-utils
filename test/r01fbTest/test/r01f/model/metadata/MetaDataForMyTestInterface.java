package r01f.model.metadata;

import com.google.common.base.Joiner;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.metadata.HasFieldsMetaData;
import r01f.model.metadata.IndexableFieldID;
import r01f.model.metadata.SearchableFieldID;
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
	public enum SEARCHABLE_METADATA 
	 implements SearchableFieldID {
		TEXTS ("description"),;
		
		@Getter private final IndexableFieldID _fieldId;
		
		SEARCHABLE_METADATA(final Object... ids) {
			_fieldId = IndexableFieldID.forId(Joiner.on(".").skipNulls().join(ids).toString());
		}
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
