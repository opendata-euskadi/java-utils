package r01f.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.Sets;

import r01f.locale.Language;
import r01f.types.url.Url;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;


/**
 * Renders html link presentation data
 * <pre class='brush:java'>
 * 		String html = HtmlLinkRenderer.render("This is my linked text",
 * 											  url,
 * 											  presentation);
 * </pre>
 * If you want to include the link XML as an XML data island use:
 * <pre class='brush:java'>
 * 		String html = HtmlLinkRenderer.render("This is my linked text",
 * 											  url,
 * 											  presentation,
 * 											  xml);
 * </pre>
 */
@GwtIncompatible
public class HtmlLinkRenderer {
///////////////////////////////////////////////////////////////////////////////
// 	FIELDS
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
	public HtmlLinkRenderer() {
		// no-args constructor
	}
///////////////////////////////////////////////////////////////////////////////
//
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Renders a link
	 * @param data
	 * @return
	 */
	public String render(final HtmlLink data) {
		return this.render(data.getText(),
						   data.getUrl(),
						   data.getPresentation());
	}
	/**
	 * Renders a link
	 * @param linkText
	 * @param url
	 * @param presentation
	 * @return
	 */
	public String render(final String linkText,
						 final Url url,
						 final HtmlLinkPresentationData presentation) {
		String outLink = Strings.customized("<a href='{}' {}>{}</a>",
									   		url.asStringNotUrlEncodingQueryStringParamsValues(),
									   		_renderPresentationData(presentation),
									   		linkText);
		return outLink;
	}
	/**
	 * Renders a link alongside with an XML representation of the link in a data island
	 * (see https://developer.mozilla.org/en/docs/Using_XML_Data_Islands_in_Mozilla)
	 * @param linkText
	 * @param url
	 * @param presentation
	 * @param xmlData
	 * @return
	 */
	public String renderWithData(final String linkText,
								 final Url url,
								 final HtmlLinkPresentationData presentation,
								 final String xmlData) {
		// An id for the link is mandatory so if none is provided one is generated
		HtmlElementId id = presentation != null ? presentation.getId() : null;
		if (id != null) id = HtmlElementId.supply();
		String outLink = Strings.customized("<a href='{}' {}>{}</a>\n" +
											"<script id='{}_data' type='application/xml'>\n{}\n</script>\n",
											url.asStringNotUrlEncodingQueryStringParamsValues(),
											_renderPresentationData(presentation),
											linkText,
											id,xmlData);		// data island
		return outLink;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _renderPresentationData(final HtmlLinkPresentationData data) {
		if (data == null) return "";

		// >>> [1] - Presentation data
		Collection<String> presentationDataParams = _presentationDataParams(data);

		// >>> [2] - Target resource data
		Collection<String> targetDataParams = _targetResourceDataParams(data.getTargetResourceData());

		// >>> [3] - window opening features
		Collection<String> windowOpeningParam = _windowOpeningModeParams(data.getNewWindowOpeningMode());

		Collection<String> allParams = Sets.newHashSet();
		if (CollectionUtils.hasData(presentationDataParams)) allParams.addAll(presentationDataParams);
		if (CollectionUtils.hasData(targetDataParams)) allParams.addAll(targetDataParams);
		if (CollectionUtils.hasData(windowOpeningParam)) allParams.addAll(windowOpeningParam);

		// ---- Return
		return _partsToString(allParams);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static Collection<String> _presentationDataParams(final HtmlLinkPresentationData data) {
		if (data == null) return null;

		Collection<String> params = new ArrayList<String>();

		// Id
		if (data.getId() != null) {
			params.add(Strings.customized("id='{}'",
							 		 	  data.getId()));
		}
		// Title
		if (Strings.isNOTNullOrEmpty(data.getTitle())) {
			params.add(Strings.customized("title='{}'",
							  		 	  data.getTitle().trim()));
		}
		// style classes
		if (CollectionUtils.hasData(data.getStyleClasses())) {
			StringBuilder sb = new StringBuilder(data.getStyleClasses().size() * 10);
			for (Iterator<CSSStyleClassName> styleIt = data.getStyleClasses().iterator(); styleIt.hasNext(); ) {
				CSSStyleClassName style = styleIt.next();
				sb.append(style);
				if (styleIt.hasNext()) sb.append(" ");
			}
			params.add(Strings.customized("class='{}'",
							  		      sb));
		}
		// inline style
		if (Strings.isNOTNullOrEmpty(data.getInlineStyle())) {
			params.add(Strings.customized("style='{}'",
							  		 	  data.getInlineStyle().trim()));
		}
		// JavaScript events
		if (CollectionUtils.hasData(data.getJavaScriptEvents())) {
			StringBuilder sb = new StringBuilder(data.getJavaScriptEvents().size() * 30);
			for (Iterator<HtmlElementJSEvent> eventIt = data.getJavaScriptEvents().iterator(); eventIt.hasNext(); ) {
				HtmlElementJSEvent event = eventIt.next();
				sb.append(Strings.customized("{}='{}'",
								 			 event.getEvent().getCode(),
								 			 event.getJsCode()));
				if (eventIt.hasNext()) sb.append(" ");
			}
			params.add(sb.toString());
		}
		return params;
	}
	private static Collection<String> _targetResourceDataParams(final HtmlLinkTargetResourceData resData) {
		if (resData == null) return null;

		Collection<String> parts = new ArrayList<String>();

		if (resData.getLanguage() != null) {
			String langStr = _languageAsString(resData.getLanguage());
			if (Strings.isNOTNullOrEmpty(langStr)) parts.add(Strings.customized("lang='{}'",
							 			 										langStr));
		}
		if (resData.getRelationWithSource() != null) {
			parts.add(Strings.customized("rel='{}'",
							 			 resData.getRelationWithSource().name().toLowerCase()));
		}
		if (resData.getMimeType() != null) {
			parts.add(Strings.customized("type='{}'",
							 			 resData.getMimeType()));
		}

		// ---- Return
		return parts;
	}
	private static Collection<String> _windowOpeningModeParams(final HtmlLinkWindowOpeningMode openMode) {
		Collection<String> parts = new ArrayList<String>();

		// TODO terminar el renderizado del modo de apertura del enlace
		if (openMode != null) {
			/* todo terminar */
		}

		// ---- Return
		return parts;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _partsToString(final Collection<String> parts) {
		System.out.println(">>>" + parts);
		// ---- Return
		StringBuilder outSb = null;
		if (CollectionUtils.hasData(parts)) {
			outSb = new StringBuilder(parts.size()*100);
			for (Iterator<String> partIt = parts.iterator(); partIt.hasNext(); ) {
				outSb.append(partIt.next());
				if (partIt.hasNext()) outSb.append(" ");
			}
		}
		return outSb != null ? outSb.toString() : null;
	}
	/**
	 * Cannot use
	 * <pre class='brush:java'>
	 * 		Languages.language(lang)
	 * </pre>
	 * since Languages is NOT GWT compatible
	 * @param lang
	 * @return
	 */
	private static String _languageAsString(final Language lang) {
		String outLang = Languages.language(lang);
		return outLang;

//		switch(lang) {
//		case ANY:
//			outLang = null;
//			break;
//		case BASQUE:
//			outLang = "eu";
//			break;
//		case SPANISH:
//			outLang = "es";
//			break;
//		case ENGLISH:
//			outLang = "en";
//			break;
//		case FRENCH:
//			outLang = "fr";
//			break;
//		case DEUTCH:
//			outLang = "de";
//			break;
//		case ITALIAN:
//			outLang = "it";
//			break;
//		case PORTUGUESE:
//			outLang = "pt";
//			break;
//		case CZECH:
//			outLang = "cz";
//			break;
//		case HUNGARIAN:
//			outLang = "hu";
//			break;
//		case JAPANESE:
//			outLang = "jp";
//			break;
//		case KOREAN:
//			outLang = "ko";
//			break;
//		case POLISH:
//			outLang = "po";
//			break;
//		case ROMANIAN:
//			outLang = "ro";
//			break;
//		case RUSSIAN:
//			outLang = "ru";
//			break;
//		case SWEDISH:
//			outLang = "sw";
//			break;
//		default:
//			outLang = null;
//			break;
//		}
//		return outLang;
	}
}
