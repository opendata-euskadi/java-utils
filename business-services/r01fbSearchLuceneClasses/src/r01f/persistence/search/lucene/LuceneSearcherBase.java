package r01f.persistence.search.lucene;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.facets.HasLanguage;
import r01f.facets.HasName;
import r01f.facets.HasOID;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.LangInDependentNamed.HasLangInDependentNamedFacet;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.IndexableModelObject;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;
import r01f.model.metadata.FieldMetaData;
import r01f.model.metadata.HasMetaDataForHasEntityVersionModelObject;
import r01f.model.metadata.HasMetaDataForHasLanguageModelObject;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.HasMetaDataForHasSummaryModelObject;
import r01f.model.metadata.IndexableFieldID;
import r01f.model.metadata.TypeMetaData;
import r01f.model.metadata.TypeMetaDataForModelObjectBase;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.model.search.SearchFilterForModelObject;
import r01f.model.search.SearchResultItemContainsModelObject;
import r01f.model.search.SearchResultItemForModelObject;
import r01f.model.search.SearchResults;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.patterns.Factory;
import r01f.persistence.index.document.IndexDocumentFieldConfigSet;
import r01f.persistence.lucene.LuceneIndex;
import r01f.persistence.lucene.LuceneSearchResultDocument;
import r01f.persistence.search.Searcher;
import r01f.persistence.search.SearcherCreatesResultItemFromIndexData;
import r01f.persistence.search.SearcherExternallyLoadsModelObject;
import r01f.persistence.search.SearcherMapsIndexedFieldsToSearchResultItemFields;
import r01f.reflection.ReflectionUtils;
import r01f.securitycontext.SecurityContext;
import r01f.types.summary.LangDependentSummary;
import r01f.types.summary.LangIndependentSummary;
import r01f.types.summary.Summary;
import r01f.util.types.collections.CollectionUtils;

/**
 * A type that is in charge of the search operations against the Lucene index
 * @param <F> the {@link SearchFilterForModelObject} type
 * @param <I> the {@link SearchResultItemForModelObject} type
 */
