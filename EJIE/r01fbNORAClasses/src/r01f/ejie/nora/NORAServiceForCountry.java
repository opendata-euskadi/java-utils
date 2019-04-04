package r01f.ejie.nora;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.nora.NORAServiceResponseParser.GeoIDFactory;
import r01f.ejie.nora.NORAServiceResponseParser.GeoLocationFactory;
import r01f.locale.LanguageTexts;
import r01f.types.geo.GeoCountry;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoOIDs.GeoCountryID;
import r01f.util.types.Strings;

@Slf4j
public class NORAServiceForCountry 
     extends NORAServiceBase<GeoCountryID,GeoCountry> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	NORAServiceForCountry(final NORAServiceConfig noraConfig) {
		super(noraConfig,
			  GEO_ID_FACTORY,GEO_LOC_FACTORY);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COJNTRY
/////////////////////////////////////////////////////////////////////////////////////////
	static final GeoIDFactory<GeoCountryID> GEO_ID_FACTORY = new GeoIDFactory<GeoCountryID>() {
																	@Override
																	public GeoCountryID from(final Long id) {
																		return GeoCountryID.forId(id);
																	}
															  };
	static final GeoLocationFactory<GeoCountryID,GeoCountry> GEO_LOC_FACTORY = new GeoLocationFactory<GeoCountryID,GeoCountry>() {
																						@Override
																						public GeoCountry create() {
																							return new GeoCountry();
																						}
																						@Override
																						public GeoCountry from(final GeoCountryID id,
																											   final String officialName,final LanguageTexts nameByLang,
																											   final GeoPosition2D pos2d) {
																							return new GeoCountry(id,
																												  officialName,nameByLang,
																												  pos2d);
																						}
																		  		};
/////////////////////////////////////////////////////////////////////////////////////////
//  COUNTRIES
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoCountry getCountry(final GeoCountryID countryId) {
		if (countryId == null) throw new IllegalArgumentException("The countryId is needed!");
		log.info("[NORA]: getCountry({})",countryId);
		Map<String,String> noraWSMethodParams = Maps.newHashMap();
		noraWSMethodParams.put("id",countryId.asString());
		GeoCountry outCountry = this.getItem("pais_getByPk",
											 noraWSMethodParams);
		return outCountry;
	}
	public Collection<GeoCountry> getCountries() {
		log.info("[NORA]: getCountries()");
		Collection<GeoCountry> outCountries = this.getAllItems("pais_getByDesc");
		return outCountries;
	}
	public Collection<GeoCountry> findCountriesByText(final String text) {
		log.info("[NORA]: findCountriesByText({})",text);
		Map<String,String> noraWSMethodParams = null;
		if (Strings.isNOTNullOrEmpty(text))	 {
			noraWSMethodParams = Maps.newLinkedHashMapWithExpectedSize(1);
			noraWSMethodParams.put("value",text);
		}
		Collection<GeoCountry> outCountries = this.getAllItems("pais_getByDesc",
															   noraWSMethodParams);
		return outCountries;
	}
}
