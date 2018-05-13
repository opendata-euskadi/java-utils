package r01f.marshalling.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cuando se pasa un tipo de java a xml es posible que un miembro est� definido como un interfaz o una clase abstracta
 * ej:
 * <pre class='brush:java'>
 * 		@XmlRootElement(name="myType")
 * 		public class MyType {
 *			@XmlElement(name="myInterfaceField")
 * 			@Getter @Setter private MyTypeInterface field;
 * 		}
 * </pre>
 * Cuando se instancia un objeto, se conoce el tipo concreto:
 * <pre class='brush:java'>
 * 		@XmlRootElement(name="myTypeImplementingInterface") 
 * 		public class MyTypeImplementingInterface {
 *			...
 * 		}
 * 		public void test() {
 * 			MyType myTypeInstance = new MyType();
 * 			myTypeInstance.setField(new MyTypeImplementingInterface());		<-- aqui se conoce el tipo concreto
 * 		}
 * </pre>
 * Si se pasa de java a xml quedar�a:
 * <pre class='brush:xml'>
 * 		<myType>
 * 			<myInterfaceField>
 * 				....
 * 			</myInterfaceField>
 * 		</myType>
 * </pre>
 * ... pero si ahor se quiere pasar de xml a java <b>�como se sabe que tipo concreto instanciar?</b>
 * 
 * La soluci�n es incorporar un atributo que indique el tipo concreto para pasar de xml a java
 * <pre class='brush:xml'>
 * 		<myType>
 * 			<myInterfaceField type='myTypeImplementingInterface'>
 * 				....
 * 			</myInterfaceField>
 * 		</myType>
 * </pre>
 * Esta anotaci�n por lo tanto se a�ade al campo no instanciable y permite indicar qu� atributo se incluye 
 * en el XML para saber qu� tipo concreto instanciar:
 * <pre class='brush:java'>
 * 		@XmlRootElement(name="myType")
 * 		public class MyType {
 *			@XmlElement(name="myInterfaceField") @XmlTypeDiscriminatorAttribute(name="type")
 * 			@Getter @Setter private MyTypeInterface field;
 * 		}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlTypeDiscriminatorAttribute {
	String name();
}
