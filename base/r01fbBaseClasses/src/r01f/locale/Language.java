package r01f.locale;


import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;

/**
 * Models languages supported by R01
 */
@Accessors(prefix="_")
public enum Language
 implements EnumWithCode<Integer,Language> {
	SPANISH		(10),
	BASQUE		(11),

	ENGLISH		(20),

	FRENCH		(30),

	DEUTCH		(40),

	KOREAN		(50),
	POLISH		(51),
	SWEDISH		(52),
	HUNGARIAN	(53),
	CZECH		(54),
	ROMANIAN	(55),
	JAPANESE	(56),
	RUSSIAN		(57),
	ITALIAN		(58),
	PORTUGUESE	(59),


	ANY			(0);

	public static Language DEFAULT = Language.SPANISH;		// TODO get it from properties

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Class<Integer> _codeType = Integer.class;
	@Getter private final Integer _code;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	private Language(final int code) {
		_code = code;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static EnumWithCodeWrapper<Integer,Language> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(Language.class);
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static Language fromName(final String name) {
		return WRAPPER.fromName(name);
	}
	public static Language fromCode(final int code) {
		return WRAPPER.fromCode(code);
	}
	public static Language fromString(final String s) {
		return Language.fromName(s);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean is(final Language other) {
		return this == other;
	}
	public boolean isNOT(final Language other) {
		return !this.is(other);
	}
	@Override
	public boolean isIn(Language... els) {
		return WRAPPER.isIn(this,els);
	}
	public boolean in(final Language... others) {
		boolean outIn = false;
		if (CollectionUtils.hasData(others)) {
			for (Language lang : others) {
				if (this == lang) {
					outIn = true;
					break;
				}
			}
		}
		return outIn;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public static String pattern() {
		return CollectionUtils.toStringSeparatedWith(FluentIterable.from(Language.values())
																   .filter(new Predicate<Language>() {
																					@Override
																					public boolean apply(final Language lang) {
																						return lang != ANY;
																					}
																   		   })
																   .toList(),
													 '|');
	}
	public static String patternOfCountryCodes() {
		return CollectionUtils.toStringSeparatedWith(FluentIterable.from(Language.values())
														   .filter(new Predicate<Language>() {
																			@Override
																			public boolean apply(final Language lang) {
																				return lang != ANY;
																			}
														   		   })
														   .transform(new Function<Language,String>() {
																				@Override
																				public String apply(final Language lang) {
																					return Languages.countryLowerCase(lang);
																				}
														   			  })
														   .toList(),
										            '|');
	}
	public static boolean canBe(final String lang) {
		return WRAPPER.canBe(lang);
	}
	public static boolean canBe(final int code) {
		return WRAPPER.canBeFromCode(code);
	}
}
