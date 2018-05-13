package r01f.persistence.search.db;

import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.IndexableFieldID;
import r01f.model.search.SearchFilter;
import r01f.util.types.Strings;

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
		String outFieldName = null;
		if (fieldId.is(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID.getFieldId())) {
			outFieldName = "_oid";
		}
		if (Strings.isNullOrEmpty(outFieldName)) throw new IllegalStateException("NO db entity field for indexable field '" + fieldId + "'");
		return outFieldName;
	}

}
