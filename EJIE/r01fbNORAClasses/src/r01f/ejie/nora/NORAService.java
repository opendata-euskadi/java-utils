package r01f.ejie.nora;

import javax.inject.Singleton;

import lombok.Getter;
import lombok.experimental.Accessors;

@Singleton
@Accessors(prefix="_")
public class NORAService {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final NORAServiceForCountry _servicesForCountries;
	@Getter private final NORAServiceForState _servicesForStates;
	@Getter private final NORAServiceForCounty _servicesForCounties;
	@Getter private final NORAServiceForRegion _servicesForRegions;
	@Getter private final NORAServiceForMunicipality _servicesForMunicipalities;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public NORAService(final NORAServiceConfig noraConfig) {
		_servicesForCountries = new NORAServiceForCountry(noraConfig);
		_servicesForStates = new NORAServiceForState(noraConfig);
		_servicesForCounties = new NORAServiceForCounty(noraConfig);
		_servicesForRegions = new NORAServiceForRegion(noraConfig);
		_servicesForMunicipalities = new NORAServiceForMunicipality(noraConfig);
	}
}
