package r01f.model.mock;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.model.mock.MyOIDs.MyOtherTestOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@ConvertToDirtyStateTrackable
@MarshallType(as="myAspectJWeavedDependentType") 
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class MyAspectJWeavedDependentType {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MyOtherTestOID _oid;
	
	@MarshallField(as="strCol",
				   whenXml=@MarshallFieldAsXml(collectionElementName="colEl"),
				   escape=true)	
	@Getter @Setter private Collection<String> _stringCol;
	
	@MarshallField(as="langTexts")
	@Getter @Setter private LanguageTexts _langTexts;
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	public static MyAspectJWeavedDependentType createMock() {
		MyAspectJWeavedDependentType out = new MyAspectJWeavedDependentType(MyOtherTestOID.forId("oid"),
																		    Lists.newArrayList("el1","el2"),
																		    new LanguageTextsMapBacked()
																			  		.add(Language.ENGLISH,"test")
																			  		.add(Language.SPANISH,"prueba")
																			);
		return out;
	}
}
