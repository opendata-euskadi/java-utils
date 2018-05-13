package r01f.model.mock;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.model.mock.MyOIDs.MyTestOID;
import r01f.model.mock.MyOIDs.MyOtherTestOID;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Path;

@MarshallType(as="myBean") 
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor 
public class MyBean {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="oidStr",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private String _oidStr;
	
	@MarshallField(as="oid1")
	@Getter @Setter private OID _oid1;
	
	@MarshallField(as="name")
    @Getter @Setter private String _name;
	
	@MarshallField(as="surname",escape=true)
    @Getter @Setter private String _surname;
	
	@MarshallField(as="oid2",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MyTestOID _oid2;	
	
	@MarshallField(as="oid3")
	@Getter @Setter private MyOtherTestOID _oid3;
	
	@MarshallField(as="strCol",
				   whenXml=@MarshallFieldAsXml(collectionElementName="colEl"),
				   escape=true)	
	@Getter @Setter private Collection<String> _stringCol;
	
	@MarshallField(as="oidCol",
				   whenXml=@MarshallFieldAsXml(collectionElementName="oidItem"),
				   escape=true)							
	@Getter @Setter private Collection<OID> _oidCol;
	
	@MarshallField(as="myOidCol",
				   whenXml=@MarshallFieldAsXml(collectionElementName="myOidItem"),
				   escape=true)							
	@Getter @Setter private Collection<MyTestOID> _myOidCol;
	
	@MarshallField(as="paths",
				   whenXml=@MarshallFieldAsXml(collectionElementName="path"))
	@Getter @Setter private Collection<Path> _pathCol;
	
	@MarshallField(as="enum",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MyEnum _enum;
	
	@MarshallField(as="enums")
	@Getter @Setter private Collection<MyEnum> _enums;
	
	@MarshallField(as="nested")
	@Getter @Setter private MyBean _nested;
	
	@MarshallIgnoredField
	@Getter @Setter private transient String _transientProp;
	

/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
}
