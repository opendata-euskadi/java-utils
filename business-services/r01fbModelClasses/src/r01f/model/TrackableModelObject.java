package r01f.model;

import r01f.model.facets.ModelObjectFacet;

public interface TrackableModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  HasTrackableFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface HasTrackableFacet 
					extends ModelObjectFacet {
		
		public TrackableModelObject asTrackable();
		
		public ModelObjectTracking getTrackingInfo();
		public void setTrackingInfo(ModelObjectTracking trackingInfo);
	}

}