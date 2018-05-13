package r01f.model.search;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.Summarizable;
import r01f.guids.OID;
import r01f.model.IndexableModelObject;
import r01f.model.PersistableModelObject;
import r01f.model.metadata.TypeMetaDataInspector;
import r01f.objectstreamer.annotations.MarshallField;


@Accessors(prefix="_")
public abstract class SearchResultItemContainsModelObjectBase<M extends PersistableModelObject<? extends OID> & IndexableModelObject>
    	   implements SearchResultItemContainsModelObject<M> {

	private static final long serialVersionUID = -2917189770601857300L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="modelObject")
	@Getter @Setter protected M _modelObject;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <O extends OID> O getOid() {
		return (O)_modelObject.getOid();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <U extends IndexableModelObject> void unsafeSetModelObject(final U modelObject) {
		_modelObject = (M)modelObject;
	}
	@Override
	public void unsafeSetOid(final OID oid) {
		_modelObject.unsafeSetOid(oid);
	}
	@Override @SuppressWarnings("unchecked")
	public Class<M> getModelObjectType() {
		return (Class<M>)_modelObject.getClass();
	}
	@Override
	public <U extends IndexableModelObject> void unsafeSetModelObjectType(final Class<U> modelObjectType) {
		// nothing
	}
	@Override
	public long getModelObjectTypeCode() {
		return TypeMetaDataInspector.singleton()
									.getTypeMetaDataFor(this.getModelObjectType())
									.getTypeMetaData()
									.modelObjTypeCode();
	}
	@Override
	public void setModelObjectTypeCode(final long code) {
		// nothing
	}
	@Override
	public long getEntityVersion() {
		return _modelObject.getEntityVersion();
	}
	@Override
	public void setEntityVersion(final long version) {
		_modelObject.setEntityVersion(version);
	}
	@Override
	public long getNumericId() {
		return _modelObject.getNumericId();
	}
	@Override
	public void setNumericId(final long numericId) {
		_modelObject.setNumericId(numericId);
	}
	@Override
	public Summarizable asSummarizable() {
		return _modelObject.asSummarizable();
	}
}
