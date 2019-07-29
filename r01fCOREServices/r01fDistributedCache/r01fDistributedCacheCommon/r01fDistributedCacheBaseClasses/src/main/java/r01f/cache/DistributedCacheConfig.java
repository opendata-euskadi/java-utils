package r01f.cache;

import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.AppComponent;

public interface DistributedCacheConfig 
        extends ContainsConfigData {

   public AppComponent getAppComponent();

   public AppCode getAppCode();
   
   public <C extends DistributedCacheConfig > C as(final Class<C> type);

}
