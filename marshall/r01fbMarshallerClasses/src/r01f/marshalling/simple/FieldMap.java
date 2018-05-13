package r01f.marshalling.simple;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.enums.EnumWithCodeAndLabel;
import r01f.enums.EnumWithCodeAndLabelWrapper;
import r01f.marshalling.simple.DataTypes.DataType;
import r01f.util.types.Strings;

/**
 * Models the java <-> xml mapping of a type's field 
 * que lo define
 */
@Accessors(prefix="_")
     class FieldMap 
implements Debuggable {
///////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
///////////////////////////////////////////////////////////////////////////////////////////
    @Getter @Setter private FieldXMLMap _xmlMap;			// XML mapping 
    @Getter @Setter private BeanMap _declaringBeanMap;     	// Field's type
    @Getter @Setter private String _name;                   // Field's name
    @Getter @Setter private DataType _dataType;             // Data type
    @Getter @Setter private boolean _final;					// true if a final field
    @Getter @Setter private Relation _relation; 			// Relation type 
    @Getter @Setter private String _createMethod;     		// Object create method
    @Getter @Setter private boolean _oid = false;          	// Is this the type's oid field    
    @Getter @Setter private boolean _tranzient = false;    	// Is the field transient? (transient fields are NOT serialized from xml)
    
//    public void setXmlMap(String nodeName,
//    					  boolean isAttribute,boolean isCDATA,
//    					  String colElsNodeName) {
//    	_xmlMap = new FieldXMLMap(nodeName,isAttribute,isCDATA,colElsNodeName);
//    }
///////////////////////////////////////////////////////////////////////////////////////////
//  Mapping
///////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
    @NoArgsConstructor @AllArgsConstructor
    class FieldXMLMap {    	
    	@Getter @Setter private String _nodeName;			// XML node (element | attribute)
    	@Getter @Setter private boolean _explicitNodeName;	// sets if the node name was explicitly set or was computed from the field's name
	    @Getter @Setter private boolean _attribute;					// Sets if it's an XML attribute
	    @Getter @Setter private boolean _expandableAsAttributes;	// If the objet has many fields that are "expanded" as attribute	
	    @Getter @Setter private boolean _cdata;    			// Is an XML CDATA node 
	    @Getter @Setter private String _valueToIgnoreWhenWritingXML;		// If the field value matches this value, the field is NOT serialized to XML	
	    @Getter @Setter private String _discriminatorWhenNotInstanciable;	// Name of the attribute to be included in the xml if the field
	    																	// is NOT instanciable (abstract type or interface)
	    @Getter @Setter private String _colElsNodeName;	// If the field is a collection, contains the name of 
	    												// every collection element
	    												// Used for simple type collections (String, long, xml, etc)
	    												// Ejemplo: 	<myCollection>
	    												//					<item>Valor 1</item>
	    												//					<item>Valor 2</item>
	    												//				<myCollection>
	    @Getter @Setter private boolean _explicitColElsNodeName;	//  sets if the node name was explicitly set or was computed from the field's name
	    
	    public FieldXMLMap(final FieldXMLMap other) {
	    	_nodeName = other.getNodeName();
	    	_explicitNodeName = other.isExplicitNodeName();
	    	_attribute = other.isAttribute();
	    	_expandableAsAttributes = other.isExpandableAsAttributes();
	    	_cdata = other.isCdata();
	    	_valueToIgnoreWhenWritingXML = other.getValueToIgnoreWhenWritingXML();
	    	_discriminatorWhenNotInstanciable = other.getDiscriminatorWhenNotInstanciable();
	    	_colElsNodeName = other.getColElsNodeName();
	    	_explicitColElsNodeName = other.isExplicitColElsNodeName();
	    }
    }
///////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
///////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Default constructor
     */
    public FieldMap() {
    	_xmlMap = new FieldXMLMap();
        // no create method by default
        _createMethod = null;
        // it's not an oid by default
        _oid = false;             
    }
    /**
     * Constructor from othero fieldMap
     * @param other
     */
    public FieldMap(final FieldMap other) {
    	_xmlMap = new FieldXMLMap(other.getXmlMap());	// clone!
    	_declaringBeanMap = other.getDeclaringBeanMap();
    	_name = other.getName();
    	_dataType = other.getDataType();
    	_final = other.isFinal();
    	_relation = other.getRelation();
    	_createMethod = other.getCreateMethod();
    	_oid = other.isOid();
    	_tranzient = other.isTranzient();
    }
    /**
     * Constructor from 
     * @param newName field's name
     * @param newDataType field's data type
     * @param isOid true if the field is an oid field
     */
    public FieldMap(final String newName,final DataType newDataType,
    				final boolean isOid) {
        this();
        _name = newName;
        _dataType = newDataType;
        _oid = isOid; 
    }    
    @Override   
    public String debugInfo() {
    	return this.toXml();
    }    
    
///////////////////////////////////////////////////////////////////////////////
//	RELATIONS
/////////////////////////////////////////////////////////////////////////////// 
    /**
     * Relation type 
     */
    @Accessors(prefix="_")
    @NoArgsConstructor(access=AccessLevel.PRIVATE)	
    public static class Relation {
    	@Getter @Setter private String _name;
    			@Setter	private RelationEnum _relation;   
		/**
		 * Factory of relation object from the name
		 * @param text
		 * @return
		 */
		public static Relation create(final String text) {
			Relation outRelation = new Relation();
			outRelation.setName(text);
			outRelation.setRelation(RelationEnum.fromRelationName(text));
			return outRelation;
		}
		@Override
		public String toString() { return _name; }	
		public boolean is(final RelationEnum relation) { return _relation == relation; }
		public boolean isIn(final RelationEnum... relations) { return _relation.isIn(relations); }		
    }    
	/**
	 * Relations
	 */
    @Accessors(prefix="_")
	@RequiredArgsConstructor 
	public enum RelationEnum implements EnumWithCodeAndLabel<Integer,RelationEnum> {
		NO(-1,"no_relation"),
		COMPOSITION(0,"composition"),
		AGGREGATION(1,"aggregation"); 	 			
		
		@Getter private final Integer _code;
		@Getter private final Class<Integer> _codeType = Integer.class;	
		
		@Getter private final String _relationName;
		
		// --- Methds EnumWithCodeAndLabel
		@Override
		public boolean is(RelationEnum rel) {
			return this == rel;
		}
		@Override
		public boolean isIn(RelationEnum... rels) {
			return enums.isIn(this,rels);
		}	
		@Override
		public boolean canBeFrom(String desc) {
			return enums.canBeFrom(desc);
		}
		@Override
		public String getLabel() {
			return _relationName;
		}
		// --- Static methods
		private static EnumWithCodeAndLabelWrapper<Integer,RelationEnum> enums = new EnumWithCodeAndLabelWrapper<Integer,RelationEnum>(RelationEnum.values()).strict();		
		public static RelationEnum fromCode(int code) {
			return enums.fromCode(code);
		}
		public static RelationEnum fromRelationName(String relationName) {
			return enums.from(relationName);
		}
		public static RelationEnum fromName(String name) {
			return enums.fromName(name);
		}		
	}
///////////////////////////////////////////////////////////////////////////////
//	DEBUG
///////////////////////////////////////////////////////////////////////////////
    /**
     * {@link FieldMap} as XML
     * @return
     */
    public String toXml() {
    	StringBuilder outField = new StringBuilder(200);
    	if (_xmlMap.isAttribute()) {
    		outField.append(Strings.customized("<member name='{}' dataType='{}' fromAttribute='{}'",
    										   _name,_dataType.toString(),_xmlMap.getNodeName()));
    	} else {
    		outField.append(Strings.customized("<member name='{}' dataType='{}' fromElement='{}'",
    										   _name,_dataType.toString(),_xmlMap.getNodeName()));
    		if (!_xmlMap.isExplicitNodeName()) outField.append(" explicitXmlElementMap='false'");
    		if ((_dataType.isCollection() || _dataType.isMap())
    		 && !Strings.isNullOrEmpty(_xmlMap.getColElsNodeName())) {
    			outField.append(" ofElements='").append(_xmlMap.getColElsNodeName()).append("'");
    			if (!_xmlMap.isExplicitColElsNodeName()) outField.append(" explicitCollectionItemsXmlElementMap='false'");
    		} else if (_dataType.isEnum()) {
    			outField.append(" enum='true'");
    		}
    		if (_xmlMap.getDiscriminatorWhenNotInstanciable() != null) outField.append(" discriminatorWhenNotInstanciable='").append(_xmlMap.getDiscriminatorWhenNotInstanciable()).append("'");
    		if (_xmlMap.isCdata()) outField.append(" isCDATA='true'");
    		if (_xmlMap.getValueToIgnoreWhenWritingXML() != null) outField.append(" doNotWriteXmlIfValueEquals='").append(_xmlMap.getDiscriminatorWhenNotInstanciable()).append("'");
    	}
    	if (_final) outField.append(" isFinal='true'");
		outField.append("/>");
    	return outField.toString();
    }
}

