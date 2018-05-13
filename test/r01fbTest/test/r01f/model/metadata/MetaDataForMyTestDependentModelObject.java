package r01f.model.metadata;

import com.google.common.base.Joiner;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.Storage;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

@MetaDataForType(modelObjTypeCode = 200,
			     description = {
						@DescInLang(language=Language.SPANISH, value="Test dependent model object"),
						@DescInLang(language=Language.BASQUE, value="[eu] Test dependent model object"),
						@DescInLang(language=Language.ENGLISH, value="Test dependent model object")
			     })
@Accessors(prefix="_")
public abstract class MetaDataForMyTestDependentModelObject 
     		  extends TypeMetaDataForModelObjectBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Accessors(prefix="_")
	public enum SEARCHABLE_METADATA 
	 implements SearchableFieldID {
		YEAR ("year"),
		MONTH_OF_YEAR ("monthOfYear");
		
		@Getter private final IndexableFieldID _fieldId;
		
		SEARCHABLE_METADATA(final Object... ids) {
			_fieldId = IndexableFieldID.forId(Joiner.on(".").skipNulls().join(ids).toString());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="Year"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] Year"),
			   				  @DescInLang(language=Language.ENGLISH, value="Year")
			   		  },
			   		  storage = @Storage(indexed=false))
	@Getter @Setter private Year _year;
	
	@MetaDataForField(description = {
			   				  @DescInLang(language=Language.SPANISH, value="Month of Year"),
			   				  @DescInLang(language=Language.BASQUE, value="[eu] Month of Year"),
			   				  @DescInLang(language=Language.ENGLISH, value="Month of Year")
			   		  },
			   		  storage = @Storage(indexed=false))
	@Getter @Setter private MonthOfYear _monthOfYear;
}
