package r01f.events.users.login;

import com.google.common.eventbus.Subscribe;

import r01f.events.PersistenceOperationEvents.PersistenceOperationErrorEvent;
import r01f.events.login.LoginEntryEventListener.PasswordRecoveryEventListener;
import r01f.model.security.login.recovery.PasswordRecoveryResponseBase;

/**
 * Default {@link PersistenceOperationErrorEvent}s listener that simply logs the op NOK events
 * @param <R>
 */
public abstract class PasswordRecoveryEntryEventListener<R extends PasswordRecoveryResponseBase>
		   implements PasswordRecoveryEventListener<R> {

	@Subscribe	// subscribes this event listener at the EventBus
	public abstract void onPasswordRecovery(final R okResponse);

}
