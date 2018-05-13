package r01f.model.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;

import lombok.RequiredArgsConstructor;
import r01f.generics.TypeRef;
import r01f.model.metadata.IndexableFieldID;
import r01f.model.metadata.SearchableFieldID;
import r01f.model.search.query.BooleanQueryClause;
import r01f.model.search.query.BooleanQueryClause.QualifiedQueryClause;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.model.search.query.ContainedInQueryClause;
import r01f.model.search.query.ContainsTextQueryClause;
import r01f.model.search.query.ContainsTextQueryClause.ContainedTextAt;
import r01f.model.search.query.EqualsQueryClause;
import r01f.model.search.query.HasDataQueryClause;
import r01f.model.search.query.QueryClause;
import r01f.model.search.query.RangeQueryClause;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@GwtIncompatible
@RequiredArgsConstructor
public class SearchFilterForModelObjectModifierWrapper<F extends SearchFilterForModelObjectBase<F>> {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private final F _wrappedFilter;

/////////////////////////////////////////////////////////////////////////////////////////
//  MODIFIERS
/////////////////////////////////////////////////////////////////////////////////////////
    public F mustMeetThisMetaDataCondition(final QueryClause metaDataCondition) {
    	return addOrUpdateClause(metaDataCondition,QueryClauseOccur.MUST);
    }
    public F mustMeetThisMetaDataCondition(final Set<QueryClause> metaDataConditions) {
    	for (final QueryClause condition : metaDataConditions) {
    		this.mustMeetThisMetaDataCondition(condition);
    	}
    	return _wrappedFilter;
    }
    public F mustNOTMeetThisMetaDataCondition(final QueryClause metaData) {
    	return addOrUpdateClause(metaData,QueryClauseOccur.MUST_NOT);
    }
    public F mustNOTMeetThisMetaDataCondition(final Set<QueryClause> metaDataConditions) {
    	for (final QueryClause condition : metaDataConditions) {
    		this.mustNOTMeetThisMetaDataCondition(condition);
    	}
    	return _wrappedFilter;
    }
    public F canMeetThisMetaDataCondition(final QueryClause metaDataCondition) {
    	return addOrUpdateClause(metaDataCondition,QueryClauseOccur.SHOULD);
    }
    public F canMeetThisMetaDataCondition(final Set<QueryClause> metaDataConditions) {
    	Preconditions.checkArgument(metaDataConditions != null,"The metadata condition cannot be null");
    	for (final QueryClause condition : metaDataConditions) {
    		this.canMeetThisMetaDataCondition(condition);
    	}
    	return _wrappedFilter;
    }
	public void addClause(final QueryClause clause,
						  final QueryClauseOccur occur) {
		if (clause == null) return;
		final QueryClauseOccur theOccur = occur != null ? occur : QueryClauseOccur.MUST;

		if (_wrappedFilter.getBooleanQuery() == null) _wrappedFilter.setBooleanQuery(new BooleanQueryClause(new HashSet<QualifiedQueryClause<? extends QueryClause>>()));
		_wrappedFilter.getBooleanQuery().add(clause,theOccur);
	}
	public boolean removeAllClausesFor(final SearchableFieldID searchableFieldId) {
		return _removeAllClausesFor(searchableFieldId.getFieldId());
	}
	private boolean _removeAllClausesFor(final IndexableFieldID fieldId) {
		if (_wrappedFilter.getBooleanQuery() == null) return false;
		return _wrappedFilter.getBooleanQuery().removeAllFor(fieldId);
	}
    public F addOrUpdateClause(final QueryClause clause,
    						   final QueryClauseOccur clauseOccur) {
		Preconditions.checkArgument(clause != null,"The clause condition cannot be null");
		final QueryClause existing = _wrappedFilter.getAccessorWrapper().queryClauses().find(clause.getFieldId());
		if (existing != null) {
			_removeAllClausesFor(clause.getFieldId());
			this.addClause(clause,
					   	   clauseOccur);
		} else {
			this.addClause(clause,
					   	   clauseOccur);
		}
    	return _wrappedFilter;
    }
    public <T> F addOrUpdateEqualsClause(final SearchableFieldID searchableFieldId,
										 final T eqValue,
										 final QueryClauseOccur clauseOccur) {
		Preconditions.checkArgument(eqValue != null,"The value to be set in the equals clause cannot be null");
		final QueryClause clause = _wrappedFilter.getAccessorWrapper().queryClauses().find(searchableFieldId);
		if (clause != null) {
			final EqualsQueryClause<T> eq = clause.as(new TypeRef<EqualsQueryClause<T>>() {/* nothing */});
			eq.setEqValue(eqValue);
		} else {
			if (eqValue instanceof Range || eqValue instanceof com.google.common.collect.Range) throw new IllegalArgumentException("For range conditions use _addOrUpdateRangeClause!");
			final EqualsQueryClause<T> eq = EqualsQueryClause.forField(searchableFieldId.getFieldId())
													   .of(eqValue);
			this.addClause(eq,clauseOccur);
		}
    	return _wrappedFilter;
    }
    public <T> F addOrUpdateContainedInClause(final SearchableFieldID searchableFieldId,
											  final T[] values,
											  final QueryClauseOccur clauseOccur) {
		Preconditions.checkArgument(CollectionUtils.hasData(values),"The values to be set in the contained in clause cannot be null");

		final QueryClause clause = _wrappedFilter.getAccessorWrapper().queryClauses().find(searchableFieldId);
		if (clause != null) {
			final ContainedInQueryClause<T> contained = clause.as(new TypeRef<ContainedInQueryClause<T>>() {/* nothing */ });
			contained.setSpectrum(values);
		} else {
			final ContainedInQueryClause<T> contained = ContainedInQueryClause.<T>forField(searchableFieldId.getFieldId())
															 			.within(values);
			this.addClause(contained,clauseOccur);
		}
    	return _wrappedFilter;
    }
	@SuppressWarnings("unchecked")
    public <T> F addOrUpdateContainedInClause(final SearchableFieldID searchableFieldId,
											  final Collection<T> values,
											  final QueryClauseOccur clauseOccur) {
		return this.addOrUpdateContainedInClause(searchableFieldId,
											 	 (T[])values.toArray(),
											 	 clauseOccur);
    }
	public <T extends Comparable<T>> F addOrUpdateRangeClause(final SearchableFieldID searchableFieldId,
												    		  final Range<T> range,
												    		  final QueryClauseOccur clauseOccur) {
		final QueryClause clause = _wrappedFilter.getAccessorWrapper().queryClauses().find(searchableFieldId);
		if (clause != null) {
			final RangeQueryClause<T> requestRangeClause = clause.as(new TypeRef<RangeQueryClause<T>>() {/* nothing */});
			requestRangeClause.setRange(range);
		} else {
			final RangeQueryClause<T> requestRangeClause = RangeQueryClause.forField(searchableFieldId.getFieldId())
																     .of(range);
			this.addClause(requestRangeClause,clauseOccur);
		}
    	return _wrappedFilter;
	}
	public <T extends Comparable<T>> F addOrUpdateRangeClause(final SearchableFieldID searchableFieldId,
												    		  final com.google.common.collect.Range<T> range,
												    		  final QueryClauseOccur clauseOccur) {
		return this.addOrUpdateRangeClause(searchableFieldId,
									   	   new Range<T>(range),
									   	   clauseOccur);
	}
	public F addOrUpdateContainsTextClause(final SearchableFieldID searchableFieldId,
										   final String text,
										   final QueryClauseOccur clauseOccur) {
		Preconditions.checkArgument(Strings.isNOTNullOrEmpty(text),"The text to be set in the contains text clause cannot be null");
		final QueryClause clause = _wrappedFilter.getAccessorWrapper().queryClauses().find(searchableFieldId);
		if (clause != null) {
			final ContainsTextQueryClause contains = clause.as(ContainsTextQueryClause.class);
			contains.setText(text);
		} else {
			final ContainsTextQueryClause contains = ContainsTextQueryClause.forField(searchableFieldId.getFieldId())
																	  .at(ContainedTextAt.FULL)
																	  .text(text)
																	  .languageIndependent();
			this.addClause(contains,clauseOccur);
		}
		return _wrappedFilter;
	}
    public F addOrUpdateHasDataClause(final SearchableFieldID searchableFieldId,
    								  final QueryClauseOccur clauseOccur) {
		final QueryClause clause = _wrappedFilter.getAccessorWrapper().queryClauses().find(searchableFieldId);
		if (clause != null) {
			// nothing... the clause already exists
		} else {
			final HasDataQueryClause hasData = HasDataQueryClause.forField(searchableFieldId.getFieldId());
			this.addClause(hasData,clauseOccur);
		}
    	return _wrappedFilter;
    }
}
