package r01f.persistence.index.document;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import r01f.exceptions.Throwables;
import r01f.model.IndexableModelObject;
import r01f.model.metadata.IndexableFieldID;
import r01f.patterns.Memoized;

@RequiredArgsConstructor
public abstract class IndexDocumentBase<M extends IndexableModelObject> 
		   implements IndexDocument<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Caches field values
	 */
	private Memoized<Map<IndexableFieldID,IndexDocumentFieldValue<?>>> _fields = new Memoized<Map<IndexableFieldID,IndexDocumentFieldValue<?>>>() {
																						@Override
																						protected Map<IndexableFieldID,IndexDocumentFieldValue<?>> supply() {
																							return IndexDocumentBase.this.getFields();
																						}
																		   		 };
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <T> IndexDocumentFieldValue<T> getField(final IndexableFieldID metaDataId) {
		return (IndexDocumentFieldValue<T>)_fields.get()
					  							  .get(metaDataId);
	}
	@Override
	public <T> T getFieldValue(final IndexableFieldID metaDataId) {
		IndexDocumentFieldValue<T> indexedField = this.getField(metaDataId);
		return indexedField != null ? indexedField.getValue() : null;
	}
	@Override @SuppressWarnings("unchecked")
	public <T> IndexDocumentFieldValue<T> getFieldOrThrow(final IndexableFieldID metaDataId) {
		IndexDocumentFieldValue<T> outField = (IndexDocumentFieldValue<T>)_fields.get()
					  							  								 .get(metaDataId);
		if (outField == null) throw new IllegalStateException(Throwables.message("The indexed document does NOT contains a field with name {}",
																				 metaDataId));
		return outField;
	}
	@Override
	public <T> T getFieldValueOrThrow(final IndexableFieldID metaDataId) {
		return this.<T>getFieldOrThrow(metaDataId)
				   .getValue();
	}

}
