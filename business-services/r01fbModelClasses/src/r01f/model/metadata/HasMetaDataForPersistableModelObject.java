package r01f.model.metadata;

import com.google.common.annotations.GwtIncompatible;

import r01f.guids.OID;

/**
 * Interface for types that describes {@link ModelObject}s
 */
@GwtIncompatible
public interface HasMetaDataForPersistableModelObject<O extends OID> 
		 extends HasMetaDataForModelObject,
		 		 HasMetaDataForHasOIDModelObject<O>,
		 		 HasMetaDataForHasEntityVersionModelObject,
		 		 HasMetaDataForHasTrackableFacetForModelObject {
	
	public OID getDOCID();
	
}
