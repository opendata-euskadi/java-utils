package r01f.ejie.nora;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.nora.NORAServiceResponseParser.GeoIDFactory;
import r01f.ejie.nora.NORAServiceResponseParser.GeoLocationFactory;
import r01f.locale.LanguageTexts;
import r01f.types.geo.GeoMunicipality;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.util.types.Strings;

@Slf4j
public class NORAServiceForMunicipality 
     extends NORAServiceBase<GeoMunicipalityID,GeoMunicipality> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	NORAServiceForMunicipality(final NORAServiceConfig noraConfig) {
		super(noraConfig,
			  GEO_ID_FACTORY,GEO_LOC_FACTORY);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COUNTRIES
/////////////////////////////////////////////////////////////////////////////////////////
	static final GeoIDFactory<GeoMunicipalityID> GEO_ID_FACTORY = new GeoIDFactory<GeoMunicipalityID>() {
																			@Override
																			public GeoMunicipalityID from(final Long id) {
																				return GeoMunicipalityID.forId(id);
																			}
																  };
	static final GeoLocationFactory<GeoMunicipalityID,GeoMunicipality> GEO_LOC_FACTORY = new GeoLocationFactory<GeoMunicipalityID,GeoMunicipality>() {
																									@Override
																									public GeoMunicipality create() {
																										return new GeoMunicipality();
																									}
																									@Override
																									public GeoMunicipality from(final GeoMunicipalityID id,
																														 final String officialName,final LanguageTexts nameByLang,
																														 final GeoPosition2D pos2d) {
																										return new GeoMunicipality(id,
																															 officialName,nameByLang,
																															 pos2d);
																									}
																					  		};
/////////////////////////////////////////////////////////////////////////////////////////
//  Municipality (spanish municipios)
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoMunicipality getMunicipality(final GeoStateID stateId,final GeoCountyID countyId,final GeoMunicipalityID munId) {
		if (countyId == null || munId == null) throw new IllegalArgumentException("Both countyId and municipalityId are needed!");
		log.info("[NORA]: getMunicipality({},{},{})",stateId,countyId,munId);
		Map<String,String> noraWSMethodParams = Maps.newHashMap();
		noraWSMethodParams.put("provinciaId",countyId.asString());
		noraWSMethodParams.put("id",munId.asString());
		GeoMunicipality outMun = this.getItem("municipio_getByPk",
											  noraWSMethodParams);
		return outMun;
	}
	public Collection<GeoMunicipality> getMunicipalities() {
		return this.getMunicipalitiesOf(null,	// no state
										null,	// no county
										null);	// no region
	}
	public Collection<GeoMunicipality> getMunicipalitiesOf(final GeoStateID stateId) {
		return this.getMunicipalitiesOf(stateId,
										null,		// no county
										null);		// no region
	}
	public Collection<GeoMunicipality> getMunicipalitiesOf(final GeoStateID stateId,final GeoCountyID countyId) {
		return this.getMunicipalitiesOf(stateId,
										countyId,		
										null);		// no region
	}
	public Collection<GeoMunicipality> getMunicipalitiesOf(final GeoStateID stateId,final GeoCountyID countyId,final GeoRegionID regionId) {
		log.info("[NORA]: getMunacipalitiesOf({},{},{})",stateId,countyId,regionId);
		Map<String,String> noraWSMethodParams = null;
		if (stateId != null || countyId != null || regionId != null) { 
			noraWSMethodParams = Maps.newHashMap();
			if (stateId != null) 	noraWSMethodParams.put("autonomiaId",stateId.asString());
			if (countyId != null) noraWSMethodParams.put("provinciaId",countyId.asString());
			if (regionId != null) noraWSMethodParams.put("comarcaId",regionId.asString());
		}
		Collection<GeoMunicipality> outMuns = this.getAllItems("municipio_getByDesc",
															   noraWSMethodParams);
		return outMuns;
	}
	public Collection<GeoMunicipality> findMunicipalitiesWithTextOf(final String text) {
		return this.findMunicipalitiesWithTextOf(null,	// no state
												 null,	// no county
												 null,	// no region
												 text);
	}
	public Collection<GeoMunicipality> findMunicipalitiesWithTextOf(final GeoStateID stateId,
																	final String text) {
		return this.findMunicipalitiesWithTextOf(stateId,	
												 null,	// no county
												 null,	// no region
												 text);
	}
	public Collection<GeoMunicipality> findMunicipalitiesWithTextOf(final GeoStateID stateId,final GeoCountyID countyId,
																	final String text) {
		return this.findMunicipalitiesWithTextOf(stateId,	
												 countyId,	
												 null,	// no region
												 text);
	}
	public Collection<GeoMunicipality> findMunicipalitiesWithTextOf(final GeoStateID stateId,final GeoCountyID countyId,final GeoRegionID regionId,
													   		  		final String text) {
		log.info("[NORA]: findMunicipalitiesWithTextOf({},{},{},{})",stateId,countyId,regionId,text);
		Map<String,String> noraWSMethodParams = null;
		if (stateId != null || countyId != null || regionId != null || Strings.isNullOrEmpty(text)) { 
			noraWSMethodParams = Maps.newHashMap();
			if (stateId != null) 	noraWSMethodParams.put("autonomiaId",stateId.asString());
			if (countyId != null) 	noraWSMethodParams.put("provinciaId",countyId.asString());
			if (regionId != null) 	noraWSMethodParams.put("comarcaId",regionId.asString());
			if (Strings.isNOTNullOrEmpty(text))	 noraWSMethodParams.put("value",text);
		}
		Collection<GeoMunicipality> outMuns = this.getAllItems("municipio_getByDesc",
														  	   noraWSMethodParams);
		return outMuns;
	}																							  		
}
