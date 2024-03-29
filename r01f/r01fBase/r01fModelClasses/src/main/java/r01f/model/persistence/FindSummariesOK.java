package r01f.model.persistence;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.exceptions.Throwables;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.PersistableModelObject;
import r01f.model.SummarizedModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="findResult",typeId="FINDSummariesOK")
@Accessors(prefix="_")
@SuppressWarnings("unchecked")
public class FindSummariesOK<M extends PersistableModelObject<? extends OID>>
	 extends PersistenceOperationOnObjectOK<Collection<? extends SummarizedModelObject<M>>>
  implements FindSummariesResult<M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Info about the model object
	 * beware that the {@link PersistenceOperationOnObjectOK} wraps a {@link Collection}
	 * of model objects
	 */
	@MarshallField(as="modelObjectType",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Class<M> _modelObjectType;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public FindSummariesOK() {
		super(PersistenceRequestedOperation.FIND,PersistencePerformedOperation.FOUND,
			  Collection.class);
	}
	protected FindSummariesOK(final Class<M> entityType) {
		this();
		_modelObjectType = entityType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the found entities' oids if the persistence find operation was successful or a PersistenteException if not
	 * @throws PersistenceException
	 */
	public <O extends OID> Collection<O> getOidsOrThrow() throws PersistenceException {
		if (CollectionUtils.isNullOrEmpty(_operationExecResult)) return Lists.newArrayList();
		return FluentIterable.from(_operationExecResult)
							 .transform(new Function<SummarizedModelObject<M>,O>() {
												@Override 
												public O apply(final SummarizedModelObject<M> entitySummary) {
													if (entitySummary instanceof HasOID) return ((HasOID<O>)entitySummary).getOid();
													throw new IllegalStateException(Throwables.message("The entity of type {} does NOT implements {}",
																									   entitySummary.getModelObjectType(),HasOID.class));
												}
								 			
							 			})
							 .toList();
	}
	/**
	 * When a single result is expected, this method returns this entity's oid
	 * @return
	 */
	public <O extends OID> O getSingleExpectedOidOrThrow() {
		SummarizedModelObject<M> outEntitySummary = this.getSingleExpectedOrThrow();
		if (outEntitySummary != null) {
			if (outEntitySummary instanceof HasOID) return ((HasOID<O>)outEntitySummary).getOid();
			throw new IllegalStateException(Throwables.message("The entity of type {} does NOT implements {}",
															   outEntitySummary.getModelObjectType(),HasOID.class));	
		}
		return null;
	}
	/**
	 * When a single result is expected, this method returns this entity
	 * @return
	 */
	public <S extends SummarizedModelObject<M>> S getSingleExpectedOrThrow() {
		S outEntitySummary = null;
		Collection<S> entities = (Collection<S>)super.getOrThrow();
		if (CollectionUtils.hasData(entities)) {
			outEntitySummary = CollectionUtils.of(entities).pickOneAndOnlyElement("A single instance of {} was expected to be found BUT {} were found",SummarizedModelObject.class,entities.size());
		} 
		return outEntitySummary;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindSummariesOK<M> asCRUDOK() {
		return this;
	}
	@Override
	public FindSummariesError<M> asCRUDError() {
		throw new ClassCastException();
	}
	
	@Override
	public Collection<? extends SummarizedModelObject<M>> getOrThrow() throws PersistenceException {
		if (this.hasFailed()) this.asOperationExecError()		
								  .throwAsPersistenceException();
		return _operationExecResult;
	}	
	
	
}
