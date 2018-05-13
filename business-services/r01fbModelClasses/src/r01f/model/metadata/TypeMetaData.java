package r01f.model.metadata;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.facets.Facet;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.patterns.Memoized;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.collections.CollectionUtils;

@GwtIncompatible
@Accessors(prefix="_")
@RequiredArgsConstructor
public class TypeMetaData<M extends MetaDataDescribable> 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Type _type;				// the metadata-describable model object type 
	@Getter private final TypeToken<? extends HasMetaDataForModelObject> _annotatedType;	// the type describing the model object
	@Getter private final MetaDataForType _typeMetaData;									// the annotation info for the model object
	@Getter private final Set<TypeMetaData<? extends MetaDataDescribable>> _facets;			// other facets
	@Getter private final Collection<TypeFieldMetaData> _fieldsMetaData = Sets.newLinkedHashSet();		// the model object's fields
	
	@SuppressWarnings("unchecked")
	public Class<M> getRawType() {
		Class<?> outRawType = TypeToken.of(_type)
									   .getRawType();
		return (Class<M>)outRawType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FACET UTILS
/////////////////////////////////////////////////////////////////////////////////////////	
    private Memoized<Map<Class<?>,Long>> _typeFacetsCodes =
    			new Memoized<Map<Class<?>,Long>>() {
						@Override
						protected Map<Class<?>,Long> supply() {
							Map<Class<?>,Long> outFacets = null;
							// a) add the supertypes & interface facets
							if (CollectionUtils.hasData(_facets)) {
								outFacets = Maps.newHashMapWithExpectedSize(_facets.size() + 1);
								for (TypeMetaData<? extends MetaDataDescribable> facet : _facets) {
									outFacets.put(facet.getRawType(),
												  facet.getTypeMetaData().modelObjTypeCode());
								}
							} else {
								outFacets = Maps.newHashMapWithExpectedSize(1);
							}
							// b) add the self-type as facet
							outFacets.put(TypeMetaData.this.getRawType(),
										  TypeMetaData.this.getTypeMetaData().modelObjTypeCode());
							return outFacets;
						}
    			};
	public Collection<Long> getTypeFacetsCodes() {
		return _typeFacetsCodes.get().values();
	}
	private Memoized<Set<Class<?>>> _facetTypes =
				new Memoized<Set<Class<?>>>() {
						@Override
						protected Set<Class<?>> supply() {
							return FluentIterable.from(_facets)
												 .transform(new Function<TypeMetaData<? extends MetaDataDescribable>,Class<?>>() {
																	@Override
																	public Class<?> apply(final TypeMetaData<? extends MetaDataDescribable> typeMetaData) {
																		return typeMetaData.getRawType();
																	}
														     })
												 .toSet();
						}
				};
	/**
	 * Browses the model object hierarchy to check if the given facet is implemented 
	 * @param facetType
	 * @return
	 */
	public <F extends Facet> boolean hasFacet(final Class<F> facetType) {
		// Try to find the first ModelObjectMetaData assignable to the interface
		if (ReflectionUtils.isImplementing(this.getRawType(),facetType)) {
			return true;
		}
		// try to find the requested type in the facets
		for (Class<?> facet : _facetTypes.get()) {
			if (facet == this.getRawType()) continue;		// ignore this type metadata
			if (ReflectionUtils.isImplementing(facet,facetType)) {
				return true;
			}
		}
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELD-RELATED METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link Collection} of {@link FieldMetaDataID}s
	 */
	public Collection<IndexableFieldID> getFieldsMetaDataIds() {
		return FluentIterable.from(_fieldsMetaData)
							 .transform(new Function<TypeFieldMetaData,IndexableFieldID>() {
												@Override
												public IndexableFieldID apply(final TypeFieldMetaData fieldMetaData) {
													return fieldMetaData.getId();
												}
							 			})
							 .toList();
	}
	/**
	 * Returns a {@link Map} of all {@link TypeFieldMetaData} indexed by their {@link FieldMetaDataID}
	 * @return 
	 */
	public Map<IndexableFieldID,TypeFieldMetaData> getTypeFieldsMetaDataMap() {
		Map<IndexableFieldID,TypeFieldMetaData> outMap = Maps.newLinkedHashMapWithExpectedSize(_fieldsMetaData.size());
		for (TypeFieldMetaData fieldMetaData : _fieldsMetaData) {
			outMap.put(fieldMetaData.getId(),
					   fieldMetaData);
		}
		return outMap;
	}
	/**
	 * Returns a {@link Map} of all {@link FieldMetaData} indexed by their {@link FieldMetaDataID}
	 * @return
	 */
	public Map<IndexableFieldID,FieldMetaData> getFieldsMetaDataMap() {
		Map<IndexableFieldID,FieldMetaData> outMap = Maps.newLinkedHashMapWithExpectedSize(_fieldsMetaData.size());
		for (TypeFieldMetaData fieldMetaData : _fieldsMetaData) {
			outMap.put(fieldMetaData.getId(),
					   fieldMetaData.asFieldMetaData());
		}
		return outMap;
	}
	/**
	 * Filters all fields that are indexed
	 * @return a collection of indexed fields
	 */
	public Collection<TypeFieldMetaData> getIndexedFieldsMetaData() {
		return FluentIterable.from(_fieldsMetaData)
							 .filter(new Predicate<TypeFieldMetaData>() {
											@Override
											public boolean apply(final TypeFieldMetaData fieldMetaData) {
												return fieldMetaData.isIndexed();
											}
									 })
							 .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the {@link FieldMetaData} for a given metadata id
	 * @param metaDataId
	 * @return
	 */
	public TypeFieldMetaData findFieldByIdOrThrow(final SearchableFieldID... searchableFieldIds) {
		IndexableFieldID[] ids = searchableFieldIds != null
										? FluentIterable.from(searchableFieldIds)
												.transform(new Function<SearchableFieldID,IndexableFieldID>() {
																	@Override
																	public IndexableFieldID apply(final SearchableFieldID searchableFieldId) {
																		return searchableFieldId.getFieldId();
																	}
														   })
												.toArray(IndexableFieldID.class)
									    : null;
		return _findFieldById(true,					// throw if not found
							  _fieldsMetaData,		// all fields
							  ids);
	}
	/**
	 * Returns a field by it's id
	 * @param id
	 * @return
	 */
	public TypeFieldMetaData findFieldByIdOrThrow(final IndexableFieldID... ids) {
		return _findFieldById(true,				// throw if not found
							  _fieldsMetaData,	// all fields
							  ids);
	}
	public TypeFieldMetaData findFieldByIdOrNull(final IndexableFieldID... ids) {
		return _findFieldById(false,			// DO NOT throw if not found
							  _fieldsMetaData,	// all fields
							  ids);
	}
	private TypeFieldMetaData _findFieldById(final boolean throwIfNotFound,
											 final Collection<TypeFieldMetaData> fields,
											 final IndexableFieldID... ids) {
		if (CollectionUtils.isNullOrEmpty(ids)) throw new IllegalArgumentException("Cannot find any field without at least an id!");
		
		TypeFieldMetaData outField = null;
		
		Collection<TypeFieldMetaData> theFields = fields;
		for (int i = 0; i < ids.length; i++) {
			final IndexableFieldID id = _composeFieldMetaDataId(Arrays.copyOfRange(ids,0,i+1));
			TypeFieldMetaData fieldMetaData =  FluentIterable.from(theFields)
													 .filter(new Predicate<TypeFieldMetaData>() {
																	@Override
																	public boolean apply(final TypeFieldMetaData fieldMetaData) {
																		return fieldMetaData.getId().equals(id);
																	}
															 })
													 .first().orNull();
			if (i < ids.length - 1) {			
				theFields = fieldMetaData.getFieldTypeMetaData().getFieldsMetaData();
			} else if (i == ids.length - 1) {
				outField = fieldMetaData;
			}
		}
		if (outField == null 
		 && throwIfNotFound) throw new IllegalStateException(String.format("The meta data field %s is NOT an available metadata of %s that describes type %s",
																	   	   _composeFieldMetaDataId(ids),_annotatedType,this.getRawType()));
		return outField;
	}
	private static IndexableFieldID _composeFieldMetaDataId(final IndexableFieldID... ids) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < ids.length; i++) {
			sb.append(ids[i]);
			if (i < ids.length - 1) sb.append(".");
		}
		return IndexableFieldID.forId(sb.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	public CharSequence debugShortInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type ").append(_type).append("\n");
		sb.append("MetaData:").append(" typeCode=").append(_typeMetaData != null ? _typeMetaData.modelObjTypeCode() : "unknown")
							  .append("\n");
		if (CollectionUtils.hasData(_facets)) {
			sb.append("Facets:\n");
			for (Map.Entry<Class<?>,Long> facet : _typeFacetsCodes.get().entrySet()) {
				sb.append("\t-[").append(facet.getValue()).append("]").append(facet.getKey()).append("\n");
			}
		}
		if (CollectionUtils.hasData(_fieldsMetaData)) {
			sb.append("Fields:\n");
			for (TypeFieldMetaData typeFieldMetaData : _fieldsMetaData) {
				sb.append("\tField").append(" id=").append(typeFieldMetaData.getId())
									.append(" type=").append(typeFieldMetaData.getFieldType())
									.append(typeFieldMetaData.getFieldTypeMetaData() != null ? " (has metadata)" : "")
									.append("\n");
			}
		}
		return sb;
	}
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type ").append(_type).append("\n");
		sb.append("MetaData:").append(" typeCode=").append(_typeMetaData != null ? _typeMetaData.modelObjTypeCode() : "unknown")
							  .append("\n");
		if (_typeMetaData != null 
		 && CollectionUtils.hasData(_typeMetaData.description())) {
			for (DescInLang desc : _typeMetaData.description()) {
				sb.append("\t").append(desc.language()).append("=").append(desc.value()).append("\n");
			}
		}
		if (CollectionUtils.hasData(_facets)) {
			sb.append("Facets:\n");
			for (Map.Entry<Class<?>,Long> facet : _typeFacetsCodes.get().entrySet()) {
				sb.append("\t-[").append(facet.getValue()).append("]").append(facet.getKey()).append("\n");
			}
		}
		if (CollectionUtils.hasData(_fieldsMetaData)) {
			sb.append("Fields:\n");
			for (TypeFieldMetaData typeFieldMetaData : _fieldsMetaData) {
				sb.append(typeFieldMetaData.debugInfo())
				  .append("\n");
			}
		}
		return sb;
	}
}
