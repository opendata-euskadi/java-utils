package r01f.ejie.nora;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.nora.NORAServiceResponseParser.GeoIDFactory;
import r01f.ejie.nora.NORAServiceResponseParser.GeoLocationFactory;
import r01f.locale.LanguageTexts;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoRegion;
import r01f.util.types.Strings;

@Slf4j
public class NORAServiceForRegion 
     extends NORAServiceBase<GeoRegionID,GeoRegion> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	NORAServiceForRegion(final NORAServiceConfig noraConfig) {
		super(noraConfig,
			  GEO_ID_FACTORY,GEO_LOC_FACTORY);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COUNTRIES
/////////////////////////////////////////////////////////////////////////////////////////
	static final GeoIDFactory<GeoRegionID> GEO_ID_FACTORY = new GeoIDFactory<GeoRegionID>() {
																		@Override
																		public GeoRegionID from(final Long id) {
																			return GeoRegionID.forId(id);
																		}
														    };
	static final GeoLocationFactory<GeoRegionID,GeoRegion> GEO_LOC_FACTORY = new GeoLocationFactory<GeoRegionID,GeoRegion>() {
																					@Override
																					public GeoRegion create() {
																						return new GeoRegion();
																					}
																					@Override
																					public GeoRegion from(final GeoRegionID id,
																										 final String officialName,final LanguageTexts nameByLang,
																										 final GeoPosition2D pos2d) {
																						return new GeoRegion(id,
																											 officialName,nameByLang,
																											 pos2d);
																					}
																	  		 };
/////////////////////////////////////////////////////////////////////////////////////////
//  Region (spanish comarcas)
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoRegion getRegion(final GeoStateID stateId,final GeoCountyID countyId,final GeoRegionID regionId) {
		if (countyId == null || regionId == null) throw new IllegalArgumentException("Both countyId and regionId are needed!");
		log.info("[NORA]: getRegion({},{},{})",stateId,countyId,regionId);
		Map<String,String> noraWSMethodParams = Maps.newHashMap();
		noraWSMethodParams.put("provinciaId",countyId.asString());
		noraWSMethodParams.put("id",regionId.asString());
		GeoRegion outRegion = this.getItem("comarca_getByPk",
										   noraWSMethodParams);
		return outRegion;
	}
	public Collection<GeoRegion> getRegions() {
		return this.getRegionsOf(null,	// no state
								 null);	// no county
	}
	public Collection<GeoRegion> getRegionsOf(final GeoStateID stateId) {
		return this.getRegionsOf(stateId,
								 null);		// no county
	}
	public Collection<GeoRegion> getRegionsOf(final GeoStateID stateId,final GeoCountyID countyId) {
		log.info("[NORA]: getRegionsOf({},{})",stateId,countyId);
		Map<String,String> noraWSMethodParams = null;
		if (stateId != null || countyId != null) { 
			noraWSMethodParams = Maps.newHashMap();
			if (stateId != null) 	noraWSMethodParams.put("autonomiaId",stateId.asString());
			if (countyId != null) noraWSMethodParams.put("provinciaId",countyId.asString());
		}
		Collection<GeoRegion> outRegions = this.getAllItems("comarca_getByDesc",
															noraWSMethodParams);
		return outRegions;
	}
	public Collection<GeoRegion> findRegionsWithText(final String text) {
		return this.findRegionsWithTextOf(null,	// no state set
										  null,	// no county set
										  text);	
	}
	public Collection<GeoRegion> findRegionsWithText(final GeoStateID stateId,
													 final String text) {
		return this.findRegionsWithTextOf(stateId,	
										  null,	// no county set
										  text);	
	}
	public Collection<GeoRegion> findRegionsWithText(final GeoCountyID countyId,
													 final String text) {
		return this.findRegionsWithTextOf(null,	// no state set	
										  countyId,	
										  text);	
	}
	public Collection<GeoRegion> findRegionsWithTextOf(final GeoStateID stateId,final GeoCountyID countyId,
													   final String text) {
		log.info("[NORA]: findRegionsWithTextOf({},{},{})",stateId,countyId,text);
		Map<String,String> noraWSMethodParams = null;
		if (stateId != null || countyId != null || Strings.isNOTNullOrEmpty(text)) { 
			noraWSMethodParams = Maps.newHashMap();
			if (stateId != null) 	noraWSMethodParams.put("autonomiaId",stateId.asString());
			if (countyId != null) 	noraWSMethodParams.put("provinciaId",countyId.asString());
			if (Strings.isNOTNullOrEmpty(text))	 noraWSMethodParams.put("value",text);
		}
		Collection<GeoRegion> outStates = this.getAllItems("comarca_getByDesc",
														   noraWSMethodParams);
		return outStates;
	}
}
