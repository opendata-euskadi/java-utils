package r01f.langtexts;

import org.junit.Assert;
import org.junit.Test;

import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;

public class TestLangTexts {
	@Test
	public void testLangTextsMapBacked() {
		LanguageTexts langTexts = new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
						 				.add(Language.SPANISH,"Nombre")
				  					    .add(Language.BASQUE,"[eu] Nombre")
				  					    .add(Language.ENGLISH,"Name");
		Assert.assertTrue(langTexts.isTextDefinedFor(Language.SPANISH)
					   && langTexts.isTextDefinedFor(Language.BASQUE)
					   && langTexts.isTextDefinedFor(Language.ENGLISH));
		Assert.assertEquals(langTexts.get(Language.SPANISH),"Nombre");
		Assert.assertEquals(langTexts.get(Language.BASQUE),"[eu] Nombre");
		Assert.assertEquals(langTexts.get(Language.ENGLISH),"Name");
	}
}
