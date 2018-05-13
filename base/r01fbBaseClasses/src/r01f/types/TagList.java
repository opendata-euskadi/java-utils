package r01f.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.Taggeable;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

/**
 * Tag container
 * @param <T> the tag type: it can be a simple String or a more complex type
 */
@MarshallType(as="tags")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public class TagList 
     extends LinkedHashSet<String>
  implements Taggeable<String> {
	
	private static final long serialVersionUID = 8637238076951337091L;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public TagList() {
		// no-args constructor
	}
	/**
	 * @param tags
	 */
	public TagList(final Collection<String> tags) {
		this.addAll(Sets.newHashSet(tags));
	}
	/**
	 * @param tags
	 */
	public TagList(final String... tags) {
		this.addAll(Arrays.asList(tags));
	}
	/**
	 * @param size
	 */
	public TagList(final int size) {
		// marshaller needs this method
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  R01MTaggeableModelObject interface
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean containsTag(final String tag) {
		return this.contains(tag);
	}
	@Override
	public boolean containsAllTags(final String... tags) {
		return this.containsAll(Arrays.asList(tags));
	}
	@Override
	public boolean containsAllTags(final Collection<String> tags) {
		return this.containsAll(tags);
	}
	@Override
	public boolean addTag(final String tag) {
		return this.add(tag);
	}
	@Override
	public boolean addTags(final Collection<String> tags) {
		if (CollectionUtils.isNullOrEmpty(tags)) return false;
		return this.addAll(tags);
	}
	@Override
	public boolean addTags(final String... tags) {
		if (CollectionUtils.isNullOrEmpty(tags)) return false;
		return this.addAll(Arrays.asList(tags));
	}
	@Override
	public boolean removeTag(final String tag) {
		return this.remove(tag);
	}
	@Override
	public void clearTags() {
		this.clear(); 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asStringSeparatedWith(final char sep) {
		if (this.isEmpty()) return "";
		
		StringBuilder outSb = new StringBuilder(this.size() * 30);	// try to estimate the initial size
		Iterator<String> tagIt = FluentIterable.from(this)
											   .toSortedSet(Ordering.natural())	// order the tags
											   .iterator();
		do {
			outSb.append(tagIt.next());
			if (tagIt.hasNext()) outSb.append(sep);
		} while(tagIt.hasNext());
		return outSb.toString();
	}
	@Override
	public String asStringQuotedAndSeparatedWith(final char startQuote,final char endQuote,
												 final char sep) {
		if (this.isEmpty()) return "";
		
		StringBuilder outSb = new StringBuilder(this.size() * 32);	// try to estimate the initial size
		Iterator<String> tagIt = FluentIterable.from(this)
											   .toSortedSet(Ordering.natural())	// order the tags
											   .iterator();
		do {
			outSb.append(startQuote)
				 .append(tagIt.next())
				 .append(endQuote);
			if (tagIt.hasNext()) outSb.append(sep);
		} while(tagIt.hasNext());
		return outSb.toString();
	}
}
