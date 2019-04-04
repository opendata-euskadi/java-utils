package r01f.marshalling;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.w3c.dom.Node;

/**
 * XML <-> objects marshaller
 * ----------------------------
 * [Marshaller creation]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
 * ----------------------------------------------------------------------------
 * [OPTION 1]: Use GUICE to inject the marshaller 
 * 			   BEWARE! The object being injeted must be also be created by guice	
 * BEWARE!! There're TWO marshaller types:
 *		<ul>
 *			<li>
 *			SINGLE-USE marshallers -> the mapping cache should NOT be stored for later use
 *			ie: a configuration xml is loaded and marshalled to objects; the XML is no longer needed and marshalled again
 *			 	There's NO need to store the mapping cache:
 *				Use the following annotations to inject a single-usage marshaller
 *					<ul>
 *						<li>{@link SingleUseSimpleMarshaller} to inject a {@link SimpleMarshaller}</li>
 *						<li>{@link SingleUseJaxbMarshaller} to inject a JAXB-based marshaller</li>
 *					</ul>
 *			</li>
 *			<li>
 *			MULTI-USE marshallers -> the marshaller is going to be used once and again to marshall / umarshall xml or beans
 *									 ... so the mapping should be cached			
 *					Use the following annotations to inject a single-usage marshaller:
 *					<ul>
 *						<li>{@link ReusableUseSimpleMarshaller} to inject a {@link SimpleMarshaller}</li>
 *						<li>{@link ReusableUseJaxbMarshaller} to inject a JAXB marshaller</li>
 *					</ul>
 *			</li>
 *		</ul>
 * 
 * 		Injecting a field: 		
 * 		<pre class='brush:java'>
 * 		public class MyMarshallerService {
 * 			@Inject @ReusableSimpleMarshaller private Marshaller _marshaller;
 * 			...
 * 		}
 * 		</pre>
 * 		or a constructor:
 * 		<pre class='brush:java'>
 * 		public class MyMarshallerService {
 * 			private Marshaller _marshaller;
 * 			@Inject
 * 			public MyMarshallerService(@ReusableSimpleMarshaller private Marshaller marshaller) {
 * 				_marshaller = marshaller;
 * 			}
 * 			...
 * 		}
 *		</pre>
 *
 * [OPTION 2]: Use the guice injector to create the marshaller (not recommended)
 * 		<pre class='brush:java'>
 * 			Marshaller marshaller = Guice.createInjector(new MarsallerGuiceModule())
 *										 .getInstance(Key.get(Marshaller.class,SingleUseSimpleMarshaller.class))
 *		</pre>
 *
 * [OPTION 3]: Without using guice
 * 		Two things must be known beforehand:
 * 		<ol>
 * 			<li>How the mappings are going to be created: using annotations / reading a mapping file</li>
 * 			<li>The type of marshaller to create</li>
 * 		</ol>
 * 		Example:
 * 		<pre class='brush:java'>
 * 			// [1] Create mappings reading the annotated types
 * 			MarshallerMappings mappings = new SimpleMarshallerMappings();
 *			mappings.loadFromAnnotatedTypes(MarshallerMappingsSearch.inPackages("r01m.model"));
 *
 *			// [2] Create the marshaller (in the sample, a single-usage marshaller is created)
 *			Marshaller marshaller = new SimpleMarshallerSingleUseImpl(mappings);
 * 		</pre>
 * 		Or better, use builders:
 * 		<pre class='brush:java'>
 *			Marshaller marshaller = new SimpleMarshallerSingleUseImpl(SimpleMarshallerMappings.createFrom(MarshallerMappingsSearch.inPackages("r01m.model")));
 * 		</pre>
 * 		Or easier:
 * 		<pre class='brush:java'>
 * 			Marshaller marshaller = SimpleMarshaller.createForPackages(""r01m.model")
 * 													.getForSingleUse();
 * 		</pre>
 *
 * [Marshaller usage]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
 * ----------------------------------------------------------------------------
 * [STEP 1]: Load the mappings
 * 			 There're two options
 * 				1.- Use the java-types annotations
 * 					<pre class='brush:java'>
 * 						marshaller.addBeans(MyBean.class);
 * 					</pre>
 * 			    2.- (legacy) use a xml mapping file
 * 					<pre class='brush:java'>
 * 						marshaller.addBeans("/mappings/myBeansMappings.xml"
 * 					</pre>
 *
 * [STEP 2]: Marshalling / UnMarshalling
 * 			 Marshall from XML to java objects
 * 				<pre class='brush:java'>
 * 					MyBean myBeanInstance = marshaller.beanFromXml(xml)
 * 				</pre>
 * 			 Marshall from java objects to XML
 * 				<pre class='brush:java'>
 * 					String xml = marshaller.xmlFromBean(myBeanInstance)
 * 				</pre>
 */
