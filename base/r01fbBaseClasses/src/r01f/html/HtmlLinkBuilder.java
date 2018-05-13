package r01f.html;

import com.google.common.base.Preconditions;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import r01f.html.HtmlLinkTargetResourceData.RelationBetweenTargetAndLinkContainerDocuments;
import r01f.html.HtmlLinkWindowOpeningMode.OpeningWindowAppearance;
import r01f.html.HtmlLinkWindowOpeningMode.OpeningWindowBars;
import r01f.html.HtmlLinkWindowOpeningMode.OpeningWindowMode;
import r01f.locale.Language;
import r01f.mime.MimeType;
import r01f.patterns.IsBuilder;
import r01f.types.url.Url;

/**
 * Builder for {@link HtmlLink} objects
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
public abstract class HtmlLinkBuilder 
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static HtmlLinkBuilderTextStep htmlLinkBuilderFor(final Url url) {
		Preconditions.checkArgument(url != null,"The url MUST NOT be null");
		return new HtmlLinkBuilder() { /* nothing */ }
						.new HtmlLinkBuilderTextStep(new HtmlLink(url));
	}
	public static HtmlLinkPresentationDataBuilder htmlPresentationDataBuilder() {
		return new HtmlLinkBuilder() { /* nothing */ }
						.new HtmlLinkPresentationDataBuilder(new HtmlLinkPresentationData());
	}
	public static HtmlLinkWindowOpeningModeBuilder htmlLinkOpenInWindowWithName(final String name) {
		return new HtmlLinkBuilder() { /* nothing */ }
						.new HtmlLinkWindowOpeningModeBuilder(new HtmlLinkWindowOpeningMode(name));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LINK
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkBuilderTextStep {
		private final HtmlLink _link;

		public HtmlLink build() {
			return _link;
		}
		public HtmlLinkBuilderTextStep withText(final String text) {
			_link.setText(text);
			return this;
		}
		public HtmlLinkBuilderTextStep withText(final String text,final Language lang) {
			_link.setText(text);
			_link.setLang(lang);
			return this;
		}
		public HtmlLinkBuilderTextStep withText(final String text,final String title,final Language lang) {
			_link.setText(text);
			_link.setPresentation(new HtmlLinkPresentationData()
											.withTitle(title));	
			_link.setLang(lang);
			return this;
		}
		public HtmlLinkBuilderTextStep presentationData(final HtmlLinkPresentationData presentationData) {
			_link.setPresentation(presentationData);
			return this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HtmlLinkPresentationDataBuilder
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkPresentationDataBuilder {
		private final HtmlLinkPresentationData _presentation;
		
		public HtmlLinkPresentationDataBuilderTargetStep withId(final HtmlElementId id) {
			_presentation.withId(id);
			return new HtmlLinkPresentationDataBuilderTargetStep(_presentation);
		}
		public HtmlLinkPresentationDataBuilderTargetStep noId() {
			return new HtmlLinkPresentationDataBuilderTargetStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkPresentationDataBuilderTargetStep {
		private final HtmlLinkPresentationData _presentation;
		
		public HtmlLinkPresentationDataBuilderAccessKeyStep targetResource(final RelationBetweenTargetAndLinkContainerDocuments relation,
																		   final Language lang,
																		   final MimeType mimeType) {
			_presentation.setTargetResourceData(new HtmlLinkTargetResourceData(relation,
																			   lang,
																			   mimeType));
			return new HtmlLinkPresentationDataBuilderAccessKeyStep(_presentation);
		}
		public HtmlLinkPresentationDataBuilderAccessKeyStep targetResource(final RelationBetweenTargetAndLinkContainerDocuments relation,
																		   final Language lang) {
			_presentation.setTargetResourceData(new HtmlLinkTargetResourceData(relation,
																			   lang,
																			   null));		// no mime-type
			return new HtmlLinkPresentationDataBuilderAccessKeyStep(_presentation);
		}
		public HtmlLinkPresentationDataBuilderAccessKeyStep targetResource(final Language lang,
																		   final MimeType mimeType) {
			_presentation.setTargetResourceData(new HtmlLinkTargetResourceData(null,		// no relationO
																			  lang,
																			  mimeType));
			return new HtmlLinkPresentationDataBuilderAccessKeyStep(_presentation);
		}
		public HtmlLinkPresentationDataBuilderAccessKeyStep targetResource(final Language lang) {
			_presentation.setTargetResourceData(new HtmlLinkTargetResourceData(null,	// no relation
																			   lang,
																			   null));	// no mime-type
			return new HtmlLinkPresentationDataBuilderAccessKeyStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkPresentationDataBuilderAccessKeyStep {
		private final HtmlLinkPresentationData _presentation;
		
		public HtmlLinkPresentationDataBuilderWindowOpeningStep withAccessKey(final HtmlLinkAccessKey accessKey) {
			_presentation.setAccessKey(accessKey);
			return new HtmlLinkPresentationDataBuilderWindowOpeningStep(_presentation);
		}
		public HtmlLinkPresentationDataBuilderWindowOpeningStep withoutAccessKey() {
			return new HtmlLinkPresentationDataBuilderWindowOpeningStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkPresentationDataBuilderWindowOpeningStep {
		private final HtmlLinkPresentationData _presentation;
		
		public HtmlLinkPresentationDataBuilderCSSStylesStep newWindowWith(final HtmlLinkWindowOpeningMode newWindowOpeningMode) {
			_presentation.setNewWindowOpeningMode(newWindowOpeningMode);
			return new HtmlLinkPresentationDataBuilderCSSStylesStep(_presentation);
		}
		public HtmlLinkPresentationDataBuilderCSSStylesStep sameWindow() {
			return new HtmlLinkPresentationDataBuilderCSSStylesStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkPresentationDataBuilderCSSStylesStep {
		private final HtmlLinkPresentationData _presentation;
		
		public HtmlLinkPresentationDataBuilderJSEventsStep withStylesWithClassNames(final String...  names) {
			_presentation.withStyleClasses(names);
			return new HtmlLinkPresentationDataBuilderJSEventsStep(_presentation);
		}
		public HtmlLinkPresentationDataBuilderJSEventsStep withoutStyleClassNames() {
			return new HtmlLinkPresentationDataBuilderJSEventsStep(_presentation);
		}		
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkPresentationDataBuilderJSEventsStep {
		private final HtmlLinkPresentationData _presentation;
		
		public HtmlLinkPresentationDataBuilderMediaQueryStep withJSEvents(final HtmlElementJSEvent...  events) {
			_presentation.withJavaScriptEvents(events);
			return new HtmlLinkPresentationDataBuilderMediaQueryStep(_presentation);
		}
		public HtmlLinkPresentationDataBuilderMediaQueryStep withoutJSEvents() {
			return new HtmlLinkPresentationDataBuilderMediaQueryStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkPresentationDataBuilderMediaQueryStep {
		private final HtmlLinkPresentationData _presentation;
		
		public HtmlLinkPresentationDataBuilderBuildStep withOrCombinedMediaQueries(final MediaQuery... mediaQueries) {
			_presentation.withOrCombinedMediaQueries(mediaQueries);
			return new HtmlLinkPresentationDataBuilderBuildStep(_presentation);
		}
		public HtmlLinkPresentationDataBuilderBuildStep withoutMediaQueries() {
			return new HtmlLinkPresentationDataBuilderBuildStep(_presentation);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkPresentationDataBuilderBuildStep {
		private final HtmlLinkPresentationData _presentation;
		
		public HtmlLinkPresentationData build() {
			return _presentation;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HtmlLinkWindowOpeningMode
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkWindowOpeningModeBuilder {
		private final HtmlLinkWindowOpeningMode _opening;
		
		public HtmlLinkWindowOpeningModeBuilderResizableStep centeredWithDimensions(final int width,final int height) {
	    	OpeningWindowAppearance appearance = new OpeningWindowAppearance();
	    	appearance.setOpeningMode(OpeningWindowMode.CENTERED);
	    	_opening.setAppearance(appearance);
	    	return new HtmlLinkWindowOpeningModeBuilderResizableStep(_opening);
		}
		public HtmlLinkWindowOpeningModeBuilderResizableStep maximized() {
	    	OpeningWindowAppearance appearance = new OpeningWindowAppearance();
	    	appearance.setOpeningMode(OpeningWindowMode.MAXIMIZED);
			_opening.setAppearance(appearance);
			return new HtmlLinkWindowOpeningModeBuilderResizableStep(_opening);
		}
		public HtmlLinkWindowOpeningModeBuilderPositionStep withDimensions(final int width,final int height) {
	    	OpeningWindowAppearance appearance = new OpeningWindowAppearance();
	    	appearance.setOpeningMode(OpeningWindowMode.SIMPLE);
			_opening.setAppearance(appearance);
			_opening.getAppearance().setWidth(width);
			_opening.getAppearance().setHeight(height);
			return new HtmlLinkWindowOpeningModeBuilderPositionStep(_opening);
		}		
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkWindowOpeningModeBuilderPositionStep {
		private final HtmlLinkWindowOpeningMode _opening;
		
		public HtmlLinkWindowOpeningModeBuilderResizableStep locatedAt(final int pixelsToTheRightFromUpperLeftCorner,final int pixelsToTheBottomFromTheUpperLeftCorner) {
			_opening.getAppearance().setX(pixelsToTheRightFromUpperLeftCorner);
			_opening.getAppearance().setY(pixelsToTheBottomFromTheUpperLeftCorner);
			return new HtmlLinkWindowOpeningModeBuilderResizableStep(_opening);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkWindowOpeningModeBuilderResizableStep {
		private final HtmlLinkWindowOpeningMode _opening;
		
		public HtmlLinkWindowOpeningModeBuilderBarsStep resizable() {
			_opening.getAppearance().setResizable(true);
			return new HtmlLinkWindowOpeningModeBuilderBarsStep(_opening);
		}
		public HtmlLinkWindowOpeningModeBuilderBarsStep notResizable() {
			_opening.getAppearance().setResizable(false);
			return new HtmlLinkWindowOpeningModeBuilderBarsStep(_opening);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkWindowOpeningModeBuilderBarsStep {
		private final HtmlLinkWindowOpeningMode _opening;
		
		public HtmlLinkWindowOpeningModeBuilderShowNewWindowIconStep withBars(final OpeningWindowBars bars) {
			_opening.getAppearance().setBars(bars);
			return new HtmlLinkWindowOpeningModeBuilderShowNewWindowIconStep(_opening);
		}
		public HtmlLinkWindowOpeningModeBuilderShowNewWindowIconStep withDefaultBars() {
			return new HtmlLinkWindowOpeningModeBuilderShowNewWindowIconStep(_opening);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkWindowOpeningModeBuilderShowNewWindowIconStep {
		private final HtmlLinkWindowOpeningMode _opening;
		
		public HtmlLinkWindowOpeningModeBuilderBuildStep showNewWindowIcon() {
			_opening.setShowNewWindowIcon(true);
			return new HtmlLinkWindowOpeningModeBuilderBuildStep(_opening);
		}
		public HtmlLinkWindowOpeningModeBuilderBuildStep doNOTShowNewWindowIcon() {
			_opening.setShowNewWindowIcon(false);
			return new HtmlLinkWindowOpeningModeBuilderBuildStep(_opening);
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class HtmlLinkWindowOpeningModeBuilderBuildStep {
		private final HtmlLinkWindowOpeningMode _opening;
		
		public HtmlLinkWindowOpeningMode build() {
			return _opening;
		}
	}	
}
