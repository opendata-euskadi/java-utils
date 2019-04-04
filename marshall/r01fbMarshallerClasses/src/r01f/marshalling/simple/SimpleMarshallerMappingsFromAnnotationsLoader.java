package r01f.marshalling.simple;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.collect.Sets;
import com.googlecode.gentyref.GenericTypeReflector;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.annotations.OidField;
import r01f.exceptions.Throwables;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsI18NBundleBacked;
import r01f.marshalling.MarshallerException;
import r01f.marshalling.annotations.XmlCDATA;
import r01f.marshalling.annotations.XmlDateFormat;
import r01f.marshalling.annotations.XmlInline;
import r01f.marshalling.annotations.XmlReadTransformer;
import r01f.marshalling.annotations.XmlTypeDiscriminatorAttribute;
import r01f.marshalling.annotations.XmlWriteIgnoredIfEquals;
import r01f.marshalling.annotations.XmlWriteTransformer;
import r01f.marshalling.simple.DataTypes.DataType;
import r01f.reflection.Reflection;
import r01f.reflection.ReflectionUtils;
import r01f.reflection.ReflectionUtils.FieldAnnotated;
import r01f.reflection.scanner.ScannerFilter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Composes mappings from the java types annotations
 * 
 * Usage
 * <pre class='brush:java'>
 * 		SimpleMarshallerMappingsFromAnnotationsLoader loader = new SimpleMarshallerMappingsFromAnnotationsLoader(MyType.class,
 * 																												 MyOterType.class,
 * 																												 ...);
 * 		Map<String,BeanMap> mappings = loader.getLoadedBeans();
 * </pre>
 * Otra forma de utilizarlo:
 * <pre class='brush:java'>
 * 		SimpleMarshallerMappingsFromAnnotationsLoader loader = new SimpleMarshallerMappingsFromAnnotationsLoader(MarshallerMappingsSearch.forTypes(MyType.class,
 * 																												 								   MyOterType.class,
 * 																												 								   ...),
 * 																												 MarshallerMappingsSearch.forPackages("com.a.b",
 * 																																					  "c.d.e",
 * 																																					  ...));
 * 		Map<String,BeanMap> mappings = loader.getLoadedBeans();
 * </pre>
 * 
 * Java types annotation guides:
 * [1] The xml tag wrapping a java type {@link XmlRootElement}
 *     <pre class='brush:java'>
 *     		@XmlRootElement(name="myType")
 *     		public class MyType {
 *     			...
 *     		}
 *     </pre>
 * ------------------------------------------------------------------------------------------------------------------
 * [2] A xml node attribue that maps a java type's field
 *     <pre class='brush:java'>
 *     		@XmlRootElement(name="myType")
 *     		public class MyType {
 *     			@XmlAttribute(name="myAttr")
 *     			private String myAttrField;
 *     		}
 *     </pre> 
 * ------------------------------------------------------------------------------------------------------------------
 * [3] A xml node that maps a java type's field
 *     <pre class='brush:java'>
 *     		@XmlRootElement(name="myType")
 *     		public class MyType {
 *     			@XmlElement(name="myEl")
 *     			private String myElementField;
 *     		}
 *     </pre>
 * ------------------------------------------------------------------------------------------------------------------
 * [4] XML CDATA node
 *     <pre class='brush:java'>
 *     		@XmlRootElement(name="myType")
 *     		public class MyType {
 *     			@XmlElement(name="myCDATAEl") @XmlCDATA
 *     			private String myElementField;
 *     		}
 *     </pre>
 * ------------------------------------------------------------------------------------------------------------------
 * [6] Ignore a java type's field when serializing xml<->java
 *     <pre class='brush:java'>
 *     		@XmlRootElement(name="myType")
 *     		public class MyType {
 *     			@XmlTransient
 *     			private int notSerializedField;
 *     			@XmlElement(name="myCDATAEl") @XmlCDATA
 *     			private String myElementField;
 *     		}
 *     </pre>  
 * ------------------------------------------------------------------------------------------------------------------
 * [7] Immutable types are detected and the correct constructor is usedSe detectan los tipos iImmutables y se invoca al constructor correcto
 * 	   In order to do this constructor invocation, ALL constructor arguments MUST be available at object creation time
 * 	   so every final fields MUST be mapped as ATTRIBUTES
 *     Example:
 *     <pre class='brush:xml'>
 *     		<myType myImmutableAttr='attrValue'/>
 *     </pre>
 *     Can be :
 *     <pre class='brush:java'>
 *     		@XmlRootElement(name="myType")
 *     		public class MyType {
 *     			@XmlAttribute(name="myImmutableAttr")
 *     			private MyImmutableType myElementImmutableField;
 *     		}
 *     		@RequiredArgsConstructor
 *     		public class MyImmutableType {
 *     			@XmlValue
 *     			private final String _value;
 *     		}
 *     </pre>
 *     on the other hand:
 *     <pre class='brush:xml'>
 *     		<myType>
 *     			<myImmutableEl finalField='finalFieldValue'>
 *     				<nonFinalField>elValue<nonFinalField>
 *     			</myImmutableEl>
 *     		<myType>
 *     </pre>
 *     can be mapped as:
 *     <pre class='brush:java'>
 *     		@XmlRootElement(name="myType")
 *     		public class MyType {
 *     			@XmlElement(name="myImmutableEl")
 *     			private MyImmutableType myElementImmutableField;
 *     		}
 *     		@RequiredArgsConstructor
 *     		public class MyImmutableType {
 *     			@XmlAttribute(name="finalField")
 *     			private final String _finalField;
 *     			@XmlElement(name="nonFinalField")
 *     			private final String _nonFinalField;
 *     		}
 *     </pre> 	
 * ------------------------------------------------------------------------------------------------------------------
 * [8] Collections (maps & lists)
 * 	   The tag name of every collection element comes from:
 * 			a) The @XmlElement annotation of the collection field
 * 			b) The @XmlRootElement of the collection elements type
 * 
 * 	   There're two cases to wrap a collection items
 * 	   CASE 1: The items are NOT wrapped inside a tag
 * 		<pre class='brush:xml'>
 * 				<myType>
 * 					<myChildType attr='attr1'/>
 * 					<myChildType attr='attr2'/>
 * 				<myType>
 * 		</pre>
 * 		<pre class='brush:java'>
 *     		public class MyType {
 *     			private Collection<MyChildType> myColField;
 *     		}
 *     		@XmlRoot(name="myChildType")
 *     		public class MyChildType {
 *     			@XmlAttribute(name="attr")
 *     			private String _attrField;
 *     		}
 * 		</pre> 
 * 		CASE 2: The items are wrapped inside a tag
 * 		<pre class='brush:xml'>
 * 				<myType>
 * 					<myChilds>
 * 						<myChildType attr='attr1'/>
 * 						<myChildType attr='attr2'/>
 * 					</myChilds>
 * 				<myType>
 * 		</pre>
 * 		<pre class='brush:java'>
 *     		public class MyType {
 *     			@XmlElelmentWrapper(name="myChilds")
 *     			private Collection<MyChildType> myColField;
 *     		}
 *     		@XmlRoot(name="myChildType")
 *     		public class MyChildType {
 *     			@XmlAttribute(name="attr")
 *     			private String _attrField;
 *     		}
 * 		</pre>
 *  ------------------------------------------------------------------------------------------------------------------
 * [9] Maps
 *     In order to do a Map marshalling the oid type must be known
 *     The @OidField annotaion is used to set which field is going to be the map item's key
 * 		<pre class='brush:xml'>
 * 				<myType>
 * 					<myChilds>
 * 						<myChildType attr='attr1'>Value1</myChildType>
 * 						<myChildType attr='attr2'>Value2</myChildType>
 * 					</myChilds>
 * 				<myType>
 * 		</pre>
 * 		<pre class='brush:java'>
 *     		public class MyType {
 *     			@XmlElelmentWrapper(name="myChilds")
 *     			private Map<String,MyChildType> myMapField;
 *     		}
 *     		@XmlRoot(name="myChildType")
 *     		public class MyChildType {
 *     			@XmlAttribute(name="attr") @OidField
 *     			private String _attrField;
 *     			@XmlValue
 *     			private String _valField;
 *     		}
 * 		</pre>
 *  ------------------------------------------------------------------------------------------------------------------
 *  [11] Marshall / unmarshall collections (Maps, Lists or Sets as the main object)
 *  	 Maps / Collection / Sets can be marshalled / unmarshalled without problem.
 *   	 The only thing to have in mind is that the xml node wrapping the map / collection / set elements
 *   	 is a fixed one:	 
 *  		- <map>...</map>
 *  		- <list>...</list>
 *  		- <set>...</set>
 *  
 *  	 Other alternative if for example a Map must be marshalled / unmarshalled:
 *  	 <pre class='brush:xml'>
 *  		<myMap>
 *  			<MyType oid='1'>uno</MyType>
 *  			<MyType oid='2'>dos</MyType>
 *  		</myMap>
 *  	 </pre>
 *  	 An annotated type extending or implementing Map should be created
 *  	 <pre class='brush:java'>
 *  		// Extending...
 *			@XmlRootElement(name="myMap")
 *			private static class MyMap
 *		           extends HashMap<String,MyType> {
 *			}
 *			// Delegating...
 *			@XmlRootElement(name="myMapDelegated")
 *			private static class MyMapDelegated
 *		     		  implements Map<String,MyType> {
 *				@Delegate @XmlTransient
 *				private final Map<String,MyType> _mapDelegate = new HashMap<String,MyType>();
 *			}
 *  	 </pre>
 *  
 *  ------------------------------------------------------------------------------------------------------------------
 *  [11] Custom Marshalling
 *  	 Use @XmlReadTransformer and @XmlWriteTransformer and implement two aux types:
 *  	 <pre>
 *  		- a type implementing {@link r01f.marshalling.simple.SimpleMarshallerCustomXmlTransformers.XmlReadTransformer}
 *  	      that transforms XML to Java 
 *  		- a type implementing {@link r01f.marshalling.simple.SimpleMarshallerCustomXmlTransformers.XmlWriteTransformer}
 *  		  that transforms Java a XML
 *  	 </pre>
 * 		 <pre class='brush:java'>
 *     		public class MyType {
 *     			private MyCustomTransformedType myCustomTransformedField;
 *     		}
 *     		@XmlReadTransformer(using=MyCustomReadTransformer.class) @XmlWriteTransformer(using=MyCustomWriteTransformer.class)
 *     		public class MyCustomTransformedType {
 *     			...
 *     		}
 * 		 </pre>
 * ------------------------------------------------------------------------------------------------------------------
 * [12] Fields defined as abstract java types or java interfaces
 * 		Ie:
 * 		<pre class='brush:java'>
 * 			// Interface
 * 			public interface MyInterface {
 * 			}
 * 			// Interface implementation
 * 			@XmlRootElement(name="typeA")
 * 			public class MyInterfaceTypeA implements MyInterface {
 * 			}
 * 			// Interface implementation
 * 			@XmlRootElement(name="typeB")
 * 			public class MyInterfaceTypeA implements MyInterface {
 * 			}
 * 		</pre>
 * 		Any of MyInterfaceTypeA or MyInterfaceTypeB can arise, how to know which instance should be created?
 * 		<pre class='brush:java'>
 * 			@XmlRootElement(name="mytType")
 * 			public class MyType {
 * 				@Getter @Setter private MyInterface myField;
 * 			}
 * 		</pre>
 * 		There're two posibilities:
 * 		[ 1 ]: Each java type has a diferent node name 
 * 				<pre class='brush:xml'>
 * 					<myType>
 * 						<typeA>
 * 							...
 *						</typeA>
 * 					</myType>
 * 				</pre>
 * 				or
 * 				<pre class='brush:xml'>
 * 					<myType>
 * 						<typeB>
 * 							...
 *						</typeB>
 * 					</myType>
 * 				</pre>
 * 				In this situation just DO NOT set the node name explicitly at @XmlElement annotation
 * 				<pre class='brush:java'>
 * 					@XmlRootElement(name="mytType")
 * 					public class MyType {
 * 						@XmlElement		// <-- NO se indica el atributo NAME
 * 						@Getter @Setter private MyInterface myField;
 * 					}
 *				</pre>
 *		[ 2 ]: Both types are wrapped inside the same xml tag:
 * 			   (the @XmlElement annotation explicitly sets the xml tag name)
 * 				<pre class='brush:java'>
 * 					@XmlRootElement(name="mytType")
 * 					public class MyType {
 * 						@XmlElement(name="myField")		// <-- SI se indica el atributo NAME
 * 						@Getter @Setter private MyInterface myField;
 * 					}
 *				</pre>
 *			   so in both cases the xml will be:
 * 			    <pre class='brush:xml'>
 * 					<myType>
 * 						<myField>		<!-- both java types will be wrapped inside the same tag -->
 * 							...
 *						</myField>
 * 					</myType>
 * 				</pre>
 * 				The marshaller needs some "clue" to know which java type really comes wrapped inside the tag
 * 			    ... the @XmlTypeDiscriminatorAttribute does the trick
 * 				<pre class='brush:java'>
 * 					@XmlRootElement(name="mytType")
 * 					public class MyType {
 * 						@XmlElement(name="myField") @XmlTypeDiscriminatorAttribute(name="type")		
 * 						@Getter @Setter private MyInterface myField;
 * 					}
 *				</pre>
 *				So the generated xml will be:
  * 			<pre class='brush:xml'>
 * 					<myType>
 * 						<myField type='typeA'>
 * 							...
 *						</myField>
 * 					</myType>
 * 				</pre>
 * 				or
 * 				<pre class='brush:xml'>
 * 					<myType>
 * 						<myField type='typeA'>
 * 							...
 *						</myField>
 * 					</myType>
 * 				</pre>
 * 				Es importante tener en cuenta que el valor del atributo discriminador (atributo type en el ejemplo) es el valor de la notacion @XmlRootElement
 * 				de MyInterfaceTypeA y MyInterfaceTypeB
 * ------------------------------------------------------------------------------------------------------------------
 * [13] Field values to be ignored when marshalling
 * 	 	In many situations, specially with booleans or number, the generated xml should NOT contain
 * 		an element or attribute if the field's value is a certain one (ie false in a boolean field)
 * 		ie:
 * 		<pre class='brush:java'>
 * 			@XmlRootElement(name="myType")
 * 			@Accessors(prefix="_")
 * 			public class MyType {
 * 				@XmlAttribute(name="myField")  
 * 				@Getter @Setter private boolean _myField;
 * 			}
 * 		</pre> 
 * 		the result will be:
 * 		<pre class='brush:xml'>
 * 			<myType myField='false'/>
 * 		</pre>
 * 		But it's possible that the myField attribute is not serialized if the field's value is false (boolean's default value)
 * 		Just annotate  _myField with @XmlWriteIgnoreIfEquals(value="false")
 * 		<pre class='brush:java'>
 * 			@XmlRootElement(name="myType")
 * 			@Accessors(prefix="_")
 * 			public class MyType {
 * 				@XmlAttribute(name="myField")  @XmlWriteIgnoreIfEquals(value="false")
 * 				@Getter @Setter private boolean _myField;
 * 			}
 * 		</pre> 
 */
@Accessors(prefix="_")
@Slf4j
public class SimpleMarshallerMappingsFromAnnotationsLoader {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////    
    @Getter private Map<String,BeanMap> _loadedBeans = new HashMap<String,BeanMap>();   
    		private Set<Class<?>> _types;		// types to scan
    		private Set<String> _packages;		// packages to scan 
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor from a type array or a package array or both
     * Usage:
     * <pre class='brush:java'>
     * 		SimpleMarshallerMappingsFromAnnotationsLoader(MarshallerMappingsSearch.forTypes(MyType.class,MyOtherType.class),
     * 													  MarshallerMappingsSearch.forPackages("com.a.b","com.c.d"));
     * </pre>
     * @param searchSpecs
     */
    public SimpleMarshallerMappingsFromAnnotationsLoader(Object... searchSpecs) {
    	if (CollectionUtils.isNullOrEmpty(searchSpecs)) return;
    	
    	// [0] Cargar los tipos y paquetes
    	for (Object spec : searchSpecs) {
    		if (CollectionUtils.isArray(spec.getClass())) { 
    		    if ( !(spec.getClass().getComponentType() == Class.class || spec.getClass().getComponentType() == String.class) ) throw new IllegalArgumentException("The SimpleMarshallerMappingsFromAnnotationsLoader constructor only accepts a types array or a packages array that are used to find annotated types");
    		    
    		    if (spec.getClass().getComponentType() == Class.class) {
    		    	if (_types == null) _types = Sets.newLinkedHashSet();
    		    	for (Class<?> type : (Class<?>[])spec) _types.add(type);
    		    	
    		    } else if (spec.getClass().getComponentType() == String.class) {
    		    	if (_packages == null) _packages = Sets.newLinkedHashSet();
    		    	for (String pckg : (String[])spec) _packages.add(pckg);
    		    }
    		    
    		} else {
    			if ( !(spec.getClass() == Class.class || spec.getClass() == String.class)) throw new IllegalArgumentException("The SimpleMarshallerMappingsFromAnnotationsLoader constructor only accepts a types or a packages that are used to find annotated types");
    			
	    		if (spec.getClass() == Class.class) {
	    			if (_types == null) _types = Sets.newLinkedHashSet();
	    			_types.add((Class<?>)spec);
	    			
	    		} else if (spec.getClass() == String.class) {
	    			if (_packages == null) _packages = Sets.newLinkedHashSet();
	    			_packages.add((String)spec);
	    		}
    		}
    	}
    	// [1] Process packages & types
    	_processTypesAndPackages();
    }
    /**
     * Type-based constructor
     * @param types whose annotations should be processed
     */
    public SimpleMarshallerMappingsFromAnnotationsLoader(final Class<?>... types) throws MarshallerException {
    	if (CollectionUtils.isNullOrEmpty(types)) return;
    
    	// [0] Load the types
    	_types = Sets.newLinkedHashSetWithExpectedSize(types.length);
    	for (Class<?> t : types) _types.add(t);
    	
    	// [1] Load packages and types
    	_processTypesAndPackages();
    }
    /**
     * Packages based constructor
     * @param packages paquetes cuyos tipos hay que procesar
     */
    public SimpleMarshallerMappingsFromAnnotationsLoader(final Package... packages) throws MarshallerException {
    	if (CollectionUtils.isNullOrEmpty(packages)) return;
    	
    	// [0] Load packages
    	_packages = Sets.newLinkedHashSetWithExpectedSize(packages.length);
    	for (Package p : packages) {
    		_packages.add(p.getName());
    	}
    	// [1] Process packages and types
    	_processTypesAndPackages();
    }
    private void _processTypesAndPackages() {
    	// [1] Process types
    	_processTypes(_types);
    	
    	// [2] Process packages
    	_processPackages(_packages);
    	
    	// [3] Finish
		// Conects the {@link BeanMap} object at collection {@link FieldMap}s
		// This must be done AFTER every bean is loaded
    	SimpleMarshallerMappings.connectBeanMappings(_loadedBeans);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  TYPE INTROSPECTION 
/////////////////////////////////////////////////////////////////////////////////////////
    private void _processTypes(final Set<Class<?>> types) {
    	if (CollectionUtils.hasData(types)) {
	    	for (Class<?> type : types) {
	    		if (type.getAnnotation(XmlRootElement.class) == null) continue;	// sometimes if the type is a subtype of another annotated with @XmlRootElement is detected as annotated
	    		
	    		// Abstract types MUST not be annotated with @XmlRootElement
	    		if (Modifier.isAbstract(type.getModifiers())) throw new MarshallerException(Throwables.message("Abstract types MUST NOT be annotated with @{}: {}",
	    																									   XmlRootElement.class,type));
	    		_beanMapFromBeanAnnotations(type);
	    	}
    	}
    }
    private void _processPackages(final Set<String> packages) {
    	if (CollectionUtils.hasData(_packages)) {
    		// [1] - Find all types annotated with @XmlRootElement 
    		Set<Class<?>> allPckgsTypes = Sets.newHashSet(); 
    		
			List<URL> urls = new ArrayList<URL>();
			//urls.addAll(ClasspathHelper.forPackage("javax.xml.bind.annotation"));
    		for (String p : packages) {
    			//Reflections typeScanner = new Reflections(p);
				urls.addAll(ClasspathHelper.forPackage(p));	// see https://code.google.com/p/reflections/issues/detail?id=53
				log.debug("Scanning package {} for @XmlRootElement annotated types",p);
    		}
			Reflections typeScanner = new Reflections(new ConfigurationBuilder()
																.setUrls(urls)
																.filterInputsBy(ScannerFilter.DEFAULT_TYPE_FILTER)
																.setScanners(new SubTypesScanner(true),
																			 new TypeAnnotationsScanner()));  
			Set<Class<?>> pckgTypes = typeScanner.getTypesAnnotatedWith(XmlRootElement.class);
			if (CollectionUtils.hasData(pckgTypes)) {
					for (Class<?> type : pckgTypes) log.trace(">Type {}",type);
				allPckgsTypes.addAll(pckgTypes);
			} else {
				log.debug("NO types annotated with @XmlRootElement");
			}
    		// [2] - Process...
    		_processTypes(allPckgsTypes);
    	}
    }
	/**
	 * Gets a bean's mapping
	 * @param bean el bean
	 * @return
	 */
	private void _beanMapFromBeanAnnotations(final Class<?> type) throws MarshallerException {
		log.trace("... processing annotations from type {}",type.getName());
		BeanMap beanMap = null;
		
		// CASE 1: (NOT a usual case) Custom xml<->java marshalled beans
		beanMap = _beanMapFromCustomXmlTransformers(type);
		// CASO 2: (USUAL CASE)  annotation based xml<->java marshalling
		if (beanMap == null) beanMap = new BeanMap(_typeNormalizedDesc(type));	//type.getName());
	
		// Tye definition: get the xml tag name and the access way
		XmlRootElement rootAnnot = ReflectionUtils.typeAnnotation(type,
																  XmlRootElement.class);
		String beanNodeName = _nodeNameFromAnnotation((rootAnnot != null ? rootAnnot.name() : null),				// nombre indicado en la anotacion o null
												      ReflectionUtils.classNameFromClassNameIncludingPackage(type.getName()));		// nombre por defecto = nombre de la clase
		beanMap.getXmlMap().setNodeName(beanNodeName);
		
		// CASO 1::::::::::::::::: en los beans procesados via CustomTransformer NO hay que procesar los fields
		if (beanMap.isCustomXmlTransformed()) return;
		
		// CASO 2::::::::::::::::: procesar los fields
		// Poner el nuevo bean en la salida...
		_loadedBeans.put(type.getName(),beanMap);
		
		XmlAccessorType accessorType = ReflectionUtils.typeAnnotation(type,XmlAccessorType.class);
		if (accessorType != null) beanMap.setUseAccessors(accessorType.value() == XmlAccessType.PROPERTY);
		
		// [Elemento XMLValue]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
		FieldAnnotated<XmlValue>[] xmlValueFields = ReflectionUtils.fieldsAnnotated(type,XmlValue.class);
		if (xmlValueFields != null) {
			for (FieldAnnotated<XmlValue> fAnnot : xmlValueFields) {
				// Asegurarse de que NO es transient
				if (fAnnot.getField().getAnnotation(XmlTransient.class) != null) throw new MarshallerException("@XmlTransient annotation cannot be used with @XmlValue. check field " + fAnnot.getField().getName() + " at " + type.getName());
				
				// Asegurarse de que NO esta anotado con XmlAttribute, XmlElement o XmlElementWrapper
				if (fAnnot.getField().getAnnotation(XmlAttribute.class) != null
				 || fAnnot.getField().getAnnotation(XmlElement.class) != null
				 || fAnnot.getField().getAnnotation(XmlElementWrapper.class) != null) throw new MarshallerException("@XmlValue annotation cannot be used with @XmlAttribute, @XmlElement or @XmlElementWrapper. Check field named " + fAnnot.getField().getName() + " at " + type.getName());
				
				log.trace("\t\t-field {}",fAnnot.getField().getName());
				
				// Crear el FieldMap
				FieldMap fieldMap = _fieldMapFromField(type,fAnnot.getField(),
													   false);		// not a xml attribute
				fieldMap.getXmlMap().setAttribute(false);
				fieldMap.setFinal(Modifier.isFinal(fAnnot.getField().getModifiers()));		// es final?
				
				// El nombre del nodo coincide con el nombre del nodo asignado a la clase (anotacion @XmlRootElement)
				String fieldNodeName = beanNodeName;
				fieldMap.getXmlMap().setNodeName(fieldNodeName);
				
				// Asegurarse de que ningun otro miembro esta anotado con XmlValue
				if (beanMap.getFieldFromXmlNode(fieldNodeName,false) != null) throw new MarshallerException("Field " + fAnnot.getField().getName() + " at " + beanMap.getTypeName() + " is annotated with @XmlValue BUT there's another @XmlValue annotated field with the same name!!");
				
//				if (fieldMap.getDataType().isCollection() || fieldMap.getDataType().isMap()) {
//					fieldMap.getXmlMap().setExplicitNodeName(false);
//					fieldMap.getXmlMap().setExplicitColElsNodeName(false);
//				}
				
				// Si el miembro es de un tipo complejo o una coleccion, hay que hacer una llamada recursiva para mapearlo
				_recursiveMapBean(fieldMap.getDataType());
				
				// añadirlo al bean padre
				beanMap.addField(fieldMap);
			}
		}
		
		
		// [Atributos XML]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
		FieldAnnotated<XmlAttribute>[] xmlAttrFields = ReflectionUtils.fieldsAnnotated(type,XmlAttribute.class);
		if (xmlAttrFields != null) {
			for (FieldAnnotated<XmlAttribute> fAnnot : xmlAttrFields) {
				// Asegurarse de que NO es transient
				if (fAnnot.getField().getAnnotation(XmlTransient.class) != null) throw new MarshallerException("@XmlTransient cannot be used with @XmlAttribute. Check " + fAnnot.getField().getName() + " field at " + type.getName());
				
				log.trace("\t\t-field {}",fAnnot.getField().getName());
				
				// Crear el fieldMap
				FieldMap fieldMap = _fieldMapFromField(type,fAnnot.getField(),
													   true);						// a xml attribute
				fieldMap.getXmlMap().setAttribute(true);							// es atributo
				fieldMap.setFinal(Modifier.isFinal(fAnnot.getField().getModifiers()));		// es final?
				
				// Obtener el nombre del nodo
				XmlAttribute xmlAttrAnnot = fAnnot.getAnnotation();
				String fieldNodeName = _nodeNameFromAnnotation((xmlAttrAnnot != null ? xmlAttrAnnot.name() : null),		// nombre indicado en la anotacion o null
								  				  	   		   fieldMap.getName());										// nombre por defecto = nombre del miembro
				fieldMap.getXmlMap().setNodeName(fieldNodeName);
				if (xmlAttrAnnot != null) fieldMap.getXmlMap().setExplicitNodeName(!_isXmlAttributeAnnotationDefaultValue(xmlAttrAnnot));	// true si el nombre del nodo xml se ha dado explicitamente con una anotacion, false si se ha calculado como el nombre del miembro
				
				// a�adirlo al bean padre
				beanMap.addField(fieldMap);
			
				// Si el tipo de dato del atributo es un objeto, ver si se puede mapear con un XmlCustomTransformer
				if (fieldMap.getDataType().isObject()) _beanMapFromCustomXmlTransformers(fieldMap.getDataType().getType());
				
				// Si el miembro es de un tipo complejo o una coleccion, hay que hacer una llamada recursiva para mapearlo
				_recursiveMapBean(fieldMap.getDataType());
			}
		}
		
		
		// [Elementos XML]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]
		// - CASO 1: Anotados con @XmlElement
		//				- Objetos en los que se especifica el nombre del nodo que "envuelve" el objeto
		//				- Colecciones / Mapas en las que se especifica el nombre del nodo que "envuelve" los elementos
		//				  NOTA: En este caso pueden o no estar tambi�n anotados con @XmlElementWrapper asi que hay que tenerlo en cuenta para el CASO 2)
		FieldAnnotated<XmlElement>[] xmlElFields = ReflectionUtils.fieldsAnnotated(type,XmlElement.class);
		if (xmlElFields != null) {
			for (FieldAnnotated<XmlElement> fAnnot : xmlElFields) {
				// Asegurarse de que NO es transient
				if (fAnnot.getField().getAnnotation(XmlTransient.class) != null) throw new MarshallerException("@XmlTransient cannot be used with @XmlElement. Check field named " + fAnnot.getField().getName() + " at " + type.getName());
				
				log.trace("\t\t-field {}",fAnnot.getField().getName());
				
				// Crear el fieldMap
				FieldMap fieldMap = _fieldMapFromField(type,fAnnot.getField(),
													   false);		// not a xml attribute
				fieldMap.getXmlMap().setAttribute(false);
				
				// Nombre del nodo que engloba el miembro y sus elementos
				//	- si se trata de un objeto "simple" el nombre del tag se indica en la anotacion @XmlElement
				//	- si se trata de una coleccion / mapa:
				//		* el nombre del tag que "engloba" cada objeto de la coleccion / mapa se toma de la anotacion @XmlElement
				//		* el nombre del tag que "engloba" la coleccion / mapa puede ser:
				//			a.- Lo que se especifique en la anotacion @XmlElementWrapper
				//			b.- El nombre del tag que "engloba" el objeto que contiene el miembro coleccion / mapa indicado en su
				//				anotacion @XmlRootElement
				XmlElement xmlElAnnot = fAnnot.getAnnotation();
				if (fieldMap.getDataType().isCollection() 
				 || fieldMap.getDataType().isMap()) {
					
					XmlElementWrapper xmlElWrapAnnot = fAnnot.getField().getAnnotation(XmlElementWrapper.class);
					// Colecci�n / Mapa
					String wrapperNodeName = xmlElWrapAnnot != null ? xmlElWrapAnnot.name() : beanNodeName;					// Por defecto el nombre del nodo que engloba al bean
					String elementsNodeName = _nodeNameFromAnnotation((xmlElAnnot != null ? xmlElAnnot.name() : null),
																	  fieldMap.getName());
					fieldMap.getXmlMap().setNodeName(wrapperNodeName);
					if (xmlElWrapAnnot != null) fieldMap.getXmlMap().setExplicitNodeName(!_isXmlElementWrapperAnnotationDefaultValue(xmlElWrapAnnot));	// true si el nombre del nodo xml se ha dado explicitamente con una anotacion, false si se ha calculado como el nombre del miembro
					fieldMap.getXmlMap().setColElsNodeName(elementsNodeName);
					if (xmlElAnnot != null) fieldMap.getXmlMap().setExplicitColElsNodeName(!_isXmlElementAnnotationDefaultValue(xmlElAnnot));					// true si el nombre del nodo xml se ha dado explicitamente con una anotacion, false si se ha calculado como el nombre del miembro
					
					// Si se fija el nombre de los elementos de la coleccion y estos NO son instanciables, hay que incluir un atributo
					// discriminador del tipo
					if (fieldMap.getXmlMap().isExplicitColElsNodeName() && fieldMap.getXmlMap().getDiscriminatorWhenNotInstanciable() == null 
					&& ( (fieldMap.getDataType().isMap() && !fieldMap.getDataType().asMap().getValueElementsDataType().isInstanciable()) 
					  || (fieldMap.getDataType().isCollection() && !fieldMap.getDataType().asCollection().getValueElementsDataType().isInstanciable()) )) {
						throw new MarshallerException("Field " + fAnnot.getField().getName() + " at " + type.getName() + " is a Collection/Map parameterized with an interface type; " +
													  "If @XmlElement with 'name' attribute is used to set a wrapper xml node, the xml node MUST have an attribute that makes possible to guess the correct interface impl: use @XmlTypeDiscriminatorAttribute; " +
													  "another option is not using 'name' at @XmlElement annotation so each wrapper xml node's tag will be the one of the concrete interface impl");
					}
					
					// Si el nodo que envuelve al elemento coincide con el nodo que envuelve al tipo, lanzar un error ya que se deberia anotar con @XmlValue
					if (xmlElWrapAnnot != null && wrapperNodeName.equals(beanNodeName)) throw new MarshallerException("@XmlElementWrapper annotation sets which is the node wrapping all the collection. " + fAnnot.getField().getName() + " at " + type.getName() + " has the same name as the node wrapping all the field's container type " + type.getName() + "; use @XmlValue if the Collection elements should be direct descendants of the xml node wrapping " + type.getName());
					
				} else {
					// Simple object 
					String fieldNodeName = _nodeNameFromAnnotation((xmlElAnnot != null ? xmlElAnnot.name() : null),		// nombre indicado en la anotacion o null
											 					   fieldMap.getName());									// nombre por defecto = nombre del miembro
					// TODO this IF is NOT totally tested! and it's related with a change at XMLFromObjsBuilder (line 427)
					// If the XmlElement annotation do not set an explicit node name AND the field data type is NOT instanciable AND NO type discriminator was set, 
					// set the node name null 
//					if (!fieldMap.getXmlMap().isExplicitNodeName() && !fieldMap.getDataType().isInstanciable() && fieldMap.getXmlMap().getDiscriminatorWhenNotInstanciable() == null) {
//						fieldNodeName = null;
//					}
					fieldMap.getXmlMap().setNodeName(fieldNodeName);
					if (xmlElAnnot != null) fieldMap.getXmlMap().setExplicitNodeName(!_isXmlElementAnnotationDefaultValue(xmlElAnnot));		// true si el nombre del nodo xml se ha dado explicitamente con una anotacion, false si se ha calculado como el nombre del miembro
				}
				
				// a�adirlo al bean padre
				beanMap.addField(fieldMap);
				
				// Si el miembro es de un tipo complejo o una coleccion, hay que hacer una llamada recursiva para mapearlo
				_recursiveMapBean(fieldMap.getDataType());
			}
		}
		// - CASO 2: Colecciones / mapas anotadas con @XmlElementWrapper donde NO se especifica el nombre del nodo que "envuelve" a cada uno 
		//		     de los elementos, es decir, NO se indica la anotacion @XmlElement y por lo tanto NO han sido procesados en el caso 1
		//			 NOTA: Hay que eliminar aquellos miembros procesados en el caso 1
		FieldAnnotated<XmlElementWrapper>[] xmlWrappedFields = ReflectionUtils.fieldsAnnotated(type,XmlElementWrapper.class);
		if (xmlWrappedFields != null) {
			for (FieldAnnotated<XmlElementWrapper> fAnnot : xmlWrappedFields) {
				// Asegurarse de que NO es transient
				if (fAnnot.getField().getAnnotation(XmlTransient.class) != null) throw new MarshallerException("@XmlTransient cannot be used with @XmlElementWrapper. Check field " + fAnnot.getField().getName() + " at " + type.getName());  
				
				// puede ser que el field YA est� procesado en el CASO 1
				String fieldName = _fieldName(fAnnot.getField());
				if (beanMap.getField(fieldName) != null) continue;
				
				log.trace("\t\t-field {}",fAnnot.getField().getName());
				
				// Crear el fieldMap
				FieldMap fieldMap = _fieldMapFromField(type,fAnnot.getField(),
													   false);		// not a xml attribute
				fieldMap.getXmlMap().setAttribute(false);
				
				// Asegurarse de que es un mapa / coleccion
				if (! (fieldMap.getDataType().isCollection() || fieldMap.getDataType().isMap()) ) throw new MarshallerException(fAnnot.getField().getName() + " at " + type.getName() + " was annotated with @XmlElementWrapper BUT the field is NOT a Collection/Map; @XmlElementWrapper can ONLY be used at Map/Set/Collection fields...");
				
				// Nombre del nodo que envuelve a los elementos
				// NOTA: 	el nombre del nodo que envuelve a cada elemento NO se conoce ya que NO se indica la anotacion @XmlElement y se 
				//  		tomara el nombre indicado con la anotacion @XmlRootElement de la clase del objeto de la coleccion / mapa
				XmlElementWrapper xmlElWrapAnnot = fAnnot.getAnnotation();
				String wrapperNodeName = _nodeNameFromAnnotation((xmlElWrapAnnot != null ? xmlElWrapAnnot.name() : null),
																 fieldMap.getName()); 
				fieldMap.getXmlMap().setNodeName(wrapperNodeName);
				if (xmlElWrapAnnot != null) fieldMap.getXmlMap().setExplicitNodeName(!_isXmlElementWrapperAnnotationDefaultValue(xmlElWrapAnnot));		// true si el nombre del nodo xml se ha dado explicitamente con una anotacion, false si se ha calculado como el nombre del miembro
				fieldMap.getXmlMap().setExplicitColElsNodeName(false);
				
				// Si el nodo que envuelve a los elementos del mapa coincide con el nodo que envuelve al tipo, lanzar un error ya que se deberia anotar con @XmlValue
				if (wrapperNodeName.equals(beanNodeName)) throw new MarshallerException("@XmlElementWrapper sets the xml node wrapping the collection / map typed field " + fAnnot.getField().getName() + " at " + type.getName() + " BUT this xml wrapper node name is the SAME as the node name wrapping all the type " + type.getName() + "; Use @XmlValue annotation if the collection/map elements should be direct descendents of the xml node wrapping " + type.getName());
				
				// añadirlo al bean padre
				beanMap.addField(fieldMap);
				
				// Si el miembro es de un tipo complejo o una coleccion, hay que hacer una llamada recursiva para mapearlo
				_recursiveMapBean(fieldMap.getDataType());
			}
		}
	}
	/**
	 * Llamada recursiva si se trata de:
	 * 		- Un objeto complejo (no un int, long, string, etc)
	 * 		- Una coleccion de objetos complejos
	 * @param dataType el tipo de dato del miembro
	 * @throws MarshallerException si no se puede mapear
	 */
	private void _recursiveMapBean(final DataType dataType) throws MarshallerException {
		Set<DataType> childDataTypes = Sets.newHashSet();
		
		// Obtener el objeto DataType hijo
		if (dataType.isObject()) {
			if (dataType.isInstanciable()) childDataTypes.add(dataType);
			
		} else if (dataType.isCollection()) {
			if (dataType.asCollection().getValueElementsDataType().isObject()) childDataTypes.add(dataType.asCollection().getValueElementsDataType());
			
		} else if (dataType.isMap()) {
			if (dataType.asMap().getKeyElementsDataType().isObject()) childDataTypes.add(dataType.asMap().getKeyElementsDataType());
			if (dataType.asMap().getValueElementsDataType().isObject()) childDataTypes.add(dataType.asMap().getValueElementsDataType());
		}
		
		// Hacer una llamada recursiva para obtener su definicion (solo si es un tipo conocido y no se ha cargado ya)
		if (CollectionUtils.hasData(childDataTypes)) {
			for (DataType dt : childDataTypes) {
				if (dt != null && dt.asObject().isKnownType()) {
					String childTypeName = dt.getName();
					if (!_loadedBeans.containsKey(childTypeName)) _beanMapFromBeanAnnotations(ReflectionUtils.typeFromClassName(childTypeName));
				}
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PRIVADOS ESTATICOS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Devuelve un esqueleto de objeto {@link FieldMap} a partir de un {@link Field} de un {@link java.lang.reflect.Type}
	 * Este esqueleto unicamente incluye:
	 * 		- el nombre del field
	 * 		- el tipo de dato
	 * 		- si es oid o no 
	 * @param type el tipo
	 * @param field el field
	 * @return
	 */
	private static FieldMap _fieldMapFromField(final Class<?> type,final Field field,
											   final boolean isAttribute) {
		// Obtener la anotacion @OidField (si la tiene)
		OidField oidFieldAnnot = field.getAnnotation(OidField.class);
		boolean isOid = oidFieldAnnot != null ? true : false;
		
		// Crear el fieldMap
		DataType dataType = _dataTypeFromType(type,field);
		FieldMap fieldMap = new FieldMap(_fieldName(field),	// Nombre del miembro 
										 dataType,			// tipo de datos
										 isOid);			// oid true / false
		
		// Si el tipo de dato es simple, es posible especificar la anotacion @XmlWriteIgnoreIfEquals
		// que provoca que NO se serialice el field en el XML si coincide con el valor especificado
		XmlWriteIgnoredIfEquals xmlWriteIgnoreIfEqualsAnnot = field.getAnnotation(XmlWriteIgnoredIfEquals.class);
		if (fieldMap.getDataType().isSimple() && xmlWriteIgnoreIfEqualsAnnot != null) {
			String val = xmlWriteIgnoreIfEqualsAnnot.value();
			if (!Strings.isNullOrEmpty(val)) fieldMap.getXmlMap().setValueToIgnoreWhenWritingXML(xmlWriteIgnoreIfEqualsAnnot.value());
		} else if (xmlWriteIgnoreIfEqualsAnnot != null) {
			throw new MarshallerException(Throwables.message("Field {} is annotated with @{}, BUT it's NOT a simple type so this behabiour is NOT possible",
															 field.getName(),XmlWriteIgnoredIfEquals.class.getName()));
		}
		

		//System.out.println("---->" + type.getName() + "." + field.getName() + ": " + dataType.getName() + " > " + dataType.getType());
		XmlTypeDiscriminatorAttribute discriminatorAttrAnnot = field.getAnnotation(XmlTypeDiscriminatorAttribute.class);
        if (!ReflectionUtils.isInstanciable(dataType.getType())) {
//        	 Si el tipo es un tipo parametrizado se puede saber el tipo del field:
//        	 Ej:	public class MyGenericTypeBase<T> {
//        				private T field;	
//        			}
//        	if (field.getGenericType() instanceof TypeVariable && type.getTypeParameters() != null) {
//        		TypeVariable<?> fieldType = ((TypeVariable<?>)field.getGenericType());
//        		TypeVariable<?>[] typeParams = type.getTypeParameters();
//        		if (CollectionUtils.hasData(typeParams)) {
//        			for (TypeVariable<?> typeParam : typeParams) {
//        				if (typeParam.getGenericDeclaration() instanceof Class) {
//        					if (fieldType.getName().equals(typeParam.getName())) {
//        						Type[] bounds = typeParam.getBounds();
//        						// ... no llego a nada...
//        					}
//        				}
//        			}
//        		}
//        	} else  {
				// Si el tipo de dato NO es instanciable hay DOS posibilidades:
				// A.- El campo se anota con @XmlElement SIN indicar el nombre del tag, con lo que en tiempo de ejecucion se averigua:
				//			- java->xml: el nombre del tag indicado en la anotacion @XmlRootElement del tipo correspondiente a la instancia 
				//			- xml->java: el tipo concreto corresponde al tipo anotado con @XmlRootElement igual al nombre del tag
				// B.- El campo se anota con @XmlElement(name=xxx) INDICANDO el nombre del tag, con lo que dado que todos los posibles tipos que 
				//	   implementan la interfaz o extienden de la clase abstracta van a llegar con mismo tag, es necesario "algo" para discriminar
				//	   el tipo concreto. Se utiliza un atributo del tag indicado en la anotacion @XmlTypeDiscriminatorAttribute
	        	if (isAttribute) {
	        		// if the type can be built from a string
	        		if (dataType.isImplementingAnyOf(OID.class)) {
	        			// although the concrete type is not known it's sure that it's an oid that can be created using 
	        			// an static valueOf(String) method
	        		} else {
	        			throw new MarshallerException("Field " + field.getType().getName() + " " + field.getName() + " at " + type.getName() + " is an interface or an abstract base type and it's mapped as a xml ATTRIBUTE. " +
	        										  "It's ONLY possible to define a mapping with an interfaces or abstract base type with xml ELEMENTS");
	        		}
	        	}
	        	// Caso B: la anotacion @XmlElement lleva el atributo name y ademas se indica la anotacion @XmlTypeDiscriminatorAttribute
	        	XmlElement xmlElAnnot = field.getAnnotation(XmlElement.class);
	        	if (xmlElAnnot != null && !_isXmlElementAnnotationDefaultValue(xmlElAnnot)		// NO se indica el atributo name de la anotacion @XmlElement
	        	 && discriminatorAttrAnnot == null) throw new MarshallerException("El miembro " + field.getType().getName() + " " + field.getName() + " de la clase " + type.getName() + " es un interfaz o una clase abstracta donde todos los posibles tipos van a estar marcados por el mismo tag XML (se ha anotado con @XmlElement indicando un atributo 'name'). En este caso es necesario anotar el miembro con @XmlTypeDiscriminatorAttribute para incluir en el XML un atributo que permita conocer el tipo concreto al pasar de XML a java; una alternativa es anotar el miembro con @XmlElement SIN indicar el nombre del tag, en este caso NO es necesario anotar con @" + XmlTypeDiscriminatorAttribute.class.getSimpleName());
//        	}
        }
        if (discriminatorAttrAnnot != null) fieldMap.getXmlMap().setDiscriminatorWhenNotInstanciable(discriminatorAttrAnnot.name());
		
		// Ver si el campo es CDATA
		if (!fieldMap.getXmlMap().isAttribute()) {
			XmlCDATA xmlCDATAFieldAnnot = field.getAnnotation(XmlCDATA.class);
			boolean isCDATA = xmlCDATAFieldAnnot != null ? true : false;
			fieldMap.getXmlMap().setCdata(isCDATA);
		}
		return fieldMap;
	}
	/**
	 * Obtiene el nombre de un nodo XML a partir de lo indicado en una anotacion {@link XmlRootElement}, {@link XmlElement} o {@link XmlAttribute}
	 * teniendo en cuenta que si NO se indica valor en la propiedad name(), el valor por defecto es ##default
	 * @param name nombre del indicado en el valor name() de la anotacion (puede ser null)
	 * @param defaultName valor por defecto en caso de que NO se indique nada en el valor name() de la anotacion
	 * @return
	 */
	private static String _nodeNameFromAnnotation(final String name,final String defaultName) {
		String outName = null;
		if (name != null && !name.equals("##default")) {
			outName = name;
		} else {
			outName = defaultName.replaceAll("\\$","_");
		}
		return outName;
	}
	private static boolean _isXmlAttributeAnnotationDefaultValue(final XmlAttribute annot) {
		return annot.name().equals("##default");
	}	
	private static boolean _isXmlElementAnnotationDefaultValue(final XmlElement annot) {
		return annot.name().equals("##default");
	}
	private static boolean _isXmlElementWrapperAnnotationDefaultValue(final XmlElementWrapper annot) {
		return annot.name().equals("##default");
	}
	/**
	 * Obtiene el tipo de dato de un field a partir de la definicion de la clase y el field
	 * @param type el tipo
	 * @param field el field
	 * @return el tipo de dato
	 */
	private static DataType _dataTypeFromType(final Class<?> type,final Field field) {
		// [PASO 0]: Si el miembro esta anotado con @XmlInline significa que es un XML inline
		if (field.getAnnotation(XmlInline.class) != null) {
			DataType outDataType = DataType.create("XML");
			return outDataType;
		}
		// [PASO 1]: Averiguar el tipo actual del miembro
		Class<?> actualFieldType = null;
		if (field.getGenericType() instanceof Class) {
			// [A] Miembro NO generico
			actualFieldType = field.getType();
			
		} else {
			// [B] Miembro generico --> Hay que intetar obtener el Parametro del tipo generico
			// see http://blog.vityuk.com/2011/03/java-generics-and-reflection.html
			// The java.lang.reflect.Type has some sub-clases:
			// 		java.lang.reflect.Type
			//			|-- java.lang.Class 						-> "normal" class
			//			|-- java.lang.reflect.ParameterizedType		-> class with a generic parameter (ie: String at List<String>)
			//			|-- java.lang.reflect.TypeVariable			-> Generic parameter of a class (ie: T at List<T>)
			//			|-- java.lang.reflect.WildcardType			-> wildcard type (ie: ? extends Number at List<? extends Number>
			//			|-- java.lang.reflect.GenericArrayType		-> Generic type of an array (ie: T en T[])
			
			// Intentar encontrar el tipo exacto
			Type concreteType = GenericTypeReflector.getExactFieldType(field,type);

			// ... si no se ha encontrado es un error
			if (concreteType == null || !(concreteType instanceof Class)) {
				if (field.getGenericType() instanceof TypeVariable) {
					// NO se ha podido encontrar el tipo exacto
					throw new MarshallerException("Field " + field.getType().getName() + " " + field.getName() + " at " + type.getName() + " is a generic parameterized type and tye parameterizing type could NOT be guessed. " + 
												  "The correct java type cannot be instantiated from the xml type");
				} else if (field.getGenericType() instanceof ParameterizedType) {
					// Se ha encontrado el tipo exacto
					ParameterizedType pType = (ParameterizedType)field.getGenericType();
					concreteType = pType.getRawType();		// ... se devuelve el tipo Raw
				}
			}
			actualFieldType = (Class<?>)concreteType;
		}
		
		// [PASO 2]: Componer la Descripcion
		String dataTypeDesc = _fieldTypeStandardDesc(type,field,
													   actualFieldType);
		//System.out.println("------->" + type.getName() + "." + field.getName() + "." + actualFieldType.getName() + ":  " + dataTypeDesc);
		DataType outDataType = DataType.create(dataTypeDesc);	// Si se trata de una coleccion queda pendiente crear el type de los elementos
		
		if (outDataType.isDate()) {
			// Buscar una anotacion que indica el formato
			XmlDateFormat dateFmtAnnot = field.getAnnotation(XmlDateFormat.class);
			if (dateFmtAnnot != null && dateFmtAnnot.value() != null) {
				String fmt = dateFmtAnnot.value();
				outDataType.asDate().setDateFormat(fmt);
			}
		}
		
//		if (outDataType.isCollection()) {
//			DataType colElsType = DataType.create(outDataType.asCollection().getValueElementsTypeName());	// Queda pendiente referenciar el BeanMap de este tipo
//			outDataType.asCollection().setValueElementsType(colElsType);
//		}
		return outDataType;
	}
	/**
	 * Gets the description of a field's type in a normalized forma
	 * @param type
	 * @param field el field
	 * @param actualFieldType field's actual data type (solving generics, etc)
	 * @return
	 */
	private static String _fieldTypeStandardDesc(final Class<?> type,
											 	 final Field field,final Class<?> actualFieldType) {
		String dataTypeDesc = null;
		if (actualFieldType != Object.class && ReflectionUtils.isTypeDef(actualFieldType)) {
			// java type
			dataTypeDesc = Class.class.getCanonicalName(); 	// "java.lang.Class";
			
		} else if (ReflectionUtils.isImplementing(actualFieldType,LanguageTexts.class) 
			   && !ReflectionUtils.isImplementing(actualFieldType,LanguageTextsI18NBundleBacked.class)) {
			//"Map:(r01f.locale.Language,java.lang.String)";
			dataTypeDesc = "Map:" + actualFieldType.getName() + "(" + Language.class.getCanonicalName() + "," + String.class.getCanonicalName() + ")";	
			
		} else if (CollectionUtils.isMap(actualFieldType)) {
			// Mapa
			Class<?> keyAndValueComponentTypes[] = _mapFieldKeyValueComponentTypes(type,field);
			if (keyAndValueComponentTypes != null && keyAndValueComponentTypes.length == 2) {
				String keyType = keyAndValueComponentTypes[0] != null ? keyAndValueComponentTypes[0].getName() 
																	  : Object.class.getCanonicalName(); //"java.lang.Object";
				String valueType = keyAndValueComponentTypes[1] != null ? keyAndValueComponentTypes[1].getName() 
																		: Object.class.getCanonicalName(); //"java.lang.Object";
				dataTypeDesc = "Map:" + actualFieldType.getName() + "(" + keyType + "," + valueType + ")";
			} else {
				dataTypeDesc = "Map:" + actualFieldType.getName();
			}
			
		} else if (CollectionUtils.isCollection(actualFieldType)) {
			// Collection
			Class<?> componentType = _collectionFieldComponentType(type,field);
			if (componentType != null) {
				dataTypeDesc = "Collection:" + actualFieldType.getName() + "(" + componentType.getName() + ")";
			} else {
				dataTypeDesc = "Collection:" + actualFieldType.getName();
			}
			
		} else if (CollectionUtils.isArray(actualFieldType)) {
			// Array
			dataTypeDesc = _collectionFieldComponentType(type,field).getName() + "[]";
			
		} else if (actualFieldType.isEnum() || actualFieldType.getAnnotation(XmlEnum.class) != null) {
			// Enum
			dataTypeDesc = "Enum(" + actualFieldType.getName() + ")";
			
		} else if (!actualFieldType.isPrimitive() && !ReflectionUtils.isInstanciable(actualFieldType)) {
			// [C] Interface
			dataTypeDesc = field.getType().getName();
	
		} else {
			// [D] Non-generic type
			dataTypeDesc = actualFieldType.getName();
		}
		return dataTypeDesc;
	}
	private static String _typeNormalizedDesc(final Class<?> type) {
		String dataTypeDesc = null;
		if (CollectionUtils.isMap(type)) {
			//@SuppressWarnings("unchecked")
			//Class<? extends Map<?,?>> mapType =  (Class<? extends Map<?,?>>)type;
			// Map:(java.lang.Object,java.lang.Object)
			dataTypeDesc = "Map:" + type.getName() + "(" + Object.class.getCanonicalName() + "," + Object.class.getCanonicalName() + ")";
			
		} else if (CollectionUtils.isCollection(type)) {
			//@SuppressWarnings("unchecked")
			//Class<? extends Collection<?>> colType =  (Class<? extends Collection<?>>)type;
			dataTypeDesc = "Collection:" + type.getName() + "(" + Object.class.getCanonicalName() + ")";	
			
		} else if (type.isEnum() || type.getAnnotation(XmlEnum.class) != null) {
			dataTypeDesc = "Enum(" + type.getName() + ")";
			
		} else {
			dataTypeDesc = type.getName();
		}
		return dataTypeDesc;
	}
	/**
	 * Gets the name of the field (it can be prefixed with _ which is removed)
	 * NOTA: 	Usually the prefix is set with lombok's {@link Accessors} annotation, BUT this annotation PERO el problema es
	 * 			que esta anotacion SOLO se mantiene en el codigo, y NO en tiempo de ejecucion 
	 * @param type el tipo
	 * @param f el miembro del tipo
	 * @return el nombre del miembro eliminando los prefijos indicados en {@link Accessors}
	 */
	private static String _fieldName(Field f) {
		String prefix = "_";
		
		String outFieldName = f.getName();
		if (f.getName().startsWith(prefix)) outFieldName = f.getName().substring(prefix.length());
		return outFieldName;
	}
    /**
     * Devuelve el tipo de elementos la clave y valor de un Mapa {@link Map} parametrizado (Map<K,V>)
     * NOTA:	Debido al type erasure de los genericos en java el tipo de K y V en un Map<K,V> SOLO se puede obtener
     * 			si se trata de un field de una clase (miembro), pero NO si se trata de una variable en un metodo
     * @param type el tipo donde esta definido el field 
     * @param field el miembro de una clase
     * @return un array de dos posiciones: 0=tipo de la clave, 1=tipo del value
     */
    @SuppressWarnings("unchecked")
	public static Class<?>[] _mapFieldKeyValueComponentTypes(final Type type,final Field field) {
    	Class<?>[] outKeyAndValueTypes = new Class<?>[2];			// array de dos posiciones: 0=key, 1=value
    	
    	Type genericFieldType = field.getGenericType();
    	if (genericFieldType instanceof Class) {
    		// Se trata de una clase que extiende de un mapa parametrizado
    		// Ej: public class LanguageTexts extends Map<Language,String> {...}
    		// Hay que obtener un type = Map<Language,String>
    		genericFieldType =  GenericTypeReflector.getExactSuperType(field.getGenericType(),
    																   Map.class);
    	} 
		// Aqui genericFieldType contiene SIEMPRE un mapa parametrizado (ej: Map<String,String>)
    	ParameterizedType mapComponentType = (ParameterizedType)genericFieldType;
    	if (mapComponentType.getActualTypeArguments().length == 2) {
    		// El field es un mapa
    		// ej: 	private Map<String,String> _theMap;
	    	outKeyAndValueTypes[0] = ReflectionUtils.classOfType(mapComponentType.getActualTypeArguments()[0]);
	    	outKeyAndValueTypes[1] = ReflectionUtils.classOfType(mapComponentType.getActualTypeArguments()[1]);
    	} else {
    		// El field es una clase que extiende de un mapa
    		// ej: 	private MyTypeImplementingMap _theMap;
    		//		siendo public class MyTypeImplementingMap extends HashMap<String,String> {...}
    		mapComponentType = (ParameterizedType)field.getType().getGenericSuperclass();
	    	outKeyAndValueTypes[0] = ReflectionUtils.classOfType(mapComponentType.getActualTypeArguments()[0]);
	    	outKeyAndValueTypes[1] = ReflectionUtils.classOfType(mapComponentType.getActualTypeArguments()[1]);
    	}
    	// Si alguno de los componentes es una variable generica hay que ver si se puede obtener el tipo concreto a traves de la parametrizacion
    	if (outKeyAndValueTypes[0] == null || outKeyAndValueTypes[1] == null) {
    		for (int i=0; i <= 1; i++) {
    		 	if (outKeyAndValueTypes[i] == null && mapComponentType.getActualTypeArguments()[i] instanceof TypeVariable) {
    		 		outKeyAndValueTypes[i] = _collectionComponentType(type,(TypeVariable<? extends Class<?>>)mapComponentType.getActualTypeArguments()[i]);
    		 	}
    		}
    	}
    	return outKeyAndValueTypes;
    }
    /**
     * Devuelve el tipo de elementos de una coleccion {@link java.util.Collection} o un array parametrizado (Collection<E>)
     * NOTA:	Debido al type erasure de los genericos en java el tipo de un List<tipo> SOLO se puede obtener
     * 			si se trata de un field de una clase (miembro), pero NO si se trata de una variable dentro del flujo 
     * @param field el miembro de una clase
     * @return el tipo 
     */
    @SuppressWarnings("unchecked")
    public static Class<?> _collectionFieldComponentType(final Type type,final Field field) {
    	Class<?> outCollectionComponentClass = null;
    	
    	
    	if (field.getType().isArray()) {
    		Type compType = GenericTypeReflector.getArrayComponentType(field.getType());	// field.getType().getComponentType();
    		outCollectionComponentClass = (compType != null  && compType instanceof Class) ? (Class<?>)compType
    																					   : Object.class;
    	} else {
    		Type genericFieldType = field.getGenericType();
    		if (genericFieldType instanceof Class) {
	    		// Se trata de una clase que extiende de una coleccion parametrizada
	    		// Ej: public class LanguageTexts extends Map<Language,String> {...}
	    		// Hay que obtener un type = Map<Language,String>
	    		genericFieldType =  GenericTypeReflector.getExactSuperType(field.getGenericType(),
		    															   Collection.class);
		    } 
    		// Aqui genericFieldType contiene SIEMPRE una coleccion parametrizada (ej: Collection<String>)
	        ParameterizedType collectionComponentType = (ParameterizedType)genericFieldType;
	        outCollectionComponentClass = ReflectionUtils.classOfType(collectionComponentType.getActualTypeArguments()[0]);
	        // If the component type is an abstract type, Object is returned
	        if (outCollectionComponentClass != null 
	         && Modifier.isAbstract(outCollectionComponentClass.getModifiers())) outCollectionComponentClass = Object.class;
	        
	        // Si alguno de los componentes es una variable generica hay que ver si se puede obtener el tipo concreto a traves de la parametrizacion
	        if (outCollectionComponentClass == null && collectionComponentType.getActualTypeArguments()[0] instanceof TypeVariable) {
	        	TypeVariable<? extends Class<?>> typeVar = (TypeVariable<? extends Class<?>>)collectionComponentType.getActualTypeArguments()[0];
	        	outCollectionComponentClass = _collectionComponentType(type,
	        														   typeVar);
	        }
    	}
    	return outCollectionComponentClass;
    }
    /**
     * Tries to guess a collection's component concrete type
     * If it cannot guess the type, {@link Object} is returned
     * @param type
     * @param typeVar
     * @return
     */
    private static Class<?> _collectionComponentType(final Type type,final TypeVariable<? extends Class<?>> typeVar) {    	
    	Class<?> outComponentType = null;
 		Type compType = GenericTypeReflector.getTypeParameter(type,typeVar);
 		outComponentType = (compType != null  
 				         && compType instanceof Class 
 				         && !Modifier.isAbstract(((Class<?>)compType).getModifiers())) ? (Class<?>)compType 
 																					   : Object.class;
 		return outComponentType;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  CUSTOM TRANSFORMERS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los transformers xml<->java definidos para un tipo utilizando las anotaciones {@link XmlReadTransformer} y {@link XmlWriteTransformer}
	 * @param type el tipo del que se quieren los transformers xml<->java
	 * @return null si el tipo no tiene las anotaciones o bien no tiene las dos anotaciones
	 * @throws MarshallerException si el tipo tiene una sola de las anotaciones requeridas
	 */
	private static SimpleMarshallerCustomXmlTransformers _customXmlTransformersOf(Class<?> type) {
		XmlReadTransformer readTransformerAnnot = ReflectionUtils.typeOrSuperTypeAnnotation(type,XmlReadTransformer.class);
		XmlWriteTransformer writeTransformerAnnot = ReflectionUtils.typeOrSuperTypeAnnotation(type,XmlWriteTransformer.class);
		if ( (readTransformerAnnot != null && writeTransformerAnnot == null) 
		||   (readTransformerAnnot == null && writeTransformerAnnot != null) ) throw new MarshallerException("Las anotaciones XmlReadTransformer y XmlWriteTransformer tienen que venir en pareja (si se incluye una, se debe incluir la otra");
		
		SimpleMarshallerCustomXmlTransformers outXmlTransformers = null;
		if (readTransformerAnnot != null && writeTransformerAnnot != null) {
			outXmlTransformers = new SimpleMarshallerCustomXmlTransformers((SimpleMarshallerCustomXmlTransformers.XmlReadCustomTransformer<?>)Reflection.wrap(readTransformerAnnot.using()).load().instance(),
																		   (SimpleMarshallerCustomXmlTransformers.XmlWriteCustomTransformer)Reflection.wrap(writeTransformerAnnot.using()).load().instance());		}
		return outXmlTransformers;
	}
	private BeanMap _beanMapFromCustomXmlTransformers(Class<?> type) {
		BeanMap outBeanMap = null;
		SimpleMarshallerCustomXmlTransformers customXmlTransformers = _customXmlTransformersOf(type);
		if (customXmlTransformers != null) {
			outBeanMap = new BeanMap(type.getName());
			outBeanMap.setCustomXMLTransformers(customXmlTransformers);
			
			// Poner el nuevo bean en la salida...
			_loadedBeans.put(type.getName(),outBeanMap);
		}
		return outBeanMap;
	}
}
