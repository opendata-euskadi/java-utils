package r01f.aspects.interfaces.logging;



/**
 * Implementaci�n simple del formateo de los par�metros de llamada 
 * a un m�todo
 * Es utilizado en la anotaci�n @LoggedMethodCalls que se utiliza junto con el aspecto
 * LoggedMethodCallsAspect
 */
public class LoggedMethodCallsParamsVoidFormatter 
  implements LoggedMethodCallsParamsFormatter {

	@Override
	public String formatParams(Object... params) {
		return "";
	}

}
