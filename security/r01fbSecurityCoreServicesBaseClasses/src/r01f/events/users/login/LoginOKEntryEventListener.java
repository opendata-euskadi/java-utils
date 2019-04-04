package r01f.events.users.login;

import com.google.common.eventbus.Subscribe;

import r01f.events.PersistenceOperationEvents.PersistenceOperationErrorEvent;
import r01f.events.login.LoginEntryEventListener.LoginOKEventListener;
import r01f.model.security.login.response.LoginResponseOK;

/**
 * Default {@link PersistenceOperationErrorEvent}s listener that simply logs the op NOK events
 * @param <R>
 */
public abstract class LoginOKEntryEventListener<R extends LoginResponseOK>
		   implements LoginOKEventListener<R> {

	@Subscribe	// subscribes this event listener at the EventBus
	public abstract void onLoginOK(final R okResponse);

}
