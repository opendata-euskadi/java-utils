package r01f.persistence.search.db;

import r01f.model.metadata.IndexableFieldID;
import r01f.model.search.SearchFilter;

/**
 * Default indexable field id to DB entity field name
 */
public class IndexableFieldIDToDBEntityFieldTranslatorByDefault<F extends SearchFilter>  
  implements TranslatesIndexableFieldIDToDBEntityField<F> {
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String dbEntityFieldNameFor(final IndexableFieldID fieldId,
									   final F filter) {
		String outFieldName = fieldId.getId();
		return outFieldName;
	}

}
