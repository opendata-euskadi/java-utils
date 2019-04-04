package r01f.events.users.login;

import com.google.common.eventbus.Subscribe;

import r01f.events.PersistenceOperationEvents.PersistenceOperationErrorEvent;
import r01f.events.login.LoginEntryEventListener.PasswordResetEventListener;
import r01f.model.security.login.reset.PasswordResetResponseBase;

/**
 * Default {@link PersistenceOperationErrorEvent}s listener that simply logs the op NOK events
 * @param <R>
 */
public abstract class PasswordResetEntryEventListener<R extends PasswordResetResponseBase>
		   implements PasswordResetEventListener<R> {

	@Subscribe	// subscribes this event listener at the EventBus
	public abstract void onPasswordReset(final R response);

}
