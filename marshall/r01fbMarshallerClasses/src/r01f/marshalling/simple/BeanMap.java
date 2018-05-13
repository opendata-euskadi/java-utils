package r01f.marshalling.simple;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.marshalling.MarshallerException;
import r01f.marshalling.simple.DataTypes.DataType;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


/**
 * Models xml mapping of a java type
 */
@Accessors(prefix="_")
class BeanMap {  
///////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
///////////////////////////////////////////////////////////////////////////////////////////    
    @Getter @Setter	private BeanXMLMap _xmlMap;
    @Getter @Setter private String _typeName;          			// Java type name (including package)
    @Getter @Setter	private DataType _dataType;					// Java type 
    @Getter @Setter private boolean _useAccessors = true;    	// Use get/set methods
    @Getter @Setter private String _oidAccessorMethod;			// Method to call to get the oid
    @Getter 	    private Map<String,FieldMap> _fields;      	// Fields indexed by the field name 
    // If custom transformers are used
    // BEWARE:	Custom transformers are SINGLETONS: for a concrete BeanMap there exist a SINGLE transformer instance
    //		    ... so the transformers MUST NOT have state
    @Getter @Setter private SimpleMarshallerCustomXmlTransformers _customXMLTransformers;
                
    				private Map<String,FieldMap> _attrFieldsByXmlNodeName;		// cache that indexes attributes by tag
    				private Map<String,FieldMap> _elementsFieldsByXmlNodeName;	// cache that indexex xml elements by tag
    				private Map<String,FieldMap> _finalFields;					// cache of final fields
    				private Map<String,FieldMap> _nonFinalFields;				// cache of non-final fields
    				private FieldMap _oidField;									// cache of oid field
    				
//    public void setXmlMap(String nodeName) {
//    	_xmlMap = new BeanXMLMap(nodeName);
//    }
///////////////////////////////////////////////////////////////////////////////////////////
//  
///////////////////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    @NoArgsConstructor @AllArgsConstructor
    class BeanXMLMap {    	
    	@Getter @Setter private String _nodeName;	// XML node name (element or attribute)
    	
