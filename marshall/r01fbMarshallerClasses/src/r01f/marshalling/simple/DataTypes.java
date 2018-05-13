package r01f.marshalling.simple;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCodeAndMultipleLabels;
import r01f.enums.EnumWithCodeAndMultipleLabelsWrapper;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.collections.CollectionUtils;

public class DataTypes {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	static final Pattern DATATYPE_PATTERN = Pattern.compile("(Map|Collection)?:?([a-zA-Z0-9.$]+)((?:\\(|\\[).*(?:\\)|\\]))?");
															//"(Map|Collection)?\\:?([a-zA-Z0-9.$]+)((?:\\(|\\[).*(?:\\)|\\]))?"
	static final Pattern DATE_PATTERN = Pattern.compile("[a-zA-Z]+(?:\\((.+)\\))?");
	static final Pattern ENUM_PATTERN = Pattern.compile("Enum(?:\\((.+)\\))?");
/////////////////////////////////////////////////////////////////////////////////////////
//	DATA TYPES
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Wraps the data type
     */
    @Accessors(prefix="_")
    @NoArgsConstructor(access=AccessLevel.PRIVATE)	
    public static class DataType {
    					private String _text;			// Data type description
    	@Getter @Setter private BeanMap _beanMap;		// set at the mapping load time (SimpleMarshallerMappings)
    	@Getter @Setter private String _name;
    	@Getter	@Setter private DataTypeEnum _typeDef;
    	@Getter @Setter	private Class<?> _type;			// Object type
    	@Getter @Setter private boolean _canBeCreatedFromString;	// true if the object can be created from a string (has a valueOf method)
    	
    	// Cache to avoid regular expressions
    	@Getter	@Setter(AccessLevel.PROTECTED) private boolean _simple;		// true if a primitive type (string, long, int, etc)
    	@Getter @Setter(AccessLevel.PROTECTED) private boolean _map;		// true if a map
    	@Getter	@Setter(AccessLevel.PROTECTED) private boolean _collection;	// true if a collection
    	@Getter	@Setter(AccessLevel.PROTECTED) private boolean _enum;		// true if enum
    	@Getter	@Setter(AccessLevel.PROTECTED) private boolean _object;		// true if a java object 
    	@Getter	@Setter(AccessLevel.PROTECTED) private boolean _date;		// true if a date
    	@Getter @Setter(AccessLevel.PROTECTED) private boolean _javaType;	// true if a java type definition (Class)
    	
    	public DataType(final String name,final DataTypeEnum typeDef) {
    		_name = name;
    		_typeDef = typeDef;
    	}
    	public boolean is(final Class<?> type) {
    		return _type == type;
    	}
    	public boolean isAnyOf(final Class<?>... types) {
    		boolean outIs = false;
    		for (Class<?> type : types) {
    			if (_type == type) {
    				outIs = true;
    				break;
    			}
    		}
    		return outIs;
    	}
    	public boolean isImplementingAnyOf(final Class<?>... types) {
    		boolean outIs = false;
    		for (Class<?> type : types) {
    			if (ReflectionUtils.isImplementing(_type,type)) {
    				outIs = true;
    				break;
    			}
    		}
    		return outIs;
    	}
    	public boolean isCollectionOrMap() {
    		return _collection || _map;
    	}
    	public boolean isXML() {
    		return _typeDef == DataTypeEnum.XML;
    	}
    	public boolean isInterface() {
    		return ReflectionUtils.isInterface(_type);
    	}
    	public boolean isAbstract() {
    		return ReflectionUtils.isAbstract(_type);
    	}
    	public boolean isInstanciable() {
    		return ReflectionUtils.isInstanciable(_type);
    	}
    	public ObjectType asObject() {
    		return (ObjectType)this;
    	}
    	public CollectionType asCollection() {
    		return (CollectionType)this;
    	}
    	public MapCollectionType asMap() {
    		return (MapCollectionType)this;
    	}
    	public EnumType asEnum() {
    		return (EnumType)this;
    	}
    	public DateType asDate() {
    		return (DateType)this;
    	}
    	public JavaClassType asJavaType() {
    		return (JavaClassType)this;
    	}
		@Override
		public String toString() { return _text; }

