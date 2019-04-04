package r01f.ejie.nora;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.nora.NORAServiceResponseParser.GeoIDFactory;
import r01f.ejie.nora.NORAServiceResponseParser.GeoLocationFactory;
import r01f.locale.LanguageTexts;
import r01f.types.geo.GeoCounty;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.util.types.Strings;

@Slf4j
public class NORAServiceForCounty 
     extends NORAServiceBase<GeoCountyID,GeoCounty> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	NORAServiceForCounty(final NORAServiceConfig noraConfig) {
		super(noraConfig,
			  GEO_ID_FACTORY,GEO_LOC_FACTORY);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COUNTRIES
/////////////////////////////////////////////////////////////////////////////////////////
	static final GeoIDFactory<GeoCountyID> GEO_ID_FACTORY = new GeoIDFactory<GeoCountyID>() {
																	@Override
																	public GeoCountyID from(final Long id) {
																		return GeoCountyID.forId(id);
																	}
														   };
	static final GeoLocationFactory<GeoCountyID,GeoCounty> GEO_LOC_FACTORY = new GeoLocationFactory<GeoCountyID,GeoCounty>() {
																					@Override
																					public GeoCounty create() {
																						return new GeoCounty();
																					}
																					@Override
																					public GeoCounty from(final GeoCountyID id,
																										 final String officialName,final LanguageTexts nameByLang,
																										 final GeoPosition2D pos2d) {
																						return new GeoCounty(id,
																											 officialName,nameByLang,
																											 pos2d);
																					}
																	  		 };
/////////////////////////////////////////////////////////////////////////////////////////
//  Counties (spanish provincias)
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoCounty getCounty(final GeoStateID stateId,final GeoCountyID countyId) {
		if (countyId == null) throw new IllegalArgumentException("The countyId is needed!");
		log.info("[NORA]: getCounty({},{})",stateId,countyId);
		Map<String,String> noraWSMethodParams = Maps.newHashMap();
		noraWSMethodParams.put("id",countyId.asString());
		GeoCounty outCounty = this.getItem("provincia_getByPk",
										   noraWSMethodParams);
		return outCounty;
	}
	public Collection<GeoCounty> getCounties() {
		return this.getCountiesOf(null);	// no state
	}
	public Collection<GeoCounty> getCountiesOf(final GeoStateID stateId) {
		log.info("[NORA]: getCountiesOf({})",stateId);
		Map<String,String> noraWSMethodParams = null;
		if (stateId != null) { 
			noraWSMethodParams = Maps.newHashMap();
			noraWSMethodParams.put("autonomiaId",stateId.asString());
		}
		Collection<GeoCounty> outCounties = this.getAllItems("provincia_getByDesc",
															 noraWSMethodParams);
		return outCounties;
	}
	public Collection<GeoCounty> findCountiesWithText(final String text) {
		return this.findCountiesWithTextOf(null,	// no state set
										   text);
	}
	public Collection<GeoCounty> findCountiesWithTextOf(final GeoStateID stateId,
													    final String text) {
		log.info("[NORA]: findCountiesWithTextOf({},{})",stateId,text);
		Map<String,String> noraWSMethodParams = null;
		if (stateId != null || Strings.isNOTNullOrEmpty(text)) { 
			noraWSMethodParams = Maps.newHashMap();
			if (stateId != null) noraWSMethodParams.put("autonomiaId",stateId.asString());
			if (Strings.isNOTNullOrEmpty(text))	 noraWSMethodParams.put("value",text);
		}
		Collection<GeoCounty> outCounties = this.getAllItems("provincia_getByDesc",
															 noraWSMethodParams);
		return outCounties;
	}
																	  		 
}
