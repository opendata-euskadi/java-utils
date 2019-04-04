package r01f.ejie.nora;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import r01f.ejie.nora.NORAServiceResponseParser.GeoIDFactory;
import r01f.ejie.nora.NORAServiceResponseParser.GeoLocationFactory;
import r01f.locale.LanguageTexts;
import r01f.types.geo.GeoOIDs.GeoStateID;
import r01f.types.geo.GeoPosition2D;
import r01f.types.geo.GeoState;
import r01f.util.types.Strings;

@Slf4j
public class NORAServiceForState 
     extends NORAServiceBase<GeoStateID,GeoState> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	NORAServiceForState(final NORAServiceConfig noraConfig) {
		super(noraConfig,
			  GEO_ID_FACTORY,GEO_LOC_FACTORY);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATE
/////////////////////////////////////////////////////////////////////////////////////////
	static final GeoIDFactory<GeoStateID> GEO_ID_FACTORY = new GeoIDFactory<GeoStateID>() {
																	@Override
																	public GeoStateID from(final Long id) {
																		return GeoStateID.forId(id);
																	}
														   };
	static final GeoLocationFactory<GeoStateID,GeoState> GEO_LOC_FACTORY = new GeoLocationFactory<GeoStateID,GeoState>() {
																					@Override
																					public GeoState create() {
																						return new GeoState();
																					}
																					@Override
																					public GeoState from(final GeoStateID id,
																										 final String officialName,final LanguageTexts nameByLang,
																										 final GeoPosition2D pos2d) {
																						return new GeoState(id,
																											officialName,nameByLang,
																											pos2d);
																					}
																	  		};
/////////////////////////////////////////////////////////////////////////////////////////
//  STATES (spanish autonomies)
/////////////////////////////////////////////////////////////////////////////////////////
	public GeoState getState(final GeoStateID stateId) {
		if (stateId == null) throw new IllegalArgumentException("The stateId is needed!");
		log.info("[NORA]: getState({})",stateId);
		Map<String,String> noraWSMethodParams = Maps.newHashMap();
		noraWSMethodParams.put("id",stateId.asString());
		GeoState outState = this.getItem("autonomia_getByPk",
									 	 noraWSMethodParams);
		return outState;
	}
	public Collection<GeoState> getStates() {
		log.info("[NORA]: getStates()");
		Collection<GeoState> outStates = this.getAllItems("autonomia_getByDesc");
		return outStates;
	}
	public Collection<GeoState> findStatesWithText(final String text) {
		log.info("[NORA]: findStatesWithText({})",text);
		Map<String,String> noraWSMethodParams = null;
		if (Strings.isNOTNullOrEmpty(text))	 {
			noraWSMethodParams = Maps.newLinkedHashMapWithExpectedSize(1);
			noraWSMethodParams.put("value",text);
		}
		Collection<GeoState> outStates = this.getAllItems("autonomia_getByDesc",
														  noraWSMethodParams);
		return outStates;
	}
}
