package r01f.bootstrap.services.config.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.ids.ServiceIDs.CoreModule;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class ServicesCoreSubModuleBootrapConfig<C extends ContainsConfigData> { 
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AppComponent _component;
	@Getter private final C _config;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <CFG extends ContainsConfigData> ServicesCoreSubModuleBootrapConfig<CFG> createFor(final AppComponent component,final CFG cfg) {
		return new ServicesCoreSubModuleBootrapConfig<CFG>(component,cfg);
	}
	public static <CFG extends ContainsConfigData> ServicesCoreSubModuleBootrapConfig<CFG> createForDBPersistenceSubModule(final CFG cfg) {
		return new ServicesCoreSubModuleBootrapConfig<CFG>(CoreModule.DBPERSISTENCE,cfg);
	}	
	public static <CFG extends ContainsConfigData> ServicesCoreSubModuleBootrapConfig<CFG> createForSearchPersistenceSubModule(final CFG cfg) {
		return new ServicesCoreSubModuleBootrapConfig<CFG>(CoreModule.SEARCHPERSISTENCE,cfg);
	}	
}