public interface Marshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
    public static final class MarshallerMappingsSearch {
    	public static Class<?>[] forTypes(final Class<?>... types) {
    		return types;
    	}
    	public static String[] inPackages(final String... packages) {
    		return packages;
    	}
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  MAPPINGS ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return the mappings
     */
    public abstract MarshallerMappings getMappings();
/////////////////////////////////////////////////////////////////////////////////////////
//  INIT USING XML MAPPINGS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds the mappings at the given xml file containing the mappings 
	 * @param mapFilePath classpath accesible path of the mappings xml file
	 */
	public abstract Marshaller addTypes(String mapFilePath);

	/**
	 * Adds the mappings at the given xml containing the mappings
	 * @param mapIS the xml stream
	 */
	public abstract Marshaller addTypes(InputStream mapsIS);
/////////////////////////////////////////////////////////////////////////////////////////
//  INIT USING ANNOTATED TYPES
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Adds the mappings looking after annotated java types starting with the given java types
     * BEWARE: The java types is crawled from the main types
     * @param annotatedTypes 
     */
    public abstract Marshaller addTypes(Class<?>... annotatedTypes);
    /**
     * Adds the mappings looking after annotated java types starting with the types at the given packages
     * BEWARE: The java types is crawled from the main types
     * @param packages the packages where to look after annotated java types
     */
    public abstract Marshaller addTypes(Package... packages);
    /**
     * Adds the mappings looking after annotated java types 
     * <pre class='brush:java'>
     * 		addBeans(MarshallerMappingsSearch.forTypes(MyType.class,MyOtherType.class),
     * 				 MarshallerMappingsSearch.inPackages("com.a.b","com.c.d"));
     * </pre>
     */
    public Marshaller addTypes(Object... searchSpecs);
/////////////////////////////////////////////////////////////////////////////////////////
//  JAVA INSTANCE CONVERSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets a java type instance from the xml string
	 * @param beanXml the xml string
	 * @return the java object instance
	 */
	public abstract <T> T beanFromXml(String beanXml);
	/**
	 * Gets a java type instance from the xml string
	 * @param beanXml the xml string
	 * @return the java object instance
	 */
	public abstract <T> T beanFromXml(CharSequence beanXml);
	/**
	 * Gets a java type instance from the xml string
	 * @param beanXml the xml string
	 * @return the java object instance
	 */
	public abstract <T> T beanFromXml(byte[] beanXml);
 	/**
	 * Gets a java type instance from the xml string
	 * @param beanXmlIS the xml string
	 * @return the java object instance
	 */
	public abstract <T> T beanFromXml(InputStream beanXmlIS);

	/**
	 * Gets a java type instance from the xml string
	 * @param beanXmlNode the xml string
	 * @return the java object instance
	 */
	public abstract <T> T beanFromXml(Node beanXmlNode);
///////////////////////////////////////////////////////////////////////////////
// 	XML CONVERSION METHODS
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Gets an xml string from the given java instance
	 * @param the java bean instance to be marshalled to xml
	 * @return the xml string
	 */
	public abstract <T> String xmlFromBean(T bean);
	/**
	 * Gets an xml string from the given java instance
	 * @param the java bean instance to be marshalled to xml
	 * @parma charset
	 * @return the xml string
	 */
	public abstract <T> String xmlFromBean(T bean,
										   Charset charset);
}