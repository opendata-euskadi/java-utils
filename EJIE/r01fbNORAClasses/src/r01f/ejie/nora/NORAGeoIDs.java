package r01f.ejie.nora;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoStateID;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class NORAGeoIDs {
	
	public static final GeoCountryID SPAIN = GeoCountryID.forId(108);
	
	public static final GeoStateID EUSKADI = GeoStateID.forId(16);
	
	public static final GeoCountyID ARABA = GeoCountyID.forId(1);
	public static final GeoCountyID BIZKAIA = GeoCountyID.forId(48);
	public static final GeoCountyID GIPUZKOA = GeoCountyID.forId(20);	
}
