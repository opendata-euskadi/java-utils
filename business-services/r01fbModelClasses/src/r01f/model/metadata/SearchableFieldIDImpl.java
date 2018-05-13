package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Joiner;

import lombok.Getter;
import lombok.experimental.Accessors;

@GwtIncompatible
@Accessors(prefix="_")
public final class SearchableFieldIDImpl 
         implements SearchableFieldID {
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final IndexableFieldID _fieldId;
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SearchableFieldIDImpl(final SearchableFieldID... ids) {
		Joiner joiner = Joiner.on(".").skipNulls();
		joiner.join(ids);
		_fieldId = IndexableFieldID.forId(joiner.toString());
	}
	
}
