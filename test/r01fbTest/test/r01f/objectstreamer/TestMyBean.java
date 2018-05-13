package r01f.objectstreamer;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import r01f.guids.OID;
import r01f.model.mock.MyBean;
import r01f.model.mock.MyEnum;
import r01f.model.mock.MyOIDs.MyTestOID;
import r01f.model.mock.MyOIDs.MyOtherTestOID;
import r01f.types.Path;
import r01f.util.types.collections.Lists;

public class TestMyBean 
	 extends TestObjectStreamerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testMyBean() throws IOException  {
		MyBean myBean = _buildMyBean();
		MarhallTestCheck<MyBean> check = new MarhallTestCheck<MyBean>() {
													@Override
													public void check(final MyBean original,final MyBean readed) {
														Assert.assertEquals(original.getOidStr(),readed.getOidStr());
														Assert.assertEquals(original.getOid1().asString(),readed.getOid1().asString());
														Assert.assertEquals(original.getOid2().asString(),readed.getOid2().asString());
														Assert.assertEquals(original.getOid3().asString(),readed.getOid3().asString());
														Assert.assertEquals(original.getOidCol().size(),readed.getOidCol().size());
														Assert.assertEquals(original.getMyOidCol().size(),readed.getMyOidCol().size());
														Assert.assertEquals(original.getPathCol().size(),readed.getPathCol().size());
														Assert.assertEquals(original.getEnums().size(),readed.getEnums().size());
														Assert.assertEquals(original.getStringCol().size(),readed.getStringCol().size());
														Assert.assertEquals(original.getName(),readed.getName());
														Assert.assertEquals(original.getSurname(),readed.getSurname());
													}
										   };
		_testJsonStreamer(myBean,
						  MyBean.class, 
						  check);
		_testXmlStreamer(myBean,
						 MyBean.class, 
						 check);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static MyBean _buildMyBean() {
		MyBean myBean = new MyBean(
								  "myOidStr",
								   MyOtherTestOID.forId("myOid_1"),
								   "myName",
								   "mySurn<wtf>ame'{ass}'",
								   MyTestOID.forId("myOid_2"),
								   MyOtherTestOID.forId("myOid_3"),
								   Lists.newArrayList("a","b"),
								   Lists.<OID>newArrayList(MyTestOID.forId("myOid_instance"),
										   			  	   MyOtherTestOID.forId("myOtherOid_instance")),
								   Lists.<MyTestOID>newArrayList(MyTestOID.forId("myOid_instance"),
										   			  	   		 MyTestOID.forId("myOid_other_instance")),
								   Lists.<Path>newArrayList(Path.from("/a/b/c"),
										   					Path.from("d/e/f")),
								   MyEnum.A,
								   Lists.newArrayList(MyEnum.A,
										   			  MyEnum.B),
								   new MyBean(
											  "myOidStr",
											   MyOtherTestOID.forId("myOid_1"),
											   "myName",
											   "mySurn<wtf>ame'{ass}'",
											   MyTestOID.forId("myOid_2"),
											   MyOtherTestOID.forId("myOid_3"),
											   Lists.newArrayList("a","b"),
											   Lists.<OID>newArrayList(MyTestOID.forId("myOid_instance"),
													   			  	   MyOtherTestOID.forId("myOtherOid_instance")),
											   Lists.<MyTestOID>newArrayList(MyTestOID.forId("myOid_instance"),
										   			  	   		 MyTestOID.forId("myOid_other_instance")),
											   Lists.<Path>newArrayList(Path.from("/foo/bar/baz"),
										   								Path.from("m/x/d")),
											   MyEnum.A,
											   Lists.newArrayList(MyEnum.A,
													   			  MyEnum.B),
											   	null,
												"TRANSIENT"),
								   "TRANSIENT"
								   );
		return myBean;
	}
}
