#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractor;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

/**
 * Supported languages.
 * @author gsikorskiy
 *
 */
public enum Language {

	/**
	 * French.
	 */
	FR("French"),
	/**
	 * Spanish.
	 */
	ES("Spanish"),
	/**
	 * English.
	 */
	EN("English");

	private final String fullName;

	Language(final String fullName) {
		this.fullName = fullName;
	}


	private LanguageProfile getProfile() {
		try {
			return new LanguageProfileReader().readBuiltIn(LdLocale.fromString(this.toString()));
		} catch (final IOException e) {
			throw new RuntimeException("Language " + this + " profile not found.", e);
		}
	}

	/**
	 * @return full name of language.
	 */
	public String getFullName() {
		return this.fullName;
	}

	/**
	 * Methods checks that text belongs to language.
	 * @param text - string to analyze
	 * @return true if text is recognized.
	 */
	public boolean verify(final String text) {
		final List<LanguageProfile> profiles = new ArrayList<>();
		for (final Language item : Language.values()) {
			profiles.add(item.getProfile());
		}

		final LanguageDetector languageDetector = LanguageDetectorBuilder
				.create(NgramExtractor.gramLength(2))
				.withProfiles(profiles)
				.build();

		final TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
		final TextObject textObject = textObjectFactory.forText(text);
		final String lang = languageDetector.detect(textObject).get().getLanguage();
		return this.toString().equals(lang);
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