@Slf4j
@Accessors(prefix="_")
public abstract class LuceneSearcherBase<F extends SearchFilterForModelObject,
		   				    		 	 I extends SearchResultItemForModelObject<? extends IndexableModelObject>> 
  		   implements Searcher<F,I> {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FINAL STATUS 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Fields config
	 */
	@Getter(AccessLevel.PROTECTED) private final IndexDocumentFieldConfigSet<? extends IndexableModelObject> _fieldsConfigSet;
	/**
	 * The Lucene index instance 
	 */
	@Getter(AccessLevel.PROTECTED) private final LuceneIndex _luceneIndex;
	/**
	 * Factory of {@link SearchResultItemForModelObject} instances
	 */
	@Getter(AccessLevel.PROTECTED) private final Factory<I> _searchResultItemsFactory;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public LuceneSearcherBase(final IndexDocumentFieldConfigSet<? extends IndexableModelObject> fieldsConfigSet,
							  final LuceneIndex luceneIndex,
							  final Factory<I> searchResultItemsFactory) {
		_fieldsConfigSet = fieldsConfigSet;
		_luceneIndex = luceneIndex;
		_searchResultItemsFactory = searchResultItemsFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SEARCH METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int countRecords(final SecurityContext securityContext,
							final F filter) {
		int outCount = 0;
		
		// [1] Build the query
		Query qry = _createQueryFor(filter);
		
		// [2] Run the query
		outCount = _luceneIndex.count(qry);
		return outCount;
	}
	/**
	 * Filters returning only the oids
	 * @param securityContext
	 * @param filter
	 * @return
	 */
	public <O extends OID> Collection<O> filterRecordsOids(final SecurityContext securityContext,	
														   final F filter) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public SearchResults<F,I> filterRecords(final SecurityContext securityContext,
											final F filter,final Collection<SearchResultsOrdering> ordering,
											final int firstRowNum,final int numberOfRows) {
		SearchResults<F,I> outResults = null; 
		
		// [1] Build the query
		Query qry = _createQueryFor(filter);
		
		// [2] Build the sort fields
		Set<SortField> sortFields = _createSortFieldsFor(filter);
		
		// [3] Run the Query				
		LucenePageResults pageResults = _luceneIndex.search(qry,sortFields,
													        firstRowNum,numberOfRows);
		
		// [4] Transform lucene documents to serarch results
		Stopwatch stopWatch = Stopwatch.createStarted();
		if (pageResults != null && CollectionUtils.hasData(pageResults.getDocuments())) {
			outResults = new SearchResults<F,I>();
			outResults.setTotalItemsCount(pageResults.getTotalHits());
			outResults.setStartPosition(firstRowNum);
			outResults.setEndPosition(firstRowNum + pageResults.getPageSize());
			outResults.setRequestedNumberOfItems(numberOfRows);
			outResults.setPageItems(new LinkedHashSet<I>(pageResults.getDocuments().size()));
			for (Document doc : pageResults.getDocuments()) {
				I item = _createSearchResultItemFor(securityContext,
													doc);// ... create the search result item
				outResults.getPageItems().add(item);	 // ... put it on the list
			}
		}
		log.info("Lucene documents transformed to search result items (elapsed time: {} milis)",NumberFormat.getNumberInstance(Locale.getDefault()).format(stopWatch.elapsed(TimeUnit.MILLISECONDS)));
		stopWatch.stop();
		
		return outResults;
	}
	/**
	 * Return all filtered records (no paging)
	 * @param securityContext
	 * @param filter the filter
	 * @return
	 */
	public Collection<I> filterRecords(final SecurityContext securityContext,
									   final F filter) {
		// [1]-Build the query
		Query qry = _createQueryFor(filter);
		
		// [2]-Build the sort fields
		Set<SortField> sortFields = _createSortFieldsFor(filter);
		
		// [3]-Run the query
		Set<Document> allDocs = _luceneIndex.searchAll(qry,sortFields);
		
		// [4] Transform lucene documents to serarch results
		Stopwatch stopWatch = Stopwatch.createStarted();
		Collection<I> outItems = null;
		if (CollectionUtils.hasData(allDocs)) {
			outItems = Sets.newLinkedHashSetWithExpectedSize(allDocs.size());
			for (Document doc : allDocs) {
				I item = _createSearchResultItemFor(securityContext,
													doc);// ... create the search result item
				outItems.add(item);						 // ... put it on the list
			}
		}
		log.info("Lucene documents transformed to search result items (elapsed time: {} milis)",NumberFormat.getNumberInstance(Locale.getDefault()).format(stopWatch.elapsed(TimeUnit.MILLISECONDS)));
		stopWatch.stop();
		
		return outItems;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BASE QUERY BUILDING METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Builds the query assembling the common fields query and the
	 * type specific query
	 * @param filter
	 * @return
	 */
	private Query _createQueryFor(final F filter) {		
		if (filter == null || filter.getBooleanQuery() == null) {
			log.warn("A filter with NO filter parameters was received... al records will be returned");
			return new MatchAllDocsQuery();
		}
		@SuppressWarnings("unchecked")
		Query outQuery = LuceneSearchQueryBuilder.forFieldConfigSet(_fieldsConfigSet)
												 .usingLuceneAnalyzer(_luceneIndex.getAnalyzer())
												 .noUILanguage()
												 .withFilterType(filter.getClass())
									 .getQuery(filter);
		log.debug("Lucene query: {}",
				  outQuery.toString());
		return outQuery;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SORT FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates the sort clauses
	 * @param filter
	 * @return
	 */
	private Set<SortField> _createSortFieldsFor(final F filter) {
		return null;	// TODO finish!! this.addSortClausesFor(filter);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates the search result item from the lucene document
	 * @param luceneDoc
	 * @return
	 */
	private I _createSearchResultItemFor(final SecurityContext securityContext,
										 final Document luceneDoc) {
		// [0] - Create a wrapper for the Lucene Document and get the model object's metadata
		LuceneSearchResultDocument<? extends IndexableModelObject> indexedDoc = LuceneSearchResultDocument.from(luceneDoc)
																		  								  .using(TypeMetaDataInspector.singleton());
		
		// [1] - Use the search result item factory to create an item
		I item = _searchResultItemsFactory.create();
		
		// [2] - Get the type code and using it get the model object metadata
		TypeMetaData<? extends IndexableModelObject> modelObjectMetaData = indexedDoc.getModelObjectMetaData();
		FieldMetaData typeFieldMetaData = modelObjectMetaData.findFieldByIdOrThrow(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.TYPE_CODE)
															 .asFieldMetaData();
		log.debug("Creating a searchResultItem from the lucene Document for a model object of type {} with code {}",
				  modelObjectMetaData.getRawType(),
				  indexedDoc.<Long>getFieldValueOrThrow(typeFieldMetaData.getFieldId()));
		
		// [2] - Create the item 
		// ... common fields
		_setResultItemCommonFields(item,
							 	   indexedDoc);
				
		// [3] - Set the search result item fields from the indexed fields
		if (this instanceof SearcherMapsIndexedFieldsToSearchResultItemFields) {
			@SuppressWarnings("unchecked")
			SearcherMapsIndexedFieldsToSearchResultItemFields<I> mapper = (SearcherMapsIndexedFieldsToSearchResultItemFields<I>)this;
			mapper.mapIndexedFieldsToSearchResultItemFields(indexedDoc,item);
		}
		
		// [4] - Create the model object and set it on the item
		//		 The model object can be created:
		//			a) by the searcher loading it from an external source (ie the BBDD using an service call)
		//			b) by the searcher creating it from the search index stored info (the document)
		//			c) here from the model object type info; only the common fields can be set
		//PersistableModelObject<? extends OID> modelObject = null;
		if (item instanceof SearchResultItemContainsModelObject) {
			Object modelObject = null;
			if (this instanceof SearcherExternallyLoadsModelObject) {
				
				modelObject = _loadModelObjectInstance(this,
													   securityContext,
													   item.getOid());			
			} else if (this instanceof SearcherCreatesResultItemFromIndexData) {
				modelObject = _createModelObject(this,
												 securityContext,
											     indexedDoc);
			} 
			else {
				// At least try to create the model object using the model object type available at the model object meta data
				modelObject = ReflectionUtils.createInstanceOf(modelObjectMetaData.getRawType());
			}
			if (modelObject == null) throw new IllegalStateException(Throwables.message("Could NOT create a {} instance from the search index returned data",
																					    modelObjectMetaData.getRawType()));
			
			IndexableModelObject indexableModelObject = (IndexableModelObject)modelObject;
			
			// Copy item common fields to the model object
			_setModelObjectCommonFields(item,indexableModelObject,
										indexedDoc);
			
			// set the object at the item
			((SearchResultItemContainsModelObject<?>)item).unsafeSetModelObject(indexableModelObject);
		}
	
		// [5] - Return
		return item;
	}
	/**
	 * Sets the model object's common fields from the search results
	 * @param item
	 * @param modelObject
	 * @param doc
	 */
	protected void _setModelObjectCommonFields(final I item,final IndexableModelObject modelObject,
											   final LuceneSearchResultDocument<? extends IndexableModelObject> doc) {
		// oid
		if (modelObject.hasFacet(HasOID.class)) {
			OID oid = item.getOid();
			((HasOID<?>)modelObject).unsafeSetOid(oid);
		}
		// NumericId
		if (modelObject.hasFacet(HasNumericID.class)) {
			long numericId = item.getNumericId();
			((HasNumericID)modelObject).setNumericId(numericId);
		}
		// language
		if (modelObject.hasFacet(HasLanguage.class)) {
			IndexableFieldID fieldId = HasMetaDataForHasLanguageModelObject.SEARCHABLE_METADATA.LANGUAGE.getFieldId();
			Language lang = doc.getFieldValue(fieldId);
			if (lang != null) {
				((HasLanguage)modelObject).setLanguage(lang);
			}
		}
		// Name from summary
		if (modelObject.hasFacet(HasName.class)) {
			if (modelObject.hasFacet(HasLangDependentNamedFacet.class)) {
				// Lang dependent name
				LangDependentSummary summary = item.asSummarizable()
							  						 	.getSummary()
							  						 	.asLangDependent();
				
				((HasLangDependentNamedFacet)modelObject).setNamesByLanguage(summary.asLanguageTexts());
			} else {
				// Lang independent name
				LangIndependentSummary summary = item.asSummarizable()
							  						   .getSummary()
							  						   .asLangIndependent();
				
				((HasLangInDependentNamedFacet)modelObject).setName(summary.asString());
			}
		}
	}
	/**
	 * Sets the item common fields 
	 * @param item
	 * @param doc
	 * @param filter
	 */
	protected void _setResultItemCommonFields(final I item,
									          final LuceneSearchResultDocument<? extends IndexableModelObject> doc) {
		TypeMetaData<? extends IndexableModelObject> typeMetaData = doc.getModelObjectMetaData();
		
		// Model object type
		item.unsafeSetModelObjectType((Class<? extends IndexableModelObject>)typeMetaData.getRawType());
		item.setModelObjectTypeCode(typeMetaData.getTypeMetaData().modelObjTypeCode());
		
		// OID
		if (typeMetaData.hasFacet(HasOID.class)) {
			IndexableFieldID fieldId = HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID.getFieldId();
			OID oid = doc.<OID>getFieldValueOrThrow(fieldId);
			item.unsafeSetOid(oid);
		}
		// numeric id
		if (typeMetaData.hasFacet(HasNumericID.class)) {
			IndexableFieldID fieldId = TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.NUMERIC_ID.getFieldId();
			Long numericId = doc.getFieldValueOrThrow(fieldId);
			if (numericId != null && numericId > 0) item.setNumericId(numericId);
		}
		// EntityVersion 
		if (typeMetaData.hasFacet(HasEntityVersion.class)) {
			IndexableFieldID fieldId = HasMetaDataForHasEntityVersionModelObject.SEARCHABLE_METADATA.ENTITY_VERSION.getFieldId();
			long entityVersion = doc.<Long>getFieldValueOrThrow(fieldId);
			item.setEntityVersion(entityVersion);
		}
		
		// Summary
		// - Language dependent summary (stored as summary.{} -a field for every lang-)
		// - Language independent summary stored in a single lucene field
		if (typeMetaData.hasFacet(HasSummaryFacet.class)) {			
			IndexableFieldID fieldId = HasMetaDataForHasSummaryModelObject.SEARCHABLE_METADATA.SUMMARY.getFieldId();
			Summary summary = doc.getFieldValue(fieldId);
			if (summary != null) {
				item.asSummarizable()
					.setSummary(summary);
			} else {
				log.warn("There're no summary fields stored in a lucene document for a result of type {}",
						 typeMetaData.getRawType(),typeMetaData.getRawType(),ModelObjectData.class);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SOME GENERICS TRICKERY (parameter type capture)
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	private static <O extends OID,P extends IndexableModelObject> P _loadModelObjectInstance(final Searcher<?,?> searcher,
																							 final SecurityContext securityContext,
																							 final O oid) {
		SearcherExternallyLoadsModelObject<O,P> loader = (SearcherExternallyLoadsModelObject<O,P>)searcher;
		return loader.loadModelObject(securityContext,
						   			  oid);
	}
	@SuppressWarnings("unchecked")
	private static <P extends IndexableModelObject> P _createModelObject(final Searcher<?,?> searcher,
																		 final SecurityContext securityContext,
																		 final LuceneSearchResultDocument<P> doc) {
		SearcherCreatesResultItemFromIndexData<LuceneSearchResultDocument<P>,P> creator = (SearcherCreatesResultItemFromIndexData<LuceneSearchResultDocument<P>,P>)searcher;
		return creator.createModelObjectFrom(securityContext,
											 doc);
	}
}