	    /**
	     * Returns the fields mapped as attribute)
	     * @return
	     */
	    public Collection<FieldMap> getFieldsMappedAsXmlAttributes() {
	    	Collection<FieldMap> outColFields = null;
	    	if (_fields == null) return null;
	        for (FieldMap fm : _fields.values()) {
	        	if (fm.getXmlMap().isAttribute()) {
	        		if (outColFields == null) outColFields = Lists.newArrayList();
	        		outColFields.add(fm);
	        	}
	        }
	        return outColFields;
	    }
	    /**
	     * Returns the fields that comes from an object expanded as attribute
	     * @return
	     */
	    public Collection<FieldMap> getFieldsExpandedAsXmlAttributes() {
	    	Collection<FieldMap> outAttrsFromExpandedObjs = null;
	    	if (_fields == null) return null;
	        for (FieldMap fm : _fields.values()) {
	        	if (fm.getXmlMap().isAttribute() && fm.getXmlMap().isExpandableAsAttributes()) {
	        		if (outAttrsFromExpandedObjs == null) outAttrsFromExpandedObjs = Lists.newArrayList();
	        		outAttrsFromExpandedObjs.add(fm);
	        	}
	        }
	        return outAttrsFromExpandedObjs;
	    }
	    /**
	     * Checks if all object's fields are mapped as attributes
	     * @return
	     */
	    public boolean areAllFieldsMappedAsXmlAttributes() {
	    	boolean allAttributes = true;
	    	if (_fields == null) return false;
	        for (FieldMap fm : _fields.values()) {
	        	if (!fm.getXmlMap().isAttribute()) {
	        		allAttributes = false;
	        		break;
	        	}
	        }
	        return allAttributes;
	    }
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
///////////////////////////////////////////////////////////////////////////////////////////    
    public BeanMap() {
        super();
        _xmlMap = new BeanXMLMap();
    }
    /**
     * using the type name (including package)
     * @param newType 
     */
    public BeanMap(final String newTypeName) {
        this();
        _typeName = newTypeName;
        _dataType = DataType.create(_typeName);
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return true if the bean is being marshalled using a custom transformer
     */
    public boolean isCustomXmlTransformed() {
    	return _customXMLTransformers != null 
    		&& _customXMLTransformers.getXmlReadTransformer() != null 
    		&& _customXMLTransformers.getXmlWriteTransformer() != null;
    }
    /** 
     * Returns a field's definition from the xml node name 
     * @param xmlNodeName xml node name
     * @param isAttribute true it the node is an attribute
     * @return the definition
     */
    public FieldMap getFieldFromXmlNode(final String xmlNodeName,
    									final boolean isAttribute) {
        if (_fields == null) return null;
        // try the cache...
        FieldMap outField = null;
        if (isAttribute && _attrFieldsByXmlNodeName != null) {
        	outField = _attrFieldsByXmlNodeName.get(xmlNodeName);
        } else if (_elementsFieldsByXmlNodeName != null) {
        	outField = _elementsFieldsByXmlNodeName.get(xmlNodeName);
        }
        return outField;        
    }
    /**
     * Gets a fields that can hold the given type
     * @param type the type
     * @return the first field that can hold the given type (if there's more than a single type, only the first is returned)
     */
    public FieldMap getFieldForType(final Class<?> type) {
    	if (_fields == null) return null;
    	FieldMap outFieldMap = null;
    	for (FieldMap fm : _fields.values()) {
    		if (fm.getDataType().getType().isAssignableFrom(type)) {
    			outFieldMap = fm;
    			break;
    		}
    	}
    	return outFieldMap;
    }
    /**
     * Returns the oid field
     * @return 
     */
    public FieldMap getOidField() {
        if (_fields == null) return null;
        return _oidField;
    }    
    /**
     * Returns the field's mapping
     * @param fieldName field's name
     * @return 
     */
    public FieldMap getField(final String fieldName) {
        if (_fields == null) return null;
        return _fields.get(fieldName);
    }
    /**
     * Returns the final field's
     * @return 
     */
    public Map<String,FieldMap> getFinalFields() {
    	return _finalFields;
    }
    /**
     * Returns the non-final fields
     * @return
     */
    public Map<String,FieldMap> getNonFinalFields() {
    	return _nonFinalFields;
    }
    /**
     * Returns the collection fields
     * @return
     */
    public Collection<FieldMap> getCollectionOrMapFields() {
    	Collection<FieldMap> outColFields = null;
    	if (_fields == null) return null;
        for (FieldMap fm : _fields.values()) {
        	DataType dataType = fm.getDataType();
        	if (dataType.isCollection() || dataType.isMap()) {
        		if (outColFields == null) outColFields = Lists.newArrayList();
        		outColFields.add(fm);
        	}
        }
        return outColFields;
    }
    /**
     * Checks if all object's fields are mapped as attributes
     * @return
     */
    public boolean areAllFieldsMappedAsAttributes() {
    	boolean allAttributes = true;
    	if (_fields == null) return false;
        for (FieldMap fm : _fields.values()) {
        	if (!fm.getXmlMap().isAttribute()) {
        		allAttributes = false;
        		break;
        	}
        }
        return allAttributes;
    }
    /**
     * Sets a field as element
     * @param newField
     * @throws MarshallerException if the new field already exists
     */
	public void addField(final FieldMap newField) throws MarshallerException {
		newField.setDeclaringBeanMap(this);	
        if (newField.getName() != null) {
            if (_fields == null) _fields = Maps.newLinkedHashMap();			// order is important
            FieldMap other = _fields.put(newField.getName(),newField);
            if (other != null) {
            	String msg = Strings.customized("{} field already exists at {}",
            					    			newField.getName(),this.getTypeName());
            	throw new MarshallerException(msg);
            }
            // Cache final fields
            if (newField.isFinal()) {
            	if (_finalFields == null) _finalFields = Maps.newLinkedHashMap();
            	_finalFields.put(newField.getName(),newField);
            } else {
            	if (_nonFinalFields == null) _nonFinalFields = Maps.newLinkedHashMap();
            	_nonFinalFields.put(newField.getName(),newField);
            }
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////
//	CACHE
///////////////////////////////////////////////////////////////////////////////////////////
	void initIndexes() {
        if (_fields == null) return;   
        
        // oid field
        for ( FieldMap fm : _fields.values() ) {
            if (fm.isOid()) {
            	_oidField = fm;
            	break;
            }
        }        
        
        // attribute and element index
		Map<String,FieldMap> attrs = new HashMap<String,FieldMap>();
		Map<String,FieldMap> els = new HashMap<String,FieldMap>();
        for (FieldMap fm : _fields.values()) {
    		if (fm.getXmlMap().isAttribute()) {
    			attrs.put(fm.getXmlMap().getNodeName(),fm);
    		} else {
    			els.put(fm.getXmlMap().getNodeName(),fm);
    		} 
        }
        if (attrs.size() > 0) {
	        _attrFieldsByXmlNodeName = new LinkedHashMap<String,FieldMap>(attrs.size(),1F);
	        _attrFieldsByXmlNodeName.putAll(attrs);
        }
        if (els.size() > 0) {
			_elementsFieldsByXmlNodeName = new LinkedHashMap<String,FieldMap>(els.size(),1F);
			_elementsFieldsByXmlNodeName.putAll(els);
        }
        
// If uncomment Add to the fields list!!! > private Map<String,List<FieldMap>> _colFieldsContainingBeansEnclosedByXmlNodeName;
//        // Init the cache that indexes collection fields wrapped by an xml tag
//        // (there can be more than a single collection field that wraps beans with the same xml tag)
//		Map<String,List<FieldMap>> colEls = new LinkedHashMap<String,List<FieldMap>>();
//        for (FieldMap fm : _fields.values()) {
//        	DataType dataType = fm.getDataType();
//        	if (dataType.isCollection() && !dataType.asCollection().getValueElementsDataType().isSimple()) {
//        		BeanMap colElsBeanMap = dataType.asCollection().getValueElementsDataType().getBeanMap();
//        		if (colElsBeanMap != null) { 	// si es null es una colecci�n de tipos simples (String, etc) 
//        			String elXmlEnclosingTag = colElsBeanMap.getXmlMap().getNodeName();
//        			List<FieldMap> colFields = colEls.get(elXmlEnclosingTag);
//        			if (colFields == null) {
//        				colFields = new ArrayList<FieldMap>();
//        				colEls.put(elXmlEnclosingTag,colFields);
//        			}
//        			colFields.add(fm);
//        		}
//        	} else if ((dataType.isMap() && !dataType.asMap().getValueElementsDataType().isSimple())
//        			|| (dataType.getType() == LanguageTexts.class)) {
//        		BeanMap mapValueElsBeanMap = dataType.asMap().getValueElementsDataType().getBeanMap();
//        		if (mapValueElsBeanMap != null) { 	// si es null es una colecci�n de tipos simples (String, etc) 
//        			String elXmlEnclosingTag = mapValueElsBeanMap.getXmlMap().getNodeName();
//        			List<FieldMap> colFields = colEls.get(elXmlEnclosingTag);
//        			if (colFields == null) {
//        				colFields = new ArrayList<FieldMap>();
//        				colEls.put(elXmlEnclosingTag,colFields);
//        			}
//        			colFields.add(fm);
//        		}
//        	}
//        }
//        if (colEls.size() > 0) {
//	        _colFieldsContainingBeansEnclosedByXmlNodeName = new LinkedHashMap<String,List<FieldMap>>(colEls.size(),1F);
//	        _colFieldsContainingBeansEnclosedByXmlNodeName.putAll(colEls);
//        }
	}
///////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
///////////////////////////////////////////////////////////////////////////////////////////    
    public String debugInfo() {
    	return toXml();
    }
    public String toXml() {
    	String outXml = null;
    	if (this.isCustomXmlTransformed()) {
    		outXml = Strings.customized("<class name='{}' fromElement='{}'>\r\n" +
    									"\t<customXmlTransformers>\r\n" +
    									"\t\t<xmlRead>{}</xmlRead>\r\n" +
    									"\t\t<xmlWrite>{}</xmlWrite>\r\n" +
    									"\t</customXmlTransformers>\r\n" +
    									"</class>",
    									_typeName,_xmlMap.getNodeName(),
    									_customXMLTransformers.getXmlReadTransformer().getClass().getName(),
    									_customXMLTransformers.getXmlWriteTransformer().getClass().getName());
    	} else {
	    	String clsOpen = Strings.customized("<class name='{}' fromElement='{}' useAccessors='{}'>",
	    										_typeName,
	    									    _xmlMap.getNodeName(),
	    									    Boolean.toString(_useAccessors));
	    	String clsEnd = null;
	    	
	    	StringBuffer fields = new StringBuffer(600);
	    	if (CollectionUtils.hasData(_fields)) {
		    	for (FieldMap fm : _fields.values()) {
		    		fields.append("\r\n\t").append(fm.toXml());
		    	}
		    	clsEnd = "\r\n</class>";
	    	} else {
	    		clsEnd = "</class>";
	    	}
	    	outXml = clsOpen + fields.toString() + clsEnd;
    	}
    	return outXml;
    }
}
