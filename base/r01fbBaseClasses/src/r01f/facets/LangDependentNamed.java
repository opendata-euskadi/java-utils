package r01f.facets;

import java.util.Collection;

import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;

/**
 * Interface for objects that can have names in multiple languages
 */
public interface LangDependentNamed {
/////////////////////////////////////////////////////////////////////////////////////////
//  HasLangDependentFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface HasLangDependentNamedFacet 
					extends HasName,
							HasSummaryFacet {	// If an object has a name... it has a summary
		public LangDependentNamed asLangDependentNamed();
		
		public LanguageTexts getNamesByLanguage();
		public void setNamesByLanguage(LanguageTexts langTexts);
		
		public void ensureNamesByLanguageContainerIsNOTNull();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets a name in a {@link Language}
	 * @param lang
	 * @param name
	 */
	public void setNameIn(final Language lang,final String name);
	/**
	 * Gets the name in a provided language
	 * @param lang
	 * @return
	 */
	public String getNameIn(Language lang);
	/**
	 * @return the list of languages in which the name is available
	 */
	public Collection<Language> getAvailableLanguages();
}
