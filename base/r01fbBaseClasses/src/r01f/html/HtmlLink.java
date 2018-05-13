package r01f.html;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.HasLanguage;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.url.Url;
import r01f.util.types.Strings;

/**
 * Models an HTML link
 * @see HtmlLinkBuilder
 * Usage:
 * <pre class="brush:java'>
 *	HtmlLink link = HtmlLinkBuilder.htmlLinkBuilderFor(Url.from("www.google.com"))
 *										.withText("Google",Language.SPANISH)
 *										.presentationData(HtmlLinkBuilder.htmlPresentationDataBuilder()
 *																	.withId(HtmlElementId.forId("myId"))
 *																	.targetResource(Language.SPANISH)
 *																	.withoutAccessKey()
 *																	.newWindowWith(HtmlLinkBuilder.htmlLinkOpenInWindowWithName("newWindow")
 *																								  .centeredWithDimensions(100,100)
 *																								  .resizable()
 *																								  .withDefaultBars()
 *																								  .showNewWindowIcon()
 *																								  .build())
 *																	.withoutStyleClassNames()
 *																	.withoutJSEvents()
 *																	.withoutMediaQueries()
 *																	.build())
 *										.build();
 * </pre>
 */
@MarshallType(as="htmlLink")
@Accessors(prefix="_")
public class HtmlLink 
  implements HasLanguage {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The URL
	 * Use R01MUrlBuilder.from(_url) to get a typed R01MUrl
	 */
	@MarshallField(as="url")
	@Getter private Url _url;
	/**
	 * Lang
	 */
	@MarshallField(as="lang",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _lang;
	/**
	 * The text
	 */
	@MarshallField(as="text",escape=true)
	@Getter @Setter private String _text;
    /**
     * link presentation
     */
	@MarshallField(as="presentation")
    @Getter @Setter private HtmlLinkPresentationData _presentation;
	/**
	 * The url to which the user is redirected BEFORE accessing the final url
	 * When the link is composed, the user is redirected to this url where the system can
	 * for example:
	 * 		a.- Collect user info 
	 * 		b.- log the url access (ie: ip, counting access, etc)
	 * 		c.- etc
	 * once this is done the user is redirected to the final url
	 * NOTA: The final url is provided to this url in a param as: ?R01PassThrough=[final url]
	 */
	@MarshallField(as="prePassThroughURL")
	@Getter @Setter private Url _prePassThroughURL;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlLink() {
		// default no-args constructor
	}
	public HtmlLink(final HtmlLink other) {
		_url = new Url(other.getUrl());
		_lang = other.getLang();
		_text = Strings.isNOTNullOrEmpty(other.getText()) ? new String(other.getText()) : null;
		_presentation = other.getPresentation() != null ? new HtmlLinkPresentationData(other.getPresentation()) : null;
		_prePassThroughURL = other.getPrePassThroughURL() != null ? new Url(other.getPrePassThroughURL()) : null;		
	}
	public HtmlLink(final Url url) {
		_url = url;
		_text = url.asStringNotUrlEncodingQueryStringParamsValues();
	}
	public HtmlLink(final Url url,
					final String text) {
		_url = url;
		_text = text;
	}
	public HtmlLink(final Url url,
					final String text,
					final HtmlLinkPresentationData presentation) {
		_text = text;
		_url = url;
		_presentation = presentation;
	}
	public HtmlLink(final Url url,
					final String text,
					final HtmlLinkPresentationData presentation,
					final Url prePassThroughURL) {
		_text = text;
		_url = url;
		_presentation = presentation;
		_prePassThroughURL = prePassThroughURL;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HAS LANGUAGE
/////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Language getLanguage() {
		return _lang;
	}
	@Override
	public void setLanguage(final Language lang) {
		_lang = lang;
	}
}
