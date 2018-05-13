package r01f.aspects.interfaces.logging;

/**
 * Enum que se utiliza en la anotaci�n @LoggedMethodCalls para indicar d�nde hay que hacer log:
 */
public enum LoggedMethodCallsWhen {
	BEGIN,		// principio del m�todo
	END,		// final del m�todo
	AROUND;		// al principio y al final del m�todo
}
