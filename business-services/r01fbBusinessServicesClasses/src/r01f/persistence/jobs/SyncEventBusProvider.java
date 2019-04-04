package r01f.persistence.jobs;

import javax.inject.Inject;

import com.google.common.eventbus.EventBus;

import r01f.types.ExecutionMode;

public class SyncEventBusProvider 
	 extends EventBusProviderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public SyncEventBusProvider() {
		super(ExecutionMode.SYNC);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected EventBus _createEventBusInstance() {
		_eventBusInstance = new EventBus("R01 SYNC EventBus");
		return _eventBusInstance;
	}
}