		public String debugInfo() {
			return new StringBuilder(_name).append(" -").append(_typeDef.getTypeNames()[0]).append("-")
							.toString();
		}
		/**
		 * Factory 
		 * <pre>
		 * 		- beans:		[type name]
		 * 		- Maps:			Map:[map type](key type,value type)
		 * 		- Colecctions:	Collection:[coleccion type](value type)
		 * </pre>
		 * @param text
		 * @return
		 */
		public static DataType create(final String text) {
			DataType outDataType = null;
			
			String name = text;
			if (name == null) throw new IllegalArgumentException("Cannot create a DataType: the description cannot be known; maybe it's due to a unknown collection element type"); 
			if (name.equalsIgnoreCase("Object")) name = "java.lang.Object";
			DataTypeEnum typeDef = DataTypeEnum.fromTypeName(text);
			// -- Complex objects
			if (typeDef.is(DataTypeEnum.OBJECT)) {
				outDataType = new ObjectType(name,typeDef,
											 !Object.class.getName().equals(name));	// is comples if NOT java.lang.Object
			}
			// -- Collections
			if (typeDef.isIn(DataTypeEnum.COLLECTION,DataTypeEnum.MAP,DataTypeEnum.ARRAY)) {
				// Sets the name of the elements of the collection
				// ...at this stage, the DataType reference of the collection elements CANNOT be set
				String keyElsTypeName = null;	// only maps
				String valueElsTypeName = null;	// lists and mapss
				Matcher m = DATATYPE_PATTERN.matcher(name);
				if (m.find()) {
					if (typeDef == DataTypeEnum.ARRAY) {
						valueElsTypeName = m.group(2);
						outDataType = new CollectionType(m.group(2),
														 typeDef,
												 	 	 valueElsTypeName);
					} else if (typeDef == DataTypeEnum.COLLECTION) {
						valueElsTypeName = m.group(3) != null ? m.group(3).substring(1,m.group(3).length()-1) 
															  : DataTypeEnum.OBJECT.getTypeNames()[0];
						
						outDataType = new CollectionType(m.group(2),
														 typeDef,
												 	 	 valueElsTypeName);
					} else if (typeDef == DataTypeEnum.MAP) {
						String[] types = m.group(3) != null ? m.group(3).substring(1,m.group(3).length()-1).split(",")
															: new String[] {DataTypeEnum.OBJECT.getTypeNames()[0],DataTypeEnum.OBJECT.getTypeNames()[0]};
						keyElsTypeName = types.length == 2 ? types[0] : DataTypeEnum.OBJECT.getTypeNames()[0];
						valueElsTypeName = types.length == 2 ? types[1] : DataTypeEnum.OBJECT.getTypeNames()[0];
						
						outDataType = new MapCollectionType(m.group(2),
															typeDef,
															keyElsTypeName,valueElsTypeName);
					}
				}
			} 

			// -- Dates
			if (typeDef.isIn(DataTypeEnum.SQLDATE,DataTypeEnum.DATE)) {
				String dateFmt = null;
				Matcher m = DATE_PATTERN.matcher(name);
				if (m.find()) {
					dateFmt = m.group(1);	// a the date format is set like Date(dd/MM/yyyy)
				} else {
					dateFmt = "millis";		// the date format is NOT set
				}
				outDataType = new DateType(name,typeDef,dateFmt);
			}
			
			// -- Enums
			if (typeDef == DataTypeEnum.ENUM) {
				String enumTypeName = null;
				Matcher m = ENUM_PATTERN.matcher(name);
				if (m.find()) { 
					enumTypeName = m.group(1);
					if (enumTypeName == null) enumTypeName = "java.lang.Enum";
					outDataType = new EnumType(name,typeDef,
											   enumTypeName);
				} 
			}
			
			// -- java objects
			if (typeDef == DataTypeEnum.JAVACLASS) {
				outDataType = new JavaClassType(name,typeDef);
			}
			
			// Simple typess
			if (outDataType == null) outDataType = new DataType(name,typeDef);
			boolean isSimple = (!typeDef.isIn(DataTypeEnum.COLLECTION,DataTypeEnum.MAP,DataTypeEnum.OBJECT));	// aqui entran tambien los enums y los Dates
			outDataType.setSimple(isSimple);
			
			// Finally try to get the type
			Class<?> type = MappingReflectionUtils.typeOf(outDataType);
			outDataType.setType(type);
			
			// Cache if the object can be created from a String
			outDataType.setCanBeCreatedFromString(ReflectionUtils.canBeCreatedFromString(type));
			
			outDataType._text = text;	// Store the description for debugging pourposess
			return outDataType;
		}
    }
///////////////////////////////////////////////////////////////////////////////
//	DATATYPE COLLECTION
///////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    public static abstract class CollectionTypeBase 
                         extends DataType {
    	
    	@Getter	@Setter	private DataType _valueElementsDataType;		// Sset at mapping load time (see SimpleMarshallerMappings)
    	
    	public CollectionTypeBase(final String name,final DataTypeEnum type,
    						      final String elsTypeName) {
    		super(name,type);
    		_valueElementsDataType = DataType.create(elsTypeName);
    	}
    	public Class<?> getValueElementsType() {
    		return _valueElementsDataType.getType();
    	}
    }
    @Accessors(prefix="_")
    public static class CollectionType 
                extends CollectionTypeBase {
    	public CollectionType(final String name,final DataTypeEnum type,
    						  final String elsTypeName) {
    		super(name,type,elsTypeName);
    		this.setCollection(true);
    	}
    }
    @Accessors(prefix="_")
    public static class MapCollectionType 
                extends CollectionTypeBase {
    	@Getter	@Setter	private DataType _keyElementsDataType;		// Set at mapping load time (see SimpleMarshallerMappings)
    	
    	public MapCollectionType(final String name,final DataTypeEnum type,
    							 final String keyElsTypeName,final String valueElsTypeName) {
    		super(name,type,valueElsTypeName);
    		_keyElementsDataType = DataType.create(keyElsTypeName);
    		this.setMap(true);
    	}
    	public Class<?> getKeyElementsType() {
    		return _keyElementsDataType.getType();
    	}
    }
///////////////////////////////////////////////////////////////////////////////
//	DATATYPE OBJECT
///////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    public static class ObjectType 
    			extends DataType {
    	
    	@Getter	private boolean _knownType;		// true if it'a an object of a known type
    	
    	public ObjectType(final String name,final DataTypeEnum type,
    					  final boolean knownType) {
    		super(name,type);
    		this.setObject(true);
    		_knownType = knownType;
    	}
    	/**
    	 * @return an array with final fields 
    	 */
    	public Map<String,FieldMap> finalFields() {
    		return super.getBeanMap().getFinalFields();
    	}
    	/**
    	 * @return an array with non final fields
    	 */
    	public Map<String,FieldMap> nonFinalFields() {
    		return super.getBeanMap().getNonFinalFields();
    	}
    	/**
    	 * @return all the data type fields
    	 */
    	public Map<String,FieldMap> fields() {
    		return super.getBeanMap().getFields();
    	}
    	/**
    	 * @return true if it has any mapped field
    	 */
    	public boolean hasFields() {
    		return CollectionUtils.hasData(super.getBeanMap().getFields());
    	}
    	/**
    	 * @return true if it's an immutable object (all fields are final)
    	 */
    	public boolean isImmutable() {
    		return this.getBeanMap().getFields() != null && this.getBeanMap().getFinalFields() != null
    			&& this.getBeanMap().getFields().size() > 0 && this.getBeanMap().getFinalFields().size() > 0
    		    && this.getBeanMap().getFields().size() == this.getBeanMap().getFinalFields().size(); 
    	}
    	/**
    	 * @return true if the object has only a single simple final field
    	 */
    	public boolean hasOnlyOneFinalSimpleField() {
    		return this.finalFields().size() == 1					// with only a single final field
	        	&& CollectionUtils.of(this.finalFields())			// that is simple (String, int, long, etc)
	        	 				  .pickOneAndOnlyEntry().getValue()	// ... for example an OID object with a final String 
	        	 				  .getDataType().isSimple();
    	}
    	/**
    	 * @return the DataType of the single final simple field (supossing this type has only a single final field)
    	 */
    	public DataType getSingleFinalSimpleField() {
    		DataType outDataType = CollectionUtils.of(this.finalFields())
    	 				   					  	  .pickOneAndOnlyEntry().getValue()
    	 				   					  	  .getDataType();
    		return outDataType;
    	}
    	/**
    	 * @return true if the object has only a single simple field
    	 */
    	public boolean hasOnlyOneSimpleField() {
    		return this.fields() != null
    			&& this.fields().size() == 1				// with only a single field
	        	&& CollectionUtils.of(this.fields())		// that is simple (String, int, long, etc)
	        	 				  .pickOneAndOnlyEntry().getValue()			// ... for example an OID object with a final String 
	        	 				  .getDataType().isSimple();
    	}
    	/**
    	 * @return the DataType of the single simple field (supossing this type has only a single field)
    	 */
    	public DataType getSingleSimpleField() {
    		DataType outDataType = CollectionUtils.of(this.fields())
    	 				   					  	  .pickOneAndOnlyEntry().getValue()
    	 				   					  	  .getDataType();
    		return outDataType;
    	}
    }
///////////////////////////////////////////////////////////////////////////////
//	DATATYPE ENUM
///////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    public static class EnumType 
    			extends DataType {
    	@Getter private String _enumTypeName;	// enum type
    	
    	public EnumType(final String name,final DataTypeEnum type,
    					final String enumTypeName) {
    		super(name,type);
    		this.setEnum(true);
    		_enumTypeName = enumTypeName;
    	}
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DATATYPE CLASS
/////////////////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    public static class JavaClassType 
    			extends DataType {
    	
    	public JavaClassType(final String name,final DataTypeEnum type) {
    		super(name,type);
    		this.setJavaType(true);
    	}
    }
///////////////////////////////////////////////////////////////////////////////
//	DATATYPE DATE
///////////////////////////////////////////////////////////////////////////////
    @Accessors(prefix="_")
    public static class DateType 
    			extends DataType {
    	@Getter @Setter private String _dateFormat;		// date format
    	
    	public DateType(final String name,final DataTypeEnum type,
    					final String dateFormat) {
    		super(name,type);
    		this.setDate(true);
    		_dateFormat = dateFormat;
    	}
    }
    
///////////////////////////////////////////////////////////////////////////////
//	DATA TYPES
///////////////////////////////////////////////////////////////////////////////
	/**
	 * Date type
	 */
    @Accessors(prefix = "_")
	public enum DataTypeEnum
	 implements EnumWithCodeAndMultipleLabels<Integer,DataTypeEnum> {
		NULL		(-1,"null"),
		STRING		(1,"java.lang.String","String"),		STRINGBUILDER	(2,"java.lang.StringBuilder","StringBuilder"),	STRINGBUFFER	(3,"java.lang.StringBuffer","StringBuffer"),
		INTEGER		(4,"java.lang.Integer","Integer"),		INTEGER_P		(5,"int"),
		LONG		(6,"java.lang.Long","Long"),			LONG_P			(7,"long"),
		DOUBLE		(8,"java.lang.Double","Double"),		DOUBLE_P		(9,"double"),
		FLOAT		(10,"java.lang.Float","Float"),			FLOAT_P			(11,"float"),
		NUMBER		(12,"java.lang.Number"),
		BOOLEAN		(13,"java.lang.Boolean","Boolean"),		BOOLEAN_P		(14,"boolean"),
		DATE		(15,"java.util.Date","Date"),
		SQLDATE		(16,"java.sql.Date","SQLDate"),
		OBJECT		(17,"java.lang.Object","Object"),
		XML			(18,"XML"),
		MAP			(19,"Map"),
		COLLECTION 	(20,"Collection"),
		ARRAY	 	(21,"Array"),
		ENUM	 	(22,"java.lang.Enum","Enum"),
		JAVACLASS	(23,"java.lang.Class","Class");	
		
			
		@Getter private final Integer _code;
		@Getter private final Class<Integer> _codeType = Integer.class;
		
		@Getter private final String[] _typeNames;  
		private DataTypeEnum(final int code,final String... typeNames) {
			_code = code;
			_typeNames = typeNames;
		}
		// --- util methods
		public boolean canBeFromTypeName(final String typeName) {
			return this.canBeFrom(typeName);
		} 
		// --- EnumWithCodeAndMultipleLabels methods		
		@Override 
		public boolean is(final DataTypeEnum otherType) {
			return this == otherType;
		}
		@Override
		public boolean isIn(final DataTypeEnum... dataTypes) {
			return enums.isIn(this,dataTypes);
		}
		@Override 
		public boolean canBeFrom(final String desc) {
			return enums.canBeFrom(this,desc);
		}
		@Override
		public String getLabel() {
			return _typeNames != null && _typeNames.length > 0  ? _typeNames[0] : null;
		}
		@Override
		public String[] getLabels() {
			return _typeNames;
		}
		// --- Metodos estaticos 
		private static EnumWithCodeAndMultipleLabelsWrapper<Integer,DataTypeEnum> enums = new EnumWithCodeAndMultipleLabelsWrapper<Integer,DataTypeEnum>(DataTypeEnum.values());
																									//.strict();		// Lanza IllegalArgumentException si NO se encuentra un elemento en un m�todo fromXX
		public static DataTypeEnum fromCode(final int code) {
			return enums.fromCode(code);
		}
		public static DataTypeEnum fromType(final Class<?> type) {
			DataTypeEnum outDataType = null;
			// type name is not enought for map, colecction, array, etc
			if (CollectionUtils.isMap(type)) {
				outDataType = DataTypeEnum.MAP;
			} else if (CollectionUtils.isCollection(type)) {
				outDataType = DataTypeEnum.COLLECTION;
			} else if (CollectionUtils.isArray(type)) {
				outDataType = DataTypeEnum.ARRAY;
			} else if (ReflectionUtils.isSubClassOf(type,Enum.class)) {
				outDataType = DataTypeEnum.ENUM;
			}
			// for every other case, the type name can be used to get the type
			else {
				outDataType = DataTypeEnum.fromTypeName(type.getClass().getName());
			}
			return outDataType;
		}
		public static DataTypeEnum fromTypeName(final String typeName) {
			DataTypeEnum theDataType = null;
			//  java types: javaType 										ie: java.lang.String
			//        Maps: Map:javaMapType(key java type,val java type)    ie: Map:java.util.Map(java.lang.String,java.lang.String))
			// Colecctions: Collection:javaColType(val java type			ie: Collection:java.util.List(java.lang.String)
			Matcher m = DATATYPE_PATTERN.matcher(typeName);	//"(Map|Collection)?\\:?([a-zA-Z0-9.$]+)((?:\\(|\\[).*(?:\\)|\\]))?");
			if (m.find()) {
				String colType = m.group(1);			// Map | Collection
				String javaType = m.group(2);			// Java Type
				String arrayColMapType = m.group(3);	// array or java collection|map elements type
				if (colType != null) {
					// Create a regular expression that matches the type nam
					if (colType.equalsIgnoreCase("Map")) {
						theDataType = DataTypeEnum.MAP;
					} else if (colType.equalsIgnoreCase("Collection")) {
						theDataType = DataTypeEnum.COLLECTION;
					} 
				} else if (arrayColMapType != null && arrayColMapType.equals("[]")) {
					theDataType = DataTypeEnum.ARRAY;
				} else {
					// Create a regula expression that mathes the type name
					Pattern p  = Pattern.compile(javaType);
					theDataType = enums.elementMatching(p);
					if (theDataType == null) theDataType = DataTypeEnum.OBJECT;
				}
			} else {
				throw new IllegalArgumentException("The provided java type is not valid");
			}
			return theDataType;
		}
		public static DataTypeEnum fromTypeName(Pattern regEx) {
			return enums.elementMatching(regEx);
		}
		public static DataTypeEnum fromName(String name) {
			return enums.fromName(name);
		}
	}
}
