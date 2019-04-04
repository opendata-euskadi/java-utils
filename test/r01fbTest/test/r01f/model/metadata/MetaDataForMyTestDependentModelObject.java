package r01f.model.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA 
	 implements FieldIDToken {
		YEAR ("year"),
		MONTH_OF_YEAR ("monthOfYear");
		
		@Getter private final String _token;
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
