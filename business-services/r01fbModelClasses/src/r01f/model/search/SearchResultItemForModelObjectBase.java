package r01f.model.search;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.facets.HasOID;
import r01f.facets.Summarizable;
import r01f.guids.OID;
import r01f.model.HasModelObjectTypeCode;
import r01f.model.HasModelObjectTypeInfo;
import r01f.model.IndexableModelObject;
import r01f.model.ModelObjectTracking;
import r01f.model.TrackableModelObject;
import r01f.model.facets.HasEntityVersion;
import r01f.model.facets.HasNumericID;
import r01f.model.search.SearchOIDs.SearchEngineDBID;
import r01f.model.search.SearchOIDs.SearchSourceID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.summary.Summary;
import r01f.util.types.Strings;

@Accessors(prefix="_")
public abstract class SearchResultItemForModelObjectBase<M extends IndexableModelObject>
           implements SearchResultItemForModelObject<M>,
           			  HasEntityVersion,
           			  HasNumericID,
           			  HasModelObjectTypeInfo<M>,HasModelObjectTypeCode,
           			  HasOID<OID>,
           			  Debuggable {
	
	private static final long serialVersionUID = 126535994364020659L;
/////////////////////////////////////////////////////////////////////////////////////////
//  MODEL OBJECT'S FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The model object type
	 */
	@MarshallField(as="modelObjectType")
	@Getter @Setter protected Class<M> _modelObjectType;
	/**
	 * A type code for the model object type
	 */
	@MarshallField(as="objectTypeCode",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected long _modelObjectTypeCode;
    /**
     * Oid
     */
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter protected OID _oid;
	/**
	 * Numeric Id
	 */
	@MarshallField(as="numericId")
	@Getter @Setter protected long _numericId;
	/**
	 * Entity Version used to achieve the optimistic locking behavior
	 */
	@MarshallField(as="entityVersion",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected long _entityVersion;
	/**
	 * Tracking info
	 */
	@MarshallField(as="trackingInfo")
	@Getter @Setter protected ModelObjectTracking _trackingInfo;
	/**
	 * A summary / abstract of the search result
	 * (it's NOT serialized)
	 */
	@MarshallField(as="summary")
	@Getter @Setter protected transient Summary _summary;
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCH ENGINE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Search results source
     */
	@MarshallField(as="sourceOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private SearchSourceID _sourceOid;
    /**
     * Search engine database 
     */
	@MarshallField(as="db",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private SearchEngineDBID _dbOid = SearchEngineDBID.forId("default");
    /**
     * item number within results
     */
	@MarshallField(as="orderNumberWithinResults",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private int _orderNumberWithinResults = -1;
    /**
     * Percentage/ranking of the item: the degree of confidence that the item verifies the searcher expectations
     */
	@MarshallField(as="score",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private float _score = -1;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public SearchResultItemForModelObjectBase() {
		// nothing
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HasSummary
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		return new Summarizable() {
						@Override
						public Summary getSummary() {
							return _summary;
						}
						@Override
						public void setSummary(final Summary summary) {
							_summary = summary;
						}
		};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TRACKABLE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public TrackableModelObject asTrackable() {
		return (TrackableModelObject)this;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <U extends IndexableModelObject> void unsafeSetModelObjectType(final Class<U> modelObjectType) {
		_modelObjectType = (Class<M>)modelObjectType;
	}
	@Override 
	public void unsafeSetOid(final OID oid) {
		_oid = oid;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String debugInfo() {
		return Strings.customized("      oid: {}\n" + 
						          "numericId: {}\n" + 
						          "  summary: {}",
						          this.getOid(),
						          this.getNumericId(),
						          this.getSummary());
	}


}
