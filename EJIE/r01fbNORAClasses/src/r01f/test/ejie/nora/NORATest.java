package r01f.test.ejie.nora;

import java.util.Collection;

import com.google.common.collect.Iterables;

import r01f.ejie.nora.NORAGeoIDs;
import r01f.ejie.nora.NORAService;
import r01f.ejie.nora.NORAServiceConfig;
import r01f.types.geo.GeoCountry;
import r01f.types.geo.GeoCounty;
import r01f.types.geo.GeoMunicipality;
import r01f.types.geo.GeoOIDs.GeoMunicipalityID;
import r01f.types.geo.GeoOIDs.GeoRegionID;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoRegion;
import r01f.types.geo.GeoState;
import r01f.types.url.Url;

public class NORATest {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(String[] args) {
		NORAServiceConfig cfg = new NORAServiceConfig(Url.from("http://svc.inter.integracion.jakina.ejiedes.net/ctxapp/t17iApiWS"));
		NORAService nora = new NORAService(cfg);

		_testCountries(nora);
		_testStates(nora);
		_testCounties(nora);
		_testRegions(nora);
		_testMunicipalities(nora);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private static void _testCountries(final NORAService nora) {
		System.out.println("[Countries]------------------------------------------------------------");
		
		// Load all countries
		Collection<GeoCountry> countries = nora.getServicesForCountries()
											   .getCountries();
		for (GeoCountry country : countries) {
			System.out.println("===>" + country.debugInfo());
		}
		// find countries by text
		Collection<GeoCountry> countriesWithText = nora.getServicesForCountries()
													   .findCountriesByText("nia");	// albania, alemania, rumania...
		for (GeoCountry countryWithText : countriesWithText) {
			System.out.println("===>" + countryWithText.debugInfo());
		}		
		// load a single country
		GeoCountry country = nora.getServicesForCountries()
								 .getCountry(Iterables.getFirst(countries,null).getId());
		System.out.println(">>>>" + country.debugInfo());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private static void _testStates(final NORAService nora) {
		System.out.println("[States]---------------------------------------------------------------");
		
		// Load all states
		Collection<GeoState> states = nora.getServicesForStates()
										  .getStates();
		for (GeoState state : states) {
			System.out.println("===>" + state.debugInfo());
		}
		// find states by text
		Collection<GeoState> statesWithText = nora.getServicesForStates()
												  .findStatesWithText("euta");	// ceuta
		for (GeoState stateWithText : statesWithText) {
			System.out.println("===>" + stateWithText.debugInfo());
		}		
		// load a single state
		GeoState state = nora.getServicesForStates()
							 .getState(Iterables.getFirst(states,null).getId());
		System.out.println(">>>>" + state.debugInfo());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _testCounties(final NORAService nora) {
		System.out.println("[Counties]-------------------------------------------------------------");
		
		// Load all counties
		Collection<GeoCounty> counties = nora.getServicesForCounties()
											 .getCounties();
		for (GeoCounty county : counties) {
			System.out.println("===>" + county.debugInfo());
		}
		// load a single county
		GeoCounty county = nora.getServicesForCounties()
							   .getCounty(NORAGeoIDs.EUSKADI,NORAGeoIDs.ARABA);
		System.out.println(">>>>" + county.debugInfo());
		
		// load counties of a state
		Collection<GeoCounty> countiesOf = nora.getServicesForCounties()
											   .getCountiesOf(GeoStateID.forId(18));	// ceuta & melilla
		for (GeoCounty countyOf : countiesOf) {
			System.out.println("===>" + countyOf.debugInfo());
		}
		// load counties of a state with text
		Collection<GeoCounty> countiesOfWithText = nora.getServicesForCounties()
													   .findCountiesWithTextOf(GeoStateID.forId(18),
																			  "ceu");	// ceuta
		for (GeoCounty countyWithTextOf : countiesOfWithText) {
			System.out.println("===>" + countyWithTextOf.debugInfo());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _testRegions(final NORAService nora) {
		System.out.println("[Regions]--------------------------------------------------------------");
		
		// Load all regions
		Collection<GeoRegion> regions = nora.getServicesForRegions()
											.getRegions();
		for (GeoRegion region : regions) {
			System.out.println("===>" + region.debugInfo());
		}
		// load a single region
		GeoRegion region = nora.getServicesForRegions()
							   .getRegion(NORAGeoIDs.EUSKADI,
										  NORAGeoIDs.ARABA,
										  GeoRegionID.forId(1));	// valles alaveses
		System.out.println(">>>>" + region.debugInfo());
		
		// load regions of a state
		Collection<GeoRegion> regionsOf = nora.getServicesForRegions()
											  .getRegionsOf(NORAGeoIDs.EUSKADI,	// euskadi
															NORAGeoIDs.ARABA);	// araba
		for (GeoRegion regionOf : regionsOf) {
			System.out.println("===>" + regionOf.debugInfo());
		}
		// load regions of a state with text
		Collection<GeoRegion> regionsOfWithText = nora.getServicesForRegions()
													  .findRegionsWithTextOf(NORAGeoIDs.EUSKADI,	// euskadi
																			 NORAGeoIDs.ARABA,	// araba
																			 "valle");				// valles alaveses
		for (GeoRegion regionWithTextOf : regionsOfWithText) {
			System.out.println("===>" + regionWithTextOf.debugInfo());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _testMunicipalities(final NORAService nora) {
		System.out.println("[Municipalities]-------------------------------------------------------");
		
		// Load all municipalities
		Collection<GeoMunicipality> muns = nora.getServicesForMunicipalities()
											   .getMunicipalities();
		for (GeoMunicipality mun : muns) {
			System.out.println("===>" + mun.debugInfo());
		}
		// load a single mun
		GeoMunicipality mun = nora.getServicesForMunicipalities()
							   .getMunicipality(NORAGeoIDs.EUSKADI,
										  		NORAGeoIDs.ARABA,
										  		GeoMunicipalityID.forId(59));	// gasteiz
		System.out.println(">>>>" + mun.debugInfo());
		
		// load muns of a state
		Collection<GeoMunicipality> munsOf = nora.getServicesForMunicipalities()
											  .getMunicipalitiesOf(NORAGeoIDs.EUSKADI,	// euskadi
																   NORAGeoIDs.ARABA);	// araba
		for (GeoMunicipality munOf : munsOf) {
			System.out.println("===>" + munOf.debugInfo());
		}
		// load muns of a state with text
		Collection<GeoMunicipality> munsOfWithText = nora.getServicesForMunicipalities()
													  .findMunicipalitiesWithTextOf(NORAGeoIDs.EUSKADI,	// euskadi
																			 		NORAGeoIDs.ARABA,	// araba
																			 		null,				// no region
																			 		"vit");				// gasteiz
		for (GeoMunicipality munWithTextOf : munsOfWithText) {
			System.out.println("===>" + munWithTextOf.debugInfo());
		}
	}
}
