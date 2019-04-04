package r01f.events.users.login;

import com.google.common.eventbus.Subscribe;

import r01f.events.PersistenceOperationEvents.PersistenceOperationErrorEvent;
import r01f.events.login.LoginEntryEventListener.LoginErrorEventListener;
import r01f.model.security.login.response.LoginResponseError;

/**
 * Default {@link PersistenceOperationErrorEvent}s listener that simply logs the op NOK events
 * @param <R>
 */
public abstract class LoginErrorEntryEventListener<R extends LoginResponseError>
		   implements LoginErrorEventListener<R> {

	@Subscribe	// subscribes this event listener at the EventBus
	public abstract void onLoginError(final R errorResponse);

}
