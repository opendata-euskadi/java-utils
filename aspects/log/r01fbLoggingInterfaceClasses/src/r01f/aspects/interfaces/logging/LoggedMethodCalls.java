package r01f.aspects.interfaces.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Anotaci�n que se pone a los tipos para los que se quiere mostrar una traza justo antes 
 * de invocar a cualquier m�todo p�blico y justo despu�s de terminar la invocaci�n
 * Ej:
 * <pre class='brush:java'>
 * 		@LoggedMethodCalls(level=LogLevel.DEBUG,when=LoggedMethodCallsWhen.AROUND,
 * 						   module="[CLIENT API]",start="[START]",end="[END]",
 * 						   paramsFormatter=MyMethodsParamsFormatter.class)
 * 		public class ClientAPI {
 * 			public void myPublicMethod() {
 * 				...
 * 			}
 * 		}
 * </pre>
 * Es equivalente a:
 * <pre class='brush:java'>
 * 		@Sl4fj
 * 		public class ClientAPI {
 * 			public void myPublicMethod() {
 * 				log.debug("[CLIENT API][START]: myPublicMethod");
 * 				...
 * 				log.debug("  [CLIENT API][END]: myPublicMethod");
 * 			}
 * 		}
 * </pre>
 * es decir, "inyecta" trazas al principio de la llamada a cada m�todo y al final de la invocaci�n del mismo
 * de forma transparente para el desarrollador.
 * Esta anotaci�n se utiliza en el aspecto LoggedMethodCallsAspect
 * 
 * Hay diferentes formateadores para los par�metros:
 * 		LoggedMethodCallsParamsDefaultFormatter --> Devuelve inforaci�n sobre n�mero y tipo de par�metros
 * 		LoggedMethodCallsParamsVoidFormatter	--> NO devuelve informaci�n sobre los par�metros
 * 		custom --> basta con proporcionar una clase que implemente el interfaz {@link LoggedMethodCallsParamsFormatter}
 * 
 * NOTA:
 * Si NO se quiere que un m�todo sea loggeado, basta con anotarlo con @DoNotLog
 * Ej:
 * <pre class='brush:java'>
 * 		@LoggedMethodCalls(level=LogLevel.DEBUG,when=LoggedMethodCallsWhen.AROUND,
 * 						   module="[CLIENT API]",start="[START]",end="[END]",
 * 						   paramsFormatter=MyMethodsParamsFormatter.class)
 * 		public class ClientAPI {
 * 			public void myPublicMethod() {
 * 				...
 * 				@DoNotLog
 * 				public void notLoggedMethod() {
 * 					...
 * 				}
 * 			}
 * 		}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoggedMethodCalls {
	/**
	 * Nivel de la traza
	 */
	LogLevel level() default LogLevel.DEBUG;
	/**
	 * Cu�ndo se hace log
	 */
	LoggedMethodCallsWhen when() default LoggedMethodCallsWhen.AROUND;
	/**
	 * M�dulo (aparece al principo del mensaje de log)
	 */
	String module() default "";
	/**
	 * Indentado del log (n�mero de tabs que se insertan al principio de la traza)
	 */
	int indent() default 0;
	/**
	 * Indicador de comienzo del m�todo
	 */
	String start() default "[START]";
	/**
	 * Indicador de fin del m�todo
	 */
	String end() default "[END]";
	/**
	 * Formateador de los par�metros
	 */
	Class<? extends LoggedMethodCallsParamsFormatter> paramsFormatter() default LoggedMethodCallsParamsVoidFormatter.class;
}
