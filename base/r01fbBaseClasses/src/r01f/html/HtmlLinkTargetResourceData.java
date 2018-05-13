package r01f.html;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.locale.Language;
import r01f.mime.MimeType;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Descriptor for the target resource (destination) of a link
 * Usage:
 * <pre class='brush:java'>
 * 		HtmlLinkTargetResourceData targetResourceData = HtmlLinkTargetResourceData.create()
 * 																.relatedAas(LICENSE)
 * 																.withLang(Language.ENGLISH)
 * 																.withMimeType(MimeTypeForDocument.PDF);
 * </pre>
 */
@ConvertToDirtyStateTrackable
@MarshallType(as="linkTargetResourceData")
@Accessors(prefix="_")
public class HtmlLinkTargetResourceData {
	/**
	 * Relation between the document containing the link and the document target of the link
	 */
	@MarshallField(as="relationWithSource",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private RelationBetweenTargetAndLinkContainerDocuments _relationWithSource;
	/**
	 * Language of the target document
	 */
	@MarshallField(as="lang",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Language _language;
	/**
	 * Mime-Type of the destination document
	 */
	@MarshallField(as="mimeType",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MimeType _mimeType;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public HtmlLinkTargetResourceData() {
		// default no-args constructor
	}
	public HtmlLinkTargetResourceData(final RelationBetweenTargetAndLinkContainerDocuments relWithSource,
									  final Language lang,
									  final MimeType mime) {
		_relationWithSource = relWithSource;
		_language = lang;
		_mimeType = mime;
	}
	public HtmlLinkTargetResourceData(final HtmlLinkTargetResourceData other) {
		_relationWithSource = other.getRelationWithSource();
		_language = other.getLanguage();
		_mimeType = other.getMimeType() != null ? new MimeType(other.getMimeType()) : null;
	}
	public static HtmlLinkTargetResourceData create() {
		HtmlLinkTargetResourceData outData = new HtmlLinkTargetResourceData();
		return outData;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public HtmlLinkTargetResourceData withLang(final Language lang) {
		_language = lang;
		return this;
	}
	public HtmlLinkTargetResourceData relatedAs(final RelationBetweenTargetAndLinkContainerDocuments rel) {
		_relationWithSource = rel;
		return this;
	}
	public HtmlLinkTargetResourceData withMimeType(final MimeType mime) {
		_mimeType = mime;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Relation between the document containing the link and the document target of the link
	 */
	public static enum RelationBetweenTargetAndLinkContainerDocuments 
		    implements EnumExtended<RelationBetweenTargetAndLinkContainerDocuments> {
		ALTERNATE,
		AUTHOR,
		BOOKMARK,
		HELP,
		LICENSE,
		NEXT,
		PREV,
		SEARCH,
		TAG,
		NOFOLLOW,
		NOREFERRER,
		PREFETCH;
		
		private static final EnumExtendedWrapper<RelationBetweenTargetAndLinkContainerDocuments> WRAPPER = EnumExtendedWrapper.wrapEnumExtended(RelationBetweenTargetAndLinkContainerDocuments.class);
		
		public static RelationBetweenTargetAndLinkContainerDocuments fromName(final String name) {
			return WRAPPER.fromName(name.toUpperCase());
		}
		public static boolean canBe(final String name) {
			return WRAPPER.canBe(name.toUpperCase());
		}
		@Override
		public boolean isIn(final RelationBetweenTargetAndLinkContainerDocuments... els) {
			return WRAPPER.isIn(this,els);
		}
		@Override
		public boolean is(final RelationBetweenTargetAndLinkContainerDocuments el) {
			return WRAPPER.is(this,el);
		}
	}
}
