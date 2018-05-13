package r01f.marshalling.simple;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.marshalling.MarshallerException;

/**
 * Models a field inside a type instance during the xml to object conversion time
 */
@Accessors(prefix="_")
class FieldInstance {
///////////////////////////////////////////////////////////////////////////////
//  MIEMBROS
///////////////////////////////////////////////////////////////////////////////    
    @Getter			private final FieldMap _mapping;	// Field definition		
    				private Object _instance;			// Field instance    	   
///////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORES
///////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor
     * @param newFieldMap field definition 
     */
    public FieldInstance(final FieldMap newFieldMap) {
	    _mapping = newFieldMap;
    }
    /** 
     * Creates an instance using the default constructor
     */
    public Object createInstance() throws MarshallerException {
    	return this.createInstance(null,
    							   null);
    }
    /** 
     * Creates an instance using a given constructor
     * @param constructorArgsTypes constructor field¡s arg types
     * @param constructorArgs constructor field's args values
     */
    public Object createInstance(final Class<?>[] constructorArgsTypes,
    						     final Object[] constructorArgs) throws MarshallerException {
        _instance = _createInstance(_mapping,
        							constructorArgsTypes,constructorArgs);
        return _instance;
    }
    public Object createInstance(final Object instance) {
    	String nodeName = _mapping.getDataType().getBeanMap().getXmlMap().getNodeName() != null ? _mapping.getDataType().getBeanMap().getXmlMap().getNodeName()
    																						    : _mapping.getName();
    	BeanInstance outInstance = new BeanInstance(_mapping.getDataType().getBeanMap(),
    												nodeName);
    	outInstance.set(instance);
    	_instance = outInstance;
    	return outInstance;
    }
    public static Object createInstance(final FieldMap mapping) {
    	return _createInstance(mapping,
    						   null,null);
    }
    private static Object _createInstance(final FieldMap mapping,
    									  final Class<?>[] constructorArgsTypes,final Object[] constructorArgs) {
    	Object outInstance = null;
        // Create the java object instance depending on the field type
        // Two scenarios can arise:
        //		1. The field's type was explicitly set at the mapping
        //		2. The field's type was NOT explicitly set at the mapping (ie simple types); an StringBuilder is created
        //		   que m�s adelante se convierte en el tipo concreto
        if (mapping != null) {
        	// [1]: Threre's info about the type (complex type or collection)
	        if (mapping.getDataType().isCollection() || mapping.getDataType().isMap()) {
	        	List<BeanInstance> instances = Lists.newArrayList();
	        	outInstance = instances;
	        	
	        } else if (mapping.getDataType().isSimple()) {
	            outInstance = new StringBuilder();
	            
	        } else if (mapping.getDataType().isObject()) {
	        	String nodeName = mapping.getDataType().getBeanMap().getXmlMap().getNodeName() != null ? mapping.getDataType().getBeanMap().getXmlMap().getNodeName()
	        																						   : mapping.getName();
	            outInstance = new BeanInstance(mapping.getDataType().getBeanMap(),
	            							   constructorArgsTypes,constructorArgs,
	            							   nodeName);
	        } else {
	        	throw new MarshallerException("El tipo de dato " + mapping.getDataType() + " del miembro " + mapping.getName() + " del bean " + mapping.getDeclaringBeanMap().getTypeName() + " NO es correcto. Revisa el documento de mapeo");
	        }
        } else {
        	// [2] There's NO info about the type (simple type)
        	outInstance = new StringBuilder();
        }
        return outInstance;
    }
///////////////////////////////////////////////////////////////////////////////
//  METHODS
///////////////////////////////////////////////////////////////////////////////     
    /**
     * Gets the field's oncrete instance
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
    	return (T)_instance;
    }
    /**
     * Sets the field's concrete instance
     * @param obj 
     */
    public <T> void set(T obj) {
    	_instance = obj;
    }
}  
