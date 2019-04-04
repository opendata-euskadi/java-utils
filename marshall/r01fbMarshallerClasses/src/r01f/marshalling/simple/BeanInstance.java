package r01f.marshalling.simple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.marshalling.MarshallerException;
import r01f.marshalling.simple.BeanMap.BeanXMLMap;
import r01f.marshalling.simple.DataTypes.DataType;
import r01f.marshalling.simple.DataTypes.DataTypeEnum;
import r01f.reflection.ReflectionException;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
       
/**
 * Models a java type during the marshalling process 
 */
@Accessors(prefix="_")
class BeanInstance {  	
///////////////////////////////////////////////////////////////////////////////
//  FIELDS
///////////////////////////////////////////////////////////////////////////////        
    @Getter 		private final BeanMap _mapping;						// type definition
    @Getter 		private final Map<String,FieldInstance> _fields;	// type fields by their xml node name
    @Getter @Setter private String _effectiveNodeName;					// xml node name 
    				private Object _instance;							// the data    
///////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////
    /**
     * Default constructor used at:
     * 	- a primitive type (String, integer, etc) that's NOT declared at the mapping file
     * 	- a collection of primitive types (String, integer, etc)
     */
    public BeanInstance() throws MarshallerException {
    	this(null,null,null,null);
    }
    public BeanInstance(final BeanMap newBeanMap,final String effectiveNodeName) {
    	_effectiveNodeName = effectiveNodeName;
        _mapping = newBeanMap;
        _fields = newBeanMap != null && CollectionUtils.hasData(newBeanMap.getFields()) ? new HashMap<String,FieldInstance>(newBeanMap.getFields().size())
        																			    : null;
    }
    /** 
     * {@link BeanInstance} builder from the {@link BeanMap} 
     * @param newBeanMap 
     * @param constructorArgsTypes the type's constructor argument types
     * @param constructorArgs the type's constructor argument values
     * @param effectiveNodeName the type's tag to be used
     */
    public BeanInstance(final BeanMap newBeanMap,
    					final Class<?>[] constructorArgsTypes,final Object[] constructorArgs,
    					final String effectiveNodeName) throws MarshallerException {
    	this(newBeanMap,effectiveNodeName);
    	
        if (_mapping != null && _mapping.getCustomXMLTransformers() != null) return;	// CustomXMLTransformers are used... nothing to do
        																				// ... the bean is created at ObjsFromXMLBuilder
        try {
	        if (_mapping != null) {
	        	// Get a type instance...
	        	if (_mapping.getDataType().isEnum()) {
	        		// an enum
	        		_instance = new StringBuilder();
	        	} else if (_mapping.getDataType().isObject() && _mapping.getFields() == null) {
	        		// an object with NO mapped fields (ie all fields are transient or annotated with @XmlTransient)
	        		// (for example r01f.types.Path)
	        		// This kind of objects can only be created using a single String-param constructor or an static valueOf(String) builder method
	        		_instance = new StringBuilder();
	        		
	        	} else {
	        		// a "usual" java type
	        		_instance = MappingReflectionUtils.createObjectInstance(_mapping,
	        																constructorArgsTypes,constructorArgs);
	        	}
	        } else {
	            // Virtual instance: there's NO definition for the type
	            // Usually the flow enter this block if it's:
	        	//		- A simple type (String, Integer, int, Long, boolean, etc)
	        	//		- A Collection of simple types (String, integer, etc)
	        	//				- List: the text is directly the field value
	        	//				- Maps:the tag name is the key and the text is the value
	        	// 				ie:
	        	//	  			- List:				 				- Map
	            //					<parameters>                       <parameters>
	            //						<param>valor1</param>                 <param_1>valor1</param_1>
	            //						<param>valor2</param>				  <param_2>valor2</param_2>
	            //					</parameters>					   </parameters>
	            _instance = new StringBuilder();
	        }
        } catch(ReflectionException refEx) {
            throw new MarshallerException("Error while creating an instance of " + (_mapping != null ? _mapping.getTypeName() : "null") + ". Maybe it does NOT have a default no-args constructor: " + refEx.getMessage(),refEx);
        }        
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the concrete bean instance
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
    	return (T)_instance;
    }
    /**
     * Sets the concrete bean instance
     * @param obj the instance
     */
    public <T> void set(final T obj) {
    	_instance = obj;
    }
    /**
     * Returns the bean xml mapping
     * @return
     */
    public BeanXMLMap getXmlMap() {
    	return _mapping.getXmlMap();
    }
    /** 
     * Adds a field instance to a bean instance
     * @param fieldMap field mapping
     * @return 
     * @throws MarshallerException 
     */
	public FieldInstance getFieldInstance(final FieldMap fieldMap) throws MarshallerException {
    	if (fieldMap == null) return null;
    	FieldInstance fieldInstance = null;
    	// If the instance already has the field, just return the existing instance; create otherwise
    	if (!_fields.isEmpty()) {
    		fieldInstance = _fields.get(fieldMap.getXmlMap().getNodeName());
    	}
    	// otherwise create... 
    	if (fieldInstance == null) {
    		fieldInstance = new FieldInstance(fieldMap);
    		_fields.put(fieldMap.getXmlMap().getNodeName(),fieldInstance);
    	} 
    	return fieldInstance;
	}
    /**
     * Builds the object instance setting every of it's properties
     */
    public Object build() {
    	if (_instance == null) return null;
    	
    	if (_mapping == null) {
    		// "Virtual" instance for collection primitive types (String, date, etc)  
    		// ... the real instance is built when building the collection at MappingReflectionUtils
    		return _instance;
    	}
    	if (_mapping.isCustomXmlTransformed()) {
    		StringBuilder xml = (StringBuilder)_instance;
    		Object builtObj = _mapping.getCustomXMLTransformers().getXmlReadTransformer()
    												  			 .beanFromXml(false,xml);
    		_instance = builtObj;
    		return _instance;
    	}
    	if (_mapping.getDataType().isEnum()) {
    		Object builtObj = MappingReflectionUtils.simpleObjFromString(_mapping.getDataType(),
    															   		 (StringBuilder)_instance);
    		_instance = builtObj;
    		return _instance;
    	}
    	// Build the object from it's fields
    	// (the build method is called when the closing xml tag is received: all it's fields are supposed to have been set and built)
    	if (CollectionUtils.hasData(_fields)) {
	    	for (FieldInstance fieldInstance : _fields.values()) {
	    		
	    		if (fieldInstance.get() == null) continue;
	    		
    			Object value = null;
    			DataType fieldDataType = fieldInstance.getMapping().getDataType();
    			
    			if (fieldDataType.isXML()) {
    				StringBuilder valueStr = fieldInstance.get();
    				if (valueStr.toString().startsWith("<" + fieldInstance.getMapping().getXmlMap().getNodeName() + ">")) {
    					// If the xml is mapped with the tag, remove the tag
    					valueStr = _extractXMLBetweenTag(valueStr);
    					fieldInstance.set(valueStr);
    				}
    			} 
    			if (fieldDataType.isCollection() || fieldDataType.isMap()) {
		        	// Collection
		        	List<BeanInstance> instances = fieldInstance.get();
		        	if ( !CollectionUtils.isNullOrEmpty(instances) ) value = instances;			        	
		        	
		        	// collection / map of xmls... extract the xml that is within the beginning and end tag (it cannot be done at other place)
		        	if ((fieldDataType.isCollection() && DataTypeEnum.XML.canBeFromTypeName(fieldDataType.asCollection().getValueElementsType().getName())
                        ||
                        (fieldDataType.isMap() && DataTypeEnum.XML.canBeFromTypeName(fieldDataType.asMap().getValueElementsType().getName())))) {
		        		for (BeanInstance colElBean : instances) {
		        			StringBuilder valueStr = colElBean.get();
		        			valueStr = _extractXMLBetweenTag(valueStr);
		        			colElBean.set(valueStr);
		        		}
		        	}
		        	
		        } else if (fieldDataType.isSimple()) {
		        	// Simple type
		            StringBuilder valueStr = fieldInstance.get();
		            if (!Strings.isNullOrEmpty(valueStr)) value = valueStr;
		        
		        } else if (fieldDataType.isObject()
		        		&& (fieldDataType.getBeanMap() == null
		        		    || !fieldDataType.getBeanMap().isCustomXmlTransformed())) {
		        	// Normal bean
		        	BeanInstance instance = fieldInstance.get();
		        	value = instance.get();
		        	
		        } else if (fieldDataType.isObject() 
		        		&& fieldDataType.getBeanMap().isCustomXmlTransformed()) {
		        	// Bean customXmlTransformed: en la instancia viene el xml
		        	BeanInstance instance = fieldInstance.get();
		        	value = instance.get();
		        	//StringBuilder xml = instance.get();
		        	//value = fieldDataType.getBeanMap().getCustomXMLTransformers().getXmlReadTransformer().beanFromXml(xml);
		        } 
    			
		        // Set the value by reflection
		        if (value != null) {
		        	MappingReflectionUtils.setFieldValue(_instance,
		        										 fieldInstance.getMapping(),
		        										 value);
		        }
	    	}
    	} 
    	return _instance;
    }
    
    // Extract xml within two tags: (<xmlTag>...xml to extract...</xmlTag>
    private static final Pattern XMLBETWEENTAGS_PATTERN = Pattern.compile("<([^>]+)>(.*)</\\1>");
    private static StringBuilder _extractXMLBetweenTag(final StringBuilder xml) {
    	StringBuilder outXML = null;
		Matcher m = XMLBETWEENTAGS_PATTERN.matcher(xml);
		if (m.find()) outXML = new StringBuilder(m.group(2));
		return outXML != null ? outXML
							  : xml;
    }
}