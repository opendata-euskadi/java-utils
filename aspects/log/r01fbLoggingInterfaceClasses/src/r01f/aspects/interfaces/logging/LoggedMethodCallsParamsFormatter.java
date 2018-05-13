package r01f.aspects.interfaces.logging;

/**
 * Interfaz que han de cumplir los tipos responsables de "pintar" log sobre los par�metros
 * de las llamadas a m�todos
 * Implementaciones concretas de este interfaz se utilizan en la anotaci�n @LoggedMethodCalls
 * Ej:
 * <pre class='brush:java'>
 * 		@LoggedMethodCalls(level=LogLevel.DEBUG,
 * 						   module="[CLIENT API]",start="[START]",end="[END]",
 * 						   paramsFormatter=MyMethodParamsFormatter.class)
 * 		public class R01MClientAPI {
 * 			public void myPublicMethod() {
 * 				...
 * 			}
 * 		}
 * </pre>
 */
public interface LoggedMethodCallsParamsFormatter {
	/**
	 * Formatea los par�metros de un m�todo
	 * @param params los par�metros
	 * @return el log
	 */
	public String formatParams(Object... params);
}
