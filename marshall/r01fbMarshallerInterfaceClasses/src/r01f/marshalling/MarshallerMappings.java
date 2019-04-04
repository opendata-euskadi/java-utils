package r01f.marshalling;

import java.io.InputStream;


/**
 * Carga la configuraci�n de marshalling, bien desde clases anotadas, bien desde alg�n fichero de configuraci�n
 * y en su caso custodia (guarda y cachea) esta configuraci�n
 */
public interface MarshallerMappings {

/////////////////////////////////////////////////////////////////////////////////////////
//  CARGA DESDE FICHEROS ANOTACIONES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A�ade los mapeos del fichero cuya ruta (que tiene que ser accesible en el classPath)
	 * se pasa como parametro
	 * @param filePath el path al fichero de mapeo
	 * @throws MarshallerException si el fichero de mapeo es incorrecto
	 */
	public abstract void loadFromMappingDefFile(String filesPath);
	/**
	 * A�ade los mapeos desde el stream que se pasa como parametro
	 * @param is el stream con la definici�n del mapeo
	 * @throws MarshallerException si el mapeo es incorrecto o no se puede cargar
	 */
	public abstract void loadFromMappingDef(InputStream is);

/////////////////////////////////////////////////////////////////////////////////////////
//  CARGA DESDE CLASES ANOTADAS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Carga el mapa de clases desde su definici�n en base a anotaciones en las propias clases java
	 * @param annotatedTypes tipo(s) desde los que empezar a buscar anotaciones
	 * 						 NOTA: 	No es necesario indicar TODOS los tipos sino �nicamente el ra�z (o ra�ces si se van a mapear distintos tipos ra�z inconexos)
	 * @throws MarshallerException si el mapeo es incorrecto o no se puede cargar
	 */
	public abstract void loadFromAnnotatedTypes(Class<?>... annotatedTypes);
	/**
	 * Carga el mapa de clases escaneando los paquetes en busca de clases anotadas
	 * @param packages loa paquetes que tienen clases anotadas
	 * @throws MarshallerException si el mapeo es incorrecto o no se puede cargar
	 */
	public abstract void loadFromAnnotatedTypesScanningPackages(Package... packages);
	/**
	 * Carga el mapa de clases a partir de anotaciones que se buscan seg�n las especificaciones
	 * que se pasan
     * La forma habitual de utilizarlo es.
     * <pre class='brush:java'>
     * 		loadFromAnnotatedTypes(MarshallerMappingsSearch.forTypes(MyType.class,MyOtherType.class),
     * 								 MarshallerMappingsSearch.forPackages("com.a.b","com.c.d"));
     * </pre>
	 * @param searchSpecs las especificaciones de b�squeda
	 * @throws MarshallerException si el mapeo es incorrecto o no se puede cargar
	 */
	public abstract void loadFromAnnotatedTypes(Object... searchSpecs);
	/**
	 * Comprueba si entre los mapeos se encuentra un determinado tipo
	 * @param type tipo
	 * @return true si el tipo est� cargado entre los mapeos
	 * @throws MarshallerException si hay alg�n error
	 */
	public abstract boolean containsType(Class<?> type);
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Devuelve informaci�n de depuracion
	 * @return
	 */
	public String debugInfo();

}