package r01f.types.url;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.LangNamed;
import r01f.facets.LangNamed.HasLangNamedFacet;
import r01f.facets.StringTagged;
import r01f.facets.StringTagged.HasTaggeableFacet;
import r01f.facets.delegates.LangNamedDelegate;
import r01f.facets.delegates.TaggeableDelegate;
import r01f.html.HtmlLinkPresentationData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.TagList;
import r01f.util.types.collections.CollectionUtils;

@ConvertToDirtyStateTrackable
@MarshallType(as="urlCollectionItem")
@Accessors(prefix="_")
public class UrlCollectionItem 
  implements Serializable,
  			 HasLangNamedFacet,
  			 HasTaggeableFacet {
	
	private static final long serialVersionUID = 4310914046678104811L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The url title
	 */
	@MarshallField(as="name",escape=true)
	@Getter @Setter private String _name;
	/**
	 * The url 
	 */
	@MarshallField(as="url")
	@Getter @Setter private Url _url;
    /**
     * link presentation
     */
	@MarshallField(as="presentation")
    @Getter private HtmlLinkPresentationData _presentation;
	/**
	 * Tags
	 */
	@MarshallField(as="tags",
				   whenXml=@MarshallFieldAsXml(collectionElementName="tag"))
	@Getter @Setter private TagList _tags;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlCollectionItem() {
		// default no-args constructor
	}
	public UrlCollectionItem(final String name,final Url url,
							 final String... tags) {
		_name = name;
		_url = url;
		if (CollectionUtils.hasData(tags)) _tags = new TagList(tags);
	}
	public UrlCollectionItem(final String name,final Url url,
							 final TagList tags) {
		_name = name;
		_url = url;
		_tags = tags;
	}
	public UrlCollectionItem(final String name,final Url url,
							 final HtmlLinkPresentationData urlPresentation,
							 final String... tags) {
		this(name,
			 url,
			 tags);
		_presentation = urlPresentation;
	}
	public UrlCollectionItem(final String name,final Url url,
							 final HtmlLinkPresentationData urlPresentation,
							 final TagList tags) {
		this(name,
			 url,
			 tags);
		_presentation = urlPresentation;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Facets
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public LangNamed asLangInDependentNamed() {
		return new LangNamedDelegate<UrlCollectionItem>(this);
	}
	@Override
	public StringTagged asTaggeable() {
		return new TaggeableDelegate<UrlCollectionItem>(this);
	}
}
