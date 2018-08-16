package r01f.persistence.search.db;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.model.metadata.HasMetaDataForHasOIDModelObject;
import r01f.model.metadata.TypeMetaDataForModelObjectBase;
import r01f.model.search.SearchFilter;
import r01f.model.search.query.BooleanQueryClause;
import r01f.model.search.query.BooleanQueryClause.QualifiedQueryClause;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.ContainedInQueryClause;
import r01f.model.search.query.ContainsTextQueryClause;
import r01f.model.search.query.EqualsQueryClause;
import r01f.model.search.query.QueryClause;
import r01f.model.search.query.RangeQueryClause;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.config.DBVendor;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Translates a search filter to JPQL
 * @param <F>
 * @param <DB>
 */
@Slf4j
public class DBSearchQueryToJPQLTranslator<F extends SearchFilter,
					       				   DB extends DBEntity> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final Class<DB> _dbEntityType;
	protected final DBModuleConfig _dbModuleConfig;
	protected final EntityManager _entityManager;		
	protected final TranslatesIndexableFieldIDToDBEntityField<F> _indexableFieldIdToDBEntityFieldTranslator;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public DBSearchQueryToJPQLTranslator(final Class<DB> dbEntityType,
						 				 final DBModuleConfig dbModuleConfig,
						 				 final EntityManager entityManager) {
		this(dbEntityType,
			 dbModuleConfig,
			 entityManager,
			 new IndexableFieldIDToDBEntityFieldTranslatorByDefault<F>());	// default translator
	}
	public DBSearchQueryToJPQLTranslator(final Class<DB> dbEntityType,
						 				 final DBModuleConfig dbModuleConfig,
						 				 final EntityManager entityManager,
						 				 final TranslatesIndexableFieldIDToDBEntityField<F> indexableFieldIdToDBEntityFieldTranslator) {
		_dbEntityType = dbEntityType;
		_dbModuleConfig = dbModuleConfig;
		_entityManager = entityManager;
		_indexableFieldIdToDBEntityFieldTranslator = indexableFieldIdToDBEntityFieldTranslator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  META-DATA TO DB COLUMN TRANSLATION
/////////////////////////////////////////////////////////////////////////////////////////
	protected String _dbEntityFieldNameForOid() {
		return _indexableFieldIdToDBEntityFieldTranslator.dbEntityFieldNameFor(HasMetaDataForHasOIDModelObject.SEARCHABLE_METADATA.OID.getFieldId(),
																			   null);	// the filter is NOT needed
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JPQL QUERY COMPOSING
/////////////////////////////////////////////////////////////////////////////////////////
	public String composeCountJPQL(final F filter) {
		return _composeJPQL("COUNT(entity)",		// count query
							filter,
							null);		// no ordering color
	}
	public String composeRetrieveJPQL(final F filter,
									  final Collection<SearchResultsOrdering> ordering) {
		return _composeJPQL("entity",	// not a count query
						    filter,
							ordering);	
	}
	public String composeRetrieveOidsJPQL(final F filter) {
		return _composeJPQL(Strings.customized("entity.{}",
											   _dbEntityFieldNameForOid()),
								   			   filter,
								   			   null);
	}
	protected String _dbEntityTypeNameFor(final F filter) {
		return _dbEntityType.getSimpleName();
	}
	protected String _composeJPQL(final String colSpec,
							      final F filter,
							      final Collection<SearchResultsOrdering> ordering) {
		// [0] - SELECT
		StringBuilder jpql = new StringBuilder(Strings.customized("SELECT {} " +
										  		  					"FROM {} entity ",
										  		  			      colSpec,		// "COUNT(entity)" : "entity",
										  		  			      _dbEntityTypeNameFor(filter)));
		// [1] - WHERE
		String jpqlWhere = _composeWhereJpqlPredicates(filter);
		if (Strings.isNOTNullOrEmpty(jpqlWhere)) {
			jpql.append("WHERE ");
			jpql.append(jpqlWhere);
		}
		
		// [2] - ORDER
		String orderClause = _composeJpqlOrderByClause(filter,
													   ordering);
		if (Strings.isNOTNullOrEmpty(orderClause)) {
			jpql.append(orderClause);
		}
		log.debug("JPQL: {}",jpql);
		
		return jpql.toString();
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  QUERY FILTER
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Creates a collection of JPQL clauses from the search query
	 * @param filter
	 * @param dbVendor
	 * @param enableFullTextSearch
	 * @return
	 */
    protected String _composeWhereJpqlPredicates(final F filter) {
		BooleanQueryClause qryClause = filter != null ? filter.getBooleanQuery()
													  : null;
		if (qryClause == null || CollectionUtils.isNullOrEmpty(qryClause.getClauses())) {
			log.warn("A filter with NO filter parameters was received... al records will be returned");
			return null;
		}
		
		StringBuilder outJPQL = new StringBuilder();
		
		Set<QualifiedQueryClause<? extends QueryClause>> clauses = qryClause.getClauses();
		
		SearchFilterClauseToJPQLWherePredicate searchFilterToJpql = new SearchFilterClauseToJPQLWherePredicate(filter);
		QueryClauseOccur prevClauseOccur = null;
		for (Iterator<QualifiedQueryClause<? extends QueryClause>> clauseIt = clauses.iterator(); clauseIt.hasNext(); ) {
			QualifiedQueryClause<? extends QueryClause> clause = clauseIt.next();
			
			// some indexable fields are NOT supported when the search engine 
			// is DB based
			boolean isDBEntitySupportedField = _isSupportedDBEntityField(clause.getClause());
			if (!isDBEntitySupportedField)  continue;
			
			String jpqlQuery = searchFilterToJpql.wherePredicateFrom(clause.getClause());
			if (jpqlQuery == null) {
				log.error("A null query clause was returned for field id={}",
						  clause.getClause().getFieldId());
				continue;
			}
			
			String jpqlJoin = _jpqlJoinFor(clause.getOccur());
			
			if (prevClauseOccur != null) outJPQL.append(jpqlJoin);
			if (clauseIt.hasNext()) prevClauseOccur = clause.getOccur();
			
			outJPQL.append("(");
			outJPQL.append(jpqlQuery);	// The clause
			outJPQL.append(")");
		}		
		return outJPQL.length() > 0 ? outJPQL.insert(0,"(")
											 .append(")").toString()
									: null;
    }
	protected String _jpqlJoinFor(final QueryClauseOccur occur) {
		String outJPQL = null;
		switch(occur) {
		case MUST:
			outJPQL = " AND ";
			break;
		case MUST_NOT:
			outJPQL = " AND NOT ";
			break;
		case SHOULD:
			outJPQL = " OR ";
			break;
		default:
			throw new IllegalArgumentException();
		}
		return outJPQL;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FILTER CLAUSES TO JPQL
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor
	protected class SearchFilterClauseToJPQLWherePredicate {
		private final F _filter;
		
		public <Q extends QueryClause> String wherePredicateFrom(final Q clause) {
			if (clause == null) return null;
			
			String outJPQL = null;
			if (clause instanceof BooleanQueryClause) {
				outJPQL = this.wherePredicateFrom((BooleanQueryClause)clause);
			} 
			else if (clause instanceof EqualsQueryClause<?>) {
				outJPQL = this.wherePredicateFrom((EqualsQueryClause<?>)clause);
			} 
			else if (clause instanceof ContainsTextQueryClause) {
				outJPQL = this.wherePredicateFrom((ContainsTextQueryClause)clause);
			} 
			else if (clause instanceof RangeQueryClause<?>) {
				outJPQL = this.wherePredicateFrom((RangeQueryClause<?>)clause);
			} 
			else if (clause instanceof ContainedInQueryClause<?>) {
				outJPQL = this.wherePredicateFrom((ContainedInQueryClause<?>)clause);
			}
			return outJPQL;
		}	
		public String wherePredicateFrom(final EqualsQueryClause<?> eqQry) {
			if (eqQry == null || eqQry.getValue() == null) return null;
			
			String dbFieldId = eqQry.getFieldId().asString();
			
			String outJPQL = Strings.customized("entity._{} = :{}",
								    			dbFieldId,dbFieldId);
			return outJPQL;
		}
		public String wherePredicateFrom(final ContainsTextQueryClause containsTextQry) {
			if (Strings.isNullOrEmpty(containsTextQry.getText())) return null;
			
			
			String template = null;
			if (containsTextQry.isBegining()) {
				template = "upper(entity._{}) LIKE '%{}'";
			} else if (containsTextQry.isEnding()) {
				template = "upper(entity._{}) LIKE '{}%'";			
			} else if (containsTextQry.isContaining()) {
				template = "upper(entity._{}) LIKE '%{}%'";
			} else if (containsTextQry.isFullText()) {
				boolean fullTextSearchEnabled = _dbModuleConfig.isFullTextSearchSupported(_entityManager); 
				log.info("FullText search enabled: {}",
						   fullTextSearchEnabled);
				if (fullTextSearchEnabled) {
					// Full text search is ENABLED: the filter expression is db platform-dependent
					// 								use SQL operator available since eclipselink 2.5
					// 								see http://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Querying/Support_for_Native_Database_Functions#SQL
					if (_dbModuleConfig.getDbSpec().getVendor()
												   .is(DBVendor.MySQL)) {
					    // IMPORTANT!! see: http://dev.mysql.com/doc/refman/5.0/en/fulltext-search.html / http://devzone.zend.com/26/using-mysql-full-text-searching/
					    //		Tables MUST be MyISAM (InnoDB)) type; to change the table type:
					    //			ALTER TABLE [table] engine=MyISAM;
					    //
					    //		also a FULLTEXT index must be added to the cols:
					    //			ALTER TABLE [table] ADD FULLTEXT [NOMBRE INDICE](col1,col2,...);
					    //		
					    //		Once the above is done, a FULL-TEXT search can be executed like:
					    //			select * 
					    //			  from [table]
					    //			 where MATCH(col1,col2) AGAINST ('[text]');
						
						// Generate:  SQL(   'MATCH(colXX) 
						//				     AGAINST(? IN BOOLEAN MODE)',':text')
						template = "SQL(  'MATCH({}) " + 
						  			    "AGAINST(? IN BOOLEAN MODE)','{}')";
					} 
					else if (_dbModuleConfig.getDbSpec().getVendor()
														.is(DBVendor.ORACLE)) {
						// IMPORTANT!! see: http://docs.oracle.com/cd/B28359_01/text.111/b28304/csql.htm#i997503
						// 		Oracle Text MUST be enabled
						
						// Generate: SQL(  'CONTAINS(?,?,1) > 0,colXX,:text)
						template = "SQL(  'CONTAINS(?,?,1) > 0',{},'{}')";
					}
				}
				else {
					// simulate full text
					template = "upper(entity._{}) LIKE '%{}%'";
				}
			}
			String dbFieldId = _indexableFieldIdToDBEntityFieldTranslator.dbEntityFieldNameFor(containsTextQry.getFieldId(),
																							   _filter);
			String text = containsTextQry.getText()
										 .trim().toUpperCase();	// important!!
			String filteringText = null;
			if (containsTextQry.isFullText()
			 && _dbModuleConfig.isFullTextSearchSupported(_entityManager)) {
				// when full text search is enabled, multiple words are supported
				filteringText = text;
			} 
			else {
				// when full text search is NOT enabled, LIKE operations ONLY supports a single word
				// (otherwise multiple LIKE clauses are needed:  X LIKE %..% OR X LIKE %..%) 
				filteringText = text.split(" ")[0];			// use only the FIRST word (no multiple word is allowed)
			}
			// a minimal sanitization of the filtering text
			filteringText = new StringBuilder(filteringText					
					   				  .replaceAll("%","")		// remove all %
									  .replaceAll("'","")		// remove all '
									  .replaceAll("\"","")		// remove all "
									  .replaceAll("ALTER","")	
									  .replaceAll("DROP","")	
									  .replaceAll("DELETE","")
									  .replaceAll("INSERT","")
									  .replaceAll("UPDATE","")
									  .replaceAll("SELECT",""))
								  .toString();
			String outPredStr = Strings.customized(template,
				          			  			   dbFieldId,filteringText);		// the field and the value!!!
			return outPredStr;
		}
		public String wherePredicateFrom(final RangeQueryClause<?> rangeQry) {
			String dbFieldId = rangeQry.getFieldId().asString();
			
			String outJPQL = null;
			// TODO mind the bound types... now only CLOSED (inclusive) bounds are being having into account 
			if (rangeQry.getRange().hasLowerBound() && rangeQry.getRange().hasUpperBound()) {
				outJPQL = Strings.customized("entity._{} BETWEEN :{}Start AND :{}End",		// SQL between is INCLUSIVE (>= lower and <= lower)
								 			 dbFieldId,dbFieldId,dbFieldId);
			} else if (rangeQry.getRange().hasLowerBound()) {
				outJPQL = Strings.customized("entity._{} >= :{}",
								 			 dbFieldId,dbFieldId);
			} else if (rangeQry.getRange().hasUpperBound()) {
				outJPQL = Strings.customized("entity._{} <= :{}",
								 			 dbFieldId,dbFieldId);
			}
			return outJPQL;
		}
		public String wherePredicateFrom(final ContainedInQueryClause<?> containedInQry) {
			String dbFieldId = containedInQry.getFieldId().asString();
			String outJPQL = Strings.customized("entity._{} IN :{}",
								    			dbFieldId,dbFieldId);
			return outJPQL;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  QUERY PARAMETERS
/////////////////////////////////////////////////////////////////////////////////////////    
	/**
	 * Sets the JPA query parameters
	 * @param qry
	 * @param filter
	 * @param dbVendor
	 * @param enableFullTextSearch
	 */
	public void setJPAQueryParameters(final Query qry,
			 						  final F filter) {
		_setJPAQueryParameters(qry,
							   filter.getBooleanQuery());
	}
	protected void _setJPAQueryParameters(final Query qry,
			 						  	  final BooleanQueryClause qryClause) {
		Set<QualifiedQueryClause<? extends QueryClause>> clauses = qryClause.getClauses();
		
		for (Iterator<QualifiedQueryClause<? extends QueryClause>> clauseIt = clauses.iterator(); clauseIt.hasNext(); ) {
			QueryClause clause = clauseIt.next().getClause();

			// some indexable fields are NOT supported when the search engine 
			// is DB based
			boolean isDBEntitySupportedField = _isSupportedDBEntityField(clause);
			if (!isDBEntitySupportedField)  continue;
			
			_setJPAQueryParameter(qry,
								  clause);
		}
	}
	protected void _setJPAQueryParameter(final Query qry,
										 final QueryClause clause) {
		String dbFieldId = clause.getFieldId().asString();
		
		if (clause instanceof BooleanQueryClause) {
			BooleanQueryClause boolQry = (BooleanQueryClause)clause;
			_setJPAQueryParameters(qry,
					  		   	   boolQry);		// recurse!!!!
		} 
		else if (clause instanceof EqualsQueryClause<?>) {
			EqualsQueryClause<?> eqQry = (EqualsQueryClause<?>)clause;
			qry.setParameter(dbFieldId,
							 eqQry.getValue().toString());
		} 
		else if (clause instanceof ContainsTextQueryClause) {
			// The contains text query clause DOES NOT USE jpa params: the param value is directly set
			// when creating the jpql where clause (see wherePredicateFrom(ContainsTextQueryClause)
//			ContainsTextQueryClause containsTxtClause = (ContainsTextQueryClause)clause;
//			qry.setParameter(dbFieldId,
//							 containsTxtClause.getText());
		} 
		else if (clause instanceof RangeQueryClause<?>) {
			RangeQueryClause<?> rangeQry = (RangeQueryClause<?>)clause;
			if (rangeQry.getRange().hasLowerBound() && rangeQry.getRange().hasUpperBound()) {
				qry.setParameter(dbFieldId + "Start",rangeQry.getRange().lowerEndpoint());
				qry.setParameter(dbFieldId + "End",rangeQry.getRange().upperEndpoint());
			} else if (rangeQry.getRange().hasLowerBound()) {
				qry.setParameter(dbFieldId,rangeQry.getRange().lowerEndpoint());
			} else if (rangeQry.getRange().hasUpperBound()) {
				qry.setParameter(dbFieldId,rangeQry.getRange().upperEndpoint());
			}
		} 
		else if (clause instanceof ContainedInQueryClause<?>) {
			ContainedInQueryClause<?> containedInQry = (ContainedInQueryClause<?>)clause;
			Collection<?> spectrum = Lists.newArrayList(containedInQry.getSpectrum());
			qry.setParameter(dbFieldId,spectrum);
		}		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ORDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Composes the order by clause
	 * @param filter
	 * @param ordering
	 * @return
	 */
	protected String _composeJpqlOrderByClause(final F filter,
											   final Collection<SearchResultsOrdering> ordering) {
		if (CollectionUtils.isNullOrEmpty(ordering)) return null;
		
		StringBuilder orderBy = new StringBuilder();
		orderBy.append("ORDER BY ");
		for (SearchResultsOrdering ord : ordering) {
			orderBy.append(Strings.customized("entity.{} {}",
											  ord.getFieldId(),ord.getDirection().getCode()));
		}
		// BEWARE!!! 	ORACLE BUG with paging & ordering: the order clause MUST include the primary key
		//				see: http://adfinmunich.blogspot.com.es/2012/03/problem-with-pagination-and-ordering-in.html
		//					 http://www.eclipse.org/forums/index.php/m/638599/
		orderBy.append(Strings.customized(",entity.{} ASC",
										   _dbEntityFieldNameForOid()));
		return orderBy.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Some indexable fields are NOT supported when the search engine
	 * is DB based
	 * @param clause
	 * @return
	 */
	protected boolean _isSupportedDBEntityField(final QueryClause clause) {
		if (clause.getFieldId().is(TypeMetaDataForModelObjectBase.SEARCHABLE_METADATA.TYPE_FACETS.getFieldId())) return false;
		return true;
	}
}